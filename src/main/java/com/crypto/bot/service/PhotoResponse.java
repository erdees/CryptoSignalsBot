package com.crypto.bot.service;

public record PhotoResponse(byte[] image, String caption, boolean markdown) implements BotResponse {}

