package com.crypto.bot.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Entity
@NoArgsConstructor
@Table(name = "history")
public class History {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "symbol")
    private Symbol symbol;

    @Column(unique=true)
    private Timestamp timestamp;
}
