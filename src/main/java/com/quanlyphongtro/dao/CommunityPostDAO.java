package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.dto.NewsFeedDTO;
import com.quanlyphongtro.model.CommunityPost;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommunityPostDAO extends BaseDAO {

    public int insert(CommunityPost post) {
        String sql = "INSERT INTO dbo.community_posts (title, content, image_url, author_id, status, reviewed_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImageUrl());
            ps.setInt(4, post.getAuthorId());
            ps.setString(5, post.getStatus() != null ? post.getStatus() : "PENDING");
            if (post.getReviewedBy() != null) {
                ps.setInt(6, post.getReviewedBy());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            logger.error("insert community post failed", e);
            throw new RuntimeException("DB Error: " + e.getMessage(), e);
        }
        return -1;
    }

    public Integer getManagerByTenant(int tenantId) {
        String sql = "SELECT TOP 1 f.manager_id FROM dbo.rooms r " +
                     "JOIN dbo.facilities f ON r.facility_id = f.facility_id " +
                     "WHERE r.tenant_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("manager_id");
                }
            }
        } catch (Exception e) {
            logger.error("getManagerByTenant failed", e);
        }
        return null;
    }

    public List<CommunityPostDTO> getPostsForManager(int managerId, int cursor, int limit) {
        List<CommunityPostDTO> list = new ArrayList<>();
        // Cursor is the minimum post_id seen so far (because we load newest first)
        String sql = "SELECT p.*, u.full_name as author_name " +
                     "FROM dbo.community_posts p " +
                     "JOIN dbo.users u ON p.author_id = u.user_id " +
                     "WHERE p.deleted_at IS NULL AND p.reviewed_by = ? ";
        if (cursor > 0) {
            sql += "AND p.post_id < ? ";
        }
        sql += "ORDER BY p.post_id DESC OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            ps.setInt(paramIndex++, managerId);
            if (cursor > 0) {
                ps.setInt(paramIndex++, cursor);
            }
            ps.setInt(paramIndex, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommunityPostDTO dto = new CommunityPostDTO();
                    dto.setId(rs.getInt("post_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setImageUrl(rs.getString("image_url"));
                    dto.setAuthorId(rs.getInt("author_id"));
                    dto.setAuthorName(rs.getString("author_name"));
                    dto.setStatus(rs.getString("status"));
                    dto.setCreatedAt(toLocalDateTime(rs, "created_at"));
                    dto.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("getPostsForManager failed", e);
        }
        return list;
    }

    public CommunityPostDTO getPostById(int postId, int currentUserId) {
        String sql = "SELECT p.*, u.full_name as author_name, u.avatar_url as author_avatar, " +
                     "(SELECT COUNT(*) FROM dbo.post_reactions pr WHERE pr.post_id = p.post_id) as total_likes, " +
                     "(SELECT COUNT(*) FROM dbo.post_comments pc WHERE pc.post_id = p.post_id) as total_comments, " +
                     "CASE WHEN EXISTS (SELECT 1 FROM dbo.post_reactions pr2 WHERE pr2.post_id = p.post_id AND pr2.user_id = ?) THEN 1 ELSE 0 END as is_liked " +
                     "FROM dbo.community_posts p " +
                     "JOIN dbo.users u ON p.author_id = u.user_id " +
                     "WHERE p.post_id = ? AND p.deleted_at IS NULL";
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommunityPostDTO dto = new CommunityPostDTO();
                    dto.setId(rs.getInt("post_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setImageUrl(rs.getString("image_url"));
                    dto.setAuthorId(rs.getInt("author_id"));
                    dto.setAuthorName(rs.getString("author_name"));
                    dto.setAuthorAvatar(rs.getString("author_avatar"));
                    dto.setStatus(rs.getString("status"));
                    dto.setCreatedAt(toLocalDateTime(rs, "created_at"));
                    dto.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
                    dto.setTotalLikes(rs.getInt("total_likes"));
                    dto.setTotalComments(rs.getInt("total_comments"));
                    dto.setLikedByCurrentUser(rs.getInt("is_liked") == 1);
                    return dto;
                }
            }
        } catch (Exception e) {
            logger.error("getPostById failed for postId={}", postId, e);
        }
        return null;
    }

    /**
     * Lấy danh sách bài viết đã APPROVED cho News Feed.
     * Cột ảnh trong DB: image_url (NVARCHAR(500), nullable).
     */
    public List<NewsFeedDTO> getNewsFeed(Integer currentUserId) {
        List<NewsFeedDTO> feed = new ArrayList<>();
        String sql = "SELECT cp.post_id, cp.title, cp.content, cp.image_url, " +
                "cp.author_id, u.full_name AS author_name, u.avatar_url AS author_avatar, " +
                "cp.created_at AS published_at, " +
                "(SELECT COUNT(*) FROM post_reactions pr WHERE pr.post_id = cp.post_id) AS like_count, " +
                "(SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = cp.post_id AND pc.deleted_at IS NULL) AS comment_count, " +
                "(SELECT COUNT(*) FROM post_reactions pr2 WHERE pr2.post_id = cp.post_id AND pr2.user_id = ?) AS is_liked " +
                "FROM community_posts cp " +
                "JOIN users u ON cp.author_id = u.user_id " +
                "WHERE cp.status = 'APPROVED' AND cp.deleted_at IS NULL " +
                "ORDER BY cp.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    feed.add(mapNewsFeedDTO(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting news feed", e);
        }
        return feed;
    }

    /**
     * Lấy chi tiết một bài viết (bất kể status) để hiển thị.
     */
    public NewsFeedDTO getPostDetail(Integer postId, Integer currentUserId) {
        String sql = "SELECT cp.post_id, cp.title, cp.content, cp.image_url, " +
                "cp.author_id, u.full_name AS author_name, u.avatar_url AS author_avatar, " +
                "cp.created_at AS published_at, cp.status, " +
                "(SELECT COUNT(*) FROM post_reactions pr WHERE pr.post_id = cp.post_id) AS like_count, " +
                "(SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = cp.post_id AND pc.deleted_at IS NULL) AS comment_count, " +
                "(SELECT COUNT(*) FROM post_reactions pr2 WHERE pr2.post_id = cp.post_id AND pr2.user_id = ?) AS is_liked " +
                "FROM community_posts cp " +
                "JOIN users u ON cp.author_id = u.user_id " +
                "WHERE cp.post_id = ? AND cp.deleted_at IS NULL";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapNewsFeedDTO(rs);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting post detail", e);
        }
        return null;
    }

    public void approvePost(int postId, int reviewerId) {
        String sql = "UPDATE dbo.community_posts SET status = 'APPROVED', reviewed_by = ?, updated_at = GETDATE() WHERE post_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, reviewerId);
            ps.setInt(2, postId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("approvePost failed for postId={}", postId, e);
        }
    }

    public void softDeletePost(int postId) {
        String sql = "UPDATE dbo.community_posts SET deleted_at = GETDATE(), updated_at = GETDATE() WHERE post_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.executeUpdate();
        } catch (Exception e) {
            logger.error("softDeletePost failed for postId={}", postId, e);
        }
    }

    /**
     * Lấy danh sách bài viết do chính Tenant tạo (mọi status).
     */
    public List<NewsFeedDTO> getMyPosts(Integer authorId) {
        List<NewsFeedDTO> posts = new ArrayList<>();
        String sql = "SELECT cp.post_id, cp.title, cp.content, cp.image_url, " +
                "cp.author_id, u.full_name AS author_name, u.avatar_url AS author_avatar, " +
                "cp.created_at AS published_at, cp.status, " +
                "(SELECT COUNT(*) FROM post_reactions pr WHERE pr.post_id = cp.post_id) AS like_count, " +
                "(SELECT COUNT(*) FROM post_comments pc WHERE pc.post_id = cp.post_id AND pc.deleted_at IS NULL) AS comment_count, " +
                "0 AS is_liked " +
                "FROM community_posts cp " +
                "JOIN users u ON cp.author_id = u.user_id " +
                "WHERE cp.author_id = ? AND cp.deleted_at IS NULL " +
                "ORDER BY cp.created_at DESC";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, authorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapNewsFeedDTO(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting my posts", e);
        }
        return posts;
    }

    /**
     * Tạo bài viết mới. Cột image_url cho phép null.
     */
    public CommunityPost createPost(CommunityPost post) {
        String sql = "INSERT INTO community_posts (title, content, image_url, author_id, status, reviewed_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImageUrl());
            ps.setInt(4, post.getAuthorId());
            ps.setString(5, post.getStatus() == null ? "PENDING" : post.getStatus());
            if (post.getReviewedBy() != null) {
                ps.setInt(6, post.getReviewedBy());
            } else {
                ps.setNull(6, java.sql.Types.INTEGER);
            }

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        post.setPostId(rs.getInt(1));
                        return post;
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error creating post", e);
        }
        return null;
    }

    /**
     * Soft-delete bài viết (tuân thủ DATA-01).
     */
    public boolean softDeletePost(Integer postId, Integer authorId) {
        String sql = "UPDATE community_posts SET deleted_at = GETDATE() WHERE post_id = ? AND author_id = ? AND status != 'APPROVED' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, authorId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.error("Error soft deleting post", e);
        }
        return false;
    }

    private NewsFeedDTO mapNewsFeedDTO(ResultSet rs) throws SQLException {
        NewsFeedDTO dto = new NewsFeedDTO();
        dto.setPostId(rs.getInt("post_id"));
        dto.setTitle(rs.getString("title"));
        dto.setContent(rs.getString("content"));
        dto.setImageUrl(rs.getString("image_url"));
        dto.setAuthorId(rs.getInt("author_id"));
        dto.setAuthorName(rs.getString("author_name"));
        dto.setAuthorAvatarUrl(rs.getString("author_avatar"));
        dto.setPublishedAt(toLocalDateTime(rs, "published_at"));
        dto.setLikeCount(rs.getInt("like_count"));
        dto.setCommentCount(rs.getInt("comment_count"));
        dto.setLikedByCurrentUser(rs.getInt("is_liked") > 0);
        if (hasColumn(rs, "status")) {
            dto.setStatus(rs.getString("status"));
        }
        return dto;
    }
}
