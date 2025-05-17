package com.crypto.bot.telegram;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final CryptoSignalsBot bot;

    public void sendMessage(Long chatId, String text) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();
        try {
            bot.getTelegramClient().execute(message);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message to {}: {}", chatId, e.getMessage());
        }
    }
}