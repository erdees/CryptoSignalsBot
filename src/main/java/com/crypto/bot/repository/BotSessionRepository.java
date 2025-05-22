package com.crypto.bot.repository;

import com.crypto.bot.entity.BotSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BotSessionRepository extends JpaRepository<BotSession, Long> {

    @Query("SELECT b.chatId FROM BotSession b WHERE b.isSubscribed = true AND b.isBotStarted = true " +
            "AND (b.muteUntil IS NULL OR b.muteUntil < :now)")
    List<Long> findActiveSubscribedChatIds(@Param("now") Instant now);

    boolean existsByChatIdAndIsBotStartedTrue(Long chatId);
}