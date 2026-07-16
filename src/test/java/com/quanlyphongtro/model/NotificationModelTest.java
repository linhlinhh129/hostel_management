package com.quanlyphongtro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Notification Model")
class NotificationModelTest {

    private Notification n;

    @BeforeEach
    void setUp() {
        n = new Notification();
        n.setId(1);
        n.setTitle("Thông báo hệ thống");
        n.setContent("Nội dung thông báo thử nghiệm.");
        n.setTargetType("ALL");
        n.setStatus("SENT");
    }

    // =========================================================
    // getRecipientId()
    // =========================================================
    @Nested
    @DisplayName("getRecipientId()")
    class RecipientId {
        @Test void facility_returnsFacilityId() {
            n.setTargetType("FACILITY"); n.setFacilityId(5);
            assertThat(n.getRecipientId()).isEqualTo(5);
        }
        @Test void room_returnsRoomId() {
            n.setTargetType("ROOM"); n.setRoomId(7);
            assertThat(n.getRecipientId()).isEqualTo(7);
        }
        @Test void all_returnsNull() {
            n.setTargetType("ALL");
            assertThat(n.getRecipientId()).isNull();
        }
    }

    // =========================================================
    // getRecipientType() alias
    // =========================================================
    @Nested
    @DisplayName("getRecipientType() alias")
    class RecipientTypeAlias {
        @Test void getReturnsTargetType() {
            n.setTargetType("FACILITY");
            assertThat(n.getRecipientType()).isEqualTo("FACILITY");
        }
        @Test void setUpdatesTargetType() {
            n.setRecipientType("ROOM");
            assertThat(n.getTargetType()).isEqualTo("ROOM");
        }
    }

    // =========================================================
    // generateSummary()
    // =========================================================
    @Nested
    @DisplayName("generateSummary()")
    class GenerateSummary {
        @Test void shortContent_noEllipsis() {
            n.setContent("Ngắn");
            n.generateSummary();
            assertThat(n.getSummary()).isEqualTo("Ngắn");
        }
        @Test void exactly100_noEllipsis() {
            n.setContent("A".repeat(100));
            n.generateSummary();
            assertThat(n.getSummary()).hasSize(100).doesNotEndWith("...");
        }
        @Test void over100_truncatedWithEllipsis() {
            n.setContent("B".repeat(150));
            n.generateSummary();
            assertThat(n.getSummary()).endsWith("...").hasSize(103);
        }
        @Test void nullContent_emptyString() {
            n.setContent(null);
            n.generateSummary();
            assertThat(n.getSummary()).isEmpty();
        }
    }

    // =========================================================
    // getCreatedDateLabel() — prefers sentAt
    // =========================================================
    @Nested
    @DisplayName("getCreatedDateLabel()")
    class CreatedDateLabel {
        @Test void noSentNoCreated_returnsNA() {
            assertThat(n.getCreatedDateLabel()).isEqualTo("N/A");
        }
        @Test void withCreatedAt_formatsCorrectly() {
            n.setCreatedAt(LocalDateTime.of(2025, 6, 15, 10, 30, 0));
            assertThat(n.getCreatedDateLabel()).isEqualTo("15/06/2025 10:30:00");
        }
        @Test void prefersSentAt_overCreatedAt() {
            n.setCreatedAt(LocalDateTime.of(2025, 1, 1, 0, 0));
            n.setSentAt(LocalDateTime.of(2025, 6, 15, 10, 30, 0));
            assertThat(n.getCreatedDateLabel()).contains("15/06/2025");
        }
    }
}
