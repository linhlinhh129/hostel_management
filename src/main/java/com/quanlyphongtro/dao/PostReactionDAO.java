package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PostReactionDAO extends BaseDAO {

    public boolean toggleReaction(Integer postId, Integer userId) {
        String checkSql = "SELECT 1 FROM post_reactions WHERE post_id = ? AND user_id = ?";
        String deleteSql = "DELETE FROM post_reactions WHERE post_id = ? AND user_id = ?";
        String insertSql = "INSERT INTO post_reactions (post_id, user_id) VALUES (?, ?)";

        try (Connection conn = DatabaseUtil.getConnection()) {
            boolean exists = false;
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, postId);
                checkPs.setInt(2, userId);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        exists = true;
                    }
                }
            }

            if (exists) {
                try (PreparedStatement deletePs = conn.prepareStatement(deleteSql)) {
                    deletePs.setInt(1, postId);
                    deletePs.setInt(2, userId);
                    deletePs.executeUpdate();
                }
                return false; // Unlike
            } else {
                try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                    insertPs.setInt(1, postId);
                    insertPs.setInt(2, userId);
                    insertPs.executeUpdate();
                }
                return true; // Like
            }
        } catch (SQLException e) {
            logger.error("Error toggling reaction", e);
        }
        return false;
    }
    
    public int getLikeCount(Integer postId) {
        String sql = "SELECT COUNT(*) FROM post_reactions WHERE post_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.error("Error getting like count", e);
        }
        return 0;
    }
}
