package com.schorsche94.medi_track.kafka;

import com.schorsche94.medi_track.domain.model.Medication;
import com.schorsche94.medi_track.domain.model.MedicationIntake;
import com.schorsche94.medi_track.domain.model.MedicationIntakeDetails;
import com.schorsche94.medi_track.telegram_bot.service.TelegramMedicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.ZoneId;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class MedicationEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(MedicationEventConsumer.class);
    private static final DateTimeFormatter SCHEDULED_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");

    private final TelegramClient telegramClient;

    public MedicationEventConsumer(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    @KafkaListener(
            topics = "medication-events",
            groupId = "${spring.kafka.consumer.group-id:medi-track}"
    )
    public void consume(MedicationIntake intake) {
        MedicationIntakeDetails details = intake.getMedication();
        if (details == null) {
            log.error("Medication intake {} has no medication details", intake.getId());
            return;
        }

        Medication medication = Medication.builder()
                .id(details.getId())
                .createdDate(details.getCreatedDate())
                .updatedDate(details.getUpdatedDate())
                .name(details.getName())
                .activeSubstance(details.getActiveSubstance())
                .dosage(details.getDosage())
                .form(details.getForm())
                .category(details.getCategory())
                .description(details.getDescription())
                .instructions(details.getInstructions())
                .addedBy(details.getAddedBy())
                .build();

        String scheduledDisplayTime = intake.getScheduledLocal() != null
                ? SCHEDULED_TIME_FORMATTER.format(LocalDateTime.parse(intake.getScheduledLocal()))
                : SCHEDULED_TIME_FORMATTER.format(intake.getScheduledAt().atZone(
                        ZoneId.of(intake.getTimeZone() != null ? intake.getTimeZone() : "Europe/Chisinau")));

        StringBuilder message = new StringBuilder("⏰ <b>Medication reminder</b>\n")
                .append("<b>").append(escapeHtml(details.getName())).append("</b>\n")
                .append("Dose: ").append(escapeHtml(intake.getDosage())).append("\n")
                .append("Scheduled: ").append(scheduledDisplayTime);

        if (details.getIntakeInstruction() != null) {
            message.append("\nInstruction: ").append(details.getIntakeInstruction());
        }
        if (details.getCustomInstruction() != null && !details.getCustomInstruction().isBlank()) {
            message.append("\n").append(escapeHtml(details.getCustomInstruction()));
        }

        try {
            TelegramMedicationService.sendMedicationIntake(
                    intake.getId(),
                    Long.valueOf(intake.getUserId()),
                    telegramClient,
                    message,
                    medication
            );
            log.info("Sent medication reminder for intake {} to chat {}", intake.getId(), intake.getUserId());
        } catch (TelegramApiException e) {
            throw new IllegalStateException("Failed to send medication reminder for intake " + intake.getId(), e);
        }
    }

    private String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
