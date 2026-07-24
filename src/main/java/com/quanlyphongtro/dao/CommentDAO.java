package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.CommentDTO;
import com.quanlyphongtro.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    private static final Logger logger = LoggerFactory.getLogger(CommentDAO.class);

    public List<CommentDTO> getCommentsByPostId(int postId) {
        List<CommentDTO> list = new ArrayList<>();
        String sql = "SELECT c.comment_id, c.post_id, c.user_id, c.content, c.created_at, u.full_name as user_name, u.avatar_url as user_avatar " +
                     "FROM dbo.post_comments c " +
                     "JOIN dbo.users u ON c.user_id = u.user_id " +
                     "WHERE c.post_id = ? " +
                     "ORDER BY c.created_at ASC"; // Oldest first
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(rs.getInt("comment_id"));
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setUserName(rs.getString("user_name"));
                    dto.setUserAvatar(rs.getString("user_avatar"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("Error getCommentsByPostId", e);
        }
        return list;
    }

    public CommentDTO insertComment(int postId, int userId, String content) {
        String sql = "INSERT INTO dbo.post_comments (post_id, user_id, content) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            ps.setString(3, content);
            
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        int newId = rs.getInt(1);
                        return getCommentById(newId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error insertComment", e);
        }
        return null;
    }
    
    public CommentDTO getCommentById(int commentId) {
        String sql = "SELECT c.comment_id, c.post_id, c.user_id, c.content, c.created_at, u.full_name as user_name, u.avatar_url as user_avatar " +
                     "FROM dbo.post_comments c " +
                     "JOIN dbo.users u ON c.user_id = u.user_id " +
                     "WHERE c.comment_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    CommentDTO dto = new CommentDTO();
                    dto.setId(rs.getInt("comment_id"));
                    dto.setPostId(rs.getInt("post_id"));
                    dto.setUserId(rs.getInt("user_id"));
                    dto.setUserName(rs.getString("user_name"));
                    dto.setUserAvatar(rs.getString("user_avatar"));
                    dto.setContent(rs.getString("content"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    return dto;
                }
            }
        } catch (Exception e) {
            logger.error("Error getCommentById", e);
        }
        return null;
    }

    public boolean deleteComment(int commentId) {
        String sql = "DELETE FROM dbo.post_comments WHERE comment_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, commentId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error deleteComment", e);
            return false;
        }
    }
}
