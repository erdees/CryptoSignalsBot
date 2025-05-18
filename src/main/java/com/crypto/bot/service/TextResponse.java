package com.crypto.bot.service;

public record TextResponse(String text, boolean markdown) implements BotResponse {}

