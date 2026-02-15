package com.hostpitami.service.email;

public interface EmailService {
    void sendPasswordResetEmail(String toEmail, String resetUrl);
    void sendBookingConfirmation(String to, String subject, String voucherHtml, String calendarIcs);
}
