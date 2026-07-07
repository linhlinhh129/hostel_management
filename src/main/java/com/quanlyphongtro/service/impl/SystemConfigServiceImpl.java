package com.quanlyphongtro.service.impl;

import com.quanlyphongtro.dao.SystemConfigDAO;
import com.quanlyphongtro.dto.EmailConfigDTO;
import com.quanlyphongtro.dto.VNPayConfigDTO;
import com.quanlyphongtro.exception.AppException;
import com.quanlyphongtro.exception.ValidationException;
import com.quanlyphongtro.service.SystemConfigService;
import com.quanlyphongtro.util.DatabaseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class SystemConfigServiceImpl implements SystemConfigService {
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigServiceImpl.class);
    private final SystemConfigDAO configDAO = new SystemConfigDAO();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    @Override
    public EmailConfigDTO getUIEmailConfig() {
        EmailConfigDTO dto = new EmailConfigDTO();
        dto.setHost(configDAO.getConfigValue("email.host"));
        dto.setPort(configDAO.getConfigValue("email.port"));
        dto.setUsername(configDAO.getConfigValue("email.username"));
        dto.setFrom(configDAO.getConfigValue("email.from"));
        
        SystemConfigDAO.ConfigMetadata meta = configDAO.getConfigMetadata("email.");
        if (meta.updatedAt != null) {
            dto.setUpdatedAt(dateFormat.format(meta.updatedAt));
            dto.setUpdatedBy(meta.updatedByName != null ? meta.updatedByName : "Hệ thống");
        } else {
            dto.setUpdatedBy("Hệ thống");
        }
        return dto;
    }

    @Override
    public VNPayConfigDTO getUIVNPayConfig() {
        VNPayConfigDTO dto = new VNPayConfigDTO();
        dto.setPayUrl(configDAO.getConfigValue("vnpay.payUrl"));
        dto.setReturnUrl(configDAO.getConfigValue("vnpay.returnUrl"));
        dto.setTmnCode(configDAO.getConfigValue("vnpay.tmnCode"));
        dto.setApiUrl(configDAO.getConfigValue("vnpay.apiUrl"));
        
        SystemConfigDAO.ConfigMetadata meta = configDAO.getConfigMetadata("vnpay.");
        if (meta.updatedAt != null) {
            dto.setUpdatedAt(dateFormat.format(meta.updatedAt));
            dto.setUpdatedBy(meta.updatedByName != null ? meta.updatedByName : "Hệ thống");
        } else {
            dto.setUpdatedBy("Hệ thống");
        }
        return dto;
    }

    @Override
    public void updateEmailConfig(String host, String portStr, String username, String password, String from, int updatedBy) throws ValidationException {
        if (host == null || host.trim().isEmpty() ||
            portStr == null || portStr.trim().isEmpty() ||
            username == null || username.trim().isEmpty() ||
            from == null || from.trim().isEmpty()) {
            throw new ValidationException("Vui lòng nhập đầy đủ các trường bắt buộc.");
        }

        int port;
        try {
            port = Integer.parseInt(portStr.trim());
            if (port < 1 || port > 65535) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            throw new ValidationException("Port phải là số nguyên từ 1 đến 65535.");
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                configDAO.updateConfigValue("email.host", host.trim(), updatedBy, conn);
                configDAO.updateConfigValue("email.port", String.valueOf(port), updatedBy, conn);
                configDAO.updateConfigValue("email.username", username.trim(), updatedBy, conn);
                configDAO.updateConfigValue("email.from", from.trim(), updatedBy, conn);
                
                // Only update password if it's provided and not a mask
                if (password != null && !password.trim().isEmpty() && !password.matches("^•+$") && !password.matches("^\\*+$")) {
                    configDAO.updateConfigValue("email.password", password.trim(), updatedBy, conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Database error while updating email config", e);
                throw new AppException("Lỗi khi cập nhật cấu hình Email", e);
            }
        } catch (SQLException e) {
            logger.error("Database connection error", e);
            throw new AppException("Lỗi kết nối cơ sở dữ liệu", e);
        }
    }

    @Override
    public void updateVNPayConfig(String payUrl, String returnUrl, String tmnCode, String secretKey, String apiUrl, int updatedBy) throws ValidationException {
        if (payUrl == null || payUrl.trim().isEmpty() ||
            returnUrl == null || returnUrl.trim().isEmpty() ||
            tmnCode == null || tmnCode.trim().isEmpty() ||
            apiUrl == null || apiUrl.trim().isEmpty()) {
            throw new ValidationException("Vui lòng nhập đầy đủ các trường bắt buộc.");
        }

        try (Connection conn = DatabaseUtil.getConnection()) {
            conn.setAutoCommit(false);
            try {
                configDAO.updateConfigValue("vnpay.payUrl", payUrl.trim(), updatedBy, conn);
                configDAO.updateConfigValue("vnpay.returnUrl", returnUrl.trim(), updatedBy, conn);
                configDAO.updateConfigValue("vnpay.tmnCode", tmnCode.trim(), updatedBy, conn);
                configDAO.updateConfigValue("vnpay.apiUrl", apiUrl.trim(), updatedBy, conn);
                
                // Only update secretKey if it's provided and not a mask
                if (secretKey != null && !secretKey.trim().isEmpty() && !secretKey.matches("^•+$") && !secretKey.matches("^\\*+$")) {
                    configDAO.updateConfigValue("vnpay.secretKey", secretKey.trim(), updatedBy, conn);
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                logger.error("Database error while updating vnpay config", e);
                throw new AppException("Lỗi khi cập nhật cấu hình VNPay", e);
            }
        } catch (SQLException e) {
            logger.error("Database connection error", e);
            throw new AppException("Lỗi kết nối cơ sở dữ liệu", e);
        }
    }
}
