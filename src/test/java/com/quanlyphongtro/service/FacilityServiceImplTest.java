package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.service.impl.FacilityServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FacilityServiceImpl — Admin quản lý cơ sở")
class FacilityServiceImplTest {

    private FacilityDAO mockFacilityDAO;
    private RoomDAO     mockRoomDAO;
    private FacilityServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockFacilityDAO = mock(FacilityDAO.class);
        mockRoomDAO     = mock(RoomDAO.class);
        service = new FacilityServiceImpl();
        Field ff = FacilityServiceImpl.class.getDeclaredField("facilityDAO");
        ff.setAccessible(true); ff.set(service, mockFacilityDAO);
        Field rf = FacilityServiceImpl.class.getDeclaredField("roomDAO");
        rf.setAccessible(true); rf.set(service, mockRoomDAO);
    }

    private Facility fac(int id, String status) {
        Facility f = new Facility();
        f.setId(id); f.setCode("HL"); f.setName("Nhà trọ A");
        f.setAddress("123 ABC"); f.setStatus(status);
        f.setFloorCount(3); f.setRoomsPerFloor(10);
        return f;
    }

    // =========================================================
    // create() — validation
    // =========================================================
    @Nested
    @DisplayName("create() — validation")
    class CreateValidation {

        @Test @DisplayName("ném ValidationException khi code trống")
        void blankCode() {
            assertThatThrownBy(() -> service.create("", "Tên", "Địa chỉ", "3", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Mã cơ sở");
        }

        @Test @DisplayName("ném ValidationException khi name trống")
        void blankName() {
            assertThatThrownBy(() -> service.create("HL", "", "Địa chỉ", "3", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Tên cơ sở");
        }

        @Test @DisplayName("ném ValidationException khi address trống")
        void blankAddress() {
            assertThatThrownBy(() -> service.create("HL", "Tên", "", "3", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Địa chỉ");
        }

        @Test @DisplayName("ném ValidationException khi code có số (không phải chữ cái)")
        void codeWithNumbers() {
            assertThatThrownBy(() -> service.create("HL1", "Tên", "Địa chỉ", "3", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("chữ cái");
        }

        @Test @DisplayName("ném ValidationException khi code quá ngắn (1 ký tự)")
        void codeTooShort() {
            assertThatThrownBy(() -> service.create("H", "Tên", "Địa chỉ", "3", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class);
        }

        @Test @DisplayName("ném ValidationException khi số tầng = 0")
        void zeroFloors() {
            assertThatThrownBy(() -> service.create("HL", "Tên", "Địa chỉ", "0", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tầng");
        }

        @Test @DisplayName("ném ValidationException khi số tầng > 99")
        void tooManyFloors() {
            assertThatThrownBy(() -> service.create("HL", "Tên", "Địa chỉ", "100", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tầng");
        }

        @Test @DisplayName("ném ValidationException khi số phòng/tầng không hợp lệ")
        void invalidRoomsPerFloor() {
            assertThatThrownBy(() -> service.create("HL", "Tên", "Địa chỉ", "3", "abc",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("phòng");
        }

        @Test @DisplayName("ném ValidationException khi mã cơ sở đã tồn tại")
        void duplicateCode() {
            when(mockFacilityDAO.countByCode("HL", null)).thenReturn(1);
            assertThatThrownBy(() -> service.create("HL", "Tên", "Địa chỉ", "3", "10",
                    "0", "0", "0", "0"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("đã tồn tại");
        }
    }

    // =========================================================
    // deactivate()
    // =========================================================
    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test @DisplayName("ném NotFoundException khi cơ sở không tồn tại")
        void notFound() {
            when(mockFacilityDAO.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.deactivate(99))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test @DisplayName("ném ValidationException khi cơ sở không phải ACTIVE")
        void notActive() {
            when(mockFacilityDAO.findById(1)).thenReturn(Optional.of(fac(1, "DRAFT")));
            assertThatThrownBy(() -> service.deactivate(1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("ACTIVE");
        }

        @Test @DisplayName("ném ValidationException khi còn phòng đang được thuê")
        void hasOccupiedRooms() {
            when(mockFacilityDAO.findById(2)).thenReturn(Optional.of(fac(2, "ACTIVE")));
            when(mockFacilityDAO.countOccupiedRooms(2)).thenReturn(3);
            assertThatThrownBy(() -> service.deactivate(2))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("3");
        }

        @Test @DisplayName("vô hiệu hóa thành công khi không có phòng đang thuê")
        void success() throws Exception {
            when(mockFacilityDAO.findById(3)).thenReturn(Optional.of(fac(3, "ACTIVE")));
            when(mockFacilityDAO.countOccupiedRooms(3)).thenReturn(0);
            service.deactivate(3);
            verify(mockFacilityDAO).updateStatus(3, "INACTIVE");
            verify(mockFacilityDAO).deactivateAllRooms(3);
        }
    }

    // =========================================================
    // activate()
    // =========================================================
    @Nested
    @DisplayName("activate()")
    class Activate {

        @Test @DisplayName("ném NotFoundException khi cơ sở không tồn tại")
        void notFound() {
            when(mockFacilityDAO.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.activate(99))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test @DisplayName("ném ValidationException khi cơ sở không phải DRAFT")
        void notDraft() {
            when(mockFacilityDAO.findById(1)).thenReturn(Optional.of(fac(1, "ACTIVE")));
            assertThatThrownBy(() -> service.activate(1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("DRAFT");
        }
    }

    // =========================================================
    // getById()
    // =========================================================
    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test @DisplayName("tìm thấy cơ sở khi tồn tại")
        void found() throws Exception {
            when(mockFacilityDAO.findById(1)).thenReturn(Optional.of(fac(1, "ACTIVE")));
            Facility result = service.getById(1);
            assertThat(result.getId()).isEqualTo(1);
        }

        @Test @DisplayName("ném NotFoundException khi không tìm thấy")
        void notFound() {
            when(mockFacilityDAO.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.getById(99))
                    .isInstanceOf(NotFoundException.class);
        }
    }

    // =========================================================
    // updateRoomArea()
    // =========================================================
    @Nested
    @DisplayName("updateRoomArea()")
    class UpdateRoomArea {

        @Test @DisplayName("ném NotFoundException khi cơ sở không tồn tại")
        void facilityNotFound() {
            when(mockFacilityDAO.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.updateRoomArea(99, 1, "30"))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test @DisplayName("ném ValidationException khi cơ sở INACTIVE")
        void inactiveFacility() {
            when(mockFacilityDAO.findById(1)).thenReturn(Optional.of(fac(1, "INACTIVE")));
            assertThatThrownBy(() -> service.updateRoomArea(1, 1, "30"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("vô hiệu hóa");
        }

        @Test @DisplayName("ném ValidationException khi diện tích âm")
        void negativeArea() {
            when(mockFacilityDAO.findById(1)).thenReturn(Optional.of(fac(1, "ACTIVE")));
            assertThatThrownBy(() -> service.updateRoomArea(1, 1, "-5"))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("âm");
        }
    }
}
