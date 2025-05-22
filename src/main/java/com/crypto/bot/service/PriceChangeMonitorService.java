package com.crypto.bot.service;

import com.crypto.bot.entity.SymbolType;
import com.crypto.bot.repository.BotHistoryRepository;
import com.crypto.bot.repository.BotSessionRepository;
import com.crypto.bot.repository.BotSymbolRepository;
import com.crypto.bot.telegram.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PriceChangeMonitorService {

    private final Logger LOGGER = LoggerFactory.getLogger(PriceChangeMonitorService.class);

    private final BotHistoryRepository historyRepository;
    private final BotSymbolRepository symbolRepository;
    private final BotSessionRepository sessionRepository;
    private final TelegramNotificationService tgNotif;

    private static final long PRICE_CHANGE_THRESHOLD = 50000; // $500 in cents
    private static final int MINUTES_INTERVAL = 30;

    @Scheduled(fixedRate = 60000)
    public void checkPriceChange() {
        var symbolType = SymbolType.BTCUSDT;

        var symbol = symbolRepository.findBySymbol(symbolType)
                .orElseThrow(
                        () -> new RuntimeException("Symbol not found: " + symbolType)
                );
        var cutoff = Timestamp.from(Instant.now().minus(Duration.ofMinutes(MINUTES_INTERVAL)));
        var historyList = historyRepository.findRecentHistory(symbol, cutoff);

        if (historyList.size() < 2) {
            return;
        }

        long oldest = historyList.getFirst().getValue();
        long newest = historyList.getLast().getValue();
        long diff = newest - oldest;

        if (Math.abs(diff) >= PRICE_CHANGE_THRESHOLD) {
            String direction = (diff > 0) ? "increased 📈" : "decreased 📉";
            double amount = Math.abs(diff) / 100.0;

            var message = String.format(
                    "⚠️ %s has %s by $%.2f over the last %d minutes.",
                    symbolType,
                    direction,
                    amount,
                    MINUTES_INTERVAL
            );
            LOGGER.info(message);

            sessionRepository.findSubscribedChatIds().forEach(chatId ->
            {
                try {
                    tgNotif.sendMessageAsync(chatId, message);
                } catch (TelegramApiException e) {
                    LOGGER.error("Failed to send a notification to subscriber: {}",
                            e.getLocalizedMessage());
                }
            });
        }
    }
}
