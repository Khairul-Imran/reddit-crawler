package com.example.reddit_crawler_backend.Config;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @ConfigurationProperties(prefix = "telegram")
// @Data
public class TelegramConfig {

    @Value("${TELEGRAM_BOT_TOKEN}")
    private String botToken;

    @Value("${TELEGRAM_BOT_USERNAME}")
    private String botUsername;

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }
}
