/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.time.LocalDateTime;
import javax.mail.Authenticator;
import java.util.Properties;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * EmailService for sending email
 *
 * @author DangPH - CE180896
 */
public class EmailService {
    private static final int LIMIT_MINUS = 15;
    private static final String eFrom = "lackguku1@gmail.com";
    private static final String ePass = "jslb ugfm lqfw xldg";
    
    public static String createToken() {
        return UUID.randomUUID().toString();
    }
    
    public static LocalDateTime expireToken() {
        return LocalDateTime.now().plusMinutes(LIMIT_MINUS);
    }
    
    public static boolean isExpireToken(LocalDateTime time) {
        return LocalDateTime.now().isAfter(time);
    }
    
    public static void sendEmail(String to, String subject, String content) {

        // Properties
        Properties props = new Properties();

        // Using which server to send mail- smtp host
        props.put("mail.smtp.host", "smtp.gmail.com");

        // TLS 587 SSL 465
        props.put("mail.smtp.port", "587");

        // Login
        props.put("mail.smtp.auth", "true");

        props.put("mail.smtp.starttls.enable", "true");

        // Login account
        Authenticator au = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(eFrom, ePass);
            }

        };
        
        Session session = Session.getInstance(props, au);

        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML, charset=UTF-8");
            msg.setFrom(new InternetAddress(eFrom));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            // Title
            msg.setSubject(subject, "UTF-8");
            // Content
            msg.setContent(content, "text/html; charset=UTF-8");
            // Send email
            Transport.send(msg);
        } catch (MessagingException e) {
            System.out.println("Send email failed: ");
            e.printStackTrace();
        }
    }
}
