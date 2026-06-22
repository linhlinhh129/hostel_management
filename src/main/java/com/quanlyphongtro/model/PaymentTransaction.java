package com.quanlyphongtro.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PaymentTransaction {
    private Integer paymentId;
    private String code;
    private Integer invoiceId;
    private Integer roomId;
    private String status;
    private LocalDate paymentDate;
    private String paymentMethod;
    private BigDecimal paymentAmount;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    private String roomCode;
    private String tenantName;

    public PaymentTransaction() {}

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }

    public Integer getRoomId() { return roomId; }
    public void setRoomId(Integer roomId) { this.roomId = roomId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDate paymentDate) { this.paymentDate = paymentDate; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public BigDecimal getPaymentAmount() { return paymentAmount; }
    public void setPaymentAmount(BigDecimal paymentAmount) { this.paymentAmount = paymentAmount; }

    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public String getRoomCode() { return roomCode; }
    public void setRoomCode(String roomCode) { this.roomCode = roomCode; }

    public String getTenantName() { return tenantName; }
    public void setTenantName(String tenantName) { this.tenantName = tenantName; }
}
