package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.dto.PostCommentDTO;
import com.quanlyphongtro.model.PostComment;
import com.quanlyphongtro.util.DatabaseUtil;
import java.sql.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PostCommentDAO extends BaseDAO {

    public boolean addComment(int postId, int userId, String content) {
        String sql = "INSERT INTO post_comments (post_id, user_id, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error adding post comment", e);
            return false;
        }
    }

    public List<PostCommentDTO> getCommentsByPostId(int postId, int currentUserId) {
        List<PostCommentDTO> comments = new ArrayList<>();
        // Join with users table to get author name
        String sql = "SELECT c.comment_id, c.post_id, c.user_id, c.content, c.created_at, u.full_name " +
                     "FROM post_comments c " +
                     "JOIN users u ON c.user_id = u.user_id " +
                     "WHERE c.post_id = ? AND c.deleted_at IS NULL " +
                     "ORDER BY c.created_at ASC";
                     
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    PostCommentDTO comment = new PostCommentDTO();
                    comment.setCommentId(rs.getInt("comment_id"));
                    comment.setPostId(rs.getInt("post_id"));
                    comment.setUserId(rs.getInt("user_id"));
                    comment.setContent(rs.getString("content"));
                    comment.setCreatedAt(toLocalDateTime(rs, "created_at"));
                    comment.setAuthorName(rs.getString("full_name"));
                    
                    // Set isAuthor flag if the current user is the author
                    comment.setIsAuthor(currentUserId == comment.getUserId());
                    
                    comments.add(comment);
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching comments for post {}", postId, e);
        }
        return comments;
    }

    public boolean deleteComment(int commentId) {
        // Soft delete
        String sql = "UPDATE post_comments SET deleted_at = CURRENT_TIMESTAMP WHERE comment_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, commentId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error deleting post comment", e);
            return false;
        }
    }
    
    public Integer getCommentAuthorId(int commentId) {
        String sql = "SELECT user_id FROM post_comments WHERE comment_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, commentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("user_id");
                }
            }
            
        } catch (SQLException e) {
            logger.error("Error fetching comment author id", e);
        }
        return null;
    }

    public CommentDTO addComment(PostComment comment) {
        String sql = "INSERT INTO post_comments (post_id, user_id, content, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, comment.getPostId());
            ps.setInt(2, comment.getUserId());
            ps.setString(3, comment.getContent());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        comment.setCommentId(rs.getInt(1));
                        return getCommentDTOById(comment.getCommentId(), conn);
                    }
                }
            }
        } catch (SQLException e) {
            logger.error("Error adding comment", e);
        }
        return null;
    }

    private CommentDTO getCommentDTOById(Integer commentId, Connection conn) throws SQLException {
        String sql = "SELECT pc.comment_id, pc.content, pc.user_id, u.full_name AS author_name, " +
                "u.avatar_url AS author_avatar, pc.created_at " +
                "FROM post_comments pc " +
                "JOIN users u ON pc.user_id = u.user_id " +
                "WHERE pc.comment_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(rs.getInt("comment_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setAuthorId(rs.getInt("user_id"));
                    dto.setAuthorName(rs.getString("author_name"));
                    dto.setAuthorAvatarUrl(rs.getString("author_avatar"));
                    dto.setCreatedAt(toLocalDateTime(rs, "created_at"));
                    return dto;
                }
            }
        }
        return null;
    }

    public List<CommentDTO> getCommentsByPostId(Integer postId) {
        List<CommentDTO> comments = new ArrayList<>();
        String sql = "SELECT pc.comment_id, pc.content, pc.user_id, u.full_name AS author_name, " +
                "u.avatar_url AS author_avatar, pc.created_at " +
                "FROM post_comments pc " +
                "JOIN users u ON pc.user_id = u.user_id " +
                "WHERE pc.post_id = ? AND pc.deleted_at IS NULL " +
                "ORDER BY pc.created_at ASC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setCommentId(rs.getInt("comment_id"));
                    dto.setContent(rs.getString("content"));
                    dto.setAuthorId(rs.getInt("user_id"));
                    dto.setAuthorName(rs.getString("author_name"));
                    dto.setAuthorAvatarUrl(rs.getString("author_avatar"));
                    dto.setCreatedAt(toLocalDateTime(rs, "created_at"));
                    comments.add(dto);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting comments by post id", e);
        }
        return comments;
    }
}
