package com.schorsche94.medi_track.schedule;

import com.schorsche94.medi_track.service.DailyScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DailyScheduleJob {

    private static final Logger log = LoggerFactory.getLogger(DailyScheduleJob.class);

    private final DailyScheduleService scheduleService;

    public DailyScheduleJob(DailyScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @Scheduled(cron = "0 0 0 * * *", zone = "Europe/Chisinau") // every day at local midnight
    public void scheduledPrepare() {
        log.info("Scheduled schedule preparation triggered");
        scheduleService.prepareIfNeeded(LocalDate.now());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application started — checking today's schedule");
        scheduleService.prepareIfNeeded(LocalDate.now());
    }
}
