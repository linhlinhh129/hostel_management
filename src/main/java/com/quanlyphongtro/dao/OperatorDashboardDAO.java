package com.quanlyphongtro.dao;

import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class OperatorDashboardDAO {

    /**
     * Lấy thống kê số lượng Yêu cầu theo từng trạng thái.
     */
    public Map<String, Integer> getTicketStats() {
        Map<String, Integer> stats = new HashMap<>();
        // Mặc định các trạng thái
        stats.put("PENDING", 0);
        stats.put("IN_PROGRESS", 0);
        stats.put("COMPLETED", 0);
        stats.put("REJECTED", 0);

        String sql = "SELECT status, COUNT(*) as cnt " +
                     "FROM requests " +
                     "WHERE deleted_at IS NULL " +
                     "GROUP BY status";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String status = rs.getString("status");
                int count = rs.getInt("cnt");
                stats.put(status, count);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    /**
     * Lấy danh sách các yêu cầu có lịch hẹn trong ngày hôm nay.
     */
    public java.util.List<com.quanlyphongtro.model.Request> getTodaysAppointments(int operatorId) {
        java.util.List<com.quanlyphongtro.model.Request> list = new java.util.ArrayList<>();
        String sql = "SELECT rq.request_id, rq.title, r.code AS room_code, rq.rejection_reason " +
                     "FROM requests rq " +
                     "LEFT JOIN users u ON rq.sender_id = u.user_id " +
                     "LEFT JOIN rooms r ON u.user_id = r.tenant_id " +
                     "WHERE rq.deleted_at IS NULL AND rq.assigned_staff_id = ? " +
                     "AND rq.status IN ('PENDING', 'IN_PROGRESS') " +
                     "AND rq.rejection_reason LIKE CONVERT(varchar, GETDATE(), 23) + '%'"; 
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    com.quanlyphongtro.model.Request req = new com.quanlyphongtro.model.Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setTitle(rs.getString("title"));
                    req.setRoomCode(rs.getString("room_code"));
                    req.setRejectionReason(rs.getString("rejection_reason")); 
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
