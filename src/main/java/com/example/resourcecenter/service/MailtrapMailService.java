package com.example.resourcecenter.service;

import io.mailtrap.client.MailtrapClient;
import io.mailtrap.config.MailtrapConfig;
import io.mailtrap.factory.MailtrapClientFactory;
import io.mailtrap.model.request.emails.Address;
import io.mailtrap.model.request.emails.MailtrapMail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailtrapMailService {

    private final MailtrapClient client;

    public MailtrapMailService() {
        MailtrapConfig config = new MailtrapConfig.Builder()
                .sandbox(true)
                .inboxId(3586840L)
                .token("2347073d184bd0fb6ba720c10818c351")
                .build();

        this.client = MailtrapClientFactory.createMailtrapClient(config);
    }

    public void sendVerification(String to, String link) {
        MailtrapMail mail = MailtrapMail.builder()
                .from(new Address("no-reply@resourcecenter.com", "Ресурсний Центр"))
                .to(List.of(new Address(to)))
                .subject("Підтвердження реєстрації")
                .text("Привіт! Щоб завершити реєстрацію, перейди за посиланням: " + link)
                .category("Email Confirmation")
                .build();

        try {
            client.send(mail);
        } catch (Exception e) {
            System.err.println("❌ Не вдалося надіслати лист: " + e.getMessage());
        }
    }
}
