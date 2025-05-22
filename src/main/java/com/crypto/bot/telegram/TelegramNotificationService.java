package com.crypto.bot.telegram;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;

@Getter
@Service
@RequiredArgsConstructor
public class TelegramNotificationService {

    private final TelegramClient telegramClient;

    public void sendMessage(Long chatId, String text) throws TelegramApiException {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .build();

        telegramClient.execute(message);
    }

    public void sendMessageAsync(Long chatId,
                                 String text,
                                 InlineKeyboardMarkup keyboard) throws TelegramApiException {
        var message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .parseMode(ParseMode.MARKDOWN)
                .replyMarkup(keyboard)
                .build();

        telegramClient.executeAsync(message);
    }

    public void sendMessage(Long chatId, byte[] chartBytes, String caption) throws TelegramApiException {
        InputFile inputFile = new InputFile(new ByteArrayInputStream(chartBytes), "image.png");

        var message = SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(inputFile)
                .caption(caption)
                .parseMode(ParseMode.MARKDOWN)
                .build();

        telegramClient.execute(message);
    }
}