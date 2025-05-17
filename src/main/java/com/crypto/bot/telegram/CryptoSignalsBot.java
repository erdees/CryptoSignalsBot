package com.crypto.bot.telegram;

import com.crypto.bot.config.ConfigProperties;
import com.crypto.bot.service.BotSessionService;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Getter
@Component
public class CryptoSignalsBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private final Logger LOGGER = LoggerFactory.getLogger(CryptoSignalsBot.class);

    private final TelegramClient telegramClient;
    private final BotSessionService botSession;
    private final ConfigProperties config;

    public CryptoSignalsBot(BotSessionService botSession, ConfigProperties config) {
        this.botSession = botSession;
        this.config = config;
        telegramClient = new OkHttpTelegramClient(getBotToken());
    }

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
            var input = update.getMessage().getText();
            var chatId = update.getMessage().getChatId();
            var response = botSession.processInput(chatId, input);
            var message = getSendMessage(chatId, response);
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                LOGGER.error(e.fillInStackTrace().getLocalizedMessage());
            }
        }
    }

    private SendMessage getSendMessage(Long chatId, String response) {
        return SendMessage.builder()
                .chatId(chatId)
                .text(response)
                .parseMode(ParseMode.MARKDOWN)
                .build();
    }

    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        LOGGER.info("Registered bot running state is: {}", botSession.isRunning());
    }
}
