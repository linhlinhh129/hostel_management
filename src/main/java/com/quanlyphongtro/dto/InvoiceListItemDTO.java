package com.quanlyphongtro.dto;

import java.math.BigDecimal;

public class InvoiceListItemDTO {
    private Integer invoiceId;
    private String invoiceCode;
    private String roomCode;
    private String tenantName;
    private String billingPeriod;
    private BigDecimal totalAmount;
    private String dueDate;
    private String status;
    
    // UI Helpers
    private String statusBadgeClass;
    private String statusLabel;

    public InvoiceListItemDTO() {}

    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }

    public String getInvoiceCode() { return invoiceCode; }
    public void setInvoiceCode(String invoiceCode) { this.invoiceCode = invoiceCode; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }

    public String getBillingPeriod() { return billingPeriod; }
    public void setBillingPeriod(String billingPeriod) { this.billingPeriod = billingPeriod; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }

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

    public String getStatusBadgeClass() { return statusBadgeClass; }
    public String getStatusLabel() { return statusLabel; }
}
