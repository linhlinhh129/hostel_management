package com.quanlyphongtro.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

public final class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final com.quanlyphongtro.dao.SystemConfigDAO configDAO = new com.quanlyphongtro.dao.SystemConfigDAO();

    private EmailService() {}

    private static Session getEmailSession() {
        String host = configDAO.getConfigValue("email.host");
        String port = configDAO.getConfigValue("email.port");
        if (port == null) port = "587";
        String smtpUser = configDAO.getConfigValue("email.username");
        String smtpPass = configDAO.getConfigValue("email.password");

        if (host == null || smtpUser == null || smtpPass == null) {
            logger.error("[EmailService] Config incomplete — host={}, user={}, pass={}",
                host, smtpUser, smtpPass == null ? "NULL" : "****");
            return null;
        }

        final String finalPass = smtpPass.replace(" ", "");
        Properties mailProps = new Properties();
        mailProps.put("mail.smtp.auth", "true");
        mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.starttls.required", "true");
        mailProps.put("mail.smtp.host", host);
        mailProps.put("mail.smtp.port", port);
        mailProps.put("mail.smtp.ssl.trust", host);
        mailProps.put("mail.smtp.ssl.protocols", "TLSv1.2");
        mailProps.put("mail.smtp.connectiontimeout", "10000");
        mailProps.put("mail.smtp.timeout", "10000");

        final String finalUser = smtpUser;
        return Session.getInstance(mailProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(finalUser, finalPass);
            }
        });
    }

    /**
     * Sends a temporary password email asynchronously.
     * Failures are logged as warnings and do NOT propagate to the caller.
     */
    public static void sendTempPassword(String toEmail, String fullName,
                                        String username, String tempPassword) {
        new Thread(() -> {
            try {
                Session session = getEmailSession();
                if (session == null) return;
                
                String smtpUser = configDAO.getConfigValue("email.username");
                String from = configDAO.getConfigValue("email.from");
                if (from == null) from = smtpUser;

                logger.info("Attempting to send temp password email to {}", maskEmail(toEmail));

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from, "Quản lý Nhà trọ"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Tài khoản đăng nhập hệ thống Quản lý nhà trọ");

                String body = buildTempPasswordBody(fullName, username, tempPassword);
                message.setContent(body, "text/html; charset=UTF-8");

                Transport.send(message);
                logger.info("Temp password email sent to {}", maskEmail(toEmail));
            } catch (Exception e) {
                logger.error("[EmailService] Unexpected error sending to {}: {}", maskEmail(toEmail), e.getMessage(), e);
            }
        }, "email-sender-temp-pwd").start();
    }

    public static void sendResetLink(String toEmail, String resetLink) {
        new Thread(() -> {
            try {
                Session session = getEmailSession();
                if (session == null) return;
                
                String smtpUser = configDAO.getConfigValue("email.username");
                String from = configDAO.getConfigValue("email.from");
                if (from == null) from = smtpUser;

                logger.info("Attempting to send reset password email to {}", maskEmail(toEmail));

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from, "Quản lý Nhà trọ"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Khôi phục mật khẩu - Quản lý Nhà trọ");
                
                String htmlContent = "<h3>Yêu cầu khôi phục mật khẩu</h3>"
                        + "<p>Bạn đã yêu cầu khôi phục mật khẩu. Vui lòng click vào đường link bên dưới để tiến hành đổi mật khẩu mới:</p>"
                        + "<p><a href='" + resetLink + "'>" + resetLink + "</a></p>"
                        + "<p><strong>Lưu ý:</strong> Link này chỉ có hiệu lực trong vòng 15 phút. Nếu bạn không yêu cầu khôi phục, vui lòng bỏ qua email này.</p>";

                message.setContent(htmlContent, "text/html; charset=UTF-8");

                Transport.send(message);
                logger.info("Reset password email sent to {}", maskEmail(toEmail));
            } catch (Exception e) {
                logger.error("[EmailService] Unexpected error sending to {}: {}", maskEmail(toEmail), e.getMessage(), e);
            }
        }, "email-sender-reset-pwd").start();
    }

    private static String buildTempPasswordBody(String fullName, String username, String tempPassword) {
        return "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;\">"
                + "<h2 style=\"color: #00d4a4; text-align: center;\">Chào mừng thành viên mới!</h2>"
                + "<p>Xin chào <strong>" + escapeHtml(fullName) + "</strong>,</p>"
                + "<p>Tài khoản của bạn đã được tạo thành công trên hệ thống Quản lý nhà trọ bởi Ban Quản Lý.</p>"
                + "<p>Dưới đây là thông tin đăng nhập tạm thời của bạn để truy cập hệ thống:</p>"
                + "<div style=\"background-color: #f8fafc; padding: 15px; border-radius: 6px; border-left: 4px solid #00d4a4; margin: 20px 0;\">"
                + "<p style=\"margin: 5px 0;\"><strong>Trang đăng nhập:</strong> <a href=\"http://localhost:8080/hostel-management/login\" style=\"color: #00b48a;\">http://localhost:8080/hostel-management/login</a></p>"
                + "<p style=\"margin: 5px 0;\"><strong>Tên đăng nhập (Email):</strong> <span style=\"font-family: monospace; font-weight: bold;\">" + escapeHtml(username) + "</span></p>"
                + "<p style=\"margin: 5px 0;\"><strong>Mật khẩu tạm thời:</strong> <span style=\"font-family: monospace; font-weight: bold; color: #dc2626;\">" + escapeHtml(tempPassword) + "</span></p>"
                + "</div>"
                + "<p style=\"color: #475569; font-size: 0.875rem;\"><em>* Lưu ý: Bạn bắt buộc phải đổi mật khẩu trong lần đăng nhập đầu tiên để bảo mật tài khoản.</em></p>"
                + "<hr style=\"border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;\"/>"
                + "<p style=\"font-size: 0.8125rem; color: #94a3b8; text-align: center;\">Đây là email tự động từ hệ thống Quản lý Nhà trọ. Vui lòng không trả lời email này.</p>"
                + "</div>";
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;");
    }

    private static String maskEmail(String email) {
        if (email == null || !email.contains("@")) return email;
        String[] parts = email.split("@");
        if (parts[0].length() <= 2) return parts[0] + "****@" + parts[1];
        return parts[0].charAt(0) + "****" + parts[0].charAt(parts[0].length() - 1) + "@" + parts[1];
    }
}
