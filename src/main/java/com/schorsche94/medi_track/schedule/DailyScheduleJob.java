package com.schorsche94.medi_track.schedule;

import com.schorsche94.medi_track.service.DailyScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;

@Component
public class DailyScheduleJob {

    private static final ZoneId SCHEDULE_ZONE = ZoneId.of("Europe/Chisinau");
    private static final LocalTime PREPARE_TOMORROW_AFTER = LocalTime.of(23, 50);

    private static final Logger log = LoggerFactory.getLogger(DailyScheduleJob.class);

    private final DailyScheduleService scheduleService;

    public DailyScheduleJob(DailyScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Chisinau") // every day at local midnight
    public void scheduledPrepare() {
        log.info("Scheduled schedule preparation triggered");
        prepareCurrentDayAndNextIfLate();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started — checking today's schedule");
        prepareCurrentDayAndNextIfLate();
    }

    private void prepareCurrentDayAndNextIfLate() {
        LocalDateTime now = LocalDateTime.now(SCHEDULE_ZONE);
        LocalDate today = now.toLocalDate();
        scheduleService.prepareIfNeeded(today);

        if (!now.toLocalTime().isBefore(PREPARE_TOMORROW_AFTER)) {
            LocalDate tomorrow = today.plusDays(1);
            log.info("Current time is at or after 23:50 — preparing tomorrow's schedule: {}", tomorrow);
            scheduleService.prepareIfNeeded(tomorrow);
        }
    }
}
