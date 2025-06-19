# 🪙 CryptoSignalsBot

CryptoSignalsBot is a Spring Boot application that monitors cryptocurrency prices in real-time and alerts you when significant changes occur.

## 📌 Features

- ⏱ Scheduled price checks every minute
- 📊 Tracks price history in a SQL database
- ⚠ Alerts when a coin changes by more than a defined threshold (e.g. $500 in 10 minutes)
- 🔄 Auto-cleans old history (older than 24 hours)
- 🔧 Easily extendable for Telegram or email notifications

## 🛠 Tech Stack

- Java 21
- Spring Boot
- Spring Data JPA
- H2 / PostgreSQL / MySQL (your choice)
- RestTemplate (for Binance API)

## 📈 Example Output

⚠️ BTCUSDT has increased 📈 by $512.45 over the last 10 minutes.

## 🏞️ Screenshots




