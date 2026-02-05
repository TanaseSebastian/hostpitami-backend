package com.hostpitami.service.mail;

public interface MailService {
    void sendBookingConfirmation(String to, String subject, String voucherHtml, String calendarIcs);
}