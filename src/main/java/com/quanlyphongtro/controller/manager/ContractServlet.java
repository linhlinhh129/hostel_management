package com.quanlyphongtro.controller.manager;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.dao.ContractDAO;
import com.quanlyphongtro.service.ContractService;
import com.quanlyphongtro.service.impl.ContractServiceImpl;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet(urlPatterns = {"/manager/contracts", "/manager/contracts/create", "/manager/contracts/detail"})
public class ContractServlet extends BaseServlet {

    private final ContractService contractService = new ContractServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        
        try {
            UserSessionDTO user = getCurrentUser(req);
            if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return;
            }

            if ("/manager/contracts/create".equals(path)) {
                showCreateForm(req, resp, user.getId());
            } else if ("/manager/contracts/detail".equals(path)) {
                showDetail(req, resp, user.getId());
            } else {
                showList(req, resp, user.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi hệ thống");
        }
    }

    private void showList(HttpServletRequest req, HttpServletResponse resp, int managerId) throws ServletException, IOException {
        String searchName = req.getParameter("searchName");
        List<Contract> contracts = contractService.getContractsByManager(managerId, searchName);
        req.setAttribute("contracts", contracts);
        req.setAttribute("searchName", searchName);
        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/list.jsp").forward(req, resp);
    }

    private void showCreateForm(HttpServletRequest req, HttpServletResponse resp, int managerId) throws ServletException, IOException {
        ContractDAO contractDAO = new ContractDAO();
        req.setAttribute("availableRooms", contractDAO.getAvailableRooms(managerId));
        req.getRequestDispatcher("/WEB-INF/views/manager/contracts/create.jsp").forward(req, resp);
    }

    private void showDetail(HttpServletRequest req, HttpServletResponse resp, int managerId) throws ServletException, IOException {
        try {
            int id = Integer.parseInt(req.getParameter("id"));
            Contract contract = contractService.getContractDetail(id, managerId);
            if (contract == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hợp đồng");
                return;
            }
            req.setAttribute("contract", contract);
            req.getRequestDispatcher("/WEB-INF/views/manager/contracts/detail.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hợp đồng không hợp lệ");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String path = req.getServletPath();
        if ("/manager/contracts/create".equals(path)) {
            try {
                UserSessionDTO user = getCurrentUser(req);
                if (user == null || (!"MANAGER".equals(user.getRole()) && !"ADMIN".equals(user.getRole()))) {
                    resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                    return;
                }

                Contract contract = new Contract();
                contract.setRoomId(Integer.parseInt(req.getParameter("roomId")));
                
                String tenantIdStr = req.getParameter("tenantId");
                if (tenantIdStr != null && !tenantIdStr.trim().isEmpty()) {
                    contract.setTenantId(Integer.parseInt(tenantIdStr));
                }

                contract.setTenantFullName(req.getParameter("tenantFullName"));
                String dobStr = req.getParameter("tenantDob");
                if (dobStr != null && !dobStr.trim().isEmpty()) {
                    contract.setTenantDob(LocalDate.parse(dobStr));
                }
                contract.setTenantPermanentAddress(req.getParameter("tenantPermanentAddress"));
                contract.setTenantIdentityNumber(req.getParameter("tenantIdentityNumber"));
                String issueDateStr = req.getParameter("tenantIdentityIssueDate");
                if (issueDateStr != null && !issueDateStr.trim().isEmpty()) {
                    contract.setTenantIdentityIssueDate(LocalDate.parse(issueDateStr));
                }
                contract.setTenantIdentityIssuePlace(req.getParameter("tenantIdentityIssuePlace"));
                contract.setTenantPhone(req.getParameter("tenantPhone"));
                contract.setAmountInWords(req.getParameter("amountInWords"));
                contract.setSignedDate(LocalDate.parse(req.getParameter("signedDate")));
                contract.setStartDate(LocalDate.parse(req.getParameter("startDate")));
                contract.setEndDate(LocalDate.parse(req.getParameter("endDate")));

                contractService.createContract(contract, user.getId());
                
                // Redirect on success
                resp.sendRedirect(req.getContextPath() + "/manager/contracts");
            } catch (Exception e) {
                e.printStackTrace();
                req.setAttribute("errorMessage", "Lỗi: " + e.getMessage());
                ContractDAO contractDAO = new ContractDAO();
                req.setAttribute("availableRooms", contractDAO.getAvailableRooms(user.getId()));
                req.getRequestDispatcher("/WEB-INF/views/manager/contracts/create.jsp").forward(req, resp);
            }
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
