package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.service.ServicePriceService;
import com.quanlyphongtro.service.impl.ServicePriceServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ServicePricePageServlet", urlPatterns = "/manager/service-prices")
public class ServicePricePageServlet extends BaseServlet {

    private ServicePriceService servicePriceService;

    @Override
    public void init() throws ServletException {
        this.servicePriceService = new ServicePriceServiceImpl();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        com.quanlyphongtro.dto.UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null || !"MANAGER".equals(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = req.getParameter("action");
        if ("history".equals(action)) {
            String priceType = req.getParameter("priceType");
            int page = 1;
            int size = 10;
            try {
                if (req.getParameter("page") != null) page = Integer.parseInt(req.getParameter("page"));
            } catch (NumberFormatException ignored) {}
            
            req.setAttribute("historyList", servicePriceService.getPriceHistory(currentUser.getId(), priceType, page, size));
            req.setAttribute("priceType", priceType);
            req.getRequestDispatcher("/WEB-INF/views/manager/service-prices/history.jsp").forward(req, resp);
        } else {
            req.setAttribute("servicePrices", servicePriceService.getCurrentPrices(currentUser.getId()));
            req.getRequestDispatcher("/WEB-INF/views/manager/service-prices/index.jsp").forward(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        com.quanlyphongtro.dto.UserSessionDTO currentUser = getCurrentUser(req);
        if (currentUser == null || !"MANAGER".equals(currentUser.getRole())) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
            return;
        }

        String action = req.getParameter("action");
        if ("update".equals(action)) {
            try {
                String priceType = req.getParameter("priceType");
                String newPriceStr = req.getParameter("newPrice");
                String note = req.getParameter("note");

                java.math.BigDecimal newPrice = new java.math.BigDecimal(newPriceStr);
                boolean success = servicePriceService.updatePrice(currentUser.getId(), priceType, newPrice, note);
                
                if (success) {
                    // removed successMessage
                    resp.sendRedirect(req.getContextPath() + "/manager/service-prices");
                } else {
                    req.setAttribute("errorMessage", "Cập nhật thất bại (Lỗi nghiệp vụ hoặc cơ sở không hợp lệ).");
                    req.setAttribute("servicePrices", servicePriceService.getCurrentPrices(currentUser.getId()));
                    req.getRequestDispatcher("/WEB-INF/views/manager/service-prices/index.jsp").forward(req, resp);
                }
            } catch (Exception e) {
                req.setAttribute("errorMessage", "Dữ liệu không hợp lệ.");
                req.setAttribute("servicePrices", servicePriceService.getCurrentPrices(currentUser.getId()));
                req.getRequestDispatcher("/WEB-INF/views/manager/service-prices/index.jsp").forward(req, resp);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
