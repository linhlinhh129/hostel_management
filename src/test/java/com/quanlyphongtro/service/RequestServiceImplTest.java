package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.AuditLogDAO;
import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.service.impl.RequestServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RequestServiceImpl — quản lý yêu cầu hỗ trợ")
class RequestServiceImplTest {

    private RequestDAO mockRequestDAO;
    private AuditLogDAO mockAuditLogDAO;
    private RequestServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockRequestDAO  = mock(RequestDAO.class);
        mockAuditLogDAO = mock(AuditLogDAO.class);
        service = new RequestServiceImpl();
        inject("requestDAO",  mockRequestDAO);
        inject("auditLogDAO", mockAuditLogDAO);
    }

    private void inject(String name, Object mock) throws Exception {
        Field f = RequestServiceImpl.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(service, mock);
    }

    /** Ticket với trạng thái và senderRole tuỳ chỉnh. */
    private Map<String, Object> ticket(int managerId, String status, String senderRole) {
        Map<String, Object> m = new HashMap<>();
        m.put("managerId",   managerId);
        m.put("status",      status);
        m.put("senderRole",  senderRole);
        m.put("senderName",  "Người gửi");
        m.put("createdAt",   "15/06/2025 10:00");
        m.put("updatedAt",   "15/06/2025 11:00");
        m.put("appointScheduleFormatted", null);
        m.put("appointSchedule",          null);
        m.put("rejectionReason",          null);
        m.put("assignedOperatorName",     "NV A");
        return m;
    }

    // =========================================================
    // acceptRequest()
    // =========================================================
    @Nested
    @DisplayName("acceptRequest()")
    class AcceptRequest {
        @Test @DisplayName("gọi updateRequestStatus với ASSIGNED/PENDING")
        void callsDAO() {
            when(mockRequestDAO.updateRequestStatus(1, "ASSIGNED", "PENDING", 5, null))
                    .thenReturn(true);
            assertThat(service.acceptRequest(1, 5)).isTrue();
            verify(mockRequestDAO).updateRequestStatus(1, "ASSIGNED", "PENDING", 5, null);
        }
    }

    // =========================================================
    // rejectRequest()
    // =========================================================
    @Nested
    @DisplayName("rejectRequest()")
    class RejectRequest {
        @Test @DisplayName("gọi updateRequestStatus với REJECTED/PENDING và lý do")
        void callsDAO() {
            when(mockRequestDAO.updateRequestStatus(1, "REJECTED", "PENDING", 5, "Lý do"))
                    .thenReturn(true);
            assertThat(service.rejectRequest(1, 5, "Lý do")).isTrue();
        }
    }

    // =========================================================
    // receiveTicket()
    // =========================================================
    @Nested
    @DisplayName("receiveTicket()")
    class ReceiveTicket {
        @Test @DisplayName("trả về false khi ticket null")
        void nullTicket_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(null);
            assertThat(service.receiveTicket(1)).isFalse();
        }

        @Test @DisplayName("trả về false khi trạng thái là closed (REJECTED)")
        void closedStatus_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "REJECTED", "TENANT"));
            assertThat(service.receiveTicket(1)).isFalse();
        }

        @Test @DisplayName("trả về false khi trạng thái đã qua NEW/PENDING (RECEIVED)")
        void receivedStatus_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "RECEIVED", "TENANT"));
            assertThat(service.receiveTicket(1)).isFalse();
        }

        @Test @DisplayName("tiếp nhận thành công khi trạng thái NEW")
        void newStatus_succeeds() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "NEW", "TENANT"));
            when(mockRequestDAO.receiveTicket(1)).thenReturn(true);
            assertThat(service.receiveTicket(1)).isTrue();
        }

        @Test @DisplayName("tiếp nhận thành công khi trạng thái PENDING")
        void pendingStatus_succeeds() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "PENDING", "TENANT"));
            when(mockRequestDAO.receiveTicket(1)).thenReturn(true);
            assertThat(service.receiveTicket(1)).isTrue();
        }
    }

    // =========================================================
    // rejectTicket()
    // =========================================================
    @Nested
    @DisplayName("rejectTicket()")
    class RejectTicket {
        @Test @DisplayName("trả về false khi closed state")
        void closed_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "DONE", "TENANT"));
            assertThat(service.rejectTicket(1, "lý do")).isFalse();
        }

        @Test @DisplayName("trả về false khi ticket null")
        void null_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(null);
            assertThat(service.rejectTicket(1, "lý do")).isFalse();
        }
    }

    // =========================================================
    // scheduleTicket()
    // =========================================================
    @Nested
    @DisplayName("scheduleTicket()")
    class ScheduleTicket {
        @Test @DisplayName("trả về false khi closed state")
        void closed_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "CANCELLED", "TENANT"));
            assertThat(service.scheduleTicket(1, LocalDateTime.now())).isFalse();
        }

        @Test @DisplayName("trả về false khi senderRole là OPERATOR")
        void operatorSender_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "RECEIVED", "OPERATOR"));
            assertThat(service.scheduleTicket(1, LocalDateTime.now())).isFalse();
        }

        @Test @DisplayName("lên lịch thành công khi TENANT và RECEIVED")
        void success() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "RECEIVED", "TENANT"));
            when(mockRequestDAO.scheduleTicket(eq(1), any(LocalDateTime.class))).thenReturn(true);
            assertThat(service.scheduleTicket(1, LocalDateTime.now())).isTrue();
        }
    }

    // =========================================================
    // completeTicket()
    // =========================================================
    @Nested
    @DisplayName("completeTicket()")
    class CompleteTicket {
        @Test @DisplayName("trả về false khi OPERATOR gửi")
        void operatorSender_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "IN_PROGRESS", "OPERATOR"));
            assertThat(service.completeTicket(1, "ghi chú", null)).isFalse();
        }

        @Test @DisplayName("trả về false khi trạng thái RESOLVED (closed)")
        void resolved_returnsFalse() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "RESOLVED", "TENANT"));
            assertThat(service.completeTicket(1, "ghi chú", null)).isFalse();
        }
    }

    // =========================================================
    // getManagerTicketDetail()
    // =========================================================
    @Nested
    @DisplayName("getManagerTicketDetail()")
    class GetManagerTicketDetail {
        @Test @DisplayName("trả về null khi ticket không tồn tại")
        void notFound_returnsNull() throws Exception {
            when(mockRequestDAO.getManagerTicketDetail(99)).thenReturn(null);
            assertThat(service.getManagerTicketDetail(99, 1)).isNull();
        }

        @Test @DisplayName("ném AccessDeniedException khi managerId không khớp")
        void wrongManager_throws() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(99, "NEW", "TENANT"));
            assertThatThrownBy(() -> service.getManagerTicketDetail(1, 1))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }

    // =========================================================
    // rescheduleTicket()
    // =========================================================
    @Nested
    @DisplayName("rescheduleTicket()")
    class RescheduleTicket {
        @Test @DisplayName("ném IllegalStateException khi senderRole là OPERATOR")
        void operatorSender_throws() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "IN_PROGRESS", "OPERATOR"));
            assertThatThrownBy(() -> service.rescheduleTicket(1, LocalDateTime.now(), "lý do", 1, "127.0.0.1"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Operator");
        }

        @Test @DisplayName("ném AccessDeniedException khi manager không khớp")
        void wrongManager_throws() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(99, "IN_PROGRESS", "TENANT"));
            assertThatThrownBy(() -> service.rescheduleTicket(1, LocalDateTime.now(), "lý do", 1, "127.0.0.1"))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test @DisplayName("ném IllegalStateException khi trạng thái không phải IN_PROGRESS")
        void notInProgress_throws() {
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(ticket(1, "RECEIVED", "TENANT"));
            assertThatThrownBy(() -> service.rescheduleTicket(1, LocalDateTime.now(), "lý do", 1, "127.0.0.1"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Đang xử lý");
        }

        @Test @DisplayName("dời lịch thành công → ghi audit log")
        void success_logsAudit() throws Exception {
            Map<String, Object> t = ticket(1, "IN_PROGRESS", "TENANT");
            t.put("appointSchedule", "");
            when(mockRequestDAO.getManagerTicketDetail(1)).thenReturn(t);
            when(mockRequestDAO.rescheduleTicket(eq(1), any())).thenReturn(true);

            boolean result = service.rescheduleTicket(1, LocalDateTime.now().plusDays(1), "lý do", 1, "127.0.0.1");

            assertThat(result).isTrue();
            verify(mockAuditLogDAO).logWithComment(eq("requests"), eq(1), eq("RESCHEDULE"),
                    any(), any(), eq("127.0.0.1"), eq(1), eq("lý do"));
        }
    }

    // =========================================================
    // getManagerTickets() — offset calculation
    // =========================================================
    @Test
    @DisplayName("getManagerTickets: offset = (page-1) × pageSize")
    void managerTickets_correctOffset() {
        when(mockRequestDAO.getManagerTickets(1, "", "", "", 20, 10))
                .thenReturn(java.util.List.of());
        service.getManagerTickets(1, "", "", "", 3, 10);
        verify(mockRequestDAO).getManagerTickets(1, "", "", "", 20, 10);
    }
}
