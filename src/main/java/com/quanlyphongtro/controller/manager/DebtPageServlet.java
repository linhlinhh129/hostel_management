package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.dto.DebtListItemDTO;
import com.quanlyphongtro.dto.DebtDetailDTO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.service.DebtService;
import com.quanlyphongtro.service.impl.DebtServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/manager/debts")
public class DebtPageServlet extends HttpServlet {
    
    private final DebtService debtService = new DebtServiceImpl();
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        UserSessionDTO currentUser = (UserSessionDTO) session.getAttribute("currentUser");
        if (!"MANAGER".equals(currentUser.getRole())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền truy cập");
            return;
        }

        String action = request.getParameter("action");
        if ("detail".equals(action)) {
            handleDetail(request, response, currentUser.getId());
        } else {
            handleList(request, response, currentUser.getId());
        }
    }

    private void handleList(HttpServletRequest request, HttpServletResponse response, int managerId) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        String status = request.getParameter("status");
        
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException ignored) {}
        }

        List<DebtListItemDTO> debts = debtService.getDebts(managerId, keyword, status, page, PAGE_SIZE);
        int totalPages = debtService.getTotalPages(managerId, keyword, status, PAGE_SIZE);
        // Tính totalRecords: các trang trước đầy, trang cuối = số thực tế
        int totalRecords = totalPages > 0
            ? (totalPages - 1) * PAGE_SIZE + (page == totalPages ? debts.size() : PAGE_SIZE)
            : 0;

        request.setAttribute("debts", debts);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", Math.max(1, totalPages));
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("keyword", keyword);
        request.setAttribute("status", status);

        request.getRequestDispatcher("/WEB-INF/views/manager/debts/index.jsp").forward(request, response);
    }

    private void handleDetail(HttpServletRequest request, HttpServletResponse response, int managerId) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/manager/debts");
            return;
        }

        try {
            int invoiceId = Integer.parseInt(idParam);
            Optional<DebtDetailDTO> optDebt = debtService.getDebtDetail(managerId, invoiceId);
            
            if (optDebt.isPresent()) {
                request.setAttribute("debt", optDebt.get());
                request.getRequestDispatcher("/WEB-INF/views/manager/debts/detail.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy công nợ hoặc không thuộc quyền quản lý");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/manager/debts");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Lỗi khi tải chi tiết công nợ: " + e.getMessage(), e);
        }
    }
}
