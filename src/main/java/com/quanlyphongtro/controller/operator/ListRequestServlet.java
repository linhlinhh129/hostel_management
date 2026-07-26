package com.quanlyphongtro.controller.operator;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.RequestDAO;
import com.quanlyphongtro.model.Request;
import com.quanlyphongtro.dto.UserSessionDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ListRequestServlet", urlPatterns = "/operator/requests")
public class ListRequestServlet extends BaseServlet {

    private RequestDAO requestDAO;

    @Override
    public void init() throws ServletException {
        this.requestDAO = new RequestDAO();
    }

    @Override
    // Xử lý lấy danh sách Yêu cầu (Support Requests) hiển thị cho Operator
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(req);
        Integer assigneeId = currentUser != null ? currentUser.getId() : null;

        // Nhận tham số phân trang và lọc từ URL (ví dụ: ?status=PENDING&category=ELECTRIC)
        String status = req.getParameter("status");
        String category = req.getParameter("category");
        
        if (status != null && status.trim().isEmpty()) status = null;
        if (category != null && category.trim().isEmpty()) category = null;

        int page = 1;
        int limit = 20;
        
        String pageParam = req.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
                if (page < 1) page = 1;
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        int offset = (page - 1) * limit;

        // Truy vấn danh sách Yêu cầu từ Database dựa trên filter và phân trang
        List<Request> requests = requestDAO.getRequests(assigneeId, status, category, offset, limit);
        
        // Đếm tổng số lượng Yêu cầu để tính toán tổng số trang
        int totalRecords = requestDAO.countRequests(assigneeId, status, category);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        // Đẩy toàn bộ dữ liệu (List data + Thông tin phân trang + Trạng thái filter) qua JSP
        req.setAttribute("requestList", requests);
        req.setAttribute("requestListSize", requests.size());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalRecords", totalRecords);
        req.setAttribute("paramStatus", status != null ? status : "");
        req.setAttribute("paramCategory", category != null ? category : "");
        
        // Lấy danh sách các danh mục (category) tồn tại để hiển thị trên Dropdown bộ lọc
        req.setAttribute("availableCategories", requestDAO.getDistinctCategories());

        req.getRequestDispatcher("/WEB-INF/views/operator/requests/list.jsp").forward(req, resp);
    }
}
