package com.crypto.bot.service;

import com.crypto.bot.telegram.TelegramNotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CommandHandlerService {

    private final Logger LOGGER = LoggerFactory.getLogger(CommandHandlerService.class);

    private final BotSessionService sessionService;
    private final TelegramNotificationService telegram;

    public void handleCommand(Long chatId, String input) throws IOException, TelegramApiException {
        var response = sessionService.processInput(chatId, input);

        if (response instanceof TextResponse text) {
            telegram.sendMessage(chatId, text.text());
        } else if (response instanceof PhotoResponse photo) {

            telegram.sendMessage(chatId, photo.image(), photo.caption());
        } else if (response instanceof EmptyResponse) {
            // Nothing to send, just log
            LOGGER.debug("No handler for response type found, do nothing.");
        }
    }
}
