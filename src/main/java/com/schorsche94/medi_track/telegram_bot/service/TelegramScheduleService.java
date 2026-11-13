package com.schorsche94.medi_track.telegram_bot.service;

import com.schorsche94.medi_track.domain.enums.IntakeInstruction;
import com.schorsche94.medi_track.domain.enums.ScheduleStatus;
import com.schorsche94.medi_track.domain.model.Schedule;
import com.schorsche94.medi_track.service.MedicationService;
import com.schorsche94.medi_track.service.ScheduleService;
import com.schorsche94.medi_track.telegram_bot.mapper.TelegramMedicationMapper;
import com.schorsche94.medi_track.telegram_bot.mapper.TelegramScheduleMapper;
import com.schorsche94.medi_track.telegram_bot.model.ConversationState;
import com.schorsche94.medi_track.telegram_bot.model.ScheduleConversation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static com.schorsche94.medi_track.telegram_bot.menu.TelegramMenu.sendMenuIntakeInstructionsForm;
import static com.schorsche94.medi_track.telegram_bot.menu.TelegramMenu.sendMenuStatusForm;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.hideKeyboard;
import static com.schorsche94.medi_track.telegram_bot.utils.TelegramUtils.sendMessage;

@Service
public class TelegramScheduleService {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private TelegramScheduleMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public ScheduleConversation scheduleMedication(Long chatId, Long medicationId, TelegramClient telegramClient) {
        ScheduleConversation scheduleConversation = new ScheduleConversation();
        scheduleConversation.setState(ConversationState.WAITING_DURATION);
        scheduleConversation.setMedicineId(String.valueOf(medicationId));
        scheduleConversation.setChatId(String.valueOf(chatId));

        sendMessage(chatId, "Enter treatment duration:", telegramClient);
        return scheduleConversation;
    }

    @SneakyThrows
    public void handleScheduleConversation(Long chatId, String text, ScheduleConversation scheduleConversation, TelegramClient telegramClient) {
        String oldValue = "";
        switch (scheduleConversation.getState()) {

            case WAITING_DURATION -> {
                if (!text.equals("-")) {
                    Integer duration;
                    try {
                        duration = Integer.valueOf(text);
                    } catch (NumberFormatException e) {
                        duration = null;
                    }
                    if (duration == null || duration <= 0) {
                        sendMessage(chatId, "Treatment duration must be a positive number of days, or - to skip.", telegramClient);
                        return;
                    }
                    scheduleConversation.setDurationDays(duration);
                }
                scheduleConversation.setState(ConversationState.WAITING_START_DATE);

                if (scheduleConversation.getStartDate() != null) {
                    oldValue = " (current: " + scheduleConversation.getStartDate() + ")";
                }
                sendMessage(chatId, "Enter start date(ex: 2026-11-13):" + oldValue, telegramClient);
            }

            case WAITING_START_DATE -> {
                LocalDate startDate = scheduleConversation.getStartDate();
                if (!text.equals("-")) {
                    try {
                        startDate = LocalDate.parse(text);
                    } catch (DateTimeParseException e) {
                        sendMessage(chatId, "Invalid date. Enter the start date as YYYY-MM-DD, or - to keep the current value.", telegramClient);
                        return;
                    }
                }
                if (startDate != null && startDate.isBefore(LocalDate.now())) {
                    sendMessage(chatId, "Start date cannot be in the past. Today is allowed; enter YYYY-MM-DD.", telegramClient);
                    return;
                }
                if (!text.equals("-")) {
                    scheduleConversation.setStartDate(startDate);
                }

                if (scheduleConversation.getDurationDays() != null) {
                    if (startDate == null) {
                        sendMessage(chatId, "A start date is required when treatment duration is set. Enter YYYY-MM-DD.", telegramClient);
                        return;
                    }
                    scheduleConversation.setEndDate(startDate.plusDays(scheduleConversation.getDurationDays() - 1L));
                    scheduleConversation.setState(ConversationState.WAITING_FREQUENCY_PERIOD);
                    if (scheduleConversation.getFrequencyPerDay() != null) {
                        oldValue = " (current: " + scheduleConversation.getFrequencyPerDay() + ")";
                    }
                    sendMessage(chatId, "Frequency (times per day):" + oldValue, telegramClient);
                    return;
                }

                scheduleConversation.setState(ConversationState.WAITING_END_DATE);

                if (scheduleConversation.getEndDate() != null) {
                    oldValue = " (current: " + scheduleConversation.getEndDate() + ")";
                }

                sendMessage(chatId, "Enter end date(ex: 2026-11-13):" + oldValue, telegramClient);
            }

            case WAITING_END_DATE -> {
                LocalDate endDate = scheduleConversation.getEndDate();
                if (!text.equals("-")) {
                    try {
                        endDate = LocalDate.parse(text);
                    } catch (DateTimeParseException e) {
                        sendMessage(chatId, "Invalid date. Enter the end date as YYYY-MM-DD, or - to keep the current value.", telegramClient);
                        return;
                    }
                }
                if (endDate != null) {
                    if (scheduleConversation.getStartDate() == null) {
                        sendMessage(chatId, "Enter a start date before setting an end date.", telegramClient);
                        return;
                    }
                    if (!endDate.isAfter(scheduleConversation.getStartDate())) {
                        sendMessage(chatId, "End date must be after the start date. Enter YYYY-MM-DD.", telegramClient);
                        return;
                    }
                    if (scheduleConversation.getDurationDays() != null
                            && ChronoUnit.DAYS.between(scheduleConversation.getStartDate(), endDate) + 1
                            != scheduleConversation.getDurationDays()) {
                        sendMessage(chatId, "The number of days from start through end, counting both dates, must match the treatment duration ("
                                + scheduleConversation.getDurationDays() + " days). Enter a matching end date.", telegramClient);
                        return;
                    }
                    scheduleConversation.setEndDate(endDate);
                }

                scheduleConversation.setState(ConversationState.WAITING_FREQUENCY_PERIOD);

                if (scheduleConversation.getFrequencyPerDay() != null) {
                    oldValue = " (current: " + scheduleConversation.getFrequencyPerDay() + ")";
                }

                sendMessage(chatId, "Enter frequency(times per day):" + oldValue, telegramClient);

            }

            case WAITING_FREQUENCY_PERIOD -> {
                if (!text.equals("-")) {
                    scheduleConversation.setFrequencyPerDay(Integer.valueOf(text));
                }
                scheduleConversation.setState(ConversationState.WAITING_SCHEDULE);

                if (scheduleConversation.getScheduledTimes() != null) {
                    oldValue = " (current: " + scheduleConversation.getScheduledTimes() + ")";
                }

                sendMessage(chatId, "Enter scheduled dosage:(example:   [{\"time\": \"08:00\", \"dosage\": \"1 tablet\"},\n {\"time\": \"20:00\", \"dosage\": \"500 mg\"}\n] )" + oldValue, telegramClient);

            }

            case WAITING_SCHEDULE -> {
                if (!text.equals("-")) {
                    JsonNode scheduledTimes;
                    try {
                        scheduledTimes = objectMapper.readTree(text);
                    } catch (JsonProcessingException e) {
                        sendMessage(chatId, "Invalid JSON. Enter scheduled dosage as a JSON array, or - to keep the current value.", telegramClient);
                        return;
                    }
                    if (scheduledTimes == null || !scheduledTimes.isArray()) {
                        sendMessage(chatId, "Scheduled dosage must be a JSON array, or enter - to keep the current value.", telegramClient);
                        return;
                    }
                    scheduleConversation.setScheduledTimes(text);
                }
                scheduleConversation.setState(ConversationState.WAITING_INTERVAL);
                if (scheduleConversation.getIntervalDays() != null) {
                    oldValue = " (current: " + scheduleConversation.getIntervalDays() + ")";
                }

                hideKeyboard(chatId, "Enter interval in days (every 2 days):" + oldValue, telegramClient);
            }

            case WAITING_INTERVAL -> {
                if (!text.equals("-")) {
                    scheduleConversation.setIntervalDays(Integer.valueOf(text));
                }
                scheduleConversation.setState(ConversationState.WAITING_PAUSE);
                if (scheduleConversation.getPauseDays() != null) {
                    oldValue = " (current: " + scheduleConversation.getPauseDays() + ")";
                }

                hideKeyboard(chatId, "Enter pause in days (every 2 days):" + oldValue, telegramClient);
            }

            case WAITING_PAUSE -> {
                if (!text.equals("-")) {
                    scheduleConversation.setPauseDays(Integer.valueOf(text));
                }
                scheduleConversation.setState(ConversationState.WAITING_INTAKE_INSTRUCTIONS);
                if (scheduleConversation.getIntakeInstruction() != null) {
                    oldValue = " (current: " + scheduleConversation.getIntakeInstruction() + ")";
                }

                sendMenuIntakeInstructionsForm(chatId, telegramClient, IntakeInstruction.values(), oldValue);

            }

            case WAITING_INTAKE_INSTRUCTIONS -> {
                if (!text.equals("-")) {
                    scheduleConversation.setIntakeInstruction(IntakeInstruction.valueOf(text));
                }
                scheduleConversation.setState(ConversationState.WAITING_CUSTOM_INSTRUCTIONS);
                if (scheduleConversation.getCustomInstruction() != null) {
                    oldValue = " (current: " + scheduleConversation.getCustomInstruction() + ")";
                }

                hideKeyboard(chatId, "Enter custom instructions:" + oldValue, telegramClient);

            }

            case WAITING_CUSTOM_INSTRUCTIONS -> {
                if (!text.equals("-")) {
                    scheduleConversation.setCustomInstruction(text);
                }
                scheduleConversation.setState(ConversationState.WAITING_STATUS);
                if (scheduleConversation.getStatus() != null) {
                    oldValue = " (current: " + scheduleConversation.getStatus() + ")";
                }

                sendMenuStatusForm(chatId, telegramClient, ScheduleStatus.values(), oldValue);

            }
            case WAITING_STATUS -> {
                if (!text.equals("-")) {
                    scheduleConversation.setStatus(ScheduleStatus.valueOf(text));
                }
                scheduleConversation.setState(ConversationState.NONE);
                Schedule schedule;
                if (scheduleConversation.getId() == null) {
                    schedule = scheduleService.createSchedule(mapper.toModel(scheduleConversation), chatId);
                } else {
                    schedule = scheduleService.updateSchedule(mapper.toModel(scheduleConversation), chatId);
                }

                StringBuilder sb = new StringBuilder();
                sb.append(scheduleConversation.getId() == null
                        ? "\uD83D\uDC8A Schedule added:\n\n"
                        : "\uD83D\uDC8A Schedule updated:\n\n");

                prepareSchedule(sb, schedule);

                sendSchedule(chatId, telegramClient, sb, schedule);
            }

            default -> {
                sendMessage(chatId, "Something went wrong", telegramClient);
                scheduleConversation.setState(ConversationState.NONE);
            }
        }
    }

    private void sendSchedule(Long chatId, TelegramClient telegramClient, StringBuilder sb, Schedule schedule) throws TelegramApiException {
        SendMessage sendMessage = SendMessage.builder()
                .text(sb.toString())
                .chatId(chatId)
                .build();
        sendMessage.setParseMode("HTML");

//        List<InlineKeyboardRow> rowsInline = new ArrayList<>();
//
//        InlineKeyboardButton button1 = InlineKeyboardButton.builder().text("Edit medication").callbackData("edit_medication:" + schedule.getId()).build();
//        InlineKeyboardButton button2 = InlineKeyboardButton.builder().text("Delete medication").callbackData("delete_medication:" + schedule.getId()).build();
//        InlineKeyboardRow row1 = new InlineKeyboardRow(button1, button2);
//
//        InlineKeyboardButton button3 = InlineKeyboardButton.builder().text("Schedule medication").callbackData("schedule_medication:" + schedule.getId()).build();
//
//        InlineKeyboardRow row2 = new InlineKeyboardRow(button3);
//
//        rowsInline.add(row1);
//        rowsInline.add(row2);
//
//        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup(rowsInline);
//        markupInline.setKeyboard(rowsInline);
//        sendMessage.setReplyMarkup(markupInline);
        ReplyKeyboardRemove keyboardRemove = new ReplyKeyboardRemove(true);

        sendMessage.setReplyMarkup(keyboardRemove);

        telegramClient.execute(sendMessage);
    }

    private void prepareSchedule(StringBuilder sb, Schedule schedule) {
        sb.append("🔹 <b>")
                .append(schedule.getStartDate())
                .append(" - ")
                .append(schedule.getEndDate())
                .append("</b>\n");

        if (schedule.getDurationDays() != null) {
            sb.append("  Duration: ")
                    .append(schedule.getDurationDays())
                    .append("\n");
        }

        if (schedule.getFrequencyPerDay() != null) {
            sb.append("  Frequency: ").append(schedule.getFrequencyPerDay())
                    .append("\n");
        }

        if (schedule.getScheduledTimes() != null) {
            sb.append("  Schedule: ")
                    .append(schedule.getScheduledTimes())
                    .append("\n");
        }

        if (schedule.getIntervalDays() != null) {
            sb.append("  Interval: ")
                    .append(schedule.getIntervalDays())
                    .append("\n");
        }

        if (schedule.getPauseDays() != null) {
            sb.append("  Pause: ")
                    .append(schedule.getPauseDays())
                    .append("\n");
        }

        if (schedule.getIntakeInstruction() != null) {
            sb.append("  Intake instructions: ")
                    .append(schedule.getIntakeInstruction())
                    .append("\n");
        }

        if (schedule.getCustomInstruction() != null) {
            sb.append("  Custom instructions: ")
                    .append(schedule.getCustomInstruction())
                    .append("\n");
        }

        if (schedule.getStatus() != null) {
            sb.append("  Status: ")
                    .append(schedule.getStatus())
                    .append("\n");
        }

        sb.append("\n");
    }

}
