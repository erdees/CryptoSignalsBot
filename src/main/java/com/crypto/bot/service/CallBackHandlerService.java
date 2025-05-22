package com.crypto.bot.service;

import com.crypto.bot.telegram.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
@RequiredArgsConstructor
public class CallBackHandlerService {

    private final Logger LOGGER = LoggerFactory.getLogger(CallBackHandlerService.class);

    private final BotSessionService sessionService;
    private final TelegramNotificationService telegram;

    public void handleCallback(Long chatId, Update update) throws TelegramApiException {
        var data = update.getCallbackQuery().getData();

        switch (data) {
            case "MUTE_30" -> muteNotifications(chatId, update, 30);
            case "MUTE_120" -> muteNotifications(chatId, update, 120);
            case "RESET_ALL" -> resetNotifications(chatId, update);
        }
    }

    private void muteNotifications(Long chatId, Update update, int minutes) throws TelegramApiException {
        sessionService.muteNotifications(chatId, minutes);

        var answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Notifications disabled \uD83D\uDE48")
                .build();

        telegram.getTelegramClient().execute(answer);
    }

    private void resetNotifications(Long chatId, Update update) throws TelegramApiException {
        sessionService.muteNotifications(chatId, 0);

        var answer = AnswerCallbackQuery.builder()
                .callbackQueryId(update.getCallbackQuery().getId())
                .text("Notifications enabled \uD83D\uDC4D")
                .build();

        telegram.getTelegramClient().execute(answer);
    }
}
