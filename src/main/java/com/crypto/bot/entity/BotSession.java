package com.crypto.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "sessions")
public class BotSession {

    @Id
    private Long chatId;

    private boolean isBotStarted;

    private boolean isSubscribed;
}
