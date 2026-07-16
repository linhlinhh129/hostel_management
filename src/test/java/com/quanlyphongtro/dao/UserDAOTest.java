package com.quanlyphongtro.dao;

import com.quanlyphongtro.model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit test cho UserDAO dùng stub (không cần DB thật).
 *
 * Mục tiêu: kiểm tra luồng trả dữ liệu của các phương thức DAO
 * theo đúng pattern TDD: viết test trước, sau đó refactor.
 *
 * Để test với DB thật → dùng Testcontainers (SQL Server module).
 */
@DisplayName("UserDAO — Unit Tests")
class UserDAOTest {

    // -----------------------------------------------------------------------
    // Stub: override phương thức DAO, trả dữ liệu cứng — không cần DB
    // -----------------------------------------------------------------------
    static class StubUserDAO extends UserDAO {

        private User buildUser(int id, String username, String role, String status, String email) {
            User u = new User();
            u.setId(id);
            u.setUsername(username);
            u.setFullName("Người dùng " + id);
            u.setRole(role);
            u.setStatus(status);
            u.setEmail(email);
            return u;
        }

        @Override
        public Optional<User> findByUsername(String username) {
            return switch (username) {
                case "admin"    -> Optional.of(buildUser(1, "admin",    "ADMIN",   "ACTIVE", "admin@hostel.vn"));
                case "manager1" -> Optional.of(buildUser(2, "manager1", "MANAGER", "ACTIVE", "mgr@hostel.vn"));
                case "tenant1"  -> Optional.of(buildUser(3, "tenant1",  "TENANT",  "ACTIVE", "t1@hostel.vn"));
                default         -> Optional.empty();
            };
        }

        @Override
        public Optional<User> findById(int id) {
            return switch (id) {
                case 1 -> Optional.of(buildUser(1, "admin",    "ADMIN",   "ACTIVE", "admin@hostel.vn"));
                case 2 -> Optional.of(buildUser(2, "manager1", "MANAGER", "ACTIVE", "mgr@hostel.vn"));
                case 3 -> Optional.of(buildUser(3, "tenant1",  "TENANT",  "ACTIVE", "t1@hostel.vn"));
                default -> Optional.empty();
            };
        }

        @Override
        public Optional<User> findByEmail(String email) {
            return switch (email) {
                case "admin@hostel.vn" -> Optional.of(buildUser(1, "admin", "ADMIN", "ACTIVE", email));
                case "t1@hostel.vn"    -> Optional.of(buildUser(3, "tenant1", "TENANT", "ACTIVE", email));
                default                -> Optional.empty();
            };
        }
    }

    private final StubUserDAO userDAO = new StubUserDAO();

    // =========================================================
    // findByUsername()
    // =========================================================
    @Nested
    @DisplayName("findByUsername()")
    class FindByUsername {

        @Test
        @DisplayName("tìm thấy admin tồn tại")
        void existingAdmin_returnsCorrectUser() {
            Optional<User> result = userDAO.findByUsername("admin");

            assertThat(result).isPresent();
            assertThat(result.get().getUsername()).isEqualTo("admin");
            assertThat(result.get().getRole()).isEqualTo("ADMIN");
            assertThat(result.get().getStatus()).isEqualTo("ACTIVE");
        }

        @Test
        @DisplayName("tìm thấy manager với đúng role")
        void existingManager_returnsCorrectRole() {
            Optional<User> result = userDAO.findByUsername("manager1");

            assertThat(result).isPresent();
            assertThat(result.get().getRole()).isEqualTo("MANAGER");
        }

        @Test
        @DisplayName("trả về empty khi username không tồn tại")
        void nonExistentUsername_returnsEmpty() {
            Optional<User> result = userDAO.findByUsername("nobody");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi username là chuỗi rỗng")
        void emptyUsername_returnsEmpty() {
            Optional<User> result = userDAO.findByUsername("");

            assertThat(result).isEmpty();
        }
    }

    // =========================================================
    // findById()
    // =========================================================
    @Nested
    @DisplayName("findById()")
    class FindById {

        @Test
        @DisplayName("tìm thấy user khi id hợp lệ")
        void validId_returnsUser() {
            Optional<User> result = userDAO.findById(1);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1);
            assertThat(result.get().getFullName()).isEqualTo("Người dùng 1");
        }

        @Test
        @DisplayName("trả về empty khi id không tồn tại")
        void invalidId_returnsEmpty() {
            Optional<User> result = userDAO.findById(9999);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về đúng role cho từng user id")
        void eachId_returnsCorrectRole() {
            assertThat(userDAO.findById(1).get().getRole()).isEqualTo("ADMIN");
            assertThat(userDAO.findById(2).get().getRole()).isEqualTo("MANAGER");
            assertThat(userDAO.findById(3).get().getRole()).isEqualTo("TENANT");
        }
    }

    // =========================================================
    // findByEmail()
    // =========================================================
    @Nested
    @DisplayName("findByEmail()")
    class FindByEmail {

        @Test
        @DisplayName("tìm thấy user qua email admin")
        void knownEmail_returnsUser() {
            Optional<User> result = userDAO.findByEmail("admin@hostel.vn");

            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("admin@hostel.vn");
        }

        @Test
        @DisplayName("trả về empty khi email không tồn tại")
        void unknownEmail_returnsEmpty() {
            Optional<User> result = userDAO.findByEmail("notexist@hostel.vn");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("trả về empty khi email null-like (chuỗi rỗng)")
        void emptyEmail_returnsEmpty() {
            Optional<User> result = userDAO.findByEmail("");

            assertThat(result).isEmpty();
        }
    }
}
