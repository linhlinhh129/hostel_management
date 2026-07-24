package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.model.PostComment;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PostCommentDAO extends BaseDAO {

    public CommentDTO addComment(PostComment comment) {
        String sql = "INSERT INTO post_comments (post_id, user_id, content) VALUES (?, ?, ?)";
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
                        
                        // Lấy thêm thông tin user để trả về CommentDTO
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
