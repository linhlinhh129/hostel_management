package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ReactionDAO {
    private static final Logger logger = LoggerFactory.getLogger(ReactionDAO.class);

    public boolean checkUserLiked(int postId, int userId) {
        String sql = "SELECT 1 FROM dbo.post_reactions WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("Error checkUserLiked", e);
        }
        return false;
    }

    public boolean insertReaction(int postId, int userId) {
        String sql = "INSERT INTO dbo.post_reactions (post_id, user_id) VALUES (?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            // Could throw due to primary key constraint if already liked
            logger.warn("Insert reaction failed (possibly already liked): {}", e.getMessage());
        }
        return false;
    }

    public boolean deleteReaction(int postId, int userId) {
        String sql = "DELETE FROM dbo.post_reactions WHERE post_id = ? AND user_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("Error deleteReaction", e);
        }
        return false;
    }
}
