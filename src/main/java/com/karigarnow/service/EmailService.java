package com.karigarnow.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    public boolean sendOtpEmail(String toEmail, String otp, String purpose) {
        String subject = "KarigarNow - Your " + purpose + " OTP: " + otp;
        String text = "Namaste,\n\n"
                + "Your 6-digit verification OTP for " + purpose + " on KarigarNow is: " + otp + "\n\n"
                + "This OTP is valid for 5 minutes. Please do not share this OTP with anyone.\n\n"
                + "Team KarigarNow\n"
                + "Empowering Skilled Hands across India";

        log.info("[OTP NOTIFICATION] To: {} | Purpose: {} | OTP: {}", toEmail, purpose, otp);

        if (mailSender == null || fromEmail == null || fromEmail.isBlank()) {
            log.warn("JavaMailSender or spring.mail.username is not configured. OTP printed in logs for testing.");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Successfully sent OTP email to {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email via SMTP to {}: {}. OTP is still valid: {}", toEmail, e.getMessage(), otp);
            return false;
        }
    }
}
