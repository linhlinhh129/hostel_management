package com.quanlyphongtro.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Contract Model")
class ContractModelTest {

    private Contract contract(LocalDate signedDate) {
        Contract c = new Contract();
        c.setSignedDate(signedDate);
        return c;
    }

    // =========================================================
    // getSignedDay() / getSignedMonth() / getSignedYear()
    // =========================================================
    @Nested
    @DisplayName("getSignedDay/Month/Year()")
    class SignedDateParts {

        @Test
        @DisplayName("trả về ngày đúng định dạng 2 chữ số")
        void day_formatted() {
            assertThat(contract(LocalDate.of(2025, 3, 5)).getSignedDay()).isEqualTo("05");
            assertThat(contract(LocalDate.of(2025, 3, 15)).getSignedDay()).isEqualTo("15");
        }

        @Test
        @DisplayName("trả về tháng đúng định dạng 2 chữ số")
        void month_formatted() {
            assertThat(contract(LocalDate.of(2025, 6, 1)).getSignedMonth()).isEqualTo("06");
            assertThat(contract(LocalDate.of(2025, 11, 1)).getSignedMonth()).isEqualTo("11");
        }

        @Test
        @DisplayName("trả về năm đầy đủ")
        void year_full() {
            assertThat(contract(LocalDate.of(2025, 6, 1)).getSignedYear()).isEqualTo("2025");
        }

        @Test
        @DisplayName("trả về placeholder khi signedDate null")
        void null_placeholders() {
            Contract c = contract(null);
            assertThat(c.getSignedDay()).isEqualTo("...");
            assertThat(c.getSignedMonth()).isEqualTo("...");
            assertThat(c.getSignedYear()).isEqualTo("......");
        }
    }

    // =========================================================
    // status field
    // =========================================================
    @Test
    @DisplayName("setStatus/getStatus lưu đúng giá trị")
    void statusField() {
        Contract c = new Contract();
        c.setStatus("ACTIVE");
        assertThat(c.getStatus()).isEqualTo("ACTIVE");
        c.setStatus("TERMINATED");
        assertThat(c.getStatus()).isEqualTo("TERMINATED");
    }

    // =========================================================
    // room / tenant relationship
    // =========================================================
    @Test
    @DisplayName("setRoom/getRoom lưu đúng đối tượng")
    void roomRelationship() {
        Contract c = new Contract();
        Room r = new Room();
        r.setCode("HL0101");
        c.setRoom(r);
        assertThat(c.getRoom().getCode()).isEqualTo("HL0101");
    }

    @Test
    @DisplayName("setTenant/getTenant lưu đúng đối tượng")
    void tenantRelationship() {
        Contract c = new Contract();
        User u = new User();
        u.setFullName("Nguyễn Văn A");
        c.setTenant(u);
        assertThat(c.getTenant().getFullName()).isEqualTo("Nguyễn Văn A");
    }
}
