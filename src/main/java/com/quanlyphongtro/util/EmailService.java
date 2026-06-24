package com.quanlyphongtro.util;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.InputStream;
import java.util.Properties;

public final class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    private EmailService() {}

    private static Properties loadEmailProperties() {
        Properties props = new Properties();
        try (InputStream in = EmailService.class
                .getClassLoader()
                .getResourceAsStream("email.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                logger.warn("email.properties not found on classpath");
            }
        } catch (Exception e) {
            logger.warn("Failed to load email.properties", e);
        }
        return props;
    }

    private static Session getEmailSession(Properties config) {
        String host = config.getProperty("email.host");
        String port = config.getProperty("email.port", "587");
        String smtpUser = config.getProperty("email.username");
        String smtpPass = config.getProperty("email.password");

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
                Properties config = loadEmailProperties();
                Session session = getEmailSession(config);
                if (session == null) return;
                
                String smtpUser = config.getProperty("email.username");
                String from = config.getProperty("email.from", smtpUser);

                logger.info("Attempting to send temp password email to {}", maskEmail(toEmail));

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from, "Hostel Management"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("[Hostel Management] Tài khoản nhân sự của bạn đã được tạo");

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
                Properties config = loadEmailProperties();
                Session session = getEmailSession(config);
                if (session == null) return;
                
                String smtpUser = config.getProperty("email.username");
                String from = config.getProperty("email.from", smtpUser);

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
        return "<!DOCTYPE html><html><body style='font-family:Arial,sans-serif;'>" +
            "<h2>Chào mừng " + escapeHtml(fullName) + "!</h2>" +
            "<p>Tài khoản nhân sự của bạn đã được tạo trong hệ thống Hostel Management.</p>" +
            "<table border='1' cellpadding='8' style='border-collapse:collapse;'>" +
            "<tr><td><b>Tên đăng nhập</b></td><td>" + escapeHtml(username) + "</td></tr>" +
            "<tr><td><b>Mật khẩu tạm thời</b></td><td>" + escapeHtml(tempPassword) + "</td></tr>" +
            "</table>" +
            "<p style='color:red;'><b>Vui lòng đăng nhập và thay đổi mật khẩu ngay sau khi nhận được email này.</b></p>" +
            "<p>Trân trọng,<br/>Hệ thống Hostel Management</p>" +
            "</body></html>";
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
