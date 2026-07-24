package com.quanlyphongtro.dto;
import java.util.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import java.math.BigDecimal;

public class InvoiceDetailDTO {
    private Integer invoiceId;
    private String invoiceCode;
    private String roomCode;
    private String tenantName;
    private String tenantPhone;
    private String tenantEmail;
    private String facilityName;
    private String facilityAddress;
    private String billingPeriod;
    private BigDecimal roomFee;
    
    private Integer meterId;
    private Integer oldElectricReading;
    private Integer newElectricReading;
    private Integer electricUsage;
    private BigDecimal electricUnitPrice;
    private BigDecimal electricAmount;
    
    private Integer oldWaterReading;
    private Integer newWaterReading;
    private Integer waterUsage;
    private BigDecimal waterUnitPrice;
    private BigDecimal waterAmount;
    
    private BigDecimal serviceFee;
    private BigDecimal internetFee;
    private BigDecimal otherFee;
    private BigDecimal lateFee;        // phí chậm nộp tính tại runtime (không lưu DB)
    
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;
    
    private String dueDate;
    private String status;
    private String note;
    
    private String createdAt;
    private String createdByName;
    private String updatedAt;
    private String updatedByName;
    
    private String electricImg;
    private String waterImg;

    // UI helpers
    private String statusBadgeClass;
    private String statusLabel;

    public InvoiceDetailDTO() {}

    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getTenantPhone() { return tenantPhone; }
    public void setTenantPhone(String tenantPhone) { this.tenantPhone = tenantPhone; }

    public String getTenantEmail() { return tenantEmail; }
    public void setTenantEmail(String tenantEmail) { this.tenantEmail = tenantEmail; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getFacilityAddress() { return facilityAddress; }
    public void setFacilityAddress(String facilityAddress) { this.facilityAddress = facilityAddress; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public BigDecimal getRoomFee() { return roomFee; }
    public void setRoomFee(BigDecimal roomFee) { this.roomFee = roomFee; }

    public Integer getMeterId() { return meterId; }
    public void setMeterId(Integer meterId) { this.meterId = meterId; }

    public Integer getOldElectricReading() { return oldElectricReading; }
    public void setOldElectricReading(Integer oldElectricReading) { this.oldElectricReading = oldElectricReading; }

    public Integer getNewElectricReading() { return newElectricReading; }
    public void setNewElectricReading(Integer newElectricReading) { this.newElectricReading = newElectricReading; }

    public Integer getElectricUsage() { return electricUsage; }
    public void setElectricUsage(Integer electricUsage) { this.electricUsage = electricUsage; }

    public BigDecimal getElectricUnitPrice() { return electricUnitPrice; }
    public void setElectricUnitPrice(BigDecimal electricUnitPrice) { this.electricUnitPrice = electricUnitPrice; }

    public BigDecimal getElectricAmount() { return electricAmount; }
    public void setElectricAmount(BigDecimal electricAmount) { this.electricAmount = electricAmount; }

    public Integer getOldWaterReading() { return oldWaterReading; }
    public void setOldWaterReading(Integer oldWaterReading) { this.oldWaterReading = oldWaterReading; }

    public Integer getNewWaterReading() { return newWaterReading; }
    public void setNewWaterReading(Integer newWaterReading) { this.newWaterReading = newWaterReading; }

    public Integer getWaterUsage() { return waterUsage; }
    public void setWaterUsage(Integer waterUsage) { this.waterUsage = waterUsage; }

    public BigDecimal getWaterUnitPrice() { return waterUnitPrice; }
    public void setWaterUnitPrice(BigDecimal waterUnitPrice) { this.waterUnitPrice = waterUnitPrice; }

    public BigDecimal getWaterAmount() { return waterAmount; }
    public void setWaterAmount(BigDecimal waterAmount) { this.waterAmount = waterAmount; }

    public BigDecimal getServiceFee() { return serviceFee; }
    public void setServiceFee(BigDecimal serviceFee) { this.serviceFee = serviceFee; }

    public BigDecimal getInternetFee() { return internetFee; }
    public void setInternetFee(BigDecimal internetFee) { this.internetFee = internetFee; }

    public BigDecimal getOtherFee() { return otherFee; }
    public void setOtherFee(BigDecimal otherFee) { this.otherFee = otherFee; }

    public BigDecimal getLateFee() { return lateFee; }
    public void setLateFee(BigDecimal lateFee) { this.lateFee = lateFee; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    
    public String getDueDateISO() {
        if (dueDate == null || dueDate.length() != 10) return "";
        // Format is dd/MM/yyyy -> yyyy-MM-dd
        return dueDate.substring(6, 10) + "-" + dueDate.substring(3, 5) + "-" + dueDate.substring(0, 2);
    }

    public String getStatus() { return status; }
    public void setStatus(String status) { 
        this.status = status;
        if ("PAID".equalsIgnoreCase(status)) {
            this.statusBadgeClass = "badge-hms badge-success";
            this.statusLabel = "Đã thanh toán";
        } else if ("OVERDUE".equalsIgnoreCase(status)) {
            this.statusBadgeClass = "badge-hms badge-danger";
            this.statusLabel = "Quá hạn";
        } else {
            this.statusBadgeClass = "badge-hms badge-warning";
            this.statusLabel = "Chưa thanh toán";
        }
    }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }

    public String getUpdatedByName() { return updatedByName; }
    public void setUpdatedByName(String updatedByName) { this.updatedByName = updatedByName; }

    public String getStatusBadgeClass() { return statusBadgeClass; }
    public String getStatusLabel() { return statusLabel; }

    public Date getCreatedAtAsDate() {
        try {
            if (createdAt == null || createdAt.trim().isEmpty()) return null;
            return Timestamp.valueOf(createdAt);
        } catch (Exception e) {
            return null;
        }
    }

    public Date getUpdatedAtAsDate() {
        try {
            if (updatedAt == null || updatedAt.trim().isEmpty()) return null;
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            return sdf.parse(updatedAt);
        } catch (Exception e) {
            try {
                return Timestamp.valueOf(updatedAt);
            } catch (Exception ex) {
                return null;
            }
        }
    }

    public String getElectricImg() { return electricImg; }
    public void setElectricImg(String electricImg) { this.electricImg = electricImg; }

    public String getWaterImg() { return waterImg; }
    public void setWaterImg(String waterImg) { this.waterImg = waterImg; }
}
