package com.quanlyphongtro.dao;
import java.util.List;
import com.quanlyphongtro.model.Request;
import java.util.ArrayList;
import java.sql.Timestamp;

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
    public Map<String, Integer> getTicketStats(int operatorId) {
        Map<String, Integer> stats = new HashMap<>();
        // Mặc định các trạng thái
        stats.put("PENDING", 0);
        stats.put("IN_PROGRESS", 0);
        stats.put("COMPLETED", 0);
        stats.put("REJECTED", 0);

        String sql = "SELECT status, COUNT(*) as cnt " +
                     "FROM requests " +
                     "WHERE deleted_at IS NULL AND assigned_staff_id = ? " +
                     "GROUP BY status";

        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
             
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String status = rs.getString("status");
                    int count = rs.getInt("cnt");
                    stats.put(status, count);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return stats;
    }

    /**
     * Lấy danh sách các yêu cầu có lịch hẹn sắp tới.
     */
    public List<Request> getUpcomingAppointments(int operatorId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT rq.request_id, rq.title, r.code AS room_code, rq.appoint_schedule, rq.status " +
                     "FROM requests rq " +
                     "LEFT JOIN users u ON rq.sender_id = u.user_id " +
                     "LEFT JOIN rooms r ON u.user_id = r.tenant_id " +
                     "WHERE rq.deleted_at IS NULL " +
                     "AND (rq.assigned_staff_id = ? OR rq.status = 'PENDING') " +
                     "AND rq.status IN ('PENDING', 'IN_PROGRESS') " +
                     "AND CAST(rq.appoint_schedule AS DATE) = CAST(GETDATE() AS DATE) " +
                     "ORDER BY rq.appoint_schedule ASC"; 
        
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setTitle(rs.getString("title"));
                    req.setRoomCode(rs.getString("room_code"));
                    Timestamp ts = rs.getTimestamp("appoint_schedule");
                    if (ts != null) {
                        req.setAppointSchedule(ts.toLocalDateTime());
                    }
                    req.setStatus(rs.getString("status"));
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
