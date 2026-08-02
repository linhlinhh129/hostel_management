package com.quanlyphongtro.controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet(name = "LogoutServlet", urlPatterns = "/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    // Hàm xử lý yêu cầu GET (khi người dùng click vào thẻ link Đăng xuất)
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Bước 1: Lấy phiên làm việc (Session) hiện tại. Tham số 'false' nghĩa là không tạo session mới nếu chưa có.
        HttpSession session = req.getSession(false);
        
        // Bước 2: Kiểm tra nếu session tồn tại (tức là người dùng đang đăng nhập)
        if (session != null) {
            // Hủy diệt toàn bộ session, xóa sạch dữ liệu tài khoản (như currentUser, role...) trên Server
            session.invalidate();
        }
        
        // Bước 3: Điều hướng (đá) người dùng quay trở lại trang đăng nhập
        resp.sendRedirect(req.getContextPath() + "/login");
    }
}
