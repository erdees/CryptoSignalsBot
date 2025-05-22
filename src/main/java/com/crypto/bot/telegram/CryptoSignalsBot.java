package com.crypto.bot.telegram;

import com.crypto.bot.config.ConfigProperties;
import com.crypto.bot.service.CallBackHandlerService;
import com.crypto.bot.service.CommandHandlerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.IOException;

@Getter
@Component
@RequiredArgsConstructor
public class CryptoSignalsBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(CryptoSignalsBot.class);

    private final CommandHandlerService commandHandler;
    private final CallBackHandlerService callbackHandler;
    private final TelegramClient telegramClient;
    private final ConfigProperties config;

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            handleTextMessage(update);
        } if (update.hasCallbackQuery()) {
            handleCallBack(update);
        }
    }

    private void handleTextMessage(Update update) {
        var chatId = update.getMessage().getChatId();
        var input = update.getMessage().getText();
        try {
            commandHandler.handleCommand(chatId, input);
        } catch (IOException | TelegramApiException e) {
            LOGGER.error(
                    "An error occur during message processing: {} ",
                    e.getMessage()
            );
        }
    }

    private void handleCallBack(Update update) {
        var chatId = update.getCallbackQuery().getMessage().getChatId();
        try {
            callbackHandler.handleCallback(chatId, update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        LOGGER.info("Registered bot running state is: {}", botSession.isRunning());
    }
}
