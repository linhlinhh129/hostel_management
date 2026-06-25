package com.quanlyphongtro.controller.tenant;

import com.quanlyphongtro.constant.StatusConstant;
import com.quanlyphongtro.controller.BaseServlet;
import com.quanlyphongtro.dao.InvoiceDAO;
import com.quanlyphongtro.dao.PaymentDAO;
import com.quanlyphongtro.dto.UserSessionDTO;
import com.quanlyphongtro.model.Invoice;
import com.quanlyphongtro.model.Room;
import com.quanlyphongtro.service.TenantService;
import com.quanlyphongtro.service.impl.TenantServiceImpl;
import com.quanlyphongtro.util.VNPayConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.quanlyphongtro.util.DatabaseUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "TenantPaymentReturnServlet", urlPatterns = {"/tenant/invoices/vnpay-return", "/tenant/payment/return"})
public class TenantPaymentReturnServlet extends BaseServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Map<String, String> fields = new HashMap<>();
            for (Enumeration<String> params = req.getParameterNames(); params.hasMoreElements();) {
                String fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII.toString());
                String fieldValue = URLEncoder.encode(req.getParameter(fieldName), StandardCharsets.US_ASCII.toString());
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = req.getParameter("vnp_SecureHash");
            if (fields.containsKey("vnp_SecureHashType")) {
                fields.remove("vnp_SecureHashType");
            }
            if (fields.containsKey("vnp_SecureHash")) {
                fields.remove("vnp_SecureHash");
            }
            
            // Hash validation
            List<String> fieldNames = new ArrayList<>(fields.keySet());
            Collections.sort(fieldNames);
            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue);
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }
            
            String signValue = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
            boolean isValid = signValue.equals(vnp_SecureHash);

            if (isValid) {
                String responseCode = req.getParameter("vnp_ResponseCode");
                String txnRef = req.getParameter("vnp_TxnRef"); // Định dạng: INV[id]T[timestamp]
                String amountStr = req.getParameter("vnp_Amount");
                BigDecimal amount = new BigDecimal(amountStr).divide(new BigDecimal(100));

                String invoiceIdStr = txnRef.substring(3, txnRef.indexOf("T"));
                int invoiceId = Integer.parseInt(invoiceIdStr);

                String vnp_TransactionNo = req.getParameter("vnp_TransactionNo");
                String paymentCode = (vnp_TransactionNo != null && !vnp_TransactionNo.trim().isEmpty()) ? vnp_TransactionNo.trim() : txnRef;

                if ("00".equals(responseCode)) {
                    // KÍCH HOẠT JDBC TRANSACTION CHỐT SỔ ĐỒNG BỘ
                    boolean isUpdated = executePaymentTransaction(invoiceId, amount, paymentCode);
                    if (isUpdated) {
                        setFlashMessage(req, "success", "Thanh toán thành công hóa đơn mã số: " + invoiceId);
                    } else {
                        setFlashMessage(req, "error", "Lỗi đồng bộ dữ liệu hệ thống nội bộ!");
                    }
                    resp.sendRedirect(req.getContextPath() + "/tenant/invoices/" + invoiceId);
                } else {
                    setFlashMessage(req, "error", "Giao dịch không thành công hoặc bị hủy.");
                    resp.sendRedirect(req.getContextPath() + "/tenant/invoices");
                }
            } else {
                setFlashMessage(req, "error", "Chữ ký bảo mật không hợp lệ!");
                resp.sendRedirect(req.getContextPath() + "/tenant/invoices");
            }
        } catch (Exception e) {
            handleException(req, resp, e);
        }
    }

    private boolean executePaymentTransaction(int invoiceId, BigDecimal amount, String txnRef) {
        // We must query room_id from invoice to insert into payments correctly
        String getRoomSql = "SELECT room_id FROM invoices WHERE invoice_id = ?";
        // Correct query based on schema.sql: payments(code, invoice_id, room_id, status, payment_date, payment_method, payment_amount, created_at, updated_at)
        String insertPayment = "INSERT INTO payments (code, invoice_id, room_id, status, payment_date, payment_method, payment_amount, created_at, updated_at) "
                             + "VALUES (?, ?, ?, 'SUCCESS', GETDATE(), 'VNPAY', ?, GETDATE(), GETDATE())";
                             
        String updateInvoice = "UPDATE invoices SET status = 'PAID', updated_at = GETDATE() WHERE invoice_id = ?";
        
        Connection conn = null;
        try {
            conn = DatabaseUtil.getConnection();
            conn.setAutoCommit(false); 

            int roomId = -1;
            try (PreparedStatement psGet = conn.prepareStatement(getRoomSql)) {
                psGet.setInt(1, invoiceId);
                try (ResultSet rs = psGet.executeQuery()) {
                    if (rs.next()) roomId = rs.getInt("room_id");
                }
            }

            if (roomId == -1) {
                conn.rollback();
                return false;
            }

            // 1. Tạo bản ghi dòng tiền thực tế vào bảng payments
            try (PreparedStatement psPay = conn.prepareStatement(insertPayment)) {
                psPay.setString(1, txnRef);
                psPay.setInt(2, invoiceId);
                psPay.setInt(3, roomId);
                psPay.setBigDecimal(4, amount);
                psPay.executeUpdate();
            }

            // 2. Chuyển trạng thái công nợ bảng invoices thành PAID
            try (PreparedStatement psInv = conn.prepareStatement(updateInvoice)) {
                psInv.setInt(1, invoiceId);
                psInv.executeUpdate();
            }

            conn.commit(); 
            return true;
        } catch (Exception e) {
            if (conn != null) {
                try { conn.rollback(); } catch (Exception ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (Exception e) {}
            }
        }
    }
}
