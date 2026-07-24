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
                                        String username, String tempPassword, String loginLink) {
        new Thread(() -> {
            try {
                Session session = getEmailSession();
                if (session == null) return;
                
                String smtpUser = configDAO.getConfigValue("email.username");
                String from = configDAO.getConfigValue("email.from");
                if (from == null) from = smtpUser;

                logger.info("Attempting to send temp password email to {}", maskEmail(toEmail));

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from, "Quản lý Nhà trọ", "UTF-8"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Tài khoản đăng nhập hệ thống Quản lý nhà trọ", "UTF-8");

                String body = buildTempPasswordBody(fullName, username, tempPassword, loginLink);
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

                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(from, "Quản lý Nhà trọ", "UTF-8"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Khôi phục mật khẩu - Quản lý Nhà trọ", "UTF-8");
                
                String htmlContent = buildResetPasswordBody(resetLink);
                message.setContent(htmlContent, "text/html; charset=UTF-8");

                Transport.send(message);
                logger.info("Reset password email sent to {}", maskEmail(toEmail));
            } catch (Exception e) {
                logger.error("[EmailService] Unexpected error sending to {}: {}", maskEmail(toEmail), e.getMessage(), e);
            }
        }, "email-sender-reset-pwd").start();
    }

    private static String buildTempPasswordBody(String fullName, String username, String tempPassword, String loginLink) {
        String template = loadTemplate("welcome.html");
        return template.replace("{{fullName}}", escapeHtml(fullName))
                       .replace("{{username}}", escapeHtml(username))
                       .replace("{{tempPassword}}", escapeHtml(tempPassword))
                       .replace("{{loginLink}}", loginLink);
    }

    private static String buildResetPasswordBody(String resetLink) {
        String template = loadTemplate("reset-password.html");
        return template.replace("{{resetLink}}", resetLink);
    }

    private static String loadTemplate(String templateName) {
        try (java.io.InputStream is = EmailService.class.getClassLoader().getResourceAsStream("email-templates/" + templateName)) {
            if (is == null) {
                logger.error("Email template not found: " + templateName);
                return "";
            }
            try (java.util.Scanner s = new java.util.Scanner(is, java.nio.charset.StandardCharsets.UTF_8.name())) {
                s.useDelimiter("\\A");
                return s.hasNext() ? s.next() : "";
            }
        } catch (Exception e) {
            logger.error("Error reading email template: " + templateName, e);
            return "";
        }
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
