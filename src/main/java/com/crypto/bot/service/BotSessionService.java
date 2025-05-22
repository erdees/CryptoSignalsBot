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
        var isExist = sessionRepository.existsByChatIdAndIsBotStartedTrue(chatId);

        if (!isExist) {
            if (input.equals("/start")) {
                var session = getOrCreateSession(chatId);
                return handleStart(session);
            } else {
                return handleNonExist(input);
            }
        } else {
            var session = getOrCreateSession(chatId);

            switch (input) {
                case "/start" -> {
                    return handleStart(session);
                }
                case "/last" -> {
                    return handleLast();
                }
                case "/chart" -> {
                    return handleChart();
                }
                case "/subscribe" -> {
                    return handleSubscribe(session);
                }
                case "/stop" -> {
                    return handleStop(session);
                }
            }
            return new EmptyResponse();
        }
    }

    private BotResponse handleNonExist(String input) {
        if (input.contains("/last") ||
                input.contains("/chart") ||
                input.contains("/subscribe") ||
                input.contains("/stop")) {
            return new TextResponse("""
                    👋 *Hey there!*
                    Looks like you haven’t started the bot yet.
                    To begin, just send */start* — it only takes a second! 🚀
                    Then you’ll be able to track prices, view charts, and get real-time alerts. 📊💸
                """, false);
        } else {
            return new EmptyResponse();
        }
    }

    private TextResponse handleStart(BotSession session) {

        if (session.isBotStarted()) {
            return new TextResponse("""
                    *You're already up and running!* 🚀
                    The bot is active and ready to send you crypto updates.
                    
                    Want to start receiving alerts?
                    Just send */subscribe* to get notified about significant market movements! 🔔📉📈
                    """,
                    false);
        } else {
            session.setBotStarted(true);
            sessionRepository.save(session);
            return new TextResponse("""
                *Welcome to CryptoSignalsBot!* \uD83D\uDE80
                Stay informed about real-time price changes in your favorite cryptocurrencies.
                We’ll alert you when the market moves significantly — up or down.
                Just send */subscribe* to get notified about significant market movements! 🔔📉📈
                
                Let’s keep an eye on the charts together! \uD83D\uDCCA\uD83D\uDCB0""",
                    false);
        }
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

    private TextResponse handleSubscribe(BotSession session) {
        if (session.isSubscribed()) {
            setSubscription(session, false);
            return new TextResponse("""
                    \uD83D\uDD15 *Notifications are turned off.*
                    Don’t miss out on important signals! Send */subscribe* to get back in the loop. \uD83D\uDE80""",
                    false);
        } else {
            setSubscription(session, true);
            return new TextResponse("""
                    \uD83D\uDD14 *You’re subscribed to notifications!*
                    Get ready for fresh updates and signals. Want some peace and quiet? Send */subscribe* to opt out. \uD83D\uDE34""",
                    false);
        }
    }

    private void setSubscription(BotSession session, boolean subscribe) {
        session.setSubscribed(subscribe);
        sessionRepository.save(session);
    }

    private TextResponse handleStop(BotSession session) {
        session.setBotStarted(false);
        session.setSubscribed(false);
        sessionRepository.save(session);

        return new TextResponse("""
                🛑 *Bot stopped.*
                You’ve successfully turned off CryptoSignalsBot.
                You won’t receive any more price updates or alerts.
                
                Whenever you're ready to jump back in — just send */start* and we’ll be here! 🚀📈
                """,
                false);
    }

    @NotNull
    private BotSession getOrCreateSession(long chatId) {
        return sessionRepository.findById(chatId).orElseGet(() -> {
            var session = new BotSession();
            session.setChatId(chatId);
            session.setBotStarted(false);
            session.setSubscribed(false);

            return sessionRepository.save(session);
        });
    }

    public void muteNotifications(Long chatId, int minutes) {
        var session = sessionRepository.findById(chatId).orElseThrow();
        session.setMuteUntil(Instant.now().plus(Duration.ofMinutes(minutes)));

        sessionRepository.save(session);
    }
}
