package com.crypto.bot.repository;

import com.crypto.bot.entity.SymbolType;
import com.crypto.bot.entity.Symbol;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BotSymbolRepository extends JpaRepository<Symbol, Long> {

    Optional<Symbol> findBySymbol(SymbolType symbol);
}
