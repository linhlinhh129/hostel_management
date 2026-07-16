package com.quanlyphongtro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("PaymentTransaction Model")
class PaymentTransactionModelTest {

    @Test
    @DisplayName("fields được set và get đúng")
    void fieldsSetAndGet() {
        PaymentTransaction p = new PaymentTransaction();
        p.setPaymentId(1);
        p.setCode("PAY-HL-001");
        p.setInvoiceId(5);
        p.setRoomId(10);
        p.setStatus("PENDING");
        p.setPaymentDate(LocalDate.of(2025, 6, 15));
        p.setPaymentMethod("VNPAY");
        p.setPaymentAmount(new BigDecimal("3000000"));
        p.setRoomCode("HL0101");
        p.setTenantName("Nguyễn Văn A");

        assertThat(p.getPaymentId()).isEqualTo(1);
        assertThat(p.getCode()).isEqualTo("PAY-HL-001");
        assertThat(p.getStatus()).isEqualTo("PENDING");
        assertThat(p.getPaymentAmount()).isEqualByComparingTo(new BigDecimal("3000000"));
        assertThat(p.getPaymentMethod()).isEqualTo("VNPAY");
        assertThat(p.getRoomCode()).isEqualTo("HL0101");
        assertThat(p.getTenantName()).isEqualTo("Nguyễn Văn A");
    }

    @Test
    @DisplayName("deletedAt null → chưa bị xóa")
    void notDeleted_when_deletedAtNull() {
        PaymentTransaction p = new PaymentTransaction();
        p.setDeletedAt(null);
        assertThat(p.getDeletedAt()).isNull();
    }

    @Test
    @DisplayName("deletedAt set → đã bị xóa")
    void deleted_when_deletedAtSet() {
        PaymentTransaction p = new PaymentTransaction();
        p.setDeletedAt(LocalDateTime.now());
        assertThat(p.getDeletedAt()).isNotNull();
    }
}
