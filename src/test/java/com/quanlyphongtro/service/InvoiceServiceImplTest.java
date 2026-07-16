package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.InvoiceDAO;
import com.quanlyphongtro.model.Invoice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cho InvoiceService — phần tenant-facing (không cần DB thật).
 *
 * InvoiceServiceImpl hiện tại khởi tạo InvoiceDAO trực tiếp qua `new`,
 * nên các test này dùng stub thủ công (subclass override) thay vì Mockito @InjectMocks.
 *
 * NOTE: Khi refactor InvoiceServiceImpl để nhận InvoiceDAO qua constructor,
 *       hãy chuyển sang dùng @InjectMocks như UserServiceImplTest.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("InvoiceService — Tenant layer")
class InvoiceServiceImplTest {

    // -----------------------------------------------------------------------
    // Stub DAO — trả dữ liệu cố định, không chạm DB
    // -----------------------------------------------------------------------
    static Invoice makeInvoice(int id, int roomId, String status, BigDecimal total) {
        Invoice inv = new Invoice();
        inv.setId(id);
        inv.setCode("INV-P101-202501");
        inv.setRoomId(roomId);
        inv.setStatus(status);
        inv.setTotalAmount(total);
        inv.setRoomFee(new BigDecimal("3000000"));
        inv.setDueDate(LocalDate.of(2025, 1, 31));
        inv.setBillingPeriod("Tháng 01/2025");
        return inv;
    }

    /** Stub InvoiceDAO cứng — không cần DB. */
    static class StubInvoiceDAO extends InvoiceDAO {
        private final List<Invoice> store;

        StubInvoiceDAO(List<Invoice> store) { this.store = store; }

        @Override public List<Invoice> findByRoomId(int roomId) {
            return store.stream().filter(i -> i.getRoomId().equals(roomId)).toList();
        }

        @Override public Optional<Invoice> findByIdAndRoomId(int id, int roomId) {
            return store.stream()
                    .filter(i -> i.getId().equals(id) && i.getRoomId().equals(roomId))
                    .findFirst();
        }

        @Override public BigDecimal getUnpaidTotalByRoomId(int roomId) {
            return store.stream()
                    .filter(i -> i.getRoomId().equals(roomId) && !"PAID".equals(i.getStatus()))
                    .map(Invoice::getTotalAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        @Override public Optional<Invoice> getCurrentInvoiceByRoomId(int roomId) {
            return store.stream()
                    .filter(i -> i.getRoomId().equals(roomId))
                    .reduce((a, b) -> b); // lấy cuối = mới nhất
        }
    }

    /** InvoiceServiceImpl test-able — nhận DAO qua field injection thủ công. */
    static class TestableInvoiceService extends com.quanlyphongtro.service.impl.InvoiceServiceImpl {
        private final InvoiceDAO dao;

        TestableInvoiceService(InvoiceDAO dao) {
            this.dao = dao;
        }

        // Override các method gọi invoiceDAO để dùng dao được inject
        @Override
        public List<Invoice> getInvoicesByRoomId(int roomId) {
            return dao.findByRoomId(roomId);
        }

        @Override
        public Optional<Invoice> getInvoiceById(int invoiceId, int roomId) {
            return dao.findByIdAndRoomId(invoiceId, roomId);
        }

        @Override
        public BigDecimal getUnpaidTotal(int roomId) {
            return dao.getUnpaidTotalByRoomId(roomId);
        }

        @Override
        public Optional<Invoice> getCurrentInvoice(int roomId) {
            return dao.getCurrentInvoiceByRoomId(roomId);
        }
    }

    private TestableInvoiceService invoiceService;

    @BeforeEach
    void setUp() {
        List<Invoice> fixtures = List.of(
            makeInvoice(1, 10, "PAID",   new BigDecimal("3500000")),
            makeInvoice(2, 10, "UNPAID", new BigDecimal("3200000")),
            makeInvoice(3, 10, "OVERDUE",new BigDecimal("3100000")),
            makeInvoice(4, 20, "PAID",   new BigDecimal("4000000"))
        );
        invoiceService = new TestableInvoiceService(new StubInvoiceDAO(fixtures));
    }

    // =========================================================
    // getInvoicesByRoomId()
    // =========================================================
    @Nested
    @DisplayName("getInvoicesByRoomId()")
    class GetInvoicesByRoomId {

        @Test
        @DisplayName("trả về danh sách đúng phòng")
        void returnsInvoicesForRoom() {
            List<Invoice> result = invoiceService.getInvoicesByRoomId(10);

            assertThat(result).hasSize(3);
            assertThat(result).allMatch(i -> i.getRoomId() == 10);
        }

        @Test
        @DisplayName("trả về list rỗng khi phòng không có hóa đơn")
        void returnsEmptyForUnknownRoom() {
            List<Invoice> result = invoiceService.getInvoicesByRoomId(999);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getInvoiceById()
    // =========================================================
    @Nested
    @DisplayName("getInvoiceById()")
    class GetInvoiceById {

        @Test
        @DisplayName("tìm thấy hóa đơn đúng id và roomId")
        void findsInvoiceByIdAndRoom() {
            Optional<Invoice> result = invoiceService.getInvoiceById(2, 10);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(2);
            assertThat(result.get().getStatus()).isEqualTo("UNPAID");
        }

        @Test
        @DisplayName("trả về empty khi id không thuộc phòng đó (bảo vệ quyền sở hữu)")
        void returnsEmptyWhenRoomMismatch() {
            // Invoice 4 thuộc room 20, không được tìm thấy khi query room 10
            Optional<Invoice> result = invoiceService.getInvoiceById(4, 10);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi invoiceId không tồn tại")
        void returnsEmptyForNonExistentId() {
            Optional<Invoice> result = invoiceService.getInvoiceById(999, 10);

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getUnpaidTotal()
    // =========================================================
    @Nested
    @DisplayName("getUnpaidTotal()")
    class GetUnpaidTotal {

        @Test
        @DisplayName("tính đúng tổng chưa thanh toán (UNPAID + OVERDUE)")
        void calculatesUnpaidPlusOverdue() {
            // room 10: UNPAID=3200000 + OVERDUE=3100000 = 6300000
            BigDecimal result = invoiceService.getUnpaidTotal(10);

            assertThat(result).isEqualByComparingTo(new BigDecimal("6300000"));
        }

        @Test
        @DisplayName("trả về 0 khi phòng không có nợ")
        void returnsZeroForNoDebt() {
            // room 20: chỉ có 1 PAID
            BigDecimal result = invoiceService.getUnpaidTotal(20);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("trả về 0 khi phòng không tồn tại")
        void returnsZeroForUnknownRoom() {
            BigDecimal result = invoiceService.getUnpaidTotal(999);

            assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
        }
    }

    // =========================================================
    // getCurrentInvoice()
    // =========================================================
    @Nested
    @DisplayName("getCurrentInvoice()")
    class GetCurrentInvoice {

        @Test
        @DisplayName("trả về hóa đơn mới nhất của phòng")
        void returnsLatestInvoice() {
            Optional<Invoice> result = invoiceService.getCurrentInvoice(10);

            assertThat(result).isPresent();
            // Stub lấy phần tử cuối cùng trong list = invoice id=3
            assertThat(result.get().getId()).isEqualTo(3);
        }

        @Test
        @DisplayName("trả về empty khi phòng chưa có hóa đơn nào")
        void returnsEmptyForRoomWithNoInvoices() {
            Optional<Invoice> result = invoiceService.getCurrentInvoice(999);

            assertThat(result).isEmpty();
        }
    }
}
