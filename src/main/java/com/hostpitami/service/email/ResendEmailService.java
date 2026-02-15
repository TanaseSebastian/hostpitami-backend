package com.hostpitami.service.email;

import com.resend.Resend;
import com.resend.services.emails.model.Attachment;
import com.resend.services.emails.model.SendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Profile("prod")
public class ResendEmailService implements EmailService {

    private final Resend resend;

    public ResendEmailService(@Value("${resend.api.key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetUrl) {

        String html = """
                <div style="font-family: Arial, sans-serif;">
                    <h2>Reimposta la password</h2>
                    <p>Clicca il pulsante sotto per reimpostare la password:</p>
                    <a href="%s"
                       style="display:inline-block;padding:12px 20px;
                              background:#111827;color:white;
                              text-decoration:none;border-radius:6px;">
                        Reset password
                    </a>
                    <p style="margin-top:20px;font-size:12px;color:#666;">
                        Il link scade tra 30 minuti.
                    </p>
                </div>
                """.formatted(resetUrl);

        SendEmailRequest request = SendEmailRequest.builder()
                .from("Hostpitami <onboarding@resend.dev>")
                .to(toEmail)
                .subject("Reimposta la password")
                .html(html)
                .build();

        resend.emails().send(request);
    }

    @Override
    public void sendBookingConfirmation(String to, String subject, String voucherHtml, String calendarIcs) {
        try {
            String icsBase64 = Base64.getEncoder().encodeToString(
                    calendarIcs.getBytes(StandardCharsets.UTF_8)
            );

            Attachment icsAttachment = Attachment.builder()
                    .fileName("booking.ics")
                    .content(icsBase64)
                    .build();

            SendEmailRequest request = SendEmailRequest.builder()
                    .from("Hostpitami <onboarding@resend.dev>")
                    .to(java.util.List.of(to))
                    .subject(subject)
                    .html(voucherHtml)
                    .attachments(java.util.List.of(icsAttachment))
                    .build();

            resend.emails().send(request);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send booking confirmation (PROD/Resend)", e);
        }
    }
}
