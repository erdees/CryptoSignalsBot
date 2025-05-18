package com.crypto.bot.service;

import com.crypto.bot.entity.BotSession;
import com.crypto.bot.entity.SymbolType;
import com.crypto.bot.repository.BotHistoryRepository;
import com.crypto.bot.repository.BotSessionRepository;
import com.crypto.bot.repository.BotSymbolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BotSessionService {

    private final BotSessionRepository sessionRepository;
    private final BotSymbolRepository symbols;
    private final BotHistoryRepository history;
    private final ChartService chartService;
    private final BotSymbolRepository symbolRepository;

    @Transactional
    public BotResponse processInput(long chatId, String input) throws IOException {
        var session = getOrCreateSession(chatId);

        switch (input) {
            case "/start" -> {
                return handleStart();
            }
            case "/last" -> {
                return handleLast();
            }
            case "/chart" -> {
                return handleChart();
            }
        }
        return new EmptyResponse();
    }

    private static TextResponse handleStart() {

        return new TextResponse("""
                *Welcome to CryptoSignalsBot!* \uD83D\uDE80
                Stay informed about real-time price changes in your favorite cryptocurrencies.
                We’ll alert you when the market moves significantly — up or down.
                
                Let’s keep an eye on the charts together! \uD83D\uDCCA\uD83D\uDCB0""", false);
    }

    @NotNull
    private TextResponse handleLast() {
        var dbSymbol = symbols.findBySymbol(SymbolType.BTCUSDT).orElseThrow();
        var value = history.findLastBySymbol(dbSymbol);
        var raw = BigDecimal.valueOf(value.getValue());
        var price = raw.divide(BigDecimal.valueOf(100));
        var dollars = price.intValue();

        return new TextResponse(String.format(
                "📈 %s is now at *%d* 💰\n",
                dbSymbol.getSymbol(),
                dollars
        ), false);
    }

    private PhotoResponse handleChart() throws IOException {
        var symbol = symbolRepository.findBySymbol(SymbolType.BTCUSDT)
                .orElseThrow(
                        () -> new RuntimeException("Symbol not found: " + SymbolType.BTCUSDT)
                );
        var to = Timestamp.from(Instant.now());
        var from = Timestamp.from(Instant.now().minus(Duration.ofMinutes(30)));
        var img = chartService.generateStyledChart(symbol, from, to);

        return new PhotoResponse(img, "📊 BTC price over the last 30m", false);
    }

    @NotNull
    private BotSession getOrCreateSession(long chatId) {
        return sessionRepository.findById(chatId).orElseGet(() -> {
            var session = new BotSession();
            session.setChatId(chatId);
            session.setBotStarted(true);

            return sessionRepository.save(session);
        });
    }
}
