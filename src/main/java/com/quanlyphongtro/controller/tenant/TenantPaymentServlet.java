package com.quanlyphongtro.controller.tenant;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.TenantServiceImpl;

import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.util.VNPayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;

import com.quanlyphongtro.dao.InvoiceDAO;

@WebServlet(name = "TenantPaymentServlet", urlPatterns = {"/tenant/invoices/pay", "/tenant/payment/create"})
public class TenantPaymentServlet extends BaseServlet {

    private final InvoiceDAO invoiceDAO = new InvoiceDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            UserSessionDTO currentUser = getCurrentUser(request);
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                return;
            }

            int sessionTenantId = currentUser.getId();

            int invoiceId;
            try {
                invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tham số không hợp lệ");
                return;
            }

            // Anti-IDOR
            if (!invoiceDAO.verifyInvoiceOwnership(invoiceId, sessionTenantId)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Bạn không có quyền thanh toán hóa đơn này");
                return;
            }

            // In SWP391 logic, we might not get amount from parameter for safety, but we calculate it.
            // But since user explicitly asked, we fetch total from DB instead to be secure.
            Optional<Invoice> invOpt = invoiceDAO.findByIdAndRoomId(invoiceId, getRoomIdByTenant(sessionTenantId));
            if (invOpt.isEmpty()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Không tìm thấy hóa đơn");
                return;
            }
            Invoice invoice = invOpt.get();
            BigDecimal baseAmount = invoice.getTotalAmount();

            if (!"UNPAID".equals(invoice.getStatus()) && !"OVERDUE".equals(invoice.getStatus())) {
                setFlashMessage(request, "error", "Hóa đơn này không thể thanh toán.");
                response.sendRedirect(request.getContextPath() + "/tenant/invoices/" + invoiceId);
                return;
            }

            // totalAmount trong Invoice đã bao gồm lateFee từ mapRow()
            BigDecimal total = baseAmount;

            long finalAmountVND = total.multiply(BigDecimal.valueOf(100)).longValue();

            Map<String, String> params = new HashMap<>();
            params.put("vnp_Version", "2.1.0");
            params.put("vnp_Command", "pay");
            params.put("vnp_TmnCode", VNPayConfig.getVnp_TmnCode());
            params.put("vnp_Amount", String.valueOf(finalAmountVND));
            params.put("vnp_CurrCode", "VND");

            // txnRef định dạng INV{id}T{timestamp}
            String txnRef = "INV" + invoiceId + "T" + System.currentTimeMillis();
            params.put("vnp_TxnRef", txnRef);
            params.put("vnp_OrderInfo", "Thanh toan hoa don " + invoiceId);
            params.put("vnp_OrderType", "other");
            params.put("vnp_Locale", "vn");
            
            String returnUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/tenant/payment/return";
            params.put("vnp_ReturnUrl", returnUrl);
            params.put("vnp_IpAddr", VNPayConfig.getIpAddress(request));

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            params.put("vnp_CreateDate", sdf.format(cld.getTime()));
            cld.add(Calendar.MINUTE, 15);
            params.put("vnp_ExpireDate", sdf.format(cld.getTime()));

            List<String> keys = new ArrayList<>(params.keySet());
            Collections.sort(keys);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            for (int i = 0; i < keys.size(); i++) {
                String k = keys.get(i);
                String v = params.get(k);
                if (v != null && !v.isEmpty()) {
                    String encodedKey = URLEncoder.encode(k, StandardCharsets.US_ASCII);
                    String encodedVal = URLEncoder.encode(v, StandardCharsets.US_ASCII);
                    hashData.append(k).append('=').append(encodedVal);
                    query.append(encodedKey).append('=').append(encodedVal);
                    if (i < keys.size() - 1) {
                        hashData.append('&');
                        query.append('&');
                    }
                }
            }

            String secureHash = VNPayConfig.hmacSHA512(VNPayConfig.getSecretKey(), hashData.toString());
            query.append("&vnp_SecureHash=").append(secureHash);

            response.sendRedirect(VNPayConfig.getVnp_PayUrl() + "?" + query);

        } catch (Exception e) {
            handleException(request, response, e);
        }
    }

    private int getRoomIdByTenant(int tenantId) {
        TenantService tenantService = new TenantServiceImpl();
        Optional<Room> roomOpt = tenantService.getTenantRoom(tenantId);
        return roomOpt.map(Room::getId).orElse(-1);
    }
}
