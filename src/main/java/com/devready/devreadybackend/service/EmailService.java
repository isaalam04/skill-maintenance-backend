package com.devready.devreadybackend.service;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

// handles all outbound email sending via the sendgrid api
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${sendgrid.api.key}")
    private String sendGridApiKey;

    @Value("${sendgrid.from.email}")
    private String fromEmail;

    // sends a password reset email containing the one-time token
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        Email from = new Email(fromEmail, "Skill Maintenance Platform");
        Email to = new Email(toEmail);
        String subject = "Your password reset token";
        Content content = new Content("text/html", buildResetEmailBody(resetToken));
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(sendGridApiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("password reset email sent successfully to {}", toEmail);
            } else {
                log.error("sendgrid returned status {} for {}: {}",
                        response.getStatusCode(), toEmail, response.getBody());
            }
        } catch (IOException e) {
            log.error("failed to send password reset email to {}: {}", toEmail, e.getMessage());
        }
    }

    // builds a clean html email for the password reset token
    private String buildResetEmailBody(String resetToken) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
              <meta charset="UTF-8">
              <style>
                body { font-family: Georgia, serif; background: #f2ede4; margin: 0; padding: 40px 20px; }
                .container { max-width: 480px; margin: 0 auto; background: #ece6da; border: 1px solid #d8d0c0; border-radius: 16px; padding: 40px; }
                .title { font-size: 22px; color: #3a3020; margin-bottom: 8px; font-weight: 400; }
                .subtitle { font-size: 14px; color: #8a7d6a; margin-bottom: 32px; font-style: italic; }
                .token-box { background: #f2ede4; border: 1px solid #d8d0c0; border-radius: 10px; padding: 20px; text-align: center; margin: 24px 0; }
                .token { font-family: monospace; font-size: 22px; color: #3a3020; letter-spacing: 0.15em; font-weight: bold; }
                .note { font-size: 12px; color: #b0a898; margin-top: 24px; line-height: 1.6; }
                .footer { font-size: 11px; color: #c0b8a8; margin-top: 32px; text-align: center; }
              </style>
            </head>
            <body>
              <div class="container">
                <p class="title">Password Reset</p>
                <p class="subtitle">Skill Maintenance Platform</p>
                <p style="font-size: 14px; color: #3a3020; line-height: 1.6;">
                  You requested a password reset. Use the token below to set a new password.
                </p>
                <div class="token-box">
                  <p style="font-size: 11px; color: #8a7d6a; margin-bottom: 10px; text-transform: uppercase; letter-spacing: 0.08em;">
                    Your Reset Token
                  </p>
                  <p class="token">%s</p>
                </div>
                <p class="note">
                  This token expires in 15 minutes and can only be used once.
                  If you didn't request a password reset, you can safely ignore this email.
                </p>
                <p class="footer">Skill Maintenance Platform &mdash; University of Huddersfield</p>
              </div>
            </body>
            </html>
            """.formatted(resetToken);
    }
}