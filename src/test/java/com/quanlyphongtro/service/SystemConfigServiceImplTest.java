package com.quanlyphongtro.service;

import com.quanlyphongtro.dao.SystemConfigDAO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.impl.SystemConfigServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit test cho SystemConfigServiceImpl — Admin cấu hình Email & VNPay.
 *
 * SystemConfigServiceImpl khởi tạo DAO qua `new` (không có constructor injection),
 * nên dùng reflection để inject mock vào private final field configDAO.
 *
 * NOTE: updateEmailConfig / updateVNPayConfig gọi DatabaseUtil.getConnection()
 * nên các test cho method đó chỉ test validation (trước khi tới DB).
 * Test DB-level cần Testcontainers.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SystemConfigServiceImpl — Admin cấu hình hệ thống")
class SystemConfigServiceImplTest {

    private SystemConfigDAO mockConfigDAO;
    private SystemConfigServiceImpl service;

    @BeforeEach
    void setUp() throws Exception {
        mockConfigDAO = mock(SystemConfigDAO.class);
        service = new SystemConfigServiceImpl();
        // Inject mock vào private final field
        Field f = SystemConfigServiceImpl.class.getDeclaredField("configDAO");
        f.setAccessible(true);
        f.set(service, mockConfigDAO);
    }

    // =========================================================
    // getUIEmailConfig()
    // =========================================================
    @Nested
    @DisplayName("getUIEmailConfig()")
    class GetUIEmailConfig {

        @Test
        @DisplayName("trả về đúng host/port/username từ DAO")
        void returnsValuesFromDAO() {
            when(mockConfigDAO.getConfigValue("email.host")).thenReturn("smtp.gmail.com");
            when(mockConfigDAO.getConfigValue("email.port")).thenReturn("587");
            when(mockConfigDAO.getConfigValue("email.username")).thenReturn("hostel@gmail.com");
            when(mockConfigDAO.getConfigValue("email.from")).thenReturn("hostel@gmail.com");
            when(mockConfigDAO.getConfigMetadata("email."))
                    .thenReturn(new SystemConfigDAO.ConfigMetadata(null, null));

            var dto = service.getUIEmailConfig();

            assertThat(dto.getHost()).isEqualTo("smtp.gmail.com");
            assertThat(dto.getPort()).isEqualTo("587");
            assertThat(dto.getUsername()).isEqualTo("hostel@gmail.com");
        }

        @Test
        @DisplayName("updatedBy mặc định là 'Hệ thống' khi metadata null")
        void nullMetadata_defaultsToSystem() {
            when(mockConfigDAO.getConfigValue(anyString())).thenReturn(null);
            when(mockConfigDAO.getConfigMetadata("email."))
                    .thenReturn(new SystemConfigDAO.ConfigMetadata(null, null));

            var dto = service.getUIEmailConfig();

            assertThat(dto.getUpdatedBy()).isEqualTo("Hệ thống");
            assertThat(dto.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("updatedBy dùng tên từ metadata khi có updatedAt")
        void withMetadata_usesUpdatedByName() {
            when(mockConfigDAO.getConfigValue(anyString())).thenReturn("value");
            java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
            when(mockConfigDAO.getConfigMetadata("email."))
                    .thenReturn(new SystemConfigDAO.ConfigMetadata(ts, "Admin Trần A"));

            var dto = service.getUIEmailConfig();

            assertThat(dto.getUpdatedBy()).isEqualTo("Admin Trần A");
            assertThat(dto.getUpdatedAt()).isNotNull();
        }
    }

    // =========================================================
    // getUIVNPayConfig()
    // =========================================================
    @Nested
    @DisplayName("getUIVNPayConfig()")
    class GetUIVNPayConfig {

        @Test
        @DisplayName("trả về đúng payUrl/returnUrl/tmnCode từ DAO")
        void returnsValuesFromDAO() {
            when(mockConfigDAO.getConfigValue("vnpay.payUrl")).thenReturn("https://sandbox.vnpayment.vn/paymentv2/vpcpay.html");
            when(mockConfigDAO.getConfigValue("vnpay.returnUrl")).thenReturn("http://localhost:8080/hostel/vnpay-return");
            when(mockConfigDAO.getConfigValue("vnpay.tmnCode")).thenReturn("ABCD1234");
            when(mockConfigDAO.getConfigValue("vnpay.apiUrl")).thenReturn("https://sandbox.vnpayment.vn/merchant_webapi/api/transaction");
            when(mockConfigDAO.getConfigMetadata("vnpay."))
                    .thenReturn(new SystemConfigDAO.ConfigMetadata(null, null));

            var dto = service.getUIVNPayConfig();

            assertThat(dto.getPayUrl()).contains("vnpayment.vn");
            assertThat(dto.getTmnCode()).isEqualTo("ABCD1234");
            assertThat(dto.getUpdatedBy()).isEqualTo("Hệ thống");
        }

        @Test
        @DisplayName("updatedByName null trong metadata → dùng 'Hệ thống'")
        void nullUpdatedByName_defaultsToSystem() {
            when(mockConfigDAO.getConfigValue(anyString())).thenReturn("x");
            java.sql.Timestamp ts = new java.sql.Timestamp(System.currentTimeMillis());
            when(mockConfigDAO.getConfigMetadata("vnpay."))
                    .thenReturn(new SystemConfigDAO.ConfigMetadata(ts, null));

            var dto = service.getUIVNPayConfig();

            assertThat(dto.getUpdatedBy()).isEqualTo("Hệ thống");
        }
    }

    // =========================================================
    // updateEmailConfig() — validation (không cần DB)
    // =========================================================
    @Nested
    @DisplayName("updateEmailConfig() — validation")
    class UpdateEmailConfigValidation {

        @Test
        @DisplayName("ném ValidationException khi host trống")
        void blankHost_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateEmailConfig("", "587", "user@gmail.com", "pass", "user@gmail.com", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }

        @Test
        @DisplayName("ném ValidationException khi port trống")
        void blankPort_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateEmailConfig("smtp.gmail.com", "", "user@gmail.com", "pass", "user@gmail.com", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }

        @Test
        @DisplayName("ném ValidationException khi username trống")
        void blankUsername_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateEmailConfig("smtp.gmail.com", "587", "", "pass", "user@gmail.com", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }

        @ParameterizedTest(name = "port = \"{0}\" không hợp lệ")
        @ValueSource(strings = {"0", "65536", "abc", "-1", "99999"})
        @DisplayName("ném ValidationException khi port ngoài dải 1–65535 hoặc không phải số")
        void invalidPort_throwsValidationException(String invalidPort) {
            assertThatThrownBy(() ->
                    service.updateEmailConfig("smtp.gmail.com", invalidPort, "u@g.com", "p", "u@g.com", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("Port");
        }
    }

    // =========================================================
    // updateVNPayConfig() — validation (không cần DB)
    // =========================================================
    @Nested
    @DisplayName("updateVNPayConfig() — validation")
    class UpdateVNPayConfigValidation {

        @Test
        @DisplayName("ném ValidationException khi payUrl trống")
        void blankPayUrl_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateVNPayConfig("", "http://return", "TMN001", "secret", "http://api", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }

        @Test
        @DisplayName("ném ValidationException khi returnUrl trống")
        void blankReturnUrl_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateVNPayConfig("http://pay", "", "TMN001", "secret", "http://api", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }

        @Test
        @DisplayName("ném ValidationException khi tmnCode trống")
        void blankTmnCode_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateVNPayConfig("http://pay", "http://return", "", "secret", "http://api", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }

        @Test
        @DisplayName("ném ValidationException khi apiUrl trống")
        void blankApiUrl_throwsValidationException() {
            assertThatThrownBy(() ->
                    service.updateVNPayConfig("http://pay", "http://return", "TMN001", "secret", "", 1))
                    .isInstanceOf(ValidationException.class)
                    .hasMessageContaining("bắt buộc");
        }
    }
}
