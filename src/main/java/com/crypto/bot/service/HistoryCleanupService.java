package com.crypto.bot.service;

import com.crypto.bot.repository.BotHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
public class HistoryCleanupService {

    private final Logger LOGGER = LoggerFactory.getLogger(HistoryCleanupService.class);

    private final BotHistoryRepository historyRepository;

    @Scheduled(cron = "0 0 * * * *") // Every hour at 00 min
    public void cleanupOldHistory() {
        var cutoff = Timestamp.from(Instant.now().minus(7, ChronoUnit.DAYS)); // 24 hours ago
        historyRepository.deleteByTimestampBefore(cutoff);

        LOGGER.info("Old history entries deleted before: {}", cutoff);
    }
}
