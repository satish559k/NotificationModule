package com.NotificationModule.NotificationModule.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    private static final int GMAIL_SMTP_PORT = 587;

    @Value("${udeesa.email.sender.host}")
    private String host;

    @Value("${udeesa.email.sender.user}")
    private String user;

    @Value("${udeesa.email.sender.password}")
    private String password;

    @Bean
    public JavaMailSender getJavaMailSender() {
        try {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost(host);
            mailSender.setPort(GMAIL_SMTP_PORT);
            mailSender.setUsername(user);
            mailSender.setPassword(password);

            Properties property = mailSender.getJavaMailProperties();
            property.put("mail.transport.protocol","smtp");
            property.put("mail.smtp.auth","true");
            property.put("mail.smtp.starttls.enable","true");
            property.put("mail.debug","true");
            property.put("mail.smtp.keepalive", "true"); // reuse connection
            //property.put("mail.smtp.ssl.enable","true");

            return mailSender;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
