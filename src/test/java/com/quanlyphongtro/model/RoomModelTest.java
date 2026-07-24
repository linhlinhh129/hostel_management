package com.quanlyphongtro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Room Model")
class RoomModelTest {

    private Room room(String code) {
        Room r = new Room();
        r.setCode(code);
        return r;
    }

    @Nested
    @DisplayName("getFloorLabel()")
    class FloorLabel {
        @ParameterizedTest(name = "code={0} → floor={1}")
        @CsvSource({"HL0103,01", "AB0201,02", "XY9910,99"})
        void extractsFloor(String code, String expected) {
            assertThat(room(code).getFloorLabel()).isEqualTo(expected);
        }
        @Test void shortCode_returnsEmpty() { assertThat(room("AB").getFloorLabel()).isEmpty(); }
        @Test void nullCode_returnsEmpty()  { assertThat(room(null).getFloorLabel()).isEmpty(); }
    }

    @Nested
    @DisplayName("getRoomLabel()")
    class RoomLabel {
        @ParameterizedTest(name = "code={0} → room={1}")
        @CsvSource({"HL0103,03", "AB0201,01", "XY9910,10"})
        void extractsRoom(String code, String expected) {
            assertThat(room(code).getRoomLabel()).isEqualTo(expected);
        }
        @Test void shortCode_returnsLast2() { assertThat(room("AB").getRoomLabel()).isEqualTo("AB"); }
        @Test void nullCode_returnsEmpty()  { assertThat(room(null).getRoomLabel()).isEmpty(); }
    }

    @Nested
    @DisplayName("isDeleted()")
    class IsDeleted {
        @Test void null_returnsFalse() {
            Room r = new Room();
            r.setDeletedAt(null);
            assertThat(r.isDeleted()).isFalse();
        }
        @Test void set_returnsTrue() {
            Room r = new Room();
            r.setDeletedAt(LocalDateTime.now());
            assertThat(r.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("getRoomId() / getId() alias")
    class IdAlias {
        @Test void setId_getRoomId_consistent() {
            Room r = new Room();
            r.setId(42);
            assertThat(r.getRoomId()).isEqualTo(42);
        }
        @Test void setRoomId_getId_consistent() {
            Room r = new Room();
            r.setRoomId(7);
            assertThat(r.getId()).isEqualTo(7);
        }
    }
}
