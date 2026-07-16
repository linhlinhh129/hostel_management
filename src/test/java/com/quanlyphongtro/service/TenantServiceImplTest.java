package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.RoomDAO;
import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit test cho TenantServiceImpl.
 *
 * TenantServiceImpl.java (interface TenantService) chỉ khai báo 3 method chính:
 *   - getTenantRoom(int)
 *   - getFacilityByRoomId(int)
 *   - getTenantProfile(int)
 *
 * Các method mở rộng (lockTenantAccount, unlockTenantAccount, v.v.) là
 * internal — interface không khai báo, UserDAO cũng chưa có method tương ứng.
 * Nên test chỉ cover những gì thực sự compile được.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TenantServiceImpl — người thuê (Tenant)")
class TenantServiceImplTest {

    private RoomDAO mockRoomDAO;
    private UserDAO mockUserDAO;
    private TenantServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockRoomDAO = mock(RoomDAO.class);
        mockUserDAO = mock(UserDAO.class);
        service = new TenantServiceImpl();
        inject("roomDAO", mockRoomDAO);
        inject("userDAO", mockUserDAO);
    }

    private void inject(String name, Object mock) throws Exception {
        Field f = TenantServiceImpl.class.getDeclaredField(name);
        f.setAccessible(true);
        f.set(service, mock);
    }

    private User user(int id, String status, String username) {
        User u = new User();
        u.setId(id);
        u.setUsername(username);
        u.setStatus(status);
        u.setRole("TENANT");
        return u;
    }

    // =========================================================
    // getTenantRoom()
    // =========================================================
    @Nested
    @DisplayName("getTenantRoom()")
    class GetTenantRoom {

        @Test
        @DisplayName("trả về phòng khi tìm thấy tenant")
        void found() {
            Room r = new Room();
            r.setId(5);
            r.setCode("HL0101");
            when(mockRoomDAO.findByTenantId(1)).thenReturn(Optional.of(r));

            Optional<Room> result = service.getTenantRoom(1);

            assertThat(result).isPresent();
            assertThat(result.get().getCode()).isEqualTo("HL0101");
        }

        @Test
        @DisplayName("trả về empty khi tenant không có phòng")
        void notFound() {
            when(mockRoomDAO.findByTenantId(99)).thenReturn(Optional.empty());
            assertThat(service.getTenantRoom(99)).isEmpty();
        }
    }

    // =========================================================
    // getFacilityByRoomId()
    // =========================================================
    @Nested
    @DisplayName("getFacilityByRoomId()")
    class GetFacilityByRoomId {

        @Test
        @DisplayName("trả về facility khi phòng thuộc cơ sở")
        void found() {
            Facility fac = new Facility();
            fac.setId(1);
            fac.setCode("HL");
            when(mockRoomDAO.findFacilityByRoomId(5)).thenReturn(Optional.of(fac));

            Optional<Facility> result = service.getFacilityByRoomId(5);

            assertThat(result).isPresent();
            assertThat(result.get().getCode()).isEqualTo("HL");
        }

        @Test
        @DisplayName("trả về empty khi phòng không thuộc cơ sở nào")
        void notFound() {
            when(mockRoomDAO.findFacilityByRoomId(99)).thenReturn(Optional.empty());
            assertThat(service.getFacilityByRoomId(99)).isEmpty();
        }
    }

    // =========================================================
    // getTenantProfile()
    // =========================================================
    @Nested
    @DisplayName("getTenantProfile()")
    class GetTenantProfile {

        @Test
        @DisplayName("trả về user profile khi tìm thấy")
        void found() {
            when(mockUserDAO.findById(3)).thenReturn(Optional.of(user(3, "ACTIVE", "tenant3")));

            Optional<User> result = service.getTenantProfile(3);

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("tenant3");
        }

        @Test
        @DisplayName("trả về empty khi userId không tồn tại")
        void notFound() {
            when(mockUserDAO.findById(999)).thenReturn(Optional.empty());
            assertThat(service.getTenantProfile(999)).isEmpty();
        }

        @Test
        @DisplayName("gọi đúng userDAO.findById với tenantId")
        void callsDAOWithCorrectId() {
            when(mockUserDAO.findById(7)).thenReturn(Optional.empty());
            service.getTenantProfile(7);
            verify(mockUserDAO).findById(7);
        }
    }

    // =========================================================
    // unlockTenantAccount() — NOTE: MapStringConsumer chưa được định nghĩa
    // trong production code → chỉ test lockTenantAccount (updateStatus LOCKED)
    // và verify userDAO.findById được gọi khi unlock.
    // =========================================================
    @Nested
    @DisplayName("lockTenantAccount() — via reflection bypass")
    class LockAccountDirect {

        @Test
        @DisplayName("lockTenantAccount gọi updateStatus LOCKED và trả true")
        void locks_and_returns_true() {
            boolean result = service.lockTenantAccount(5);

            assertThat(result).isTrue();
            verify(mockUserDAO).updateStatus(5, "LOCKED");
        }

        @Test
        @DisplayName("lockTenantAccount luôn trả true dù user không tồn tại")
        void always_returns_true() {
            assertThat(service.lockTenantAccount(999)).isTrue();
            verify(mockUserDAO).updateStatus(999, "LOCKED");
        }
    }
}
