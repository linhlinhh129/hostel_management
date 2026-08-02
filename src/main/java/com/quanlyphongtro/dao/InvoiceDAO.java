package com.quanlyphongtro.dao;

import java.sql.Date;
import java.time.temporal.ChronoUnit;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.util.DatabaseUtil;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDAO extends BaseDAO {

    public static class InvoiceRoomSnapshot {
        public int roomId;
        public int facilityId;
        public Integer contractId;
        public Integer tenantId;
        public String status;
        public boolean hasTenant;
        public BigDecimal roomFee;
        public BigDecimal electricityPrice;
        public BigDecimal waterPrice;
        public BigDecimal internetFee;
        public BigDecimal serviceFee;
    }

    public static class InvoicePriceSnapshot {
        public BigDecimal roomFee;
        public BigDecimal electricityPrice;
        public BigDecimal waterPrice;
        public BigDecimal internetFee;
        public BigDecimal serviceFee;
        public LocalDate dueDate;
        public LocalDate readingDate;
    }

    public InvoiceRoomSnapshot getRoomSnapshotForInvoice(String roomCode, int managerId) throws SQLException {
        String sql = "SELECT r.room_id, r.facility_id, r.status, COALESCE(r.tenant_id, c.tenant_id) AS tenant_id, c.contract_id, r.room_fee, f.electricity_price, f.water_price, f.internet_fee, f.service_fee "
                +
                "FROM rooms r INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                "LEFT JOIN contracts c ON c.contract_id = (SELECT TOP 1 contract_id FROM contracts WHERE room_id = r.room_id AND status = 'ACTIVE' AND deleted_at IS NULL ORDER BY created_at DESC) "
                +
                "WHERE r.code = ? AND f.manager_id = ? AND r.deleted_at IS NULL AND f.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, roomCode);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InvoiceRoomSnapshot snapshot = new InvoiceRoomSnapshot();
                    snapshot.status = rs.getString("status");
                    int tenantIdVal = rs.getInt("tenant_id");
                    snapshot.hasTenant = !rs.wasNull() && tenantIdVal > 0;
                    if (snapshot.hasTenant) {
                        snapshot.tenantId = tenantIdVal;
                    }
                    int contractIdVal = rs.getInt("contract_id");
                    if (!rs.wasNull() && contractIdVal > 0) {
                        snapshot.contractId = contractIdVal;
                    }
                    snapshot.roomId = rs.getInt("room_id");
                    snapshot.facilityId = rs.getInt("facility_id");
                    snapshot.roomFee = rs.getBigDecimal("room_fee") != null ? rs.getBigDecimal("room_fee")
                            : BigDecimal.ZERO;
                    snapshot.electricityPrice = rs.getBigDecimal("electricity_price") != null
                            ? rs.getBigDecimal("electricity_price")
                            : BigDecimal.ZERO;
                    snapshot.waterPrice = rs.getBigDecimal("water_price") != null ? rs.getBigDecimal("water_price")
                            : BigDecimal.ZERO;
                    snapshot.internetFee = rs.getBigDecimal("internet_fee") != null ? rs.getBigDecimal("internet_fee")
                            : BigDecimal.ZERO;
                    snapshot.serviceFee = rs.getBigDecimal("service_fee") != null ? rs.getBigDecimal("service_fee")
                            : BigDecimal.ZERO;
                    return snapshot;
                }
            }
        }
        return null;
    }

    public List<RoomDTO> getAvailableRoomsForInvoice(int managerId, String billingPeriod) throws SQLException {
        List<RoomDTO> list = new ArrayList<>();
        int year = Integer.parseInt(billingPeriod.substring(0, 4));
        int month = Integer.parseInt(billingPeriod.substring(4, 6));

        String sql = "SELECT DISTINCT r.room_id, r.code, u.full_name AS tenant_name " +
                "FROM rooms r " +
                "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                "INNER JOIN contracts c ON r.room_id = c.room_id AND c.status = 'ACTIVE' AND c.deleted_at IS NULL " +
                "LEFT JOIN users u ON c.tenant_id = u.user_id " +
                "WHERE f.manager_id = ? " +
                "  AND r.status = 'OCCUPIED' " +
                "  AND r.deleted_at IS NULL " +
                "  AND f.deleted_at IS NULL " +
                "  AND NOT EXISTS (SELECT 1 FROM invoices i WHERE i.room_id = r.room_id AND i.deleted_at IS NULL AND i.code LIKE '%-' + ?) "
                +
                "  AND EXISTS (SELECT 1 FROM meter_readings mr WHERE mr.room_id = r.room_id AND mr.deleted_at IS NULL AND YEAR(mr.reading_date) = ? AND MONTH(mr.reading_date) = ?) "
                +
                "ORDER BY r.code ASC";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, managerId);
            ps.setString(2, billingPeriod);
            ps.setInt(3, year);
            ps.setInt(4, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RoomDTO dto = new RoomDTO();
                    dto.setId(rs.getInt("room_id"));
                    dto.setCode(rs.getString("code"));
                    dto.setRoomNumber(rs.getString("code"));
                    dto.setTenantName(rs.getString("tenant_name"));
                    list.add(dto);
                }
            }
        }
        return list;
    }

    public boolean checkInvoiceCodeExists(String invoiceCode) throws SQLException {
        String sql = "SELECT invoice_id FROM invoices WHERE code = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, invoiceCode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public InvoicePriceSnapshot getInvoicePriceSnapshot(int invoiceId) throws SQLException {
        String sql = "SELECT i.room_fee, i.electricity_price, i.water_price, i.internet_fee, i.service_fee, i.due_date, mr.reading_date "
                +
                "FROM invoices i LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id WHERE i.invoice_id = ?";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InvoicePriceSnapshot snap = new InvoicePriceSnapshot();
                    snap.roomFee = rs.getBigDecimal("room_fee") != null ? rs.getBigDecimal("room_fee")
                            : BigDecimal.ZERO;
                    snap.electricityPrice = rs.getBigDecimal("electricity_price") != null
                            ? rs.getBigDecimal("electricity_price")
                            : BigDecimal.ZERO;
                    snap.waterPrice = rs.getBigDecimal("water_price") != null ? rs.getBigDecimal("water_price")
                            : BigDecimal.ZERO;
                    snap.internetFee = rs.getBigDecimal("internet_fee") != null ? rs.getBigDecimal("internet_fee")
                            : BigDecimal.ZERO;
                    snap.serviceFee = rs.getBigDecimal("service_fee") != null ? rs.getBigDecimal("service_fee")
                            : BigDecimal.ZERO;

                    Date existingDue = rs.getDate("due_date");
                    if (existingDue != null)
                        snap.dueDate = existingDue.toLocalDate();

                    Date rdDate = rs.getDate("reading_date");
                    if (rdDate != null)
                        snap.readingDate = rdDate.toLocalDate();

                    return snap;
                }
            }
        }
        return null;
    }


    // --- Methods from HEAD (Tenant / Room specific) ---

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getInt("invoice_id"));
        i.setCode(rs.getString("code"));
        i.setRoomId(getInteger(rs, "room_id"));
        i.setMeterId(getInteger(rs, "meter_id"));
        i.setDueDate(toLocalDate(rs, "due_date"));
        i.setStatus(rs.getString("status"));
        i.setOtherFee(rs.getBigDecimal("other_fee"));
        i.setRoomFee(rs.getBigDecimal("room_fee"));
        i.setElectricityPrice(rs.getBigDecimal("electricity_price"));
        i.setWaterPrice(rs.getBigDecimal("water_price"));
        i.setInternetFee(rs.getBigDecimal("internet_fee"));
        i.setServiceFee(rs.getBigDecimal("service_fee"));
        i.setTotalAmount(rs.getBigDecimal("total_amount"));
        i.setNote(rs.getString("note"));
        i.setCreatedBy(getInteger(rs, "created_by"));
        i.setCreatedAt(toLocalDateTime(rs, "created_at"));
        i.setUpdatedAt(toLocalDateTime(rs, "updated_at"));
        i.setDeletedAt(toLocalDateTime(rs, "deleted_at"));

        // Lấy phí chậm nộp từ DB nếu đã thanh toán, nếu chưa thì tính runtime
        BigDecimal lateFee = BigDecimal.ZERO;
        LocalDate dueDate = i.getDueDate();

        if ("PAID".equals(i.getStatus())) {
            if (hasColumn(rs, "late_fee")) {
                try {
                    BigDecimal dbLateFee = rs.getBigDecimal("late_fee");
                    if (dbLateFee != null)
                        lateFee = dbLateFee;
                } catch (SQLException ignore) {
                }
            }
        } else {
            if (dueDate != null && i.getRoomFee() != null) {
                LocalDate endDate = LocalDate.now();
                if (hasColumn(rs, "pending_payment_date")) {
                    try {
                        Date pendingDate = rs.getDate("pending_payment_date");
                        if (pendingDate != null) {
                            endDate = pendingDate.toLocalDate();
                        }
                    } catch (SQLException ignore) {
                    }
                }
                if (endDate.isAfter(dueDate)) {
                    long daysLate = ChronoUnit.DAYS.between(dueDate, endDate);
                    lateFee = i.getRoomFee()
                            .multiply(new BigDecimal("0.01"))
                            .multiply(new BigDecimal(daysLate))
                            .setScale(0, RoundingMode.HALF_UP);
                }
            }

            if (i.getTotalAmount() != null) {
                i.setTotalAmount(i.getTotalAmount().add(lateFee));
            }
        }
        i.setLateFee(lateFee);

        if (hasColumn(rs, "old_electric")) {
            i.setOldElectricReading(getInteger(rs, "old_electric"));
            i.setNewElectricReading(getInteger(rs, "new_electric"));
            i.setOldWaterReading(getInteger(rs, "old_water"));
            i.setNewWaterReading(getInteger(rs, "new_water"));

            if (hasColumn(rs, "electric_img")) {
                try {
                    i.setElectricImg(rs.getString("electric_img"));
                } catch (SQLException ignore) {
                }
            }
            if (hasColumn(rs, "water_img")) {
                try {
                    i.setWaterImg(rs.getString("water_img"));
                } catch (SQLException ignore) {
                }
            }

            if (i.getNewElectricReading() != null && i.getOldElectricReading() != null
                    && i.getElectricityPrice() != null) {
                int used = i.getNewElectricReading() - i.getOldElectricReading();
                i.setElectricAmount(i.getElectricityPrice().multiply(new BigDecimal(used)));
            } else {
                i.setElectricAmount(BigDecimal.ZERO);
            }
            if (i.getNewWaterReading() != null && i.getOldWaterReading() != null && i.getWaterPrice() != null) {
                int used = i.getNewWaterReading() - i.getOldWaterReading();
                i.setWaterAmount(i.getWaterPrice().multiply(new BigDecimal(used)));
            } else {
                i.setWaterAmount(BigDecimal.ZERO);
            }

            if (hasColumn(rs, "reading_date") && rs.getDate("reading_date") != null) {
                LocalDate rd = toLocalDate(rs, "reading_date");
                i.setBillingPeriod("Tháng " + String.format("%02d/%d", rd.getMonthValue(), rd.getYear()));
            } else if (i.getDueDate() != null) {
                i.setBillingPeriod(
                        "Tháng " + String.format("%02d/%d", i.getDueDate().getMonthValue(), i.getDueDate().getYear()));
            }
        } else {
            try {
                i.setBillingPeriod(rs.getString("billing_period"));
            } catch (SQLException ignore) {
            }
        }

        if (hasColumn(rs, "meter_status")) {
            try {
                i.setMeterReadingStatus(rs.getString("meter_status"));
            } catch (SQLException ignore) {
            }
        }

        return i;
    }

    public List<Invoice> findByRoomId(int roomId) {
        String sql = "SELECT i.*, " +
                "  mr.electric AS new_electric, mr.water AS new_water, mr.status AS meter_status, " +
                "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, "
                +
                "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, "
                +
                "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period, " +
                "  (SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                +
                "FROM invoices i " +
                "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                "WHERE i.room_id = ? AND i.deleted_at IS NULL " +
                "ORDER BY i.created_at DESC";
        List<Invoice> list = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByRoomId failed for roomId={}", roomId, e);
        }
        return list;
    }

    public Optional<Invoice> findByIdAndRoomId(int id, int roomId) {
        String sql = "SELECT i.*, " +
                "  mr.electric AS new_electric, mr.water AS new_water, mr.status AS meter_status, mr.electric_img, mr.water_img, "
                +
                "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, "
                +
                "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, "
                +
                "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period, " +
                "  (SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                +
                "FROM invoices i " +
                "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                "WHERE i.invoice_id = ? AND i.room_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("findByIdAndRoomId failed for id={}, roomId={}", id, roomId, e);
        }
        return Optional.empty();
    }

    public BigDecimal getUnpaidTotalByRoomId(int roomId) {
        // Lấy tổng base amount + tính lateFee theo từng hóa đơn chưa thanh toán
        String sql = "SELECT total_amount, room_fee, due_date, " +
                "(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = invoices.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                +
                "FROM invoices " +
                "WHERE room_id = ? AND status != 'PAID' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                BigDecimal total = BigDecimal.ZERO;
                LocalDate today = LocalDate.now();
                while (rs.next()) {
                    BigDecimal baseAmount = rs.getBigDecimal("total_amount");
                    if (baseAmount == null)
                        continue;
                    total = total.add(baseAmount);
                    // Cộng thêm lateFee nếu quá hạn
                    Date dueDateSql = rs.getDate("due_date");
                    BigDecimal roomFee = rs.getBigDecimal("room_fee");
                    if (dueDateSql != null && roomFee != null) {
                        LocalDate dueDate = dueDateSql.toLocalDate();
                        LocalDate endDate = today;
                        Date pendingDate = rs.getDate("pending_payment_date");
                        if (pendingDate != null) {
                            endDate = pendingDate.toLocalDate();
                        }

                        if (endDate.isAfter(dueDate)) {
                            long daysLate = ChronoUnit.DAYS.between(dueDate, endDate);
                            BigDecimal lateFee = roomFee
                                    .multiply(new BigDecimal("0.01"))
                                    .multiply(new BigDecimal(daysLate))
                                    .setScale(0, RoundingMode.HALF_UP);
                            total = total.add(lateFee);
                        }
                    }
                }
                return total;
            }
        } catch (Exception e) {
            logger.error("getUnpaidTotalByRoomId failed for roomId={}", roomId, e);
        }
        return BigDecimal.ZERO;
    }

    public Optional<Invoice> getCurrentInvoiceByRoomId(int roomId) {
        String sql = "SELECT TOP 1 i.*, " +
                "  mr.electric AS new_electric, mr.water AS new_water, " +
                "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, "
                +
                "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, "
                +
                "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period, " +
                "  (SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                +
                "FROM invoices i " +
                "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                "WHERE i.room_id = ? AND i.deleted_at IS NULL " +
                "ORDER BY i.created_at DESC";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }
        } catch (Exception e) {
            logger.error("getCurrentInvoiceByRoomId failed for roomId={}", roomId, e);
        }
        return Optional.empty();
    }

    public boolean updateStatus(int invoiceId, String status) {
        if ("PAID".equals(status)) {
            try (Connection conn = DatabaseUtil.getConnection()) {
                markInvoiceAsPaid(conn, invoiceId);
                return true;
            } catch (Exception e) {
                logger.error("updateStatus to PAID failed for invoiceId={}", invoiceId, e);
                return false;
            }
        }

        String sql = "UPDATE dbo.invoices SET status = ?, updated_at = GETDATE() WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, invoiceId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            logger.error("updateStatus failed for invoiceId={}", invoiceId, e);
        }
        return false;
    }

    public void markInvoiceAsPaid(Connection conn, int invoiceId) throws SQLException {
        markInvoiceAsPaid(conn, invoiceId, LocalDate.now());
    }

    public void markInvoiceAsPaid(Connection conn, int invoiceId, LocalDate paymentDate) throws SQLException {
        String fetchSql = "SELECT room_fee, due_date, total_amount FROM invoices WHERE invoice_id = ? AND status != 'PAID'";
        BigDecimal roomFee = BigDecimal.ZERO;
        java.sql.Date dueDate = null;
        BigDecimal totalAmount = BigDecimal.ZERO;
        boolean found = false;

        try (PreparedStatement psFetch = conn.prepareStatement(fetchSql)) {
            psFetch.setInt(1, invoiceId);
            try (ResultSet rs = psFetch.executeQuery()) {
                if (rs.next()) {
                    roomFee = rs.getBigDecimal("room_fee");
                    dueDate = rs.getDate("due_date");
                    totalAmount = rs.getBigDecimal("total_amount");
                    found = true;
                }
            }
        }

        if (found) {
            BigDecimal lateFee = BigDecimal.ZERO;
            if (dueDate != null && roomFee != null) {
                LocalDate dueLocalDate = dueDate.toLocalDate();
                if (paymentDate.isAfter(dueLocalDate)) {
                    long daysLate = ChronoUnit.DAYS.between(dueLocalDate, paymentDate);
                    lateFee = roomFee.multiply(new BigDecimal("0.01"))
                            .multiply(new BigDecimal(daysLate))
                            .setScale(0, RoundingMode.HALF_UP);
                }
            }
            if (totalAmount == null)
                totalAmount = BigDecimal.ZERO;
            BigDecimal finalTotal = totalAmount.add(lateFee);

            String updateSql = "UPDATE invoices SET status = 'PAID', late_fee = ?, total_amount = ?, updated_at = GETDATE() WHERE invoice_id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                psUpdate.setBigDecimal(1, lateFee);
                psUpdate.setBigDecimal(2, finalTotal);
                psUpdate.setInt(3, invoiceId);
                psUpdate.executeUpdate();
            }
        }
    }

    public boolean verifyInvoiceOwnership(int invoiceId, int tenantId) {
        String sql = "SELECT 1 FROM dbo.invoices i " +
                "JOIN dbo.rooms r ON i.room_id = r.room_id " +
                "WHERE i.invoice_id = ? AND r.tenant_id = ? AND i.deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, tenantId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            logger.error("verifyInvoiceOwnership failed for invoiceId={}, tenantId={}", invoiceId, tenantId, e);
        }
        return false;
    }

    public BigDecimal calculateRealtimeLatePenalty(int invoiceId) {
        String sql = "SELECT room_fee, due_date, " +
                "(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = invoices.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                +
                "FROM dbo.invoices WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal roomFee = rs.getBigDecimal("room_fee");
                    LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                    LocalDate endDate = LocalDate.now();
                    Date pendingDate = rs.getDate("pending_payment_date");
                    if (pendingDate != null) {
                        endDate = pendingDate.toLocalDate();
                    }
                    if (dueDate != null && roomFee != null && endDate.isAfter(dueDate)) {
                        long daysLate = ChronoUnit.DAYS.between(dueDate, endDate);
                        BigDecimal penaltyRate = new BigDecimal("0.01").multiply(new BigDecimal(daysLate));
                        return roomFee.multiply(penaltyRate).setScale(0, RoundingMode.HALF_UP);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("calculateRealtimeLatePenalty failed for invoiceId={}", invoiceId, e);
        }
        return BigDecimal.ZERO;
    }

    public List<InvoiceListItemDTO> findInvoices(int managerId, String keyword, String status, String billingPeriod,
            int offset, int limit) {
        List<InvoiceListItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT i.invoice_id, i.code, i.total_amount, i.room_fee, i.due_date, i.status, r.code AS room_code, COALESCE(u.full_name, c.tenant_full_name) AS tenant_name, "
                        +
                        "FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period, "
                        +
                        "(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                        +
                        "FROM invoices i " +
                        "INNER JOIN rooms r ON i.room_id = r.room_id " +
                        "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                        "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                        "LEFT JOIN payments pay ON i.invoice_id = pay.invoice_id AND pay.deleted_at IS NULL " +
                        "LEFT JOIN contracts c ON c.contract_id = i.contract_id " +
                        "LEFT JOIN users u ON u.user_id = i.tenant_id " +
                        "WHERE i.deleted_at IS NULL AND f.manager_id = ? ");

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR COALESCE(u.full_name, c.tenant_full_name) LIKE ?) ");
        }
        if (billingPeriod != null && !billingPeriod.trim().isEmpty() && billingPeriod.length() == 6) {
            sql.append("AND (YEAR(mr.reading_date) = ? AND MONTH(mr.reading_date) = ? OR i.code LIKE ?) ");
        }

        sql.append("ORDER BY i.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, managerId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }
            if (billingPeriod != null && !billingPeriod.trim().isEmpty() && billingPeriod.length() == 6) {
                try {
                    int year = Integer.parseInt(billingPeriod.substring(0, 4));
                    int month = Integer.parseInt(billingPeriod.substring(4, 6));
                    ps.setInt(paramIndex++, year);
                    ps.setInt(paramIndex++, month);
                    ps.setString(paramIndex++, "%-" + billingPeriod);
                } catch (NumberFormatException ignored) {
                }
            }
            ps.setInt(paramIndex++, offset);
            ps.setInt(paramIndex++, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InvoiceListItemDTO dto = new InvoiceListItemDTO();
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    dto.setInvoiceCode(rs.getString("code"));
                    BigDecimal rawBaseTotal = rs.getBigDecimal("total_amount");
                    BigDecimal baseTotal = rawBaseTotal;
                    BigDecimal lateFee = BigDecimal.ZERO;
                    String invoiceStatus = rs.getString("status");
                    Date d = rs.getDate("due_date");
                    BigDecimal roomFee = rs.getBigDecimal("room_fee");

                    if (!"PAID".equals(invoiceStatus) && d != null && roomFee != null) {
                        LocalDate dueLocalDate = d.toLocalDate();
                        LocalDate endDate = LocalDate.now();
                        Date pendingDate = rs.getDate("pending_payment_date");
                        if (pendingDate != null) {
                            endDate = pendingDate.toLocalDate();
                        }
                        if (endDate.isAfter(dueLocalDate)) {
                            long daysLate = ChronoUnit.DAYS.between(dueLocalDate, endDate);
                            lateFee = roomFee.multiply(new BigDecimal("0.01"))
                                    .multiply(new BigDecimal(daysLate))
                                    .setScale(0, RoundingMode.HALF_UP);
                            if (baseTotal != null)
                                baseTotal = baseTotal.add(lateFee);
                            if ("UNPAID".equals(invoiceStatus)) {
                                invoiceStatus = "OVERDUE";
                            }
                        }
                    }

                    dto.setBaseAmount(rawBaseTotal);
                    dto.setLateFee(lateFee);
                    dto.setTotalAmount(baseTotal);
                    if (d != null)
                        dto.setDueDate(new SimpleDateFormat("dd/MM/yyyy").format(d));
                    dto.setStatus(invoiceStatus);
                    dto.setRoomCode(rs.getString("room_code"));
                    dto.setTenantName(rs.getString("tenant_name"));

                    String bp = rs.getString("billing_period");
                    if (bp != null && !bp.trim().isEmpty()) {
                        dto.setBillingPeriod("Tháng " + bp);
                    } else if (dto.getInvoiceCode() != null && dto.getInvoiceCode().contains("-")) {
                        String[] parts = dto.getInvoiceCode().split("-");
                        if (parts.length >= 3 && parts[parts.length - 1].length() == 6) {
                            String period = parts[parts.length - 1];
                            dto.setBillingPeriod("Tháng " + period.substring(4, 6) + "/" + period.substring(0, 4));
                        } else if (d != null) {
                            dto.setBillingPeriod("Tháng " + new SimpleDateFormat("MM/yyyy").format(d));
                        } else {
                            dto.setBillingPeriod("—");
                        }
                    } else if (d != null) {
                        dto.setBillingPeriod("Tháng " + new SimpleDateFormat("MM/yyyy").format(d));
                    } else {
                        dto.setBillingPeriod("—");
                    }

                    list.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countInvoices(int managerId, String keyword, String status, String billingPeriod) {
        int count = 0;
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(1) " +
                        "FROM invoices i " +
                        "INNER JOIN rooms r ON i.room_id = r.room_id " +
                        "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                        "LEFT JOIN meter_readings mr ON i.meter_id = mr.meter_id " +
                        "LEFT JOIN payments pay ON i.invoice_id = pay.invoice_id AND pay.deleted_at IS NULL " +
                        "LEFT JOIN contracts c ON c.contract_id = i.contract_id " +
                        "LEFT JOIN users u ON u.user_id = i.tenant_id " +
                        "WHERE i.deleted_at IS NULL AND f.manager_id = ? ");

        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR COALESCE(u.full_name, c.tenant_full_name) LIKE ?) ");
        }
        if (billingPeriod != null && !billingPeriod.trim().isEmpty() && billingPeriod.length() == 6) {
            sql.append("AND (YEAR(mr.reading_date) = ? AND MONTH(mr.reading_date) = ? OR i.code LIKE ?) ");
        }

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            ps.setInt(paramIndex++, managerId);

            if (status != null && !status.trim().isEmpty()) {
                ps.setString(paramIndex++, status);
            }
            if (keyword != null && !keyword.trim().isEmpty()) {
                String kw = "%" + keyword + "%";
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
                ps.setString(paramIndex++, kw);
            }
            if (billingPeriod != null && !billingPeriod.trim().isEmpty() && billingPeriod.length() == 6) {
                try {
                    int year = Integer.parseInt(billingPeriod.substring(0, 4));
                    int month = Integer.parseInt(billingPeriod.substring(4, 6));
                    ps.setInt(paramIndex++, year);
                    ps.setInt(paramIndex++, month);
                    ps.setString(paramIndex++, "%-" + billingPeriod);
                } catch (NumberFormatException ignored) {
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    public InvoiceDetailDTO findById(int managerId, int invoiceId) {
        String sql = "SELECT i.*, r.code AS room_code, " +
                "COALESCE(u.full_name, c.tenant_full_name) AS tenant_name, " +
                "COALESCE(u.phone, c.tenant_phone) AS tenant_phone, " +
                "u.email AS tenant_email, " +
                "f.name AS facility_name, f.address AS facility_address, " +
                "c.start_date AS contract_start_date, c.end_date AS contract_end_date, c.code AS contract_code, " +
                "mr_curr.electric AS new_electric, mr_curr.water AS new_water, mr_curr.electric_img, mr_curr.water_img, "
                +
                "(SELECT TOP 1 electric FROM meter_readings mr_old WHERE mr_old.room_id = i.room_id AND mr_old.reading_date < mr_curr.reading_date ORDER BY mr_old.reading_date DESC) AS old_electric, "
                +
                "(SELECT TOP 1 water FROM meter_readings mr_old WHERE mr_old.room_id = i.room_id AND mr_old.reading_date < mr_curr.reading_date ORDER BY mr_old.reading_date DESC) AS old_water, "
                +
                "(SELECT full_name FROM users WHERE user_id = i.created_by) AS creator_name, " +
                "FORMAT(mr_curr.reading_date, 'MM/yyyy') AS billing_period, " +
                "(SELECT TOP 1 created_at FROM payments p WHERE p.invoice_id = i.invoice_id AND p.status = 'PENDING' AND p.deleted_at IS NULL ORDER BY p.created_at DESC) AS pending_payment_date "
                +
                "FROM invoices i " +
                "INNER JOIN rooms r ON i.room_id = r.room_id " +
                "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                "LEFT JOIN payments pay ON i.invoice_id = pay.invoice_id AND pay.deleted_at IS NULL " +
                "LEFT JOIN contracts c ON c.contract_id = i.contract_id " +
                "LEFT JOIN users u ON u.user_id = i.tenant_id " +
                "LEFT JOIN meter_readings mr_curr ON i.meter_id = mr_curr.meter_id " +
                "WHERE i.invoice_id = ? AND i.deleted_at IS NULL AND f.manager_id = ?";

        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setInt(2, managerId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    InvoiceDetailDTO dto = new InvoiceDetailDTO();
                    dto.setInvoiceId(rs.getInt("invoice_id"));
                    dto.setInvoiceCode(rs.getString("code"));
                    dto.setStatus(rs.getString("status"));

                    Date d = rs.getDate("due_date");
                    if (d != null)
                        dto.setDueDate(new SimpleDateFormat("dd/MM/yyyy").format(d));

                    Timestamp created = rs.getTimestamp("created_at");
                    if (created != null)
                        dto.setCreatedAt(created.toString());

                    dto.setRoomId(rs.getInt("room_id"));
                    dto.setRoomCode(rs.getString("room_code"));
                    dto.setTenantName(rs.getString("tenant_name"));
                    dto.setTenantPhone(rs.getString("tenant_phone"));
                    dto.setTenantEmail(rs.getString("tenant_email"));
                    dto.setFacilityName(rs.getString("facility_name"));
                    dto.setFacilityAddress(rs.getString("facility_address"));

                    Date cStart = rs.getDate("contract_start_date");
                    Date cEnd = rs.getDate("contract_end_date");
                    if (cStart != null && cEnd != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        dto.setContractPeriod(sdf.format(cStart) + " - " + sdf.format(cEnd));
                    } else {
                        dto.setContractPeriod("Chưa có hợp đồng");
                    }
                    dto.setContractCode(rs.getString("contract_code"));

                    dto.setRoomFee(rs.getBigDecimal("room_fee"));
                    dto.setElectricImg(rs.getString("electric_img"));
                    dto.setWaterImg(rs.getString("water_img"));
                    dto.setMeterId(rs.getObject("meter_id") != null ? rs.getInt("meter_id") : null);

                    int ne = rs.getObject("new_electric") != null ? rs.getInt("new_electric") : 0;
                    int oe = rs.getObject("old_electric") != null ? rs.getInt("old_electric") : 0;
                    int nw = rs.getObject("new_water") != null ? rs.getInt("new_water") : 0;
                    int ow = rs.getObject("old_water") != null ? rs.getInt("old_water") : 0;

                    dto.setNewElectricReading(ne);
                    dto.setOldElectricReading(oe);
                    dto.setNewWaterReading(nw);
                    dto.setOldWaterReading(ow);

                    dto.setElectricUsage(Math.max(0, ne - oe));
                    dto.setWaterUsage(Math.max(0, nw - ow));

                    dto.setElectricUnitPrice(rs.getBigDecimal("electricity_price"));
                    dto.setWaterUnitPrice(rs.getBigDecimal("water_price"));

                    if (dto.getElectricUnitPrice() != null) {
                        dto.setElectricAmount(
                                dto.getElectricUnitPrice().multiply(new BigDecimal(dto.getElectricUsage())));
                    } else {
                        dto.setElectricAmount(BigDecimal.ZERO);
                    }

                    if (dto.getWaterUnitPrice() != null) {
                        dto.setWaterAmount(dto.getWaterUnitPrice().multiply(new BigDecimal(dto.getWaterUsage())));
                    } else {
                        dto.setWaterAmount(BigDecimal.ZERO);
                    }

                    dto.setInternetFee(rs.getBigDecimal("internet_fee"));
                    dto.setServiceFee(rs.getBigDecimal("service_fee"));
                    dto.setOtherFee(rs.getBigDecimal("other_fee"));

                    // Tính phí chậm nộp runtime hoặc lấy từ DB
                    BigDecimal lateFee = BigDecimal.ZERO;
                    if ("PAID".equals(dto.getStatus())) {
                        try {
                            BigDecimal dbLateFee = rs.getBigDecimal("late_fee");
                            if (dbLateFee != null)
                                lateFee = dbLateFee;
                        } catch (SQLException ignore) {
                        }
                    } else {
                        Date dueDateSql = rs.getDate("due_date");
                        if (dueDateSql != null && dto.getRoomFee() != null) {
                            LocalDate dueLocalDate = dueDateSql.toLocalDate();
                            LocalDate endDate = LocalDate.now();
                            if (hasColumn(rs, "pending_payment_date")) {
                                try {
                                    Timestamp pendingTimestamp = rs.getTimestamp("pending_payment_date");
                                    if (pendingTimestamp != null)
                                        endDate = pendingTimestamp.toLocalDateTime().toLocalDate();
                                } catch (Exception ignore) {
                                }
                            }
                            if (endDate.isAfter(dueLocalDate)) {
                                long daysLate = ChronoUnit.DAYS.between(dueLocalDate, endDate);
                                lateFee = dto.getRoomFee()
                                        .multiply(new BigDecimal("0.01"))
                                        .multiply(new BigDecimal(daysLate))
                                        .setScale(0, RoundingMode.HALF_UP);
                                if ("UNPAID".equals(dto.getStatus())) {
                                    dto.setStatus("OVERDUE");
                                }
                            }
                        }
                    }
                    dto.setLateFee(lateFee);
                    // otherFee giữ nguyên giá trị từ DB, không cộng lateFee vào

                    BigDecimal subtotal = BigDecimal.ZERO;
                    if (dto.getRoomFee() != null)
                        subtotal = subtotal.add(dto.getRoomFee());
                    if (dto.getElectricAmount() != null)
                        subtotal = subtotal.add(dto.getElectricAmount());
                    if (dto.getWaterAmount() != null)
                        subtotal = subtotal.add(dto.getWaterAmount());
                    if (dto.getServiceFee() != null)
                        subtotal = subtotal.add(dto.getServiceFee());
                    if (dto.getInternetFee() != null)
                        subtotal = subtotal.add(dto.getInternetFee());
                    if (dto.getOtherFee() != null)
                        subtotal = subtotal.add(dto.getOtherFee());
                    subtotal = subtotal.add(lateFee); // cộng phí chậm nộp vào tạm tính
                    dto.setSubtotal(subtotal);

                    // Tổng tiền = subtotal (đã gồm lateFee)
                    dto.setTotalAmount(subtotal);
                    dto.setNote(rs.getString("note"));

                    dto.setCreatedByName(rs.getString("creator_name"));

                    String bp = rs.getString("billing_period");
                    if (bp != null) {
                        dto.setBillingPeriod(bp);
                    } else if (dto.getInvoiceCode() != null) {
                        String[] partsCode = dto.getInvoiceCode().split("-");
                        if (partsCode.length >= 3) {
                            String period = partsCode[partsCode.length - 1];
                            if (period.length() == 6) {
                                dto.setBillingPeriod(period.substring(4, 6) + "/" + period.substring(0, 4));
                            }
                        }
                    }

                    try {
                        Timestamp updated = rs.getTimestamp("updated_at");
                        if (updated != null) {
                            dto.setUpdatedAt(
                                    updated.toLocalDateTime()
                                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                        }
                    } catch (Exception ignore) {
                    }
                    dto.setUpdatedByName("");

                    return dto;
                }
            }
        } catch (Exception e) {
            logger.error("findById failed for invoiceId={}, managerId={}", invoiceId, managerId, e);
        }
        return null;
    }

    public void insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (code, room_id, meter_id, due_date, status, other_fee, " +
                "room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by, contract_id, tenant_id) "
                +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice.getCode());
            ps.setInt(2, invoice.getRoomId());
            ps.setInt(3, invoice.getMeterId());
            ps.setDate(4, Date.valueOf(invoice.getDueDate()));
            ps.setString(5, invoice.getStatus());
            ps.setBigDecimal(6, invoice.getOtherFee());
            ps.setBigDecimal(7, invoice.getRoomFee());
            ps.setBigDecimal(8, invoice.getElectricityPrice());
            ps.setBigDecimal(9, invoice.getWaterPrice());
            ps.setBigDecimal(10, invoice.getInternetFee());
            ps.setBigDecimal(11, invoice.getServiceFee());
            ps.setBigDecimal(12, invoice.getTotalAmount());
            ps.setString(13, invoice.getNote());
            ps.setInt(14, invoice.getCreatedBy());
            if (invoice.getContractId() != null) {
                ps.setInt(15, invoice.getContractId());
            } else {
                ps.setNull(15, java.sql.Types.INTEGER);
            }
            if (invoice.getTenantId() != null) {
                ps.setInt(16, invoice.getTenantId());
            } else {
                ps.setNull(16, java.sql.Types.INTEGER);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    invoice.setInvoiceId(rs.getInt(1));
                }
            }
        }
    }


    public void update(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET due_date = ?, other_fee = ?, total_amount = ?, note = ?, updated_at = GETDATE() "
                +
                "WHERE invoice_id = ? AND deleted_at IS NULL";
        String updateMeterSql = "UPDATE dbo.meter_readings SET status = 'UPDATED', updated_at = GETDATE() "
                +
                "WHERE meter_id = (SELECT meter_id FROM dbo.invoices WHERE invoice_id = ?) AND status = 'REPORTED'";
        try (Connection conn = DatabaseUtil.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(invoice.getDueDate()));
            ps.setBigDecimal(2, invoice.getOtherFee());
            ps.setBigDecimal(3, invoice.getTotalAmount());
            ps.setString(4, invoice.getNote());
            ps.setInt(5, invoice.getInvoiceId());
            ps.executeUpdate();

            try (PreparedStatement psMeter = conn.prepareStatement(updateMeterSql)) {
                psMeter.setInt(1, invoice.getInvoiceId());
                psMeter.executeUpdate();
            } catch (Exception e) {
                logger.error("Failed to update meter reading status to UPDATED for invoiceId=" + invoice.getInvoiceId(),
                        e);
            }
        }
    }

}
