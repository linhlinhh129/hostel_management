package com.quanlyphongtro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Facility Model")
class FacilityModelTest {

    private Facility fac(String status, int floors, int roomsPerFloor) {
        Facility f = new Facility();
        f.setCode("HL");
        f.setName("Nhà trọ Hoàng Long");
        f.setStatus(status);
        f.setFloorCount(floors);
        f.setRoomsPerFloor(roomsPerFloor);
        return f;
    }

    // =========================================================
    // getTotalRooms()
    // =========================================================
    @Nested
    @DisplayName("getTotalRooms()")
    class TotalRooms {
        @Test
        @DisplayName("trả về 0 khi status là DRAFT")
        void draft_returnsZero() {
            assertThat(fac("DRAFT", 3, 10).getTotalRooms()).isEqualTo(0);
        }

        @Test
        @DisplayName("trả về floors × roomsPerFloor khi ACTIVE")
        void active_returnsProduct() {
            assertThat(fac("ACTIVE", 3, 10).getTotalRooms()).isEqualTo(30);
        }

        @Test
        @DisplayName("trả về 0 khi floorCount null")
        void nullFloors_returnsZero() {
            Facility f = new Facility();
            f.setStatus("ACTIVE");
            f.setFloorCount(null);
            f.setRoomsPerFloor(10);
            assertThat(f.getTotalRooms()).isEqualTo(0);
        }

        @Test
        @DisplayName("trả về 0 khi roomsPerFloor null")
        void nullRooms_returnsZero() {
            Facility f = new Facility();
            f.setStatus("ACTIVE");
            f.setFloorCount(3);
            f.setRoomsPerFloor(null);
            assertThat(f.getTotalRooms()).isEqualTo(0);
        }
    }

    // =========================================================
    // isDeleted()
    // =========================================================
    @Nested
    @DisplayName("isDeleted()")
    class IsDeleted {
        @Test void null_returnsFalse() {
            Facility f = fac("ACTIVE", 1, 1);
            f.setDeletedAt(null);
            assertThat(f.isDeleted()).isFalse();
        }

        @Test void set_returnsTrue() {
            Facility f = fac("ACTIVE", 1, 1);
            f.setDeletedAt(LocalDateTime.now());
            assertThat(f.isDeleted()).isTrue();
        }
    }

    // =========================================================
    // getId() / getFacilityId() alias
    // =========================================================
    @Nested
    @DisplayName("getId() / getFacilityId() alias")
    class IdAlias {
        @Test void setId_getFacilityId_consistent() {
            Facility f = new Facility();
            f.setId(5);
            assertThat(f.getFacilityId()).isEqualTo(5);
        }

        @Test void setFacilityId_getId_consistent() {
            Facility f = new Facility();
            f.setFacilityId(9);
            assertThat(f.getId()).isEqualTo(9);
        }
    }
}
