package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.NotificationDAO;
import com.quanlyphongtro.dto.PageDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Notification;
import com.quanlyphongtro.service.impl.NotificationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("NotificationServiceImpl")
class NotificationServiceImplTest {

    private NotificationDAO mockDAO;
    private NotificationServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockDAO = mock(NotificationDAO.class);
        service = new NotificationServiceImpl();
        Field f = NotificationServiceImpl.class.getDeclaredField("notificationDAO");
        f.setAccessible(true);
        f.set(service, mockDAO);
    }

    // =========================================================
    // createAdminNotification() — validation
    // =========================================================
    @Nested
    @DisplayName("createAdminNotification() — validation")
    class CreateAdminNotification {

        @Test @DisplayName("ném ValidationException khi title trống")
        void blankTitle() {
            assertThatThrownBy(() -> service.createAdminNotification("", "Nội dung", 1))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("Tiêu đề");
        }

        @Test @DisplayName("ném ValidationException khi title null")
        void nullTitle() {
            assertThatThrownBy(() -> service.createAdminNotification(null, "Nội dung", 1))
                    .isInstanceOf(ValidationException.class);
        }

        @Test @DisplayName("ném ValidationException khi title quá 255 ký tự")
        void titleTooLong() {
            String long256 = "A".repeat(256);
            assertThatThrownBy(() -> service.createAdminNotification(long256, "Nội dung", 1))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("255");
        }

        @Test @DisplayName("ném ValidationException khi content trống")
        void blankContent() {
            assertThatThrownBy(() -> service.createAdminNotification("Tiêu đề", "", 1))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("Nội dung");
        }

        @Test @DisplayName("ném ValidationException khi content quá 1000 ký tự")
        void contentTooLong() {
            String long1001 = "B".repeat(1001);
            assertThatThrownBy(() -> service.createAdminNotification("Tiêu đề", long1001, 1))
                    .isInstanceOf(ValidationException.class).hasMessageContaining("1000");
        }

        @Test @DisplayName("tạo thành công khi title 255 ký tự đúng")
        void exactTitle255_succeeds() throws Exception {
            String exact255 = "A".repeat(255);
            when(mockDAO.generateCode("ALL")).thenReturn("NTF-ALL-001");
            service.createAdminNotification(exact255, "Nội dung hợp lệ", 1);
            verify(mockDAO).insert(any(Notification.class));
        }

        @Test @DisplayName("tạo thành công với input hợp lệ — insert được gọi")
        void validInput_insertsNotification() throws Exception {
            when(mockDAO.generateCode("ALL")).thenReturn("NTF-ALL-001");
            service.createAdminNotification("Thông báo hệ thống", "Nội dung thử nghiệm", 1);
            verify(mockDAO).insert(argThat(n ->
                "Thông báo hệ thống".equals(n.getTitle()) &&
                "ALL".equals(n.getTargetType()) &&
                "SENT".equals(n.getStatus())
            ));
        }
    }

    // =========================================================
    // sendNotification() — recipientType validation
    // =========================================================
    @Nested
    @DisplayName("sendNotification() — recipientType routing")
    class SendNotification {

        @Test @DisplayName("ném IllegalArgumentException khi title trống")
        void blankTitle() {
            assertThatThrownBy(() -> service.sendNotification("", "content", "FACILITY", 1, null, 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test @DisplayName("ném IllegalArgumentException khi recipientType không hợp lệ")
        void invalidRecipientType() {
            assertThatThrownBy(() ->
                    service.sendNotification("title", "content", "ALL_SYSTEM", null, null, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("không hợp lệ");
        }

        @Test @DisplayName("ném IllegalArgumentException khi FACILITY nhưng recipientId null")
        void facilityWithNullId() {
            assertThatThrownBy(() ->
                    service.sendNotification("title", "content", "FACILITY", null, null, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("cơ sở");
        }

        @Test @DisplayName("ném AccessDeniedException khi không quản lý cơ sở đó")
        void facilityAccessDenied() {
            when(mockDAO.verifyFacilityManager(5, 1)).thenReturn(false);
            assertThatThrownBy(() ->
                    service.sendNotification("title", "content", "FACILITY", 5, null, 1))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test @DisplayName("ném IllegalArgumentException khi ROOM nhưng recipientId null")
        void roomWithNullId() {
            assertThatThrownBy(() ->
                    service.sendNotification("title", "content", "ROOM", null, null, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("phòng");
        }
    }

    // =========================================================
    // reportIncorrectInvoice() — access control
    // =========================================================
    @Nested
    @DisplayName("reportIncorrectInvoice()")
    class ReportIncorrectInvoice {

        @Test @DisplayName("ném IllegalArgumentException khi hóa đơn không tồn tại")
        void invoiceNotFound() {
            when(mockDAO.getInvoiceVerifyDetails(99)).thenReturn(null);
            assertThatThrownBy(() -> service.reportIncorrectInvoice(99, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("không tồn tại");
        }

        @Test @DisplayName("ném AccessDeniedException khi không phải manager của cơ sở")
        void accessDenied() {
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("managerId", 99);
            invoice.put("status", "UNPAID");
            invoice.put("meterId", 5);
            when(mockDAO.getInvoiceVerifyDetails(1)).thenReturn(invoice);
            assertThatThrownBy(() -> service.reportIncorrectInvoice(1, 1))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test @DisplayName("ném IllegalStateException khi hóa đơn đã PAID")
        void paidInvoice() {
            Map<String, Object> invoice = new HashMap<>();
            invoice.put("managerId", 1);
            invoice.put("status", "PAID");
            invoice.put("meterId", 5);
            when(mockDAO.getInvoiceVerifyDetails(1)).thenReturn(invoice);
            assertThatThrownBy(() -> service.reportIncorrectInvoice(1, 1))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("đã thanh toán");
        }
    }

    // =========================================================
    // getAdminNotifications()
    // =========================================================
    @Nested
    @DisplayName("getAdminNotifications()")
    class GetAdminNotifications {

        @Test @DisplayName("trả về PageDTO với đúng total")
        void returnsPageDTO() {
            when(mockDAO.count("")).thenReturn(15);
            when(mockDAO.findAll("", 1, 10)).thenReturn(List.of());

            PageDTO<Notification> result = service.getAdminNotifications("", 1, 10);

            assertThat(result.getTotal()).isEqualTo(15);
            assertThat(result.getPage()).isEqualTo(1);
        }
    }

    // =========================================================
    // sendDebtReminder()
    // =========================================================
    @Nested
    @DisplayName("sendDebtReminder()")
    class SendDebtReminder {

        @Test @DisplayName("ném IllegalArgumentException khi hóa đơn không tồn tại")
        void invoiceNotFound() {
            when(mockDAO.getInvoiceDetailsForSendDebt(99)).thenReturn(null);
            assertThatThrownBy(() -> service.sendDebtReminder(99, "title", "content", 1))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test @DisplayName("ném AccessDeniedException khi không có quyền")
        void accessDenied() {
            Map<String, Object> inv = new HashMap<>();
            inv.put("managerId", 99);
            inv.put("roomId", 5);
            when(mockDAO.getInvoiceDetailsForSendDebt(1)).thenReturn(inv);
            assertThatThrownBy(() -> service.sendDebtReminder(1, "title", "content", 1))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
