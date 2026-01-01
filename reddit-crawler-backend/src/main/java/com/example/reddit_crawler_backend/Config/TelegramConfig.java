package com.example.reddit_crawler_backend.Config;

import org.springframework.beans.factory.annotation.Value;
// import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// @ConfigurationProperties(prefix = "telegram")
// @Data
public class TelegramConfig {

    @Value("${telegram.bot-token}")
    private String botToken;

    @Value("${telegram.bot-username}")
    private String botUsername;

    public String getBotToken() {
        return botToken;
    }

    public String getBotUsername() {
        return botUsername;
    }
}
