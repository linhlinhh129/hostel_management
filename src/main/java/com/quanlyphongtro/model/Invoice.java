package com.quanlyphongtro.model;

import com.quanlyphongtro.constant.StatusConstant;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Invoice {
    private Integer id;
    private String code;
    private Integer roomId;
    private Integer meterId;
    private LocalDate dueDate;
    private String status;
    private BigDecimal tax;
    private BigDecimal otherFee;
    private BigDecimal roomFee;
    private BigDecimal electricityPrice;
    private BigDecimal waterPrice;
    private BigDecimal internetFee;
    private BigDecimal serviceFee;
    private BigDecimal totalAmount;
    private String note;
    private Integer createdBy;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    // Transient fields for View
    private Integer oldElectricReading;
    private Integer newElectricReading;
    private Integer oldWaterReading;
    private Integer newWaterReading;
    private BigDecimal electricAmount;
    private BigDecimal waterAmount;
    private BigDecimal lateFee;          // phí chậm nộp tính runtime, không lưu DB
    private String billingPeriod; // Example: "Tháng 05/2026"
    // Transient from JOIN
    private String roomCodeCache;
    private boolean hasPendingPayment;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public Invoice() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }


    public Integer getInvoiceId() { return id; }
    public void setInvoiceId(Integer invoiceId) { this.id = invoiceId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public Integer getMeterId() { return meterId; }
    public void setMeterId(Integer meterId) { this.meterId = meterId; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTax() { return tax; }
    public void setTax(BigDecimal tax) { this.tax = tax; }

    public BigDecimal getOtherFee() { return otherFee; }
    public void setOtherFee(BigDecimal otherFee) { this.otherFee = otherFee; }

    public BigDecimal getRoomFee() { return roomFee; }
    public void setRoomFee(BigDecimal roomFee) { this.roomFee = roomFee; }

    public BigDecimal getElectricityPrice() { return electricityPrice; }
    public void setElectricityPrice(BigDecimal electricityPrice) { this.electricityPrice = electricityPrice; }

    public BigDecimal getWaterPrice() { return waterPrice; }
    public void setWaterPrice(BigDecimal waterPrice) { this.waterPrice = waterPrice; }

    public BigDecimal getInternetFee() { return internetFee; }
    public void setInternetFee(BigDecimal internetFee) { this.internetFee = internetFee; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public boolean isDeleted() { return deletedAt != null; }

    // Backward compatibility for JSP fmt:formatDate
    public java.util.Date getCreatedAtAsDate() {
        if (createdAt == null) return null;
        return java.sql.Timestamp.valueOf(createdAt);
    }

    public java.util.Date getUpdatedAtAsDate() {
        if (updatedAt == null) return null;
        return java.sql.Timestamp.valueOf(updatedAt);
    }

    public Integer getOldElectricReading() { return oldElectricReading; }
    public void setOldElectricReading(Integer oldElectricReading) { this.oldElectricReading = oldElectricReading; }

    public Integer getNewElectricReading() { return newElectricReading; }
    public void setNewElectricReading(Integer newElectricReading) { this.newElectricReading = newElectricReading; }

    public Integer getOldWaterReading() { return oldWaterReading; }
    public void setOldWaterReading(Integer oldWaterReading) { this.oldWaterReading = oldWaterReading; }

    public Integer getNewWaterReading() { return newWaterReading; }
    public void setNewWaterReading(Integer newWaterReading) { this.newWaterReading = newWaterReading; }

    public BigDecimal getElectricAmount() { return electricAmount; }
    public void setElectricAmount(BigDecimal electricAmount) { this.electricAmount = electricAmount; }

    public BigDecimal getWaterAmount() { return waterAmount; }
    public void setWaterAmount(BigDecimal waterAmount) { this.waterAmount = waterAmount; }

    public BigDecimal getLateFee() { return lateFee; }
    public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public String getRoomCodeCache() { return roomCodeCache; }
    public void setRoomCodeCache(String roomCodeCache) { this.roomCodeCache = roomCodeCache; }

    public boolean isHasPendingPayment() { return hasPendingPayment; }
    public void setHasPendingPayment(boolean hasPendingPayment) { this.hasPendingPayment = hasPendingPayment; }

    // Helpers for View
    public String getDueDateLabel() {
        return dueDate != null ? dueDate.format(DATE_FORMATTER) : "N/A";
    }

    public String getPeriodLabel() {
        return billingPeriod != null ? billingPeriod : "N/A";
    }

    public boolean isOverdue() {
        return StatusConstant.INVOICE_OVERDUE.equals(this.status);
    }

    public String getStatusBadgeClass() {
        if (hasPendingPayment) return "badge-info";
        if (StatusConstant.INVOICE_PAID.equals(status)) return "badge-success";
        if (StatusConstant.INVOICE_OVERDUE.equals(status)) return "badge-danger";
        return "badge-warning";
    }

    public String getStatusLabel() {
        if (hasPendingPayment) return "Chờ duyệt";
        if (StatusConstant.INVOICE_PAID.equals(status)) return "Đã thanh toán";
        if (StatusConstant.INVOICE_OVERDUE.equals(status)) return "Quá hạn";
        return "Chưa thanh toán";
    }

}
