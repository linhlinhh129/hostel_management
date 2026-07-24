package com.quanlyphongtro.controller.admin;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.EmailConfigDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.dto.VNPayConfigDTO;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.SystemConfigService;
import com.quanlyphongtro.service.impl.SystemConfigServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "AdminSystemConfigServlet", urlPatterns = {"/admin/system-config", "/admin/system-config/email", "/admin/system-config/vnpay"})
public class AdminSystemConfigServlet extends BaseServlet {

    private SystemConfigService configService;

    @Override
    public void init() throws ServletException {
        super.init();
        this.configService = new SystemConfigServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserSessionDTO currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        
        String path = request.getServletPath();
        
        if ("/admin/system-config".equals(path) || "/admin/system-config/email".equals(path) || "/admin/system-config/vnpay".equals(path)) {
            EmailConfigDTO emailConfig = configService.getUIEmailConfig();
            VNPayConfigDTO vnpayConfig = configService.getUIVNPayConfig();
            
            request.setAttribute("emailConfig", emailConfig);
            request.setAttribute("vnpayConfig", vnpayConfig);
            
            String success = request.getParameter("success");
            if ("email_updated".equals(success)) {
                request.setAttribute("successMessage", "Cập nhật cấu hình Email thành công.");
            } else if ("vnpay_updated".equals(success)) {
                request.setAttribute("successMessage", "Cập nhật cấu hình VNPay thành công.");
            }
            
            request.getRequestDispatcher("/WEB-INF/views/admin/system-config.jsp").forward(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        UserSessionDTO currentUser = getCurrentUser(request);
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            if ("/admin/system-config/email".equals(path)) {
                String host     = request.getParameter("host");
                String port     = request.getParameter("port");
                String username = request.getParameter("username");
                String password = request.getParameter("password");

                configService.updateEmailConfig(host, port, username, password, currentUser.getId());
                response.sendRedirect(request.getContextPath() + "/admin/system-config?success=email_updated");
                return;

            } else if ("/admin/system-config/vnpay".equals(path)) {
                String payUrl = request.getParameter("payUrl");
                String returnUrl = request.getParameter("returnUrl");
                String tmnCode = request.getParameter("tmnCode");
                String secretKey = request.getParameter("secretKey");
                String apiUrl = request.getParameter("apiUrl");

                configService.updateVNPayConfig(payUrl, returnUrl, tmnCode, secretKey, apiUrl, currentUser.getId());
                response.sendRedirect(request.getContextPath() + "/admin/system-config?success=vnpay_updated");
                return;
            }
            
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        } catch (ValidationException e) {
            request.setAttribute("errorMessage", e.getMessage());
            doGet(request, response);
        } catch (Exception e) {
            handleException(request, response, e);
        }
    }
}
