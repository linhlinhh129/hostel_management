package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.UserDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.User;
import com.quanlyphongtro.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

/**
 * Unit test cho UserServiceImpl.
 *
 * Strategy:
 *  - Mock UserDAO → không cần DB thật.
 *  - Kiểm tra từng nhánh logic của login() và getSessionById().
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("UserServiceImpl")
class UserServiceImplTest {

    @Mock
    private UserDAO userDAO;

    // Dùng constructor injection: UserServiceImpl(UserDAO userDAO)
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDAO);
    }

    // =========================================================
    // Helper tạo User mẫu
    // =========================================================
    private User buildActiveUser(String username, String role) {
        User u = new User();
        u.setId(1);
        u.setUsername(username);
        // BCrypt hash của "Abc@1234" — hash cố định để test
        u.setPasswordHash("$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy");
        u.setRole(role);
        u.setFullName("Nguyễn Văn A");
        u.setEmail("a@hostel.vn");
        u.setStatus("ACTIVE");
        u.setForceChangePass(false);
        return u;
    }

    private User buildLockedUser(String username) {
        User u = buildActiveUser(username, "TENANT");
        u.setStatus("LOCKED");
        return u;
    }

    private User buildDeletedUser(String username) {
        User u = buildActiveUser(username, "TENANT");
        u.setDeletedAt(java.time.LocalDateTime.now());
        return u;
    }

    // =========================================================
    // login()
    // =========================================================
    @Nested
    @DisplayName("login()")
    class LoginTests {

        @Test
        @DisplayName("trả về empty khi username null")
        void login_nullUsername_returnsEmpty() {
            Optional<UserSessionDTO> result = userService.login(null, "Abc@1234");
            assertThat(result).isEmpty();
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("trả về empty khi password null")
        void login_nullPassword_returnsEmpty() {
            Optional<UserSessionDTO> result = userService.login("admin", null);
            assertThat(result).isEmpty();
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("trả về empty khi password quá ngắn (< 7 ký tự)")
        void login_shortPassword_returnsEmpty() {
            Optional<UserSessionDTO> result = userService.login("admin", "abc12");
            assertThat(result).isEmpty();
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("trả về empty khi username trống")
        void login_blankUsername_returnsEmpty() {
            Optional<UserSessionDTO> result = userService.login("   ", "Abc@1234");
            assertThat(result).isEmpty();
            verifyNoInteractions(userDAO);
        }

        @Test
        @DisplayName("trả về empty khi username không tồn tại trong DB")
        void login_usernameNotFound_returnsEmpty() {
            when(userDAO.findByUsername("ghost")).thenReturn(Optional.empty());
            when(userDAO.findByEmail("ghost")).thenReturn(Optional.empty());

            Optional<UserSessionDTO> result = userService.login("ghost", "Abc@1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("ném ForbiddenException khi tài khoản bị LOCKED")
        void login_lockedAccount_throwsForbiddenException() {
            User locked = buildLockedUser("user_locked");
            when(userDAO.findByUsername("user_locked")).thenReturn(Optional.of(locked));

            assertThatThrownBy(() -> userService.login("user_locked", "Abc@1234"))
                    .isInstanceOf(com.quanlyphongtro.exception.ForbiddenException.class)
                    .hasMessageContaining("LOCKED");
        }

        @Test
        @DisplayName("trả về empty khi tài khoản đã bị xóa mềm")
        void login_deletedAccount_returnsEmpty() {
            User deleted = buildDeletedUser("user_deleted");
            when(userDAO.findByUsername("user_deleted")).thenReturn(Optional.of(deleted));

            Optional<UserSessionDTO> result = userService.login("user_deleted", "Abc@1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi tài khoản INACTIVE")
        void login_inactiveAccount_returnsEmpty() {
            User inactive = buildActiveUser("user_inactive", "TENANT");
            inactive.setStatus("INACTIVE");
            when(userDAO.findByUsername("user_inactive")).thenReturn(Optional.of(inactive));

            Optional<UserSessionDTO> result = userService.login("user_inactive", "Abc@1234");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi sai password")
        void login_wrongPassword_returnsEmpty() {
            User user = buildActiveUser("admin", "ADMIN");
            when(userDAO.findByUsername("admin")).thenReturn(Optional.of(user));

            // Truyền password sai (hash trên không khớp với "WrongPass!")
            Optional<UserSessionDTO> result = userService.login("admin", "WrongPass!");

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // getSessionById()
    // =========================================================
    @Nested
    @DisplayName("getSessionById()")
    class GetSessionByIdTests {

        @Test
        @DisplayName("trả về empty khi userId không tồn tại")
        void getSessionById_notFound_returnsEmpty() {
            when(userDAO.findById(999)).thenReturn(Optional.empty());

            Optional<UserSessionDTO> result = userService.getSessionById(999);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi user bị xóa mềm")
        void getSessionById_deletedUser_returnsEmpty() {
            User deleted = buildDeletedUser("del_user");
            when(userDAO.findById(2)).thenReturn(Optional.of(deleted));

            Optional<UserSessionDTO> result = userService.getSessionById(2);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi user không ACTIVE")
        void getSessionById_inactiveUser_returnsEmpty() {
            User inactive = buildActiveUser("u", "TENANT");
            inactive.setStatus("INACTIVE");
            when(userDAO.findById(3)).thenReturn(Optional.of(inactive));

            Optional<UserSessionDTO> result = userService.getSessionById(3);

            assertThat(result).isEmpty();
        }
    }
}
