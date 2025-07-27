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
        final String password = "ogtj ihgd xyud yeje"; // Mật khẩu ứng dụng

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

    /**
     * Send password reset code to user's email
     * 
     * @param recipientEmail User's email address
     * @param resetCode      Password reset code
     */
    public static void sendPasswordResetEmail(String recipientEmail, String resetCode) {

        final String fromEmail = "lackguku1@gmail.com";
        final String password = "jslb ugfm lqfw xldg";

        String subject = "LightHouse Password Reset Code";

        String content = "<!DOCTYPE html>"
                + "<html><head><style>"
                + "body { font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px; }"
                + ".container { background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto; }"
                + ".header { background: linear-gradient(135deg, #e83e8c, #fd86b3); padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }"
                + ".header h1 { color: white; margin: 0; font-size: 24px; }"
                + ".content { padding: 20px; }"
                + ".code-box { font-size: 32px; font-weight: bold; color: #e83e8c; margin: 20px 0; text-align: center; letter-spacing: 5px; }"
                + ".note { background-color: #f8f8f8; padding: 15px; border-radius: 5px; margin-top: 20px; font-size: 14px; color: #666; }"
                + ".footer { text-align: center; margin-top: 20px; font-size: 14px; color: #999; }"
                + "</style></head><body>"
                + "<div class='container'>"
                + "<div class='header'><h1>Password Reset Request</h1></div>"
                + "<div class='content'>"
                + "<p>Hello,</p>"
                + "<p>We received a request to reset your password for your LightHouse account. Please use the following code to complete the process:</p>"
                + "<div class='code-box'>" + resetCode + "</div>"
                + "<p>This code will expire in 10 minutes for security reasons.</p>"
                + "<div class='note'><strong>Note:</strong> If you didn't request this password reset, please ignore this email or contact our support team if you have concerns.</div>"
                + "</div>"
                + "<div class='footer'>LightHouse Learning Platform &copy; 2025</div>"
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
        try {
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=UTF-8");

            Transport.send(message);
        } catch (MessagingException e) {
            System.out.println("Error sendPasswordResetEmail: " + e);
        }
    }

    /**
     * Send new password to user's email
     * 
     * @param recipientEmail User's email address
     * @param newPassword    New password
     */
    public static void sendNewPasswordEmail(String recipientEmail, String newPassword) {

        final String fromEmail = "lackguku1@gmail.com";
        final String password = "jslb ugfm lqfw xldg";

        String subject = "Your New LightHouse Password";

        String content = "<!DOCTYPE html>"
                + "<html><head><style>"
                + "body { font-family: Arial, sans-serif; background-color: #f8f8f8; padding: 20px; }"
                + ".container { background-color: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); max-width: 600px; margin: 0 auto; }"
                + ".header { background: linear-gradient(135deg, #e83e8c, #fd86b3); padding: 20px; border-radius: 10px 10px 0 0; text-align: center; }"
                + ".header h1 { color: white; margin: 0; font-size: 24px; }"
                + ".content { padding: 20px; }"
                + ".password-box { font-size: 24px; font-weight: bold; color: #e83e8c; margin: 20px 0; text-align: center; background-color: #f8f8f8; padding: 15px; border-radius: 5px; }"
                + ".warning { background-color: #fff3cd; padding: 15px; border-radius: 5px; margin-top: 20px; font-size: 14px; color: #856404; }"
                + ".footer { text-align: center; margin-top: 20px; font-size: 14px; color: #999; }"
                + ".button { display: inline-block; background: linear-gradient(135deg, #e83e8c, #fd86b3); color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px; margin-top: 15px; }"
                + "</style></head><body>"
                + "<div class='container'>"
                + "<div class='header'><h1>Your Password Has Been Reset</h1></div>"
                + "<div class='content'>"
                + "<p>Hello,</p>"
                + "<p>Your password has been successfully reset. Here is your new password:</p>"
                + "<div class='password-box'>" + newPassword + "</div>"
                + "<p>Please use this password to log in to your account. We recommend changing it to a more memorable password after logging in.</p>"
                + "<div class='warning'><strong>Important:</strong> For security reasons, please change this password immediately after logging in.</div>"
                + "<p style='text-align: center;'><a href='" + System.getenv("APP_URL")
                + "/login' class='button'>Login Now</a></p>"
                + "</div>"
                + "<div class='footer'>LightHouse Learning Platform &copy; 2025</div>"
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
        try {
            message.setFrom(new InternetAddress(fromEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (MessagingException e) {
            System.out.println("Error sendNewPasswordEmail: " + e);
        }
    }

}
