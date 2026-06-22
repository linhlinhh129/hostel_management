package com.quanlyphongtro.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public final class EmailUtil {
    private static final Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    private static final Properties emailProps = new Properties();

    static {
        try (java.io.InputStream is = EmailUtil.class.getClassLoader().getResourceAsStream("email.properties")) {
            if (is != null) {
                emailProps.load(is);
                logger.info("Loaded email.properties successfully");
            } else {
                logger.warn("Could not find email.properties in classpath, using default fallbacks");
            }
        } catch (Exception e) {
            logger.error("Error loading email.properties", e);
        }
    }

    private EmailUtil() {}

    public static void sendTemporaryPasswordEmail(String toEmail, String fullName, String username, String tempPassword) {
        new Thread(() -> {
            String host = emailProps.getProperty("email.host", "smtp.gmail.com");
            String port = emailProps.getProperty("email.port", "587");
            String fromEmail = emailProps.getProperty("email.from", "a2k55ndu@gmail.com");
            String usernameConfig = emailProps.getProperty("email.username", "a2k55ndu@gmail.com");
            String appPassword = emailProps.getProperty("email.password", "khul ujup nmzr bcvu");

            Properties prop = new Properties();
            prop.put("mail.smtp.auth", "true");
            prop.put("mail.smtp.starttls.enable", "true");
            prop.put("mail.smtp.host", host);
            prop.put("mail.smtp.port", port);
            prop.put("mail.smtp.ssl.protocols", "TLSv1.2");

            Session session = Session.getInstance(prop, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(usernameConfig, appPassword);
                }
            });

            try {
                MimeMessage message = new MimeMessage(session);
                message.setFrom(new InternetAddress(fromEmail, "Hostel Management System"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
                message.setSubject("Tài khoản đăng nhập hệ thống Quản lý nhà trọ", "UTF-8");
                
                String htmlContent = "<div style=\"font-family: Arial, sans-serif; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #e2e8f0; border-radius: 8px;\">"
                        + "<h2 style=\"color: #00d4a4; text-align: center;\">Chào mừng cư dân mới!</h2>"
                        + "<p>Xin chào <strong>" + fullName + "</strong>,</p>"
                        + "<p>Tài khoản của bạn đã được tạo thành công trên hệ thống Quản lý nhà trọ bởi Ban Quản Lý.</p>"
                        + "<p>Dưới đây là thông tin đăng nhập tạm thời của bạn để truy cập hệ thống:</p>"
                        + "<div style=\"background-color: #f8fafc; padding: 15px; border-radius: 6px; border-left: 4px solid #00d4a4; margin: 20px 0;\">"
                        + "<p style=\"margin: 5px 0;\"><strong>Trang đăng nhập:</strong> <a href=\"http://localhost:8080/hostel-management/login\" style=\"color: #00b48a;\">http://localhost:8080/hostel-management/login</a></p>"
                        + "<p style=\"margin: 5px 0;\"><strong>Tên đăng nhập (Email):</strong> <span style=\"font-family: monospace; font-weight: bold;\">" + username + "</span></p>"
                        + "<p style=\"margin: 5px 0;\"><strong>Mật khẩu tạm thời:</strong> <span style=\"font-family: monospace; font-weight: bold; color: #dc2626;\">" + tempPassword + "</span></p>"
                        + "</div>"
                        + "<p style=\"color: #475569; font-size: 0.875rem;\"><em>* Lưu ý: Bạn bắt buộc phải đổi mật khẩu trong lần đăng nhập đầu tiên để bảo mật tài khoản.</em></p>"
                        + "<hr style=\"border: none; border-top: 1px solid #e2e8f0; margin: 20px 0;\"/>"
                        + "<p style=\"font-size: 0.8125rem; color: #94a3b8; text-align: center;\">Đây là email tự động từ hệ thống Hostel Management. Vui lòng không trả lời email này.</p>"
                        + "</div>";
                
                message.setContent(htmlContent, "text/html; charset=UTF-8");
                Transport.send(message);
                logger.info("Sent temporary password email successfully to {}", toEmail);
            } catch (Exception e) {
                logger.error("Failed to send temporary password email to {}", toEmail, e);
            }
        }).start();
    }
}
