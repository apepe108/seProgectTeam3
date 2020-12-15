package it.unisa.diem.se.team3.servlet;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class SendMail {
    private final Session session;
    private final String mail;

    public SendMail(String mail, String password) {
        String host = "smtp.gmail.com"; // sending email from through gmail smtp

        Properties properties = System.getProperties();

        // Setup mail server
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        this.mail = mail;
        this.session = Session.getInstance(properties, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(mail, password);
            }
        });
    }

    public boolean send(String to, String cpy, String subject, String html) {
        // session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(mail));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.addRecipient(Message.RecipientType.CC, new InternetAddress(cpy));
            message.setSubject(subject);

            Multipart multipart = new MimeMultipart();
            MimeBodyPart htmlPart = new MimeBodyPart();

            htmlPart.setContent(html, "text/html");

            multipart.addBodyPart(htmlPart);

            message.setContent(multipart);

            Transport.send(message);
            return true;
        } catch (MessagingException mex) {
            return false;
        }
    }
}
