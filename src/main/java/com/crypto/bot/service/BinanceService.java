package com.crypto.bot.service;

import com.crypto.bot.entity.Symbol;
import com.crypto.bot.entity.History;
import com.crypto.bot.entity.SymbolType;
import com.crypto.bot.model.BtcPrice;
import com.crypto.bot.repository.BotHistoryRepository;
import com.crypto.bot.repository.BotSymbolRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.sql.Timestamp;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BinanceService {

    private final Logger LOGGER = LoggerFactory.getLogger(BinanceService.class);

    private final RestTemplate restTemplate;
    private final BotSymbolRepository symbolRepository;
    private final BotHistoryRepository historyRepository;

    @Scheduled(fixedRate = 60000)
    public void fetchAndSaveBtcPrice() {
        String url = "https://api.binance.com/api/v3/ticker/price?symbol=BTCUSDT";
        BtcPrice btcPrice = restTemplate.getForObject(url, BtcPrice.class);

        if (btcPrice != null) {
            var symbol = SymbolType.valueOf(btcPrice.getSymbol());
            var value = parsePriceToLong(btcPrice.getPrice());

            Symbol dbSymbol = symbolRepository.findBySymbol(symbol)
                    .orElseGet(() -> {
                        Symbol newCoin = new Symbol();
                        newCoin.setSymbol(symbol);
                        return symbolRepository.save(newCoin);
                    });

            History history = new History();
            history.setSymbol(dbSymbol);
            history.setValue(value);
            history.setTimestamp(Timestamp.from(Instant.now()));

            historyRepository.save(history);

            LOGGER.info("Saved BTC price: {}", value);
        }
    }

    private long parsePriceToLong(String priceStr) {
        double price = Double.parseDouble(priceStr);
        return Math.round(price * 100); // Convert to long
    }
}
