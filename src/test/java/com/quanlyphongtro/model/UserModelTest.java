package com.quanlyphongtro.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("User Model")
class UserModelTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setUsername("admin");
        user.setFullName("Nguyễn Văn A");
        user.setRole("ADMIN");
        user.setStatus("ACTIVE");
    }

    @Nested
    @DisplayName("isActive()")
    class IsActive {
        @Test void active_returnsTrue()   { user.setStatus("ACTIVE");   assertThat(user.isActive()).isTrue();  }
        @Test void inactive_returnsFalse(){ user.setStatus("INACTIVE"); assertThat(user.isActive()).isFalse(); }
        @Test void locked_returnsFalse()  { user.setStatus("LOCKED");   assertThat(user.isActive()).isFalse(); }
    }

    @Nested
    @DisplayName("isLocked()")
    class IsLocked {
        @Test void locked_returnsTrue()   { user.setStatus("LOCKED");   assertThat(user.isLocked()).isTrue();  }
        @Test void active_returnsFalse()  { user.setStatus("ACTIVE");   assertThat(user.isLocked()).isFalse(); }
        @Test void inactive_returnsFalse(){ user.setStatus("INACTIVE"); assertThat(user.isLocked()).isFalse(); }
    }

    @Nested
    @DisplayName("isDeleted()")
    class IsDeleted {
        @Test void deletedAt_null_returnsFalse() {
            user.setDeletedAt(null);
            assertThat(user.isDeleted()).isFalse();
        }
        @Test void deletedAt_set_returnsTrue() {
            user.setDeletedAt(LocalDateTime.now());
            assertThat(user.isDeleted()).isTrue();
        }
    }

    @Nested
    @DisplayName("facilityNames")
    class FacilityNames {
        @Test void defaultFacilityNames_isEmpty() {
            User u = new User();
            assertThat(u.getFacilityNames()).isNotNull().isEmpty();
        }
        @Test void setFacilityNamesNull_givesEmptyList() {
            user.setFacilityNames(null);
            assertThat(user.getFacilityNames()).isNotNull().isEmpty();
        }
        @Test void setFacilityNames_storesCorrectly() {
            user.setFacilityNames(java.util.List.of("Cơ sở A", "Cơ sở B"));
            assertThat(user.getFacilityNames()).containsExactly("Cơ sở A", "Cơ sở B");
        }
    }
}
