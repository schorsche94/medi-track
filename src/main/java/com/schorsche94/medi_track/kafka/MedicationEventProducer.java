package com.schorsche94.medi_track.kafka;

import com.schorsche94.medi_track.domain.model.MedicationIntake;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
public class MedicationEventProducer {
    private static final DateTimeFormatter MESSAGE_KEY_FORMATTER =
            DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm'Z'").withZone(ZoneOffset.UTC);

    private final KafkaTemplate<String, MedicationIntake> medicationEvents;

    public MedicationEventProducer(KafkaTemplate<String, MedicationIntake> kafkaTemplate) {
        this.medicationEvents = kafkaTemplate;
    }

    public CompletableFuture<SendResult<String, MedicationIntake>> send(MedicationIntake message) {
        String key = MESSAGE_KEY_FORMATTER.format(message.getScheduledAt());
        return medicationEvents.send("medication-events", key, message);
    }

    public void sendAll(List<MedicationIntake> messages) {
        messages.forEach(this::send);
    }
}
