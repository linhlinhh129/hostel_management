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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        UserSessionDTO currentUser = getCurrentUser(req);
        Integer assigneeId = currentUser != null ? currentUser.getId() : null;

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

        List<Request> requests = requestDAO.getRequests(assigneeId, status, category, offset, limit);
        int totalRecords = requestDAO.countRequests(assigneeId, status, category);
        int totalPages = (int) Math.ceil((double) totalRecords / limit);

        req.setAttribute("requestList", requests);
        req.setAttribute("requestListSize", requests.size());
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalRecords", totalRecords);
        req.setAttribute("paramStatus", status != null ? status : "");
        req.setAttribute("paramCategory", category != null ? category : "");

        req.getRequestDispatcher("/WEB-INF/views/operator/requests/list.jsp").forward(req, resp);
    }
}
