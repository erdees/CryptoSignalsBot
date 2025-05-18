package com.crypto.bot.utils;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;

public class TelegramUtils {

    public static SendPhoto createChartPhotoMessage(Long chatId, byte[] chartBytes, String caption) {
        var inputFile = new InputFile(new ByteArrayInputStream(chartBytes), "chart.png");

        return SendPhoto.builder()
                .chatId(chatId.toString())
                .photo(inputFile)
                .caption(caption)
                .build();
    }
}
