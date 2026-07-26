package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostReactionDAO extends BaseDAO {

    public boolean addReaction(int postId, int userId) {
        String sql = "INSERT INTO post_reactions (post_id, user_id, created_at) VALUES (?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            // Check for duplicate key violation
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                logger.warn("User {} already reacted to post {}", userId, postId);
                return false;
            }
            logger.error("Error adding post reaction", e);
            return false;
        }
    }

    public boolean removeReaction(int postId, int userId) {
        String sql = "DELETE FROM post_reactions WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            logger.error("Error removing post reaction", e);
            return false;
        }
    }

    public boolean hasReacted(int postId, int userId) {
        String sql = "SELECT 1 FROM post_reactions WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            logger.error("Error checking post reaction", e);
            return false;
        }
    }
}
