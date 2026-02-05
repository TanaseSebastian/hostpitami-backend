package com.hostpitami.service.mail;

import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Override
    public void sendBookingConfirmation(String to, String subject, String voucherHtml, String calendarIcs) {
        System.out.println("SEND MAIL TO: " + to);
        System.out.println("SUBJECT: " + subject);
        System.out.println("VOUCHER HTML LEN: " + voucherHtml.length());
        System.out.println("ICS LEN: " + calendarIcs.length());
    }
}