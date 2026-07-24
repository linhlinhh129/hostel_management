package com.quanlyphongtro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DebtDetailDTO {
    private Integer invoiceId;
    private String invoiceCode;
    private String roomCode;
    
    private Integer tenantId;
    private String tenantName;
    private String tenantPhone;
    private String tenantEmail;
    
    private Integer facilityId;
    private String facilityCode;
    private String facilityName;
    
    private String billingPeriod;
    private BigDecimal roomFee;
    
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
    private BigDecimal subtotal;
    private BigDecimal taxRate;
    private BigDecimal taxAmount;
    
    private BigDecimal invoiceTotalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    
    private LocalDate dueDate;
    private Integer overdueDays;
    private BigDecimal lateFeePreview;
    
    private String status;
    private String note;
    
    // We can hold payment info here if needed, but for MVP just basics.
    // Spec mentions payments list, we can add it later if needed or skip if unnecessary for display.
    
    private LocalDateTime createdAt;
    private Integer createdBy;
    private LocalDateTime updatedAt;
    private Integer updatedBy;

    // Getters and Setters
    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public Integer getTenantId() { return tenantId; }
    public void setTenantId(Integer tenantId) { this.tenantId = tenantId; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getTenantPhone() { return tenantPhone; }
    public void setTenantPhone(String tenantPhone) { this.tenantPhone = tenantPhone; }

    public String getTenantEmail() { return tenantEmail; }
    public void setTenantEmail(String tenantEmail) { this.tenantEmail = tenantEmail; }

    public Integer getFacilityId() { return facilityId; }
    public void setFacilityId(Integer facilityId) { this.facilityId = facilityId; }

    public String getFacilityCode() { return facilityCode; }
    public void setFacilityCode(String facilityCode) { this.facilityCode = facilityCode; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public BigDecimal getRoomFee() { return roomFee; }
    public void setRoomFee(BigDecimal roomFee) { this.roomFee = roomFee; }

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

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getTaxRate() { return taxRate; }
    public void setTaxRate(BigDecimal taxRate) { this.taxRate = taxRate; }

    public BigDecimal getTaxAmount() { return taxAmount; }
    public void setTaxAmount(BigDecimal taxAmount) { this.taxAmount = taxAmount; }

    public BigDecimal getInvoiceTotalAmount() { return invoiceTotalAmount; }
    public void setInvoiceTotalAmount(BigDecimal invoiceTotalAmount) { this.invoiceTotalAmount = invoiceTotalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getDebtAmount() { return debtAmount; }
    public void setDebtAmount(BigDecimal debtAmount) { this.debtAmount = debtAmount; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    /** Trả về dueDate dạng String "yyyy-MM-dd" để dùng với fmt:parseDate trong JSP */
    public String getDueDateStr() {
        return dueDate != null ? dueDate.toString() : "";
    }

    public Integer getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Integer overdueDays) { this.overdueDays = overdueDays; }

    public BigDecimal getLateFeePreview() { return lateFeePreview; }
    public void setLateFeePreview(BigDecimal lateFeePreview) { this.lateFeePreview = lateFeePreview; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public Integer getUpdatedBy() { return updatedBy; }
    public void setUpdatedBy(Integer updatedBy) { this.updatedBy = updatedBy; }
}
