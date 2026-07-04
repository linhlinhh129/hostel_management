package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.util.DatabaseUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DashboardDAO {

    public int getPendingRequestsCount(int operatorId) {
        String sql = "SELECT COUNT(*) FROM requests rq WHERE rq.deleted_at IS NULL AND rq.status = 'PENDING' AND (rq.assigned_staff_id = ? OR EXISTS(SELECT 1 FROM users u WHERE u.user_id = rq.sender_id AND u.role = 'TENANT'))";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getInProgressRequestsCount(int operatorId) {
        String sql = "SELECT COUNT(*) FROM requests rq WHERE rq.deleted_at IS NULL AND rq.status = 'IN_PROGRESS' AND rq.assigned_staff_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getIncidentsReportedCount(int operatorId) {
        String sql = "SELECT COUNT(*) FROM requests rq WHERE rq.sender_id = ? AND rq.deleted_at IS NULL AND MONTH(rq.created_at) = MONTH(GETDATE()) AND YEAR(rq.created_at) = YEAR(GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalRooms() {
        String sql = "SELECT COUNT(*) FROM rooms r WHERE r.status = 'RENTED'";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getUpdatedMeterReadingsCount() {
        String sql = "SELECT COUNT(*) FROM meter_readings mr WHERE MONTH(mr.reading_date) = MONTH(GETDATE()) AND YEAR(mr.reading_date) = YEAR(GETDATE())";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Request> getTodaysAppointments(int operatorId) {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT rq.*, u.full_name AS sender_name, r.code AS room_code, f.name AS facility_name " +
                     "FROM requests rq " +
                     "LEFT JOIN users u ON rq.sender_id = u.user_id " +
                     "LEFT JOIN rooms r ON u.user_id = r.tenant_id " +
                     "LEFT JOIN facilities f ON r.facility_id = f.facility_id " +
                     "WHERE rq.deleted_at IS NULL AND rq.assigned_staff_id = ? AND rq.status IN ('PENDING', 'IN_PROGRESS') " +
                     "AND CAST(rq.appoint_schedule AS DATE) = CAST(GETDATE() AS DATE)"; 
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, operatorId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Request req = new Request();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setTitle(rs.getString("title"));
                    req.setRoomCode(rs.getString("room_code"));
                    java.sql.Timestamp ts = rs.getTimestamp("appoint_schedule");
                    if (ts != null) {
                        req.setAppointSchedule(ts.toLocalDateTime());
                    }
                    list.add(req);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
