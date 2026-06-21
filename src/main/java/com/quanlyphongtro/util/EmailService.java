package com.quanlyphongtro.util;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    // IMPORTANT: Thay đổi thông tin này bằng email và App Password của bạn
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "atu023@gmail.com";
    private static final String SENDER_PASSWORD = "ywgq bjng ymfo bpol";

    public static void sendResetLink(String toEmail, String resetLink) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, "Quản lý Nhà trọ"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject("Khôi phục mật khẩu - Quản lý Nhà trọ");
            
            String htmlContent = "<h3>Yêu cầu khôi phục mật khẩu</h3>"
                    + "<p>Bạn đã yêu cầu khôi phục mật khẩu. Vui lòng click vào đường link bên dưới để tiến hành đổi mật khẩu mới:</p>"
                    + "<p><a href='" + resetLink + "'>" + resetLink + "</a></p>"
                    + "<p><strong>Lưu ý:</strong> Link này chỉ có hiệu lực trong vòng 15 phút. Nếu bạn không yêu cầu khôi phục, vui lòng bỏ qua email này.</p>";

            message.setContent(htmlContent, "text/html; charset=utf-8");
            
            System.out.println("Đang gửi email thực tế đến: " + toEmail + " ...");
            Transport.send(message);
            System.out.println("Gửi email thành công!");
            
        } catch (Exception e) {
            System.err.println("Lỗi khi gửi email đến " + toEmail);
            e.printStackTrace();
        }
    }
}
