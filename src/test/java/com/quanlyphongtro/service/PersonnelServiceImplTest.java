package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.FacilityDAO;
import com.quanlyphongtro.dao.PersonnelDAO;
import com.quanlyphongtro.exception.NotFoundException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.model.Facility;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.impl.PersonnelServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test cho PersonnelServiceImpl — tất cả nghiệp vụ admin quản lý nhân sự.
 *
 * PersonnelServiceImpl khởi tạo DAO qua `new` (không có constructor injection),
 * nên dùng reflection để inject mock vào private final field.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PersonnelServiceImpl — Admin quản lý nhân sự")
class PersonnelServiceImplTest {

    private PersonnelDAO mockPersonnelDAO;
    private FacilityDAO  mockFacilityDAO;
    private PersonnelServiceImpl service;

    // ── Các hằng số dùng chung trong test ────────────────────────────────
    private static final String VALID_EMAIL    = "manager@hostel.vn";
    private static final String VALID_PHONE    = "0912345678";
    private static final String VALID_IDENTITY = "012345678901"; // 12 chữ số
    private static final String VALID_NAME     = "Nguyễn Văn B";
    private static final String LOGIN_LINK     = "http://localhost:8080/hostel/login";

    @BeforeEach
    void setUp() throws Exception {
        mockPersonnelDAO = mock(PersonnelDAO.class);
        mockFacilityDAO  = mock(FacilityDAO.class);
        service = new PersonnelServiceImpl();

        // Inject mock vào private final field qua reflection
        injectField(service, "personnelDAO", mockPersonnelDAO);
        injectField(service, "facilityDAO",  mockFacilityDAO);
    }

    /** Tiện ích inject mock vào private field. */
    private static void injectField(Object target, String fieldName, Object mock)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = target.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(target, mock);
    }

    /** Tạo User mẫu đơn giản. */
    private User buildUser(int id, String status, String role) {
        User u = new User();
        u.setId(id);
        u.setFullName("Nhân sự " + id);
        u.setEmail("user" + id + "@hostel.vn");
        u.setPhone("090000000" + id);
        u.setRole(role);
        u.setStatus(status);
        u.setIdentityNumber("01234567890" + id);
        return u;
    }

    // =========================================================
    // toggleStatus()
    // =========================================================
    @Nested
    @DisplayName("toggleStatus()")
    class ToggleStatus {

        @Test
        @DisplayName("ném ValidationException khi tự toggle status của chính mình")
        void selfToggle_throwsValidationException() {
            assertThatThrownBy(() -> service.toggleStatus(5, 5))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("chính mình");
        }

        @Test
        @DisplayName("ném NotFoundException khi user không tồn tại")
        void userNotFound_throwsNotFoundException() {
            when(mockPersonnelDAO.findById(99)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.toggleStatus(99, 1))
                    .isInstanceOf(NotFoundException.class);
        }

        @Test
        @DisplayName("chuyển ACTIVE → INACTIVE thành công")
        void activeUser_toggesToInactive() throws Exception {
            User active = buildUser(10, "ACTIVE", "MANAGER");
            when(mockPersonnelDAO.findById(10)).thenReturn(Optional.of(active));

            service.toggleStatus(10, 1);

            verify(mockPersonnelDAO).updateStatus(10, "INACTIVE");
        }

        @Test
        @DisplayName("chuyển INACTIVE → ACTIVE thành công")
        void inactiveUser_togglesToActive() throws Exception {
            User inactive = buildUser(11, "INACTIVE", "OPERATOR");
            when(mockPersonnelDAO.findById(11)).thenReturn(Optional.of(inactive));

            service.toggleStatus(11, 1);

            verify(mockPersonnelDAO).updateStatus(11, "ACTIVE");
        }
    }



    // =========================================================
    // create() — validation
    // =========================================================
    @Nested
    @DisplayName("create() — validation đầu vào")
    class CreateValidation {

        @Test
        @DisplayName("ném ValidationException khi fullName trống")
        void blankFullName_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    "", VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Họ tên");
        }

        @Test
        @DisplayName("ném ValidationException khi email trống")
        void blankEmail_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, "", VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Email");
        }

        @Test
        @DisplayName("ném ValidationException khi email sai định dạng")
        void invalidEmailFormat_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, "not-an-email", VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("định dạng");
        }

        @Test
        @DisplayName("ném ValidationException khi phone trống")
        void blankPhone_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, "", "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("điện thoại");
        }

        @Test
        @DisplayName("ném ValidationException khi phone không phải số VN")
        void invalidVnPhone_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, "12345", "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("điện thoại");
        }

        @Test
        @DisplayName("ném ValidationException khi role là ADMIN")
        void adminRole_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "ADMIN",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("ADMIN");
        }

        @Test
        @DisplayName("ném ValidationException khi role không hợp lệ (TENANT)")
        void invalidRole_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "TENANT",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Vai trò");
        }

        @Test
        @DisplayName("ném ValidationException khi identityNumber trống")
        void blankIdentityNumber_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    "", null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("CMND");
        }

        @Test
        @DisplayName("ném ValidationException khi identityNumber sai định dạng (8 chữ số)")
        void invalidIdentityNumber_throwsValidationException() {
            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    "12345678", null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("CMND");
        }
    }

    // =========================================================
    // create() — uniqueness checks
    // =========================================================
    @Nested
    @DisplayName("create() — kiểm tra trùng lặp dữ liệu")
    class CreateUniqueness {

        @Test
        @DisplayName("ném ValidationException khi email đã tồn tại")
        void duplicateEmail_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(true);

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Email");
        }

        @Test
        @DisplayName("ném ValidationException khi phone đã tồn tại")
        void duplicatePhone_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByPhone(VALID_PHONE, null)).thenReturn(true);

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("điện thoại");
        }

        @Test
        @DisplayName("ném ValidationException khi CMND/CCCD đã tồn tại")
        void duplicateIdentityNumber_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByPhone(VALID_PHONE, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByIdentityNumber(VALID_IDENTITY, null)).thenReturn(true);

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, null, 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("CMND");
        }
    }

    // =========================================================
    // create() — facility validation
    // =========================================================
    @Nested
    @DisplayName("create() — kiểm tra cơ sở")
    class CreateFacilityValidation {

        @Test
        @DisplayName("ném ValidationException khi cơ sở không tồn tại")
        void facilityNotFound_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByPhone(VALID_PHONE, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByIdentityNumber(VALID_IDENTITY, null)).thenReturn(false);
            when(mockFacilityDAO.findById(5)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, "5", 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("tồn tại");
        }

        @Test
        @DisplayName("ném ValidationException khi cơ sở không ACTIVE")
        void inactiveFacility_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByPhone(VALID_PHONE, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByIdentityNumber(VALID_IDENTITY, null)).thenReturn(false);

            Facility inactiveFac = new Facility();
            inactiveFac.setId(5);
            inactiveFac.setStatus("INACTIVE");
            when(mockFacilityDAO.findById(5)).thenReturn(Optional.of(inactiveFac));

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, "5", 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("ACTIVE");
        }

        @Test
        @DisplayName("ném ValidationException khi cơ sở đã có MANAGER")
        void facilityAlreadyHasManager_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByPhone(VALID_PHONE, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByIdentityNumber(VALID_IDENTITY, null)).thenReturn(false);

            Facility activeFac = new Facility();
            activeFac.setId(5);
            activeFac.setStatus("ACTIVE");
            when(mockFacilityDAO.findById(5)).thenReturn(Optional.of(activeFac));
            when(mockPersonnelDAO.countActiveManagerForFacility(5, null)).thenReturn(1);

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "MANAGER",
                    VALID_IDENTITY, null, null, null, "5", 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Ban Quản Lý");
        }

        @Test
        @DisplayName("ném ValidationException khi cơ sở đã có OPERATOR")
        void facilityAlreadyHasOperator_throwsValidationException() {
            when(mockPersonnelDAO.existsByEmail(VALID_EMAIL, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByPhone(VALID_PHONE, null)).thenReturn(false);
            when(mockPersonnelDAO.existsByIdentityNumber(VALID_IDENTITY, null)).thenReturn(false);

            Facility activeFac = new Facility();
            activeFac.setId(6);
            activeFac.setStatus("ACTIVE");
            when(mockFacilityDAO.findById(6)).thenReturn(Optional.of(activeFac));
            when(mockPersonnelDAO.countActiveOperatorForFacility(6, null)).thenReturn(1);

            assertThatThrownBy(() -> service.create(
                    VALID_NAME, VALID_EMAIL, VALID_PHONE, "OPERATOR",
                    VALID_IDENTITY, null, null, null, "6", 1, LOGIN_LINK))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("vận hành");
        }
    }

    // =========================================================
    // getById()
    // =========================================================
    @Nested
    @DisplayName("getById()")
    class GetById {

        @Test
        @DisplayName("trả về user kèm facilityNames khi tồn tại")
        void existingUser_returnsUserWithFacilityNames() throws Exception {
            User u = buildUser(20, "ACTIVE", "MANAGER");
            when(mockPersonnelDAO.findById(20)).thenReturn(Optional.of(u));
            when(mockPersonnelDAO.findFacilityNamesForUser(20))
                    .thenReturn(List.of("Cơ sở A", "Cơ sở B"));

            User result = service.getById(20);

            assertThat(result.getId()).isEqualTo(20);
            assertThat(result.getFacilityNames()).containsExactly("Cơ sở A", "Cơ sở B");
        }

        @Test
        @DisplayName("ném NotFoundException khi user không tồn tại")
        void notFound_throwsNotFoundException() {
            when(mockPersonnelDAO.findById(999)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> service.getById(999))
                    .isInstanceOf(NotFoundException.class);
        }
    }
}
