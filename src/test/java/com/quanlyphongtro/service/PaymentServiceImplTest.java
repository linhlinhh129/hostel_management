package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.PaymentDAO;
import com.quanlyphongtro.dto.PaymentDetailDTO;
import com.quanlyphongtro.dto.PaymentListItemDTO;
import com.quanlyphongtro.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PaymentServiceImpl — quản lý thanh toán")
class PaymentServiceImplTest {

    private PaymentDAO mockDAO;
    private PaymentServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockDAO = mock(PaymentDAO.class);
        service = new PaymentServiceImpl();
        Field f = PaymentServiceImpl.class.getDeclaredField("paymentDAO");
        f.setAccessible(true);
        f.set(service, mockDAO);
    }

    // ── helpers ───────────────────────────────────────────────────────────
    private PaymentListItemDTO listItem(int id, String status) {
        PaymentListItemDTO d = new PaymentListItemDTO();
        d.setPaymentId(id);
        d.setStatus(status);
        d.setAmount(new BigDecimal("3000000"));
        d.setTransactionCode("PAY-001");
        return d;
    }

    // =========================================================
    // findPayments() — delegation
    // =========================================================
    @Nested
    @DisplayName("findPayments()")
    class FindPayments {

        @Test
        @DisplayName("delegates to DAO with all params")
        void delegatesToDAO() {
            when(mockDAO.findPayments(1, "kw", "PENDING", "2025-01-01", "2025-12-31",
                    "6", "2025", 0, 10))
                    .thenReturn(List.of(listItem(1, "PENDING")));

            List<PaymentListItemDTO> result = service.findPayments(1, "kw", "PENDING",
                    "2025-01-01", "2025-12-31", "6", "2025", 0, 10);

            assertThat(result).hasSize(1);
            verify(mockDAO).findPayments(1, "kw", "PENDING",
                    "2025-01-01", "2025-12-31", "6", "2025", 0, 10);
        }

        @Test
        @DisplayName("trả về danh sách rỗng khi DAO trả rỗng")
        void emptyResult() {
            when(mockDAO.findPayments(anyInt(), any(), any(), any(), any(), any(), any(),
                    anyInt(), anyInt()))
                    .thenReturn(List.of());
            assertThat(service.findPayments(1, "", "", "", "", "", "", 0, 10)).isEmpty();
        }
    }

    // =========================================================
    // countPayments() — delegation
    // =========================================================
    @Test
    @DisplayName("countPayments delegates to DAO")
    void countPayments() {
        when(mockDAO.countPayments(1, "", "PENDING", "", "", "", "")).thenReturn(42);
        assertThat(service.countPayments(1, "", "PENDING", "", "", "", "")).isEqualTo(42);
    }

    // =========================================================
    // findById() — delegation
    // =========================================================
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("trả về DTO khi tìm thấy")
        void found() {
            PaymentDetailDTO dto = new PaymentDetailDTO();
            dto.setPaymentId(5);
            dto.setStatus("PENDING");
            when(mockDAO.findById(1, 5)).thenReturn(dto);

            PaymentDetailDTO result = service.findById(1, 5);
            assertThat(result.getPaymentId()).isEqualTo(5);
        }

        @Test
        @DisplayName("trả về null khi không tìm thấy")
        void notFound() {
            when(mockDAO.findById(1, 99)).thenReturn(null);
            assertThat(service.findById(1, 99)).isNull();
        }
    }

    // =========================================================
    // approvePayment()
    // =========================================================
    @Nested
    @DisplayName("approvePayment()")
    class ApprovePayment {

        @Test
        @DisplayName("gọi paymentDAO.approvePayment với đúng params")
        void delegatesToDAO() throws Exception {
            service.approvePayment(5, 1);
            verify(mockDAO).approvePayment(5, 1);
        }

        @Test
        @DisplayName("ném RuntimeException khi DAO ném SQLException")
        void sqlException_wrappedAsRuntime() throws Exception {
            doThrow(new SQLException("DB error")).when(mockDAO).approvePayment(5, 1);
            assertThatThrownBy(() -> service.approvePayment(5, 1))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error approving payment");
        }
    }

    // =========================================================
    // rejectPayment()
    // =========================================================
    @Nested
    @DisplayName("rejectPayment()")
    class RejectPayment {

        @Test
        @DisplayName("gọi paymentDAO.rejectPayment với đúng params")
        void delegatesToDAO() throws Exception {
            service.rejectPayment(7, 1);
            verify(mockDAO).rejectPayment(7, 1);
        }

        @Test
        @DisplayName("ném RuntimeException khi DAO ném SQLException")
        void sqlException_wrappedAsRuntime() throws Exception {
            doThrow(new SQLException("DB error")).when(mockDAO).rejectPayment(7, 1);
            assertThatThrownBy(() -> service.rejectPayment(7, 1))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Error rejecting payment");
        }
    }
}
