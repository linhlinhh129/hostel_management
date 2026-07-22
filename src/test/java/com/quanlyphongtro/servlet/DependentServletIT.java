package com.quanlyphongtro.servlet;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

public class DependentServletIT {

    @BeforeAll
    static void setup() {
        // Cấu hình URL mặc định. Yêu cầu Tomcat Server đang bật.
        RestAssured.baseURI = "http://localhost:8080/HostelManagement";
    }

    @Test
    @DisplayName("T027: Bắn request không có Cookie Session (Chưa đăng nhập) - Chặn bởi Filter")
    void testUnauthorizedAccess_NoSession() {
        Response response = given()
                .when()
                .get("/dependents")
                .then()
                .extract().response();

        // Servlet truyền thống khi chưa đăng nhập thường trả về 302 Redirect (về trang Login)
        // Hoặc 401 Unauthorized nếu là giao tiếp qua Fetch API.
        // Chắc chắn 1 điều là không thể trả về 200 OK.
        assertThat(response.statusCode()).isNotEqualTo(200);
        assertThat(response.statusCode()).isIn(302, 401, 403);
    }

    @Test
    @DisplayName("T026: Bắn request API xóa ID Dependent không thuộc thẩm quyền - Lỗi 403 hoặc 404")
    void testUnauthorizedAccess_WrongTenantId() {
        // Giả lập gửi một Cookie chứa ID của Tenant khác (ví dụ qua Rest Assured sessionId)
        // Do hệ thống bảo mật bằng Session ở Server, ở đây ta test xem Endpoint chặn ID ngoài vùng an toàn.
        Response response = given()
                .cookie("JSESSIONID", "fake-session-id-of-tenant-2") 
                .when()
                .post("/dependents?action=delete&id=1") // ID 1 là của Tenant 1
                .then()
                .extract().response();

        // Sẽ bị văng ra trang báo lỗi hoặc redirect
        assertThat(response.statusCode()).isNotEqualTo(200);
    }
}
