package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dto.PageResult;
import com.quanlyphongtro.dto.RoomDTO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.impl.RoomServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoomServiceImpl — quản lý phòng")
class RoomServiceImplTest {

    private RoomDAO mockRoomDAO;
    private RoomServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockRoomDAO = mock(RoomDAO.class);
        service = new RoomServiceImpl();
        Field f = RoomServiceImpl.class.getDeclaredField("roomDAO");
        f.setAccessible(true);
        f.set(service, mockRoomDAO);
    }

    // ── helpers ───────────────────────────────────────────────────────────
    private Map<String, Object> roomMap(String facilityStatus) {
        Map<String, Object> m = new HashMap<>();
        m.put("facilityStatus", facilityStatus);
        m.put("id", 1);
        return m;
    }

    // =========================================================
    // getDetailForAdmin()
    // =========================================================
    @Nested
    @DisplayName("getDetailForAdmin()")
    class GetDetailForAdmin {
        @Test @DisplayName("trả về map khi tìm thấy phòng")
        void found() throws Exception {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("ACTIVE")));
            assertThat(service.getDetailForAdmin(1)).isNotNull();
        }

        @Test @DisplayName("ném NotFoundException khi không tìm thấy")
        void notFound() {
            when(mockRoomDAO.findDetailForAdmin(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.getDetailForAdmin(99))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // =========================================================
    // updateAreaAndFee()
    // =========================================================
    @Nested
    @DisplayName("updateAreaAndFee()")
    class UpdateAreaAndFee {

        @Test @DisplayName("ném NotFoundException khi phòng không tồn tại")
        void roomNotFound() {
            when(mockRoomDAO.findDetailForAdmin(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.updateAreaAndFee(99, "30", "3000000"))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test @DisplayName("ném ValidationException khi cơ sở INACTIVE")
        void inactiveFacility() {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("INACTIVE")));
            assertThatThrownBy(() -> service.updateAreaAndFee(1, "30", "3000000"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("vô hiệu hóa");
        }

        @Test @DisplayName("ném ValidationException khi diện tích âm")
        void negativeArea() {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("ACTIVE")));
            assertThatThrownBy(() -> service.updateAreaAndFee(1, "-5", "3000000"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("âm");
        }

        @Test @DisplayName("ném ValidationException khi diện tích không phải số")
        void invalidArea() {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("ACTIVE")));
            assertThatThrownBy(() -> service.updateAreaAndFee(1, "abc", "3000000"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Diện tích");
        }

        @Test @DisplayName("ném ValidationException khi giá phòng âm")
        void negativeFee() {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("ACTIVE")));
            assertThatThrownBy(() -> service.updateAreaAndFee(1, "30", "-1"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("âm");
        }

        @Test @DisplayName("cập nhật thành công khi diện tích và giá hợp lệ")
        void success() throws Exception {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("ACTIVE")));
            when(mockRoomDAO.updateAreaAndFee(1, new BigDecimal("30"), new BigDecimal("3000000")))
                    .thenReturn(true);
            service.updateAreaAndFee(1, "30", "3000000");
            verify(mockRoomDAO).updateAreaAndFee(1, new BigDecimal("30"), new BigDecimal("3000000"));
        }

        @Test @DisplayName("blank area → null (xóa diện tích)")
        void blankArea_setsNull() throws Exception {
            when(mockRoomDAO.findDetailForAdmin(1)).thenReturn(Optional.of(roomMap("ACTIVE")));
            when(mockRoomDAO.updateAreaAndFee(1, null, new BigDecimal("3000000"))).thenReturn(true);
            service.updateAreaAndFee(1, "", "3000000");
            verify(mockRoomDAO).updateAreaAndFee(1, null, new BigDecimal("3000000"));
        }
    }

    // =========================================================
    // getFacilityRoomsPage()
    // =========================================================
    @Nested
    @DisplayName("getFacilityRoomsPage()")
    class FacilityRoomsPage {

        @Test @DisplayName("totalPages = 1 khi không có phòng")
        void zeroTotal_returnsOnePage() {
            when(mockRoomDAO.countFacilityRooms(1, "")).thenReturn(0);
            when(mockRoomDAO.findFacilityRooms(1, "", 0, 10)).thenReturn(List.of());

            PageResult<RoomDTO> result = service.getFacilityRoomsPage(1, "", 1, 10);

            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getItems()).isEmpty();
        }

        @Test @DisplayName("totalPages = ceil(total / pageSize)")
        void calculatesPages() {
            when(mockRoomDAO.countFacilityRooms(1, "")).thenReturn(25);
            when(mockRoomDAO.findFacilityRooms(1, "", 0, 10)).thenReturn(List.of());

            PageResult<RoomDTO> result = service.getFacilityRoomsPage(1, "", 1, 10);

            assertThat(result.getTotalPages()).isEqualTo(3);
            assertThat(result.getTotal()).isEqualTo(25);
        }

        @Test @DisplayName("offset = (page-1) × pageSize")
        void correctOffset() {
            when(mockRoomDAO.countFacilityRooms(1, "")).thenReturn(30);
            when(mockRoomDAO.findFacilityRooms(1, "", 20, 10)).thenReturn(List.of());

            service.getFacilityRoomsPage(1, "", 3, 10);

            verify(mockRoomDAO).findFacilityRooms(1, "", 20, 10);
        }
    }

    // =========================================================
    // getRoomDetail()
    // =========================================================
    @Nested
    @DisplayName("getRoomDetail()")
    class GetRoomDetail {

        @Test @DisplayName("trả về null khi phòng không tồn tại")
        void notFound_returnsNull() throws Exception {
            when(mockRoomDAO.findRoomDetail(99)).thenReturn(null);
            assertThat(service.getRoomDetail(99, 1)).isNull();
        }

        @Test @DisplayName("ném AccessDeniedException khi manager không phụ trách cơ sở")
        void wrongManager_throws() {
            Map<String, Object> raw = new HashMap<>();
            raw.put("managerId", 99);
            raw.put("id", 1); raw.put("facilityId", 1);
            raw.put("facilityCode", "HL"); raw.put("facilityName", "Nhà trọ");
            raw.put("code", "HL0101"); raw.put("status", "AVAILABLE");
            raw.put("createdAt", ""); raw.put("updatedAt", "");
            raw.put("floor", "01"); raw.put("roomNumber", "01");
            when(mockRoomDAO.findRoomDetail(1)).thenReturn(raw);

            assertThatThrownBy(() -> service.getRoomDetail(1, 1))
                    .isInstanceOf(AccessDeniedException.class);
        }
    }
}
