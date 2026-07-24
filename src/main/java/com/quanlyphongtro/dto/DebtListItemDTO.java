package com.quanlyphongtro.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class DebtListItemDTO {
    private Integer invoiceId;
    private String invoiceCode;
    private String roomCode;
    private Integer tenantId;
    private String tenantName;
    private String tenantPhone;
    private Integer facilityId;
    private String facilityCode;
    private String facilityName;
    private String billingPeriod;
    private BigDecimal invoiceTotalAmount;
    private BigDecimal paidAmount;
    private BigDecimal debtAmount;
    private BigDecimal roomFee;
    private LocalDate dueDate;
    private Integer overdueDays;
    private BigDecimal lateFeePreview;
    private String status;
    private Integer roomId;

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

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

    public Integer getFacilityId() { return facilityId; }
    public void setFacilityId(Integer facilityId) { this.facilityId = facilityId; }

    public String getFacilityCode() { return facilityCode; }
    public void setFacilityCode(String facilityCode) { this.facilityCode = facilityCode; }

    public String getFacilityName() { return facilityName; }
    public void setFacilityName(String facilityName) { this.facilityName = facilityName; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public BigDecimal getInvoiceTotalAmount() { return invoiceTotalAmount; }
    public void setInvoiceTotalAmount(BigDecimal invoiceTotalAmount) { this.invoiceTotalAmount = invoiceTotalAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public BigDecimal getDebtAmount() { return debtAmount; }
    public void setDebtAmount(BigDecimal debtAmount) { this.debtAmount = debtAmount; }

    public BigDecimal getRoomFee() { return roomFee; }
    public void setRoomFee(BigDecimal roomFee) { this.roomFee = roomFee; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public Integer getOverdueDays() { return overdueDays; }
    public void setOverdueDays(Integer overdueDays) { this.overdueDays = overdueDays; }

    public BigDecimal getLateFeePreview() { return lateFeePreview; }
    public void setLateFeePreview(BigDecimal lateFeePreview) { this.lateFeePreview = lateFeePreview; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
