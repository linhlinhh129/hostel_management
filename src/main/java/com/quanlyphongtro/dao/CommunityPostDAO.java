package com.quanlyphongtro.dao;

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
        String sql = "INSERT INTO community_posts (title, content, image_url, author_id, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, post.getTitle());
            ps.setString(2, post.getContent());
            ps.setString(3, post.getImageUrl());
            ps.setInt(4, post.getAuthorId());
            ps.setString(5, post.getStatus() == null ? "PENDING" : post.getStatus());

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
