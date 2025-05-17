package com.crypto.bot.service;

import com.crypto.bot.config.ConfigProperties;
import com.crypto.bot.entity.BotSession;
import com.crypto.bot.entity.SymbolType;
import com.crypto.bot.repository.BotHistoryRepository;
import com.crypto.bot.repository.BotSessionRepository;
import com.crypto.bot.repository.BotSymbolRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BotSessionService {

    private final BotSessionRepository sessionRepository;
    private final BotSymbolRepository symbols;
    private final BotHistoryRepository history;
    private final ConfigProperties config;

    @Transactional
    public String processInput(long chatId, String input) {
        var session = getOrCreateSession(chatId);

        if (input.equals("/start")) {
            return """
                    *Welcome to CryptoSignalsBot!* \uD83D\uDE80
                    Stay informed about real-time price changes in your favorite cryptocurrencies.
                    We’ll alert you when the market moves significantly — up or down.
                    
                    Let’s keep an eye on the charts together! \uD83D\uDCCA\uD83D\uDCB0""";
        }

        if (input.equals("/last")) {
            var dbSymbol = symbols.findBySymbol(SymbolType.BTCUSDT).orElseThrow();
            var value = history.findLastBySymbol(dbSymbol);
            BigDecimal raw = BigDecimal.valueOf(value.getValue());
            BigDecimal price = raw.divide(BigDecimal.valueOf(100));
            int dollars = price.intValue();

            return String.format(
                    "📈 %s is now at *%d* 💰\n",
                    dbSymbol.getSymbol(),
                    dollars
            );
        }

        return input;
    }

    @NotNull
    private BotSession getOrCreateSession(long chatId) {
        return sessionRepository.findById(chatId).orElseGet(() -> {
            BotSession session = new BotSession();
            session.setChatId(chatId);
            session.setBotStarted(true);

            return sessionRepository.save(session);
        });
    }
}
