package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Contract;
import com.quanlyphongtro.service.ContractService;
import com.quanlyphongtro.service.impl.ContractServiceImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "TenantContractServlet", urlPatterns = "/tenant/contracts")
public class TenantContractServlet extends BaseServlet {

    private final ContractService contractService = new ContractServiceImpl();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            UserSessionDTO currentUser = getCurrentUser(req);
            String idParam = req.getParameter("id");

            if (idParam == null || idParam.trim().isEmpty()) {
                // View Contract List
                List<Contract> contracts = contractService.getContractsByTenant(currentUser.getId());
                req.setAttribute("contracts", contracts);
                req.setAttribute("activeMenu", "contracts");
                req.getRequestDispatcher("/WEB-INF/views/tenant/contracts/list.jsp").forward(req, resp);
            } else {
                // View Contract Detail
                int contractId;
                try {
                    contractId = Integer.parseInt(idParam);
                } catch (NumberFormatException e) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID hợp đồng không hợp lệ");
                    return;
                }

                Contract contract = contractService.getContractDetailForTenant(contractId, currentUser.getId());
                if (contract == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hợp đồng hoặc bạn không có quyền xem.");
                    return;
                }

                req.setAttribute("contract", contract);
                req.setAttribute("activeMenu", "contracts");
                req.getRequestDispatcher("/WEB-INF/views/tenant/contracts/detail.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            forwardError(req, resp, "/WEB-INF/views/tenant/contracts/list.jsp", e);
        }
    }
}
