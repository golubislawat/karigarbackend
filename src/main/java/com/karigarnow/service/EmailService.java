package com.karigarnow.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Value("${spring.mail.username:arybislawat@gmail.com}")
    private String fromEmail;

    /**
     * Send 6-digit verification OTP for Sign up / Registration and Sign in / Login.
     * Works for both Consumers and Thekedars.
     * Runs asynchronously so the user experiences zero latency (<50ms API response).
     */
    @Async
    public void sendOtpEmail(String toEmail, String otp, String purpose) {
        if (toEmail == null || toEmail.isBlank()) return;

        String cleanEmail = toEmail.trim();
        String formattedPurpose = "Registration".equalsIgnoreCase(purpose) || "REGISTER".equalsIgnoreCase(purpose)
                ? "Account Registration"
                : "Sign In";

        String subject = "KarigarNow - Your Verification Code: " + otp;

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="utf-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
            </head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f7f9f8; margin: 0; padding: 24px; color: #1a1a1a;">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 20px; border: 1px solid #e5e9e7; box-shadow: 0 4px 20px rgba(0,0,0,0.06); overflow: hidden;">
                <tr>
                  <td style="padding: 28px 32px; background: #0f2e23;">
                    <h1 style="margin: 0; font-size: 26px; font-weight: 900; color: #ffffff; letter-spacing: -0.5px;">Karigar<span style="color: #22c55e;">Now</span></h1>
                    <p style="margin: 6px 0 0 0; font-size: 13px; color: rgba(255,255,255,0.7);">Empowering India's Skilled Workforce</p>
                  </td>
                </tr>
                <tr>
                  <td style="padding: 32px;">
                    <h2 style="margin: 0 0 12px 0; font-size: 18px; font-weight: 700; color: #1a1a1a;">%s Verification</h2>
                    <p style="margin: 0 0 20px 0; font-size: 14px; line-height: 1.6; color: #4a5568;">
                      Namaste,<br><br>
                      Use the 6-digit One-Time Password (OTP) below to complete your <strong>%s</strong> on KarigarNow.
                    </p>
                    <div style="background: #f0fdf4; border: 2px dashed #22c55e; border-radius: 14px; padding: 20px; text-align: center; margin: 0 0 20px 0;">
                      <span style="font-family: 'Courier New', Courier, monospace; font-size: 38px; font-weight: 900; letter-spacing: 12px; color: #166534; display: inline-block;">%s</span>
                    </div>
                    <div style="background: #f8fafc; border-radius: 10px; padding: 14px 18px; margin: 0 0 20px 0; border: 1px solid #e2e8f0;">
                      <p style="margin: 0; font-size: 13px; color: #64748b; line-height: 1.5;">
                        ⏱️ This verification code is valid for <strong>5 minutes</strong>.<br>
                        🔒 For your safety, do not share this OTP with anyone.
                      </p>
                    </div>
                    <p style="margin: 0; font-size: 13px; color: #94a3b8; line-height: 1.5;">
                      If you did not request this verification, you can safely ignore this email.
                    </p>
                  </td>
                </tr>
                <tr>
                  <td style="padding: 20px 32px; background: #f8faf9; border-top: 1px solid #edf2f0; text-align: center;">
                    <p style="margin: 0; font-size: 12px; color: #94a3b8;">
                      © 2026 KarigarNow India. All rights reserved.
                    </p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(formattedPurpose, formattedPurpose, otp);

        String textContent = "Namaste,\n\n"
                + "Your 6-digit verification OTP for " + formattedPurpose + " on KarigarNow is: " + otp + "\n\n"
                + "This OTP is valid for 5 minutes. Please do not share this OTP with anyone.\n\n"
                + "Team KarigarNow\n"
                + "Empowering Skilled Hands across India";

        sendHtmlEmail(cleanEmail, subject, htmlContent, textContent);
    }

    /**
     * Send 4-digit arrival verification code to consumer when job is accepted/dispatched.
     */
    @Async
    public void sendBookingOtpToConsumer(String toEmail, String consumerName, String serviceName, String thekedarName, String otp) {
        if (toEmail == null || toEmail.isBlank()) return;

        String cleanEmail = toEmail.trim();
        String subject = "KarigarNow - Arrival Verification Code: " + otp + " for " + serviceName;
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="utf-8"></head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f7f9f8; margin: 0; padding: 24px;">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 20px; border: 1px solid #e5e9e7; overflow: hidden;">
                <tr>
                  <td style="padding: 28px 32px; background: #0f2e23;">
                    <h1 style="margin: 0; font-size: 24px; font-weight: 900; color: #ffffff;">Karigar<span style="color: #22c55e;">Now</span></h1>
                    <p style="margin: 4px 0 0 0; font-size: 13px; color: rgba(255,255,255,0.7);">Order Confirmed & Arrival Code</p>
                  </td>
                </tr>
                <tr>
                  <td style="padding: 32px;">
                    <p style="margin: 0 0 16px 0; font-size: 15px; color: #1a1a1a;">
                      Namaste <strong>%s</strong>,
                    </p>
                    <p style="margin: 0 0 20px 0; font-size: 14px; line-height: 1.6; color: #4a5568;">
                      Great news! Your booking for <strong>%s</strong> has been accepted by contractor <strong>%s</strong>.
                    </p>
                    <div style="background: #f0fdf4; border: 2px dashed #22c55e; border-radius: 14px; padding: 20px; text-align: center; margin: 0 0 20px 0;">
                      <p style="margin: 0 0 8px 0; font-size: 11px; font-weight: 800; letter-spacing: 2px; color: #166534; text-transform: uppercase;">Arrival Verification Code</p>
                      <span style="font-family: 'Courier New', Courier, monospace; font-size: 38px; font-weight: 900; letter-spacing: 12px; color: #166534;">%s</span>
                    </div>
                    <div style="background: #fefce8; border: 1px solid #fef08a; border-radius: 10px; padding: 14px 18px; margin: 0 0 20px 0;">
                      <p style="margin: 0; font-size: 13px; color: #854d0e; line-height: 1.5;">
                        ⚠️ <strong>Important:</strong> Enter or share this code with the Karigar team <em>only after they physically arrive</em> at your site.
                      </p>
                    </div>
                    <p style="margin: 0; font-size: 13px; color: #94a3b8;">
                      Team KarigarNow
                    </p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                consumerName != null ? consumerName : "Customer",
                serviceName != null ? serviceName : "Service",
                thekedarName != null ? thekedarName : "Verified Contractor",
                otp
            );

        String textContent = "Namaste " + (consumerName != null ? consumerName : "Customer") + ",\n\n"
                + "Your booking for " + serviceName + " has been accepted by " + thekedarName + ".\n\n"
                + "YOUR ARRIVAL VERIFICATION OTP: " + otp + "\n\n"
                + "Please share this code only after the team physically arrives at your site.\n\n"
                + "Team KarigarNow";

        sendHtmlEmail(cleanEmail, subject, htmlContent, textContent);
    }

    /**
     * Send Job Alert to Thekedar when booking is confirmed.
     */
    @Async
    public void sendBookingAlertToThekedar(String toEmail, String thekedarName, String serviceName, String consumerName) {
        if (toEmail == null || toEmail.isBlank()) return;

        String cleanEmail = toEmail.trim();
        String subject = "KarigarNow - Job Confirmed: " + serviceName;
        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="utf-8"></head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f7f9f8; margin: 0; padding: 24px;">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 20px; border: 1px solid #e5e9e7; padding: 32px;">
                <tr>
                  <td>
                    <h2 style="margin: 0 0 12px 0; color: #0f2e23;">Job Confirmed: %s</h2>
                    <p style="margin: 0 0 16px 0; font-size: 14px; color: #4a5568; line-height: 1.6;">
                      Namaste <strong>%s</strong>,<br><br>
                      You have accepted the booking for <strong>%s</strong> requested by <strong>%s</strong>.<br>
                      The Arrival Verification OTP has been securely sent to the consumer.<br>
                      Please dispatch your workers and collect the OTP upon arrival to start the work session.
                    </p>
                    <p style="margin: 0; font-size: 13px; color: #94a3b8;">
                      Team KarigarNow
                    </p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                serviceName != null ? serviceName : "Service",
                thekedarName != null ? thekedarName : "Contractor",
                serviceName != null ? serviceName : "Service",
                consumerName != null ? consumerName : "Customer"
            );

        sendHtmlEmail(cleanEmail, subject, htmlContent, "Job Confirmed: " + serviceName);
    }

    /**
     * Send completion confirmation to Consumer and Thekedar.
     */
    @Async
    public void sendJobCompletedEmail(String toEmail, String recipientName, String serviceName, String amount, boolean isThekedar) {
        if (toEmail == null || toEmail.isBlank()) return;

        String cleanEmail = toEmail.trim();
        String subject = "KarigarNow - Job Completed: " + serviceName;
        String messageLine = isThekedar
                ? "Payment of <strong>" + amount + "</strong> has been released to your KarigarNow payout balance."
                : "Payment of <strong>" + amount + "</strong> has been released to the contractor. Thank you for choosing KarigarNow!";

        String htmlContent = """
            <!DOCTYPE html>
            <html>
            <head><meta charset="utf-8"></head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background-color: #f7f9f8; margin: 0; padding: 24px;">
              <table width="100%" border="0" cellspacing="0" cellpadding="0" style="max-width: 520px; margin: 0 auto; background: #ffffff; border-radius: 20px; border: 1px solid #e5e9e7; padding: 32px;">
                <tr>
                  <td>
                    <h2 style="margin: 0 0 12px 0; color: #0f2e23;">Work Completed: %s</h2>
                    <p style="margin: 0 0 16px 0; font-size: 14px; color: #4a5568; line-height: 1.6;">
                      Namaste <strong>%s</strong>,<br><br>
                      The job for <strong>%s</strong> has been successfully finalized.<br>
                      %s
                    </p>
                    <p style="margin: 0; font-size: 13px; color: #94a3b8;">
                      Team KarigarNow
                    </p>
                  </td>
                </tr>
              </table>
            </body>
            </html>
            """.formatted(
                serviceName != null ? serviceName : "Service",
                recipientName != null ? recipientName : "User",
                serviceName != null ? serviceName : "Service",
                messageLine
            );

        sendHtmlEmail(cleanEmail, subject, htmlContent, "Job Completed: " + serviceName);
    }

    /**
     * Internal helper sending via Jakarta Mail (MimeMessage with MimeMessageHelper).
     */
    private void sendHtmlEmail(String toEmail, String subject, String htmlContent, String fallbackText) {
        log.info("[JAKARTA MAIL SENDING] To: {} | Subject: {}", toEmail, subject);

        if (mailSender == null) {
            log.warn("JavaMailSender is not configured. Email logged to console.");
            return;
        }

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            String sender = (fromEmail != null && !fromEmail.isBlank()) ? fromEmail.trim() : "arybislawat@gmail.com";
            helper.setFrom(new InternetAddress(sender, "KarigarNow"));
            helper.setTo(toEmail.trim());
            helper.setSubject(subject);
            helper.setText(fallbackText, htmlContent);

            mailSender.send(mimeMessage);
            log.info("Successfully delivered Jakarta Mail to: {}", toEmail);
        } catch (Exception e) {
            log.error("Failed to send Jakarta Mail to {}: {}", toEmail, e.getMessage(), e);
        }
    }
}
