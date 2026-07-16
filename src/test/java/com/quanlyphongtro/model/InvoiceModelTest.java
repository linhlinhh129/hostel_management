package com.quanlyphongtro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cho Invoice model — test các helper method thuần Java, không cần DB.
 *
 * Đây là loại test nhanh nhất trong TDD: test business logic
 * nằm trong model (getStatusLabel, getStatusBadgeClass, isOverdue, ...).
 */
@DisplayName("Invoice Model")
class InvoiceModelTest {

    private Invoice invoice;

    @BeforeEach
    void setUp() {
        invoice = new Invoice();
        invoice.setId(1);
        invoice.setCode("INV-P101-202501");
        invoice.setRoomId(10);
        invoice.setDueDate(LocalDate.of(2025, 1, 31));
        invoice.setTotalAmount(new BigDecimal("3500000"));
        invoice.setRoomFee(new BigDecimal("3000000"));
    }

    // =========================================================
    // isOverdue()
    // =========================================================
    @Nested
    @DisplayName("isOverdue()")
    class IsOverdue {

        @Test
        @DisplayName("trả về true khi status là OVERDUE")
        void returnsTrue_whenStatusOverdue() {
            invoice.setStatus("OVERDUE");
            assertThat(invoice.isOverdue()).isTrue();
        }

        @Test
        @DisplayName("trả về false khi status là UNPAID")
        void returnsFalse_whenStatusUnpaid() {
            invoice.setStatus("UNPAID");
            assertThat(invoice.isOverdue()).isFalse();
        }

        @Test
        @DisplayName("trả về false khi status là PAID")
        void returnsFalse_whenStatusPaid() {
            invoice.setStatus("PAID");
            assertThat(invoice.isOverdue()).isFalse();
        }
    }

    // =========================================================
    // getStatusLabel()
    // =========================================================
    @Nested
    @DisplayName("getStatusLabel()")
    class StatusLabel {

        @Test
        @DisplayName("hiển thị 'Đã thanh toán' khi PAID")
        void paid_showsCorrectLabel() {
            invoice.setStatus("PAID");
            assertThat(invoice.getStatusLabel()).isEqualTo("Đã thanh toán");
        }

        @Test
        @DisplayName("hiển thị 'Quá hạn' khi OVERDUE")
        void overdue_showsCorrectLabel() {
            invoice.setStatus("OVERDUE");
            assertThat(invoice.getStatusLabel()).isEqualTo("Quá hạn");
        }

        @Test
        @DisplayName("hiển thị 'Chưa thanh toán' khi UNPAID")
        void unpaid_showsCorrectLabel() {
            invoice.setStatus("UNPAID");
            assertThat(invoice.getStatusLabel()).isEqualTo("Chưa thanh toán");
        }

        @Test
        @DisplayName("hiển thị 'Chờ duyệt' khi hasPendingPayment = true (bất kể status)")
        void pendingPayment_overridesStatusLabel() {
            invoice.setStatus("UNPAID");
            invoice.setHasPendingPayment(true);
            assertThat(invoice.getStatusLabel()).isEqualTo("Chờ duyệt");
        }
    }

    // =========================================================
    // getStatusBadgeClass()
    // =========================================================
    @Nested
    @DisplayName("getStatusBadgeClass()")
    class StatusBadgeClass {

        @Test
        @DisplayName("badge-success khi PAID")
        void paid_successBadge() {
            invoice.setStatus("PAID");
            assertThat(invoice.getStatusBadgeClass()).isEqualTo("badge-success");
        }

        @Test
        @DisplayName("badge-danger khi OVERDUE")
        void overdue_dangerBadge() {
            invoice.setStatus("OVERDUE");
            assertThat(invoice.getStatusBadgeClass()).isEqualTo("badge-danger");
        }

        @Test
        @DisplayName("badge-warning khi UNPAID")
        void unpaid_warningBadge() {
            invoice.setStatus("UNPAID");
            assertThat(invoice.getStatusBadgeClass()).isEqualTo("badge-warning");
        }

        @Test
        @DisplayName("badge-info khi có pending payment")
        void pendingPayment_infoBadge() {
            invoice.setStatus("UNPAID");
            invoice.setHasPendingPayment(true);
            assertThat(invoice.getStatusBadgeClass()).isEqualTo("badge-info");
        }
    }

    // =========================================================
    // getDueDateLabel()
    // =========================================================
    @Nested
    @DisplayName("getDueDateLabel()")
    class DueDateLabel {

        @Test
        @DisplayName("format ngày đúng dd/MM/yyyy")
        void formatsDateCorrectly() {
            invoice.setDueDate(LocalDate.of(2025, 3, 5));
            assertThat(invoice.getDueDateLabel()).isEqualTo("05/03/2025");
        }

        @Test
        @DisplayName("trả về 'N/A' khi dueDate null")
        void returnsNA_whenNull() {
            invoice.setDueDate(null);
            assertThat(invoice.getDueDateLabel()).isEqualTo("N/A");
        }
    }

    // =========================================================
    // getPeriodLabel()
    // =========================================================
    @Nested
    @DisplayName("getPeriodLabel()")
    class PeriodLabel {

        @Test
        @DisplayName("trả về billingPeriod khi đã set")
        void returnsBillingPeriod() {
            invoice.setBillingPeriod("Tháng 01/2025");
            assertThat(invoice.getPeriodLabel()).isEqualTo("Tháng 01/2025");
        }

        @Test
        @DisplayName("trả về 'N/A' khi billingPeriod null")
        void returnsNA_whenNull() {
            invoice.setBillingPeriod(null);
            assertThat(invoice.getPeriodLabel()).isEqualTo("N/A");
        }
    }

    // =========================================================
    // isDeleted()
    // =========================================================
    @Nested
    @DisplayName("isDeleted()")
    class IsDeleted {

        @Test
        @DisplayName("trả về false khi deletedAt null (chưa xóa)")
        void notDeleted_whenDeletedAtNull() {
            invoice.setDeletedAt(null);
            assertThat(invoice.isDeleted()).isFalse();
        }

        @Test
        @DisplayName("trả về true khi deletedAt có giá trị (đã soft-delete)")
        void deleted_whenDeletedAtSet() {
            invoice.setDeletedAt(java.time.LocalDateTime.now());
            assertThat(invoice.isDeleted()).isTrue();
        }
    }
}
