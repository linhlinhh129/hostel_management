package com.quanlyphongtro.dao;

import com.quanlyphongtro.dto.CommunityPostDTO;
import com.quanlyphongtro.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class NewsFeedDAO {
    private static final Logger logger = LoggerFactory.getLogger(NewsFeedDAO.class);

    public List<CommunityPostDTO> getNewsFeedForToday(int currentUserId, int offset, int limit) {
        List<CommunityPostDTO> list = new ArrayList<>();
        // Query only APPROVED posts approved TODAY
        String sql = "SELECT p.post_id, p.title, p.content, p.image_url, p.author_id, p.status, " +
                     "p.reviewed_by, p.created_at, p.updated_at, " +
                     "u1.full_name as author_name, u1.avatar_url as author_avatar, u2.full_name as reviewer_name, " +
                     "(SELECT COUNT(*) FROM dbo.post_reactions pr WHERE pr.post_id = p.post_id) as total_likes, " +
                     "(SELECT COUNT(*) FROM dbo.post_comments pc WHERE pc.post_id = p.post_id) as total_comments, " +
                     "CASE WHEN EXISTS (SELECT 1 FROM dbo.post_reactions pr2 WHERE pr2.post_id = p.post_id AND pr2.user_id = ?) THEN 1 ELSE 0 END as is_liked " +
                     "FROM dbo.community_posts p " +
                     "JOIN dbo.users u1 ON p.author_id = u1.user_id " +
                     "LEFT JOIN dbo.users u2 ON p.reviewed_by = u2.user_id " +
                     "WHERE p.status = 'APPROVED' " +
                     "AND p.updated_at >= DATEADD(hour, -24, GETDATE()) " +
                     "AND p.deleted_at IS NULL " +
                     "ORDER BY p.updated_at DESC " +
                     "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, currentUserId);
            ps.setInt(2, offset);
            ps.setInt(3, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommunityPostDTO dto = new CommunityPostDTO();
                    dto.setId(rs.getInt("post_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setImageUrl(rs.getString("image_url"));
                    dto.setAuthorId(rs.getInt("author_id"));
                    dto.setAuthorName(rs.getString("author_name"));
                    dto.setAuthorAvatar(rs.getString("author_avatar"));
                    dto.setStatus(rs.getString("status"));
                    dto.setReviewedBy(rs.getInt("reviewed_by"));
                    dto.setReviewerName(rs.getString("reviewer_name"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    
                    dto.setTotalLikes(rs.getInt("total_likes"));
                    dto.setTotalComments(rs.getInt("total_comments"));
                    dto.setLikedByCurrentUser(rs.getInt("is_liked") == 1);
                    
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("Error getNewsFeedForToday", e);
        }
        return list;
    }

    public List<CommunityPostDTO> getTopInteractivePosts(int limit) {
        List<CommunityPostDTO> list = new ArrayList<>();
        String sql = "SELECT p.post_id, p.title, p.content, p.image_url, p.author_id, p.status, " +
                     "p.reviewed_by, p.created_at, p.updated_at, " +
                     "u1.full_name as author_name, u1.avatar_url as author_avatar, u2.full_name as reviewer_name, " +
                     "(SELECT COUNT(*) FROM dbo.post_reactions pr WHERE pr.post_id = p.post_id) as total_likes, " +
                     "(SELECT COUNT(*) FROM dbo.post_comments pc WHERE pc.post_id = p.post_id) as total_comments " +
                     "FROM dbo.community_posts p " +
                     "JOIN dbo.users u1 ON p.author_id = u1.user_id " +
                     "LEFT JOIN dbo.users u2 ON p.reviewed_by = u2.user_id " +
                     "WHERE p.status = 'APPROVED' " +
                     "AND p.updated_at >= DATEADD(hour, -24, GETDATE()) " +
                     "AND p.deleted_at IS NULL " +
                     "ORDER BY ( " +
                     "  (SELECT COUNT(*) FROM dbo.post_reactions pr WHERE pr.post_id = p.post_id) + " +
                     "  (SELECT COUNT(*) FROM dbo.post_comments pc WHERE pc.post_id = p.post_id) " +
                     ") DESC " +
                     "OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CommunityPostDTO dto = new CommunityPostDTO();
                    dto.setId(rs.getInt("post_id"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setImageUrl(rs.getString("image_url"));
                    dto.setAuthorId(rs.getInt("author_id"));
                    dto.setAuthorName(rs.getString("author_name"));
                    dto.setAuthorAvatar(rs.getString("author_avatar"));
                    dto.setStatus(rs.getString("status"));
                    dto.setReviewedBy(rs.getInt("reviewed_by"));
                    dto.setReviewerName(rs.getString("reviewer_name"));
                    dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    dto.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    
                    dto.setTotalLikes(rs.getInt("total_likes"));
                    dto.setTotalComments(rs.getInt("total_comments"));
                    dto.setLikedByCurrentUser(false); // not strictly needed for the top widget unless we show like button
                    
                    list.add(dto);
                }
            }
        } catch (Exception e) {
            logger.error("Error getTopInteractivePosts", e);
        }
        return list;
    }
}
