package com.quanlyphongtro.dao;

import com.quanlyphongtro.constant.StatusConstant;
import com.quanlyphongtro.dto.InvoiceListItemDTO;
import com.quanlyphongtro.dto.InvoiceDetailDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.util.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InvoiceDAO extends BaseDAO {

    // --- Methods from HEAD (Tenant / Room specific) ---

    private Invoice mapRow(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getInt("invoice_id"));
        i.setCode(rs.getString("code"));
        i.setRoomId(getInteger(rs, "room_id"));
        i.setMeterId(getInteger(rs, "meter_id"));
        i.setDueDate(toLocalDate(rs, "due_date"));
        i.setStatus(rs.getString("status"));
        i.setTax(rs.getBigDecimal("tax"));
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

        if (hasColumn(rs, "old_electric")) {
            i.setOldElectricReading(getInteger(rs, "old_electric"));
            i.setNewElectricReading(getInteger(rs, "new_electric"));
            i.setOldWaterReading(getInteger(rs, "old_water"));
            i.setNewWaterReading(getInteger(rs, "new_water"));
            
            if (i.getNewElectricReading() != null && i.getOldElectricReading() != null && i.getElectricityPrice() != null) {
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
                i.setBillingPeriod("Tháng " + String.format("%02d/%d", i.getDueDate().getMonthValue(), i.getDueDate().getYear()));
            }
        } else {
            try {
                i.setBillingPeriod(rs.getString("billing_period"));
            } catch (SQLException ignore) {}
        }
        
        return i;
    }

    public List<Invoice> findByRoomId(int roomId) {
        String sql = "SELECT i.*, " +
                     "  mr.electric AS new_electric, mr.water AS new_water, " +
                     "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, " +
                     "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, " +
                     "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period " +
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
                     "  mr.electric AS new_electric, mr.water AS new_water, " +
                     "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, " +
                     "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, " +
                     "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period " +
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
        String sql = "SELECT SUM(total_amount) FROM invoices WHERE room_id = ? AND status != 'PAID' AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal(1);
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (Exception e) {
            logger.error("getUnpaidTotalByRoomId failed for roomId={}", roomId, e);
        }
        return BigDecimal.ZERO;
    }

    public Optional<Invoice> getCurrentInvoiceByRoomId(int roomId) {
        String sql = "SELECT TOP 1 i.*, " +
                     "  mr.electric AS new_electric, mr.water AS new_water, " +
                     "  COALESCE((SELECT TOP 1 electric FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_electric, " +
                     "  COALESCE((SELECT TOP 1 water FROM meter_readings mr2 WHERE mr2.room_id = i.room_id AND mr2.reading_date < mr.reading_date ORDER BY mr2.reading_date DESC), 0) AS old_water, " +
                     "  FORMAT(mr.reading_date, 'MM/yyyy') AS billing_period " +
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
        String sql = "SELECT room_fee, due_date FROM dbo.invoices WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal roomFee = rs.getBigDecimal("room_fee");
                    LocalDate dueDate = rs.getDate("due_date").toLocalDate();
                    LocalDate today = LocalDate.now();
                    if (dueDate != null && roomFee != null && today.isAfter(dueDate)) {
                        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(dueDate, today);
                        BigDecimal penaltyRate = new BigDecimal("0.01").multiply(new BigDecimal(daysLate));
                        return roomFee.multiply(penaltyRate).setScale(0, java.math.RoundingMode.HALF_UP);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("calculateRealtimeLatePenalty failed for invoiceId={}", invoiceId, e);
        }
        return BigDecimal.ZERO;
    }

    public List<InvoiceListItemDTO> findInvoices(int managerId, String keyword, String status, String billingPeriod, int offset, int limit) {
        List<InvoiceListItemDTO> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
            "SELECT i.invoice_id, i.code, i.total_amount, i.due_date, i.status, r.code AS room_code, COALESCE(u.full_name, c.tenant_full_name, c_del.tenant_full_name) AS tenant_name " +
            "FROM invoices i " +
            "INNER JOIN rooms r ON i.room_id = r.room_id " +
            "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
            "LEFT JOIN payments pay ON i.invoice_id = pay.invoice_id AND pay.deleted_at IS NULL " +
            "LEFT JOIN contracts c ON i.room_id = c.room_id AND CAST(i.created_at AS DATE) BETWEEN c.start_date AND c.end_date AND c.deleted_at IS NULL " +
            "LEFT JOIN contracts c_del ON i.room_id = c_del.room_id AND CAST(i.created_at AS DATE) BETWEEN c_del.start_date AND c_del.end_date AND c_del.deleted_at IS NOT NULL " +
            "LEFT JOIN users u ON COALESCE(pay.created_by, c.tenant_id, c_del.tenant_id, r.tenant_id) = u.user_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR COALESCE(u.full_name, c.tenant_full_name, c_del.tenant_full_name) LIKE ?) ");
        }
        if (billingPeriod != null && !billingPeriod.trim().isEmpty()) {
            if (billingPeriod.length() == 6) {
                String year = billingPeriod.substring(0, 4);
                String month = billingPeriod.substring(4, 6);
                sql.append("AND YEAR(i.due_date) = ").append(year).append(" ");
                sql.append("AND MONTH(i.due_date) = ").append(month).append(" ");
            }
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
             if (billingPeriod != null && billingPeriod.length() == 6) {
                 ps.setString(paramIndex++, "%-" + billingPeriod);
             }
             ps.setInt(paramIndex++, offset);
             ps.setInt(paramIndex++, limit);
             
             try (ResultSet rs = ps.executeQuery()) {
                 while (rs.next()) {
                     InvoiceListItemDTO dto = new InvoiceListItemDTO();
                     dto.setInvoiceId(rs.getInt("invoice_id"));
                     dto.setInvoiceCode(rs.getString("code"));
                     dto.setTotalAmount(rs.getBigDecimal("total_amount"));
                     java.sql.Date d = rs.getDate("due_date");
                     if (d != null) dto.setDueDate(d.toString());
                     dto.setStatus(rs.getString("status"));
                     dto.setRoomCode(rs.getString("room_code"));
                     dto.setTenantName(rs.getString("tenant_name"));
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
            "LEFT JOIN payments pay ON i.invoice_id = pay.invoice_id AND pay.deleted_at IS NULL " +
            "LEFT JOIN contracts c ON i.room_id = c.room_id AND CAST(i.created_at AS DATE) BETWEEN c.start_date AND c.end_date AND c.deleted_at IS NULL " +
            "LEFT JOIN contracts c_del ON i.room_id = c_del.room_id AND CAST(i.created_at AS DATE) BETWEEN c_del.start_date AND c_del.end_date AND c_del.deleted_at IS NOT NULL " +
            "LEFT JOIN users u ON COALESCE(pay.created_by, c.tenant_id, c_del.tenant_id, r.tenant_id) = u.user_id " +
            "WHERE i.deleted_at IS NULL AND f.manager_id = ? "
        );
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append("AND i.status = ? ");
        }
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (i.code LIKE ? OR r.code LIKE ? OR COALESCE(u.full_name, c.tenant_full_name, c_del.tenant_full_name) LIKE ?) ");
        }
        if (billingPeriod != null && billingPeriod.length() == 6) {
            sql.append("AND i.code LIKE ? "); 
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
             if (billingPeriod != null && billingPeriod.length() == 6) {
                 ps.setString(paramIndex++, "%-" + billingPeriod);
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
                     "COALESCE(u.full_name, c.tenant_full_name, c_del.tenant_full_name) AS tenant_name, " +
                     "COALESCE(u.phone, c.tenant_phone, c_del.tenant_phone) AS tenant_phone, " +
                     "u.email AS tenant_email, " +
                     "f.name AS facility_name, f.address AS facility_address, " +
                     "mr_curr.electric AS new_electric, mr_curr.water AS new_water, mr_curr.electric_img, mr_curr.water_img, " +
                     "(SELECT TOP 1 electric FROM meter_readings mr_old WHERE mr_old.room_id = i.room_id AND mr_old.reading_date < mr_curr.reading_date ORDER BY mr_old.reading_date DESC) AS old_electric, " +
                     "(SELECT TOP 1 water FROM meter_readings mr_old WHERE mr_old.room_id = i.room_id AND mr_old.reading_date < mr_curr.reading_date ORDER BY mr_old.reading_date DESC) AS old_water, " +
                     "(SELECT full_name FROM users WHERE user_id = i.created_by) AS creator_name, " +
                     "FORMAT(mr_curr.reading_date, 'MM/yyyy') AS billing_period " +
                     "FROM invoices i " +
                     "INNER JOIN rooms r ON i.room_id = r.room_id " +
                     "INNER JOIN facilities f ON r.facility_id = f.facility_id " +
                     "LEFT JOIN payments pay ON i.invoice_id = pay.invoice_id AND pay.deleted_at IS NULL " +
                     "LEFT JOIN contracts c ON i.room_id = c.room_id AND CAST(i.created_at AS DATE) BETWEEN c.start_date AND c.end_date AND c.deleted_at IS NULL " +
                     "LEFT JOIN contracts c_del ON i.room_id = c_del.room_id AND CAST(i.created_at AS DATE) BETWEEN c_del.start_date AND c_del.end_date AND c_del.deleted_at IS NOT NULL " +
                     "LEFT JOIN users u ON COALESCE(pay.created_by, c.tenant_id, c_del.tenant_id, r.tenant_id) = u.user_id " +
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
                      
                      java.sql.Date d = rs.getDate("due_date");
                      if (d != null) dto.setDueDate(d.toString());
                      
                      java.sql.Timestamp created = rs.getTimestamp("created_at");
                      if (created != null) dto.setCreatedAt(created.toString());
                      
                      dto.setRoomCode(rs.getString("room_code"));
                      dto.setTenantName(rs.getString("tenant_name"));
                      dto.setTenantPhone(rs.getString("tenant_phone"));
                      dto.setTenantEmail(rs.getString("tenant_email"));
                      dto.setFacilityName(rs.getString("facility_name"));
                      dto.setFacilityAddress(rs.getString("facility_address"));
                      
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
                          dto.setElectricAmount(dto.getElectricUnitPrice().multiply(new java.math.BigDecimal(dto.getElectricUsage())));
                      } else {
                          dto.setElectricAmount(java.math.BigDecimal.ZERO);
                      }
                      
                      if (dto.getWaterUnitPrice() != null) {
                          dto.setWaterAmount(dto.getWaterUnitPrice().multiply(new java.math.BigDecimal(dto.getWaterUsage())));
                      } else {
                          dto.setWaterAmount(java.math.BigDecimal.ZERO);
                      }
                      
                      dto.setInternetFee(rs.getBigDecimal("internet_fee"));
                      dto.setServiceFee(rs.getBigDecimal("service_fee"));
                      dto.setOtherFee(rs.getBigDecimal("other_fee"));
                      
                      java.math.BigDecimal subtotal = java.math.BigDecimal.ZERO;
                      if (dto.getRoomFee() != null) subtotal = subtotal.add(dto.getRoomFee());
                      if (dto.getElectricAmount() != null) subtotal = subtotal.add(dto.getElectricAmount());
                      if (dto.getWaterAmount() != null) subtotal = subtotal.add(dto.getWaterAmount());
                      if (dto.getServiceFee() != null) subtotal = subtotal.add(dto.getServiceFee());
                      if (dto.getInternetFee() != null) subtotal = subtotal.add(dto.getInternetFee());
                      if (dto.getOtherFee() != null) subtotal = subtotal.add(dto.getOtherFee());
                      dto.setSubtotal(subtotal);
                      
                      dto.setTaxRate(rs.getBigDecimal("tax"));
                      if (dto.getTaxRate() != null) {
                          dto.setTaxAmount(subtotal.multiply(dto.getTaxRate()).divide(new java.math.BigDecimal("100"), 2, java.math.RoundingMode.HALF_UP));
                      } else {
                          dto.setTaxAmount(java.math.BigDecimal.ZERO);
                      }
                      
                      dto.setTotalAmount(rs.getBigDecimal("total_amount"));
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
                      
                      Timestamp updated = rs.getTimestamp("updated_at");
                      if (updated != null) dto.setUpdatedAt(
                          updated.toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                      dto.setUpdatedByName(""); 
                      
                      return dto;
                  }
              }
         } catch (SQLException e) {
             e.printStackTrace();
         }
         return null;
     }
    
    public void insert(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO invoices (code, room_id, meter_id, due_date, status, tax, other_fee, " +
                     "room_fee, electricity_price, water_price, internet_fee, service_fee, total_amount, note, created_by) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, invoice.getCode());
            ps.setInt(2, invoice.getRoomId());
            ps.setInt(3, invoice.getMeterId());
            ps.setDate(4, java.sql.Date.valueOf(invoice.getDueDate()));
            ps.setString(5, invoice.getStatus());
            ps.setBigDecimal(6, invoice.getTax());
            ps.setBigDecimal(7, invoice.getOtherFee());
            ps.setBigDecimal(8, invoice.getRoomFee());
            ps.setBigDecimal(9, invoice.getElectricityPrice());
            ps.setBigDecimal(10, invoice.getWaterPrice());
            ps.setBigDecimal(11, invoice.getInternetFee());
            ps.setBigDecimal(12, invoice.getServiceFee());
            ps.setBigDecimal(13, invoice.getTotalAmount());
            ps.setString(14, invoice.getNote());
            ps.setInt(15, invoice.getCreatedBy());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    invoice.setInvoiceId(rs.getInt(1));
                }
            }
        }
    }
    
    public void update(Invoice invoice) throws SQLException {
        String sql = "UPDATE invoices SET due_date = ?, tax = ?, other_fee = ?, total_amount = ?, note = ?, updated_at = GETDATE() " +
                     "WHERE invoice_id = ? AND deleted_at IS NULL";
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, java.sql.Date.valueOf(invoice.getDueDate()));
            ps.setBigDecimal(2, invoice.getTax());
            ps.setBigDecimal(3, invoice.getOtherFee());
            ps.setBigDecimal(4, invoice.getTotalAmount());
            ps.setString(5, invoice.getNote());
            ps.setInt(6, invoice.getInvoiceId());
            ps.executeUpdate();
        }
    }

}
