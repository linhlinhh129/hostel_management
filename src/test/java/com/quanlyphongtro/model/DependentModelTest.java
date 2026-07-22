package com.quanlyphongtro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Dependent Model")
class DependentModelTest {

    private Dependent dep(LocalDate dob, String identity) {
        Dependent d = new Dependent();
        d.setId(1);
        d.setTenantId(10);
        d.setFullName("Nguyễn Thị B");
        d.setDob(dob);
        d.setIdentityNumber(identity);
        return d;
    }

    // =========================================================
    // getDobLabel()
    // =========================================================
    @Nested
    @DisplayName("getDobLabel()")
    class DobLabel {

        @Test
        @DisplayName("format ngày sinh đúng dd/MM/yyyy")
        void formats_correctly() {
            assertThat(dep(LocalDate.of(1990, 5, 3), null).getDobLabel())
                    .isEqualTo("03/05/1990");
        }

        @Test
        @DisplayName("trả về 'N/A' khi dob null")
        void null_returnsNA() {
            assertThat(dep(null, null).getDobLabel()).isEqualTo("N/A");
        }
    }

    // =========================================================
    // getMaskedIdentityNumber()
    // =========================================================
    @Nested
    @DisplayName("getMaskedIdentityNumber()")
    class MaskedIdentity {

        @Test
        @DisplayName("che 6 ký tự giữa với 12 chữ số")
        void masks_12digits() {
            String result = dep(null, "012345678901").getMaskedIdentityNumber();
            assertThat(result).isEqualTo("012******901");
        }

        @Test
        @DisplayName("che 6 ký tự giữa với 9 chữ số")
        void masks_9digits() {
            String result = dep(null, "012345678").getMaskedIdentityNumber();
            assertThat(result).isEqualTo("012******678");
        }

        @DisplayName("trả về nguyên bản khi identity quá ngắn (< 6 ký tự)")
        @ParameterizedTest(name = "identity={0}")
        @DisplayName("trả về nguyên bản khi identity quá ngắn (< 6 ký tự)")
        @ValueSource(strings = {"12345", "abc", ""})
        void shortIdentity_returnsOriginal(String identity) {
            assertThat(dep(null, identity).getMaskedIdentityNumber())
                    .isEqualTo(identity);
        }

        @Test
        @DisplayName("trả về null khi identityNumber null")
        void nullIdentity_returnsNull() {
            assertThat(dep(null, null).getMaskedIdentityNumber()).isNull();
        }
    }
}
