package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.ContractDAO;
import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.impl.ContractServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ContractServiceImpl — quản lý hợp đồng")
class ContractServiceImplTest {

    private ContractDAO mockContractDAO;
    private RoomDAO     mockRoomDAO;
    private ContractServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockContractDAO = mock(ContractDAO.class);
        mockRoomDAO     = mock(RoomDAO.class);
        service = new ContractServiceImpl();
        inject("contractDAO", mockContractDAO);
        inject("roomDAO",     mockRoomDAO);
    }

    private void inject(String name, Object mock) throws Exception {
        Field f = ContractServiceImpl.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(service, mock);
    }

    /** Contract hợp lệ đầy đủ trường bắt buộc. */
    private Contract validContract(int roomId) {
        Contract c = new Contract();
        c.setRoomId(roomId);
        c.setTenantFullName("Nguyễn Văn A");
        c.setTenantIdentityNumber("012345678901");
        c.setSignedDate(LocalDate.now());
        c.setStartDate(LocalDate.now());
        c.setEndDate(LocalDate.now().plusMonths(12));
        return c;
    }

    private Room room(int id, String code) {
        Room r = new Room(); r.setId(id); r.setCode(code); r.setStatus("AVAILABLE");
        return r;
    }

    private Facility facility(int managerId) {
        Facility f = new Facility(); f.setId(1); f.setManagerId(managerId);
        return f;
    }

    // =========================================================
    // createContract() — validation
    // =========================================================
    @Nested
    @DisplayName("createContract() — validation")
    class CreateContractValidation {

        @Test @DisplayName("ném Exception khi phòng không tồn tại")
        void roomNotFound() {
            when(mockRoomDAO.findById(99)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.createContract(validContract(99), 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Phòng không tồn tại");
        }

        @Test @DisplayName("ném Exception khi manager không quản lý cơ sở chứa phòng")
        void wrongManager() {
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(room(1, "HL0101")));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.of(facility(99)));
            assertThatThrownBy(() -> service.createContract(validContract(1), 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("quyền");
        }

        @Test @DisplayName("ném Exception khi cơ sở không tồn tại")
        void facilityNotFound() {
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(room(1, "HL0101")));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.empty());
            assertThatThrownBy(() -> service.createContract(validContract(1), 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("quyền");
        }

        @Test @DisplayName("ném Exception khi phòng đã có hợp đồng ACTIVE")
        void activeContractExists() {
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(room(1, "HL0101")));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.of(facility(1)));
            when(mockContractDAO.findActiveContractByRoomId(1)).thenReturn(Optional.of(new Contract()));
            assertThatThrownBy(() -> service.createContract(validContract(1), 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("ACTIVE");
        }

        @Test @DisplayName("ném Exception khi tenantFullName trống")
        void blankTenantName() {
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(room(1, "HL0101")));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.of(facility(1)));
            when(mockContractDAO.findActiveContractByRoomId(1)).thenReturn(Optional.empty());

            Contract c = validContract(1);
            c.setTenantFullName("");
            assertThatThrownBy(() -> service.createContract(c, 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Tên người thuê");
        }

        @Test @DisplayName("ném Exception khi tenantIdentityNumber trống")
        void blankIdentity() {
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(room(1, "HL0101")));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.of(facility(1)));
            when(mockContractDAO.findActiveContractByRoomId(1)).thenReturn(Optional.empty());

            Contract c = validContract(1);
            c.setTenantIdentityNumber("");
            assertThatThrownBy(() -> service.createContract(c, 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("CCCD");
        }

        @Test @DisplayName("ném Exception khi signedDate null")
        void nullSignedDate() {
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(room(1, "HL0101")));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.of(facility(1)));
            when(mockContractDAO.findActiveContractByRoomId(1)).thenReturn(Optional.empty());

            Contract c = validContract(1);
            c.setSignedDate(null);
            assertThatThrownBy(() -> service.createContract(c, 1))
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("Ngày");
        }

        @Test @DisplayName("tạo hợp đồng thành công — code bắt đầu bằng HD-")
        void successfulCreate() throws Exception {
            Room r = room(1, "HL0101");
            when(mockRoomDAO.findById(1)).thenReturn(Optional.of(r));
            when(mockRoomDAO.findFacilityByRoomId(1)).thenReturn(Optional.of(facility(1)));
            when(mockContractDAO.findActiveContractByRoomId(1)).thenReturn(Optional.empty());
            when(mockContractDAO.create(any(Contract.class))).thenReturn(10);

            Contract c = validContract(1);
            service.createContract(c, 1);

            assertThat(c.getCode()).startsWith("HD-HL0101-");
            assertThat(c.getStatus()).isEqualTo("ACTIVE");
            verify(mockRoomDAO).update(r);
        }
    }

    // =========================================================
    // getContractForAddTenant()
    // =========================================================
    @Nested
    @DisplayName("getContractForAddTenant()")
    class GetContractForAddTenant {

        @Test @DisplayName("trả về null khi hợp đồng không tìm thấy")
        void notFound_returnsNull() throws Exception {
            when(mockContractDAO.getContractForAddTenant(99, 1)).thenReturn(null);
            assertThat(service.getContractForAddTenant(99, 1)).isNull();
        }

        @Test @DisplayName("ném IllegalStateException khi hợp đồng đã có tenant")
        void alreadyHasTenant_throws() {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("tenantId", 5);
            when(mockContractDAO.getContractForAddTenant(1, 1)).thenReturn(data);
            assertThatThrownBy(() -> service.getContractForAddTenant(1, 1))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("tài khoản");
        }
    }

    // =========================================================
    // addTenantFromContract() — validation chain
    // =========================================================
    @Nested
    @DisplayName("addTenantFromContract() — validation")
    class AddTenantValidation {

        @Test @DisplayName("ném IllegalArgumentException khi fullName trống")
        void blankFullName() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "", "0901234567", "a@b.com", "012345678901",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Họ tên");
        }

        @Test @DisplayName("ném IllegalArgumentException khi email trống")
        void blankEmail() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "Tên", "0901234567", "", "012345678901",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Email");
        }

        @Test @DisplayName("ném IllegalArgumentException khi email sai định dạng")
        void invalidEmail() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "Tên", "0901234567", "not-email", "012345678901",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("định dạng");
        }

        @Test @DisplayName("ném IllegalArgumentException khi phone trống")
        void blankPhone() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "Tên", "", "a@b.com", "012345678901",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("điện thoại");
        }

        @Test @DisplayName("ném IllegalArgumentException khi phone không hợp lệ")
        void invalidPhone() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "Tên", "12345", "a@b.com", "012345678901",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("điện thoại");
        }

        @Test @DisplayName("ném IllegalArgumentException khi identityNumber trống")
        void blankIdentity() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "Tên", "0901234567", "a@b.com", "",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CMND");
        }

        @Test @DisplayName("ném IllegalArgumentException khi CCCD sai định dạng")
        void invalidIdentity() {
            assertThatThrownBy(() -> service.addTenantFromContract(
                    1, 1, "Tên", "0901234567", "a@b.com", "1234",
                    null, null, null, null, false, 1, "http://link"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("CMND");
        }
    }

    // =========================================================
    // softDeleteContract()
    // =========================================================
    @Test @DisplayName("softDeleteContract delegates to DAO")
    void softDelete_delegates() {
        when(mockContractDAO.softDeleteContract(5)).thenReturn(true);
        assertThat(service.softDeleteContract(5)).isTrue();
        verify(mockContractDAO).softDeleteContract(5);
    }
}
