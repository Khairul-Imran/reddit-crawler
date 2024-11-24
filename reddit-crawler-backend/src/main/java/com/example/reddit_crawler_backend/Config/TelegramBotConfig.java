package com.example.reddit_crawler_backend.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.example.reddit_crawler_backend.Services.RedditReportBot;

@Configuration
public class TelegramBotConfig {
    
    // Where the application starts (registation)
    @Bean
    public TelegramBotsApi telegramBotsApi(RedditReportBot bot) throws TelegramApiException {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        api.registerBot(bot);
        return api;
    }

    /**
     * After registration:
     * -> Bot starts "polling" Telegram's servers for updates
     * -> This means it continuously checks for new messages/interactions
     * -> It's like the bot is constantly asking "Any new messages for me?"

     * When a user sends a message to your bot:
     * -> Telegram's servers receive the message
     * -> Your bot's next poll picks up this update
     * -> The onUpdateReceived method is automatically triggered
     * -> The Update object contains all the information about the user's message
     * 
     */
}
