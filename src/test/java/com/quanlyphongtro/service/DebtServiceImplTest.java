package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.DebtDAO;
import com.quanlyphongtro.dto.DebtDetailDTO;
import com.quanlyphongtro.dto.DebtListItemDTO;
import com.quanlyphongtro.service.impl.DebtServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit test cho DebtServiceImpl.
 *
 * Logic quan trọng nhất là pure-Java tính toán nằm sau khi DAO trả dữ liệu:
 *  - debtAmount = max(0, total - paid)
 *  - overdueDays từ dueDate so với ngày hiện tại
 *  - lateFeePreview = roomFee × 0.01 × overdueDays (chỉ khi OVERDUE)
 *  - status UNPAID quá hạn → tự động đổi thành OVERDUE
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("DebtServiceImpl — tính toán công nợ")
class DebtServiceImplTest {

    private DebtDAO mockDebtDAO;
    private DebtServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockDebtDAO = mock(DebtDAO.class);
        service = new DebtServiceImpl();
        Field f = DebtServiceImpl.class.getDeclaredField("debtDAO");
        f.setAccessible(true);
        f.set(service, mockDebtDAO);
    }

    // ── Helper builders ───────────────────────────────────────────────────

    private DebtListItemDTO listItem(BigDecimal total, BigDecimal paid,
                                     LocalDate dueDate, String status, BigDecimal roomFee) {
        DebtListItemDTO d = new DebtListItemDTO();
        d.setInvoiceTotalAmount(total);
        d.setPaidAmount(paid);
        d.setDueDate(dueDate);
        d.setStatus(status);
        d.setRoomFee(roomFee);
        return d;
    }

    private DebtDetailDTO detailItem(BigDecimal total, BigDecimal paid,
                                     LocalDate dueDate, String status, BigDecimal roomFee) {
        DebtDetailDTO d = new DebtDetailDTO();
        d.setInvoiceTotalAmount(total);
        d.setPaidAmount(paid);
        d.setDueDate(dueDate);
        d.setStatus(status);
        d.setRoomFee(roomFee);
        return d;
    }

    // =========================================================
    // getDebts() — debtAmount calculation
    // =========================================================
    @Nested
    @DisplayName("getDebts() — tính debtAmount")
    class DebtAmountCalc {

        @Test
        @DisplayName("debtAmount = total - paid khi total > paid")
        void normalDebt_calculatesCorrectly() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), new BigDecimal("1000000"),
                LocalDate.now().plusDays(10), "UNPAID", new BigDecimal("3000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getDebtAmount())
                .isEqualByComparingTo(new BigDecimal("2000000"));
        }

        @Test
        @DisplayName("debtAmount = 0 khi paid > total (không âm)")
        void overpaid_debtIsZero() {
            DebtListItemDTO item = listItem(
                new BigDecimal("1000000"), new BigDecimal("2000000"),
                LocalDate.now().plusDays(10), "UNPAID", new BigDecimal("1000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getDebtAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("debtAmount = 0 khi total null")
        void nullTotal_debtIsZero() {
            DebtListItemDTO item = listItem(null, new BigDecimal("500000"),
                LocalDate.now().plusDays(5), "UNPAID", new BigDecimal("2000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getDebtAmount())
                .isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // =========================================================
    // getDebts() — overdue promotion
    // =========================================================
    @Nested
    @DisplayName("getDebts() — tự động chuyển status UNPAID → OVERDUE")
    class OverduePromotion {

        @Test
        @DisplayName("UNPAID quá hạn → đổi thành OVERDUE")
        void unpaidPastDue_becomesOverdue() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), BigDecimal.ZERO,
                LocalDate.now().minusDays(5), "UNPAID", new BigDecimal("3000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getStatus()).isEqualTo("OVERDUE");
        }

        @Test
        @DisplayName("UNPAID chưa quá hạn → giữ nguyên UNPAID")
        void unpaidNotDue_staysUnpaid() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), BigDecimal.ZERO,
                LocalDate.now().plusDays(5), "UNPAID", new BigDecimal("3000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getStatus()).isEqualTo("UNPAID");
            assertThat(result.get(0).getOverdueDays()).isEqualTo(0);
        }

        @Test
        @DisplayName("PAID quá hạn → giữ nguyên PAID (không đổi)")
        void paidPastDue_stays_paid() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), new BigDecimal("3000000"),
                LocalDate.now().minusDays(10), "PAID", new BigDecimal("3000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getStatus()).isEqualTo("PAID");
        }
    }

    // =========================================================
    // getDebts() — lateFeePreview
    // =========================================================
    @Nested
    @DisplayName("getDebts() — tính lateFeePreview")
    class LateFee {

        @Test
        @DisplayName("lateFee = roomFee × 0.01 × overdueDays khi OVERDUE")
        void overdue_calculatesLateFee() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), BigDecimal.ZERO,
                LocalDate.now().minusDays(10), "OVERDUE", new BigDecimal("3000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            // 3,000,000 × 0.01 × 10 = 300,000
            assertThat(result.get(0).getLateFeePreview())
                .isEqualByComparingTo(new BigDecimal("300000.0"));
        }

        @Test
        @DisplayName("lateFee = 0 khi UNPAID chưa quá hạn")
        void unpaidNotDue_lateFeeIsZero() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), BigDecimal.ZERO,
                LocalDate.now().plusDays(5), "UNPAID", new BigDecimal("3000000"));
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getLateFeePreview())
                .isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("lateFee = 0 khi roomFee null")
        void nullRoomFee_lateFeeIsZero() {
            DebtListItemDTO item = listItem(
                new BigDecimal("3000000"), BigDecimal.ZERO,
                LocalDate.now().minusDays(5), "OVERDUE", null);
            when(mockDebtDAO.findDebts(1, "", "", 0, 10)).thenReturn(List.of(item));

            List<DebtListItemDTO> result = service.getDebts(1, "", "", 1, 10);

            assertThat(result.get(0).getLateFeePreview())
                .isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // =========================================================
    // getTotalPages()
    // =========================================================
    @Nested
    @DisplayName("getTotalPages()")
    class TotalPages {

        @Test
        @DisplayName("ceil(total / pageSize)")
        void calculatesPages() {
            when(mockDebtDAO.countDebts(1, "", "")).thenReturn(25);
            assertThat(service.getTotalPages(1, "", "", 10)).isEqualTo(3);
        }

        @Test
        @DisplayName("0 rows → 0 pages")
        void zeroRows_returnsZero() {
            when(mockDebtDAO.countDebts(1, "", "")).thenReturn(0);
            assertThat(service.getTotalPages(1, "", "", 10)).isEqualTo(0);
        }
    }

    // =========================================================
    // getDebtDetail()
    // =========================================================
    @Nested
    @DisplayName("getDebtDetail()")
    class DebtDetail {

        @Test
        @DisplayName("trả về empty khi DAO không tìm thấy")
        void notFound_returnsEmpty() {
            when(mockDebtDAO.findDebtDetail(1, 99)).thenReturn(Optional.empty());
            assertThat(service.getDebtDetail(1, 99)).isEmpty();
        }

        @Test
        @DisplayName("tính debtAmount đúng cho detail")
        void found_calculatesDebtAmount() {
            DebtDetailDTO dto = detailItem(
                new BigDecimal("5000000"), new BigDecimal("2000000"),
                LocalDate.now().plusDays(5), "UNPAID", new BigDecimal("4000000"));
            when(mockDebtDAO.findDebtDetail(1, 1)).thenReturn(Optional.of(dto));

            Optional<DebtDetailDTO> result = service.getDebtDetail(1, 1);

            assertThat(result).isPresent();
            assertThat(result.get().getDebtAmount())
                .isEqualByComparingTo(new BigDecimal("3000000"));
        }
    }
}
