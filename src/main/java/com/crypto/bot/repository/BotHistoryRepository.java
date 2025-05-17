package com.crypto.bot.repository;

import com.crypto.bot.entity.History;
import com.crypto.bot.entity.Symbol;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface BotHistoryRepository extends JpaRepository<History, Long> {

    @Transactional
    void deleteByTimestampBefore(Timestamp cutoff);

    @Query("SELECT h FROM History h WHERE h.symbol = :symbol AND h.timestamp >= :cutoff ORDER BY h.timestamp ASC")
    List<History> findRecentHistory(
            @Param("symbol") Symbol symbol,
            @Param("cutoff") Timestamp cutoff
    );

    @Query("SELECT h FROM History h WHERE h.symbol = :symbol ORDER BY h.timestamp DESC LIMIT 1")
    History findLastBySymbol(@Param("symbol") Symbol symbol);
}