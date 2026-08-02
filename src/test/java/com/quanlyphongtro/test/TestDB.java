package com.quanlyphongtro.test;

import com.quanlyphongtro.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TestDB {
    public static void main(String[] args) {
        String sql = "SELECT m.meter_id, m.room_id, r.code as room_code, m.electric, m.electric_usage, " +
                     "m.electric_status, m.electric_old_final, m.electric_new_start, m.reading_date, m.status " +
                     "FROM meter_readings m " +
                     "JOIN rooms r ON m.room_id = r.room_id " +
                     "WHERE m.deleted_at IS NULL " +
                     "ORDER BY m.reading_date DESC, m.meter_id DESC";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("METER READINGS:");
            int count = 0;
            while (rs.next() && count < 10) {
                System.out.printf("Room: %s, Electric: %d, Usage: %d, Status: %s, OldFinal: %d, NewStart: %d%n",
                    rs.getString("room_code"),
                    rs.getInt("electric"),
                    rs.getInt("electric_usage"),
                    rs.getString("electric_status"),
                    rs.getInt("electric_old_final"),
                    rs.getInt("electric_new_start"));
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
