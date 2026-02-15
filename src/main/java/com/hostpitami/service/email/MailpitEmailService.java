package com.hostpitami.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
@Profile("dev")
public class MailpitEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public MailpitEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendPasswordResetEmail(String to, String resetLink) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject("Reset password - Hostpitami (DEV)");
        msg.setText("Clicca qui per resettare la password:\n\n" + resetLink);
        msg.setFrom("no-reply@hostpitami.local");
        mailSender.send(msg);
    }

    @Override
    public void sendBookingConfirmation(String to, String subject, String voucherHtml, String calendarIcs) {
        try {
            MimeMessage message = mailSender.createMimeMessage();

            // multipart=true per allegati
            MimeMessageHelper helper = new MimeMessageHelper(
                    message,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("no-reply@hostpitami.local");

            // HTML body
            helper.setText(voucherHtml, true);

            // Allegato ICS
            byte[] icsBytes = calendarIcs.getBytes(StandardCharsets.UTF_8);
            helper.addAttachment(
                    "booking.ics",
                    new ByteArrayResource(icsBytes),
                    "text/calendar; charset=UTF-8; method=REQUEST"
            );

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send booking confirmation (DEV/Mailpit)", e);
        }
    }
}