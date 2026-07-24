package com.quanlyphongtro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Request Model")
class RequestModelTest {

    private Request request;

    @BeforeEach
    void setUp() {
        request = new Request();
        request.setId(1);
        request.setCode("REQ-HL-001");
        request.setTitle("Sự cố điện tại phòng HL0101");
        request.setStatus("PENDING");
    }

    // =========================================================
    // getStatusLabel()
    // =========================================================
    @Nested
    @DisplayName("getStatusLabel()")
    class StatusLabel {

        @ParameterizedTest(name = "status={0} → label={1}")
        @CsvSource({
            "PENDING,Mới tạo",
            "ASSIGNED,Đã tiếp nhận",
            "IN_PROGRESS,Đang xử lý",
            "DONE,Hoàn thành",
            "REJECTED,Từ chối",
            "CANCELLED,Đã hủy"
        })
        void mapsCorrectly(String status, String expected) {
            request.setStatus(status);
            assertThat(request.getStatusLabel()).isEqualTo(expected);
        }

        @Test
        @DisplayName("null status → 'Mới tạo'")
        void nullStatus() {
            request.setStatus(null);
            assertThat(request.getStatusLabel()).isEqualTo("Mới tạo");
        }

        @Test
        @DisplayName("unknown status → returns status itself")
        void unknownStatus() {
            request.setStatus("UNKNOWN_STATUS");
            assertThat(request.getStatusLabel()).isEqualTo("UNKNOWN_STATUS");
        }
    }

    // =========================================================
    // getStatusBadgeClass()
    // =========================================================
    @Nested
    @DisplayName("getStatusBadgeClass()")
    class StatusBadgeClass {

        @Test void done_successBadge()          { request.setStatus("DONE");        assertThat(request.getStatusBadgeClass()).isEqualTo("badge-success"); }
        @Test void rejected_dangerBadge()        { request.setStatus("REJECTED");    assertThat(request.getStatusBadgeClass()).isEqualTo("badge-danger");  }
        @Test void cancelled_dangerBadge()       { request.setStatus("CANCELLED");   assertThat(request.getStatusBadgeClass()).isEqualTo("badge-danger");  }
        @Test void inProgress_warningBadge()     { request.setStatus("IN_PROGRESS"); assertThat(request.getStatusBadgeClass()).isEqualTo("badge-warning"); }
        @Test void assigned_infoBadge()          { request.setStatus("ASSIGNED");    assertThat(request.getStatusBadgeClass()).isEqualTo("badge-info");    }
        @Test void pending_infoBadge()           { request.setStatus("PENDING");     assertThat(request.getStatusBadgeClass()).isEqualTo("badge-info");    }
    }

    // =========================================================
    // getTypeLabel()
    // =========================================================
    @Nested
    @DisplayName("getTypeLabel()")
    class TypeLabel {

        @ParameterizedTest(name = "category={0} → contains {1}")
        @CsvSource({
            "ELECTRIC,Điện",
            "WATER,Nước",
            "INTERNET,Internet",
            "INFRASTRUCTURE,Cơ sở"
        })
        void knownCategories(String category, String expectedSubstr) {
            request.setCategory(category);
            assertThat(request.getTypeLabel()).contains(expectedSubstr);
        }

        @Test void nullCategory_returnsKhac() {
            request.setCategory(null);
            assertThat(request.getTypeLabel()).isEqualTo("Khác");
        }

        @Test void unknownCategory_returnsKhac() {
            request.setCategory("CLEANING");
            assertThat(request.getTypeLabel()).contains("Khác");
        }
    }

    // =========================================================
    // getImages() — parses comma-separated URLs
    // =========================================================
    @Nested
    @DisplayName("getImages()")
    class GetImages {

        @Test
        @DisplayName("null attachments → empty list")
        void nullAttachments() {
            assertThat(request.getImages()).isEmpty();
        }

        @Test
        @DisplayName("single URL in attachmentUrls1")
        void singleUrl() {
            request.setAttachmentUrls1("http://img1.jpg");
            assertThat(request.getImages()).containsExactly("http://img1.jpg");
        }

        @Test
        @DisplayName("comma-separated URLs merged from both fields")
        void multipleUrls() {
            request.setAttachmentUrls1("http://img1.jpg,http://img2.jpg");
            request.setAttachmentUrls2("http://img3.jpg");
            List<String> images = request.getImages();
            assertThat(images).hasSize(3)
                    .contains("http://img1.jpg", "http://img2.jpg", "http://img3.jpg");
        }

        @Test
        @DisplayName("empty strings are skipped")
        void emptyStringsSkipped() {
            request.setAttachmentUrls1(" , ,http://img1.jpg, ");
            assertThat(request.getImages()).containsExactly("http://img1.jpg");
        }
    }

    // =========================================================
    // getRequestId() / getId() alias
    // =========================================================
    @Test
    @DisplayName("getRequestId() và getId() trỏ cùng field")
    void idAlias() {
        request.setId(10);
        assertThat(request.getRequestId()).isEqualTo(10);
        request.setRequestId(20);
        assertThat(request.getId()).isEqualTo(20);
    }

    // =========================================================
    // getAppointScheduleForInput()
    // =========================================================
    @Test
    @DisplayName("getAppointScheduleForInput trả về chuỗi rỗng khi null")
    void nullSchedule_emptyString() {
        request.setAppointSchedule(null);
        assertThat(request.getAppointScheduleForInput()).isEmpty();
    }

    @Test
    @DisplayName("getAppointScheduleForInput format yyyy-MM-dd'T'HH:mm")
    void formatsScheduleForInput() {
        request.setAppointSchedule(LocalDateTime.of(2025, 6, 15, 14, 30));
        assertThat(request.getAppointScheduleForInput()).isEqualTo("2025-06-15T14:30");
    }
}
