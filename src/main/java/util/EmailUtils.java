package util;

import java.util.Properties;
import javax.mail.*; // 
import javax.mail.internet.*;

/**
 *
 * @author NhiDTY-CE180492
 */
public class EmailUtils {

    private static String code;

    public static void sendVerificationCode(String recipientEmail, String verificationCode)
            throws MessagingException {

        final String fromEmail = "yennhidoan08042004@gmail.com"; // Gmail của bạn
        final String password = "ogtj ihgd xyud yeje";            // Mật khẩu ứng dụng

        String subject = "Your LightHouse Verification Code";

        String content = "<!DOCTYPE html>"
                + "<html><head><style>"
                + "body { font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px; }"
                + ".container { background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }"
                + ".code-box { font-size: 24px; font-weight: bold; color: #e83e8c; margin: 20px 0; }"
                + "</style></head><body>"
                + "<div class='container'>"
                + "<h2>Welcome to LightHouse 🌟</h2>"
                + "<p>Use the following verification code to activate your account:</p>"
                + "<div class='code-box'>" + verificationCode + "</div>"
                + "<p>This code will expire soon. Please verify your account promptly.</p>"
                + "<p>Regards,<br>LightHouse Team</p>"
                + "</div></body></html>";

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
        message.setSubject(subject);
        message.setContent(content, "text/html; charset=UTF-8");

        Transport.send(message);
    }

    public static void sendVerificationEmail(String email, String verificationCode) {
        try {
            sendVerificationCode(email, verificationCode);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to send verification email: " + e.getMessage());
        }
    }

}
