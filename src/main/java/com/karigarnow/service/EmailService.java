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

    public boolean sendBookingOtpToConsumer(String toEmail, String consumerName, String serviceName, String thekedarName, String otp) {
        String subject = "KarigarNow - Arrival Verification OTP for " + serviceName + " (#" + otp + ")";
        String text = "Namaste " + (consumerName != null ? consumerName : "Customer") + ",\n\n"
                + "Great news! Your booking for " + serviceName + " has been accepted by contractor " + (thekedarName != null ? thekedarName : "our verified Thekedar") + ".\n\n"
                + "YOUR ARRIVAL VERIFICATION OTP: " + otp + "\n\n"
                + "Please share this 4-digit OTP with the Karigar team when they arrive at your location to begin work.\n\n"
                + "Safety Tip: Only share the OTP after the team physically arrives at your site.\n\n"
                + "Team KarigarNow\n"
                + "Empowering Skilled Hands across India";

        return sendRawEmail(toEmail, subject, text);
    }

    public boolean sendBookingAlertToThekedar(String toEmail, String thekedarName, String serviceName, String consumerName) {
        String subject = "KarigarNow - Job Confirmed: " + serviceName;
        String text = "Namaste " + (thekedarName != null ? thekedarName : "Contractor") + ",\n\n"
                + "You have successfully accepted the booking for " + serviceName + " requested by " + (consumerName != null ? consumerName : "Customer") + ".\n\n"
                + "The Arrival Verification OTP has been securely emailed to the consumer.\n"
                + "Please dispatch your workers and collect the OTP from the consumer upon arrival to start the session.\n\n"
                + "Team KarigarNow";

        return sendRawEmail(toEmail, subject, text);
    }

    public boolean sendJobCompletedEmail(String toEmail, String recipientName, String serviceName, String amount, boolean isThekedar) {
        String subject = "KarigarNow - Job Completed: " + serviceName;
        String text = "Namaste " + (recipientName != null ? recipientName : "") + ",\n\n"
                + "Job for " + serviceName + " has been marked completed!\n"
                + (isThekedar 
                    ? "Payment of " + amount + " has been released to your KarigarNow account.\n"
                    : "Payment of " + amount + " has been released to the contractor.\nThank you for using KarigarNow!\n")
                + "\nTeam KarigarNow";

        return sendRawEmail(toEmail, subject, text);
    }

    private boolean sendRawEmail(String toEmail, String subject, String text) {
        if (toEmail == null || toEmail.isBlank()) return false;
        log.info("[EMAIL NOTIFICATION] To: {} | Subject: {}", toEmail, subject);

        if (mailSender == null || fromEmail == null || fromEmail.isBlank()) {
            log.warn("JavaMailSender is not configured. Email logged to console.");
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            log.info("Email successfully sent to {}", toEmail);
            return true;
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
            return false;
        }
    }
}
