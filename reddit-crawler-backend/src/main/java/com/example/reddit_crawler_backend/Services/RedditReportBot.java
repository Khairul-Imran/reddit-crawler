package com.example.reddit_crawler_backend.Services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.example.reddit_crawler_backend.Config.TelegramConfig;

@Component
public class RedditReportBot extends TelegramLongPollingBot {
    
    private final RedditService redditService;
    private final ReportService reportService;
    private final TelegramConfig telegramConfig;
    private final Logger logger = LoggerFactory.getLogger(RedditReportBot.class);

    @Autowired
    public RedditReportBot(RedditService redditService, ReportService reportService, TelegramConfig telegramConfig) {
        super(telegramConfig.getBotToken());
        this.redditService = redditService;
        this.reportService = reportService;
        this.telegramConfig = telegramConfig;
    }

    @Override
    public String getBotUsername() {
        return telegramConfig.getBotUsername();
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            if (update.hasMessage() && update.getMessage().hasText()) {
                handleTextMessage(update);
            } else if (update.hasCallbackQuery()) {
                handleCallbackQuery(update.getCallbackQuery());
            }
        } catch (Exception e) {
            logger.error("Error processing update: ", e);
        }
    }

    private void handleTextMessage(Update update) {
        String messageText = update.getMessage().getText();
        long chatId = update.getMessage().getChatId();

        if ("/start".equals(messageText)) {
            // First-time user interaction
            sendWelcomeMessage(chatId);
            sendSubredditOptions(chatId);
        } else if ("/help".equals(messageText)) {
            // User requesting help
            sendHelpMessage(chatId);
        }
    }

    // Report has been requested
    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String subreddit = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        String messageId = callbackQuery.getId();

        // Acknowledge the callback query
        try {
            execute(AnswerCallbackQuery.builder()
                .callbackQueryId(messageId)
                .text("Fetching report for r/" + subreddit + "...")
                .build());
        } catch (TelegramApiException e) {
            logger.error("Error acknowledging callback query: ", e);
        }

        // Fetch and send the report
        fetchAndSendReport(chatId, subreddit);
    }

    private void sendWelcomeMessage(long chatId) {
        String welcomeText = """
            👋 Welcome to Reddit Top 20 Bot!
            
            I can help you get reports of top posts from various subreddits.
            Use the buttons below to select a subreddit.
            
            Type /help for more information.
            """;

        sendMessage(chatId, welcomeText);
    }

    private void sendHelpMessage(long chatId) {
        String helpText = """
            📌 Available Commands:
            /start - Show subreddit options
            /help - Show this help message
            
            Simply click on any subreddit button to get its report!
            """;

        sendMessage(chatId, helpText);
    }

    private void sendMessage(long chatId, String text) {
        try {
            execute(SendMessage.builder()
                .chatId(String.valueOf(chatId))
                .text(text)
                .build());
        } catch (TelegramApiException e) {
            logger.error("Error sending message: ", e);
        }
    }
    
    private void sendSubredditOptions(long chatId) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        
        // Add buttons for each subreddit
        keyboard.add(Arrays.asList(
        createSubredditButton("r/memes", "memes")
        // createSubredditButton("r/funny", "funny")
        ));
            
        markup.setKeyboard(keyboard);
            
        SendMessage message = SendMessage.builder()
            .chatId(String.valueOf(chatId))
            .text("Select a subreddit to get a report:")
            .replyMarkup(markup)
            .build();
            
        try {
            execute(message);
        } catch (TelegramApiException e) {
            logger.error("Error sending subreddit options: ", e);
        }
    }
        
        
    private void fetchAndSendReport(long chatId, String subreddit) {
        try {
            // Send "processing" message
            sendMessage(chatId, "📊 Fetching latest posts from r/" + subreddit + "...");
                
            // Fetch the data using RedditService
            redditService.getTop20Posts(subreddit);
                
            // Generate report using ReportService
            byte[] reportData = reportService.generateReport();
                
            // Send the report file
            try (InputStream is = new ByteArrayInputStream(reportData)) {
                SendDocument document = SendDocument.builder()
                    .chatId(String.valueOf(chatId))
                    .document(new InputFile(is, subreddit + "_report.txt"))
                    .caption("📑 Here's your report for r/" + subreddit)
                    .build();
                    
                execute(document);
            }
        } catch (Exception e) {
            logger.error("Error fetching and sending report for {}: ", subreddit, e);
            sendMessage(chatId, "❌ Error generating report for r/" + subreddit + ". Please try again later.");
        }
    }
        
    private InlineKeyboardButton createSubredditButton(String label, String subreddit) {
        return InlineKeyboardButton.builder()
            .text(label)
            .callbackData(subreddit)
            .build();
    }
        
    // @Override
    // public void onUpdateReceived(Update update) {
    //     if (update.hasMessage() && update.getMessage().hasText()) {
    //         String messageText = update.getMessage().getText();
    //         long chatId = update.getMessage().getChatId();
    
    //         if ("/start".equals(messageText)) {
    //             // First-time user interaction
    //             sendMessage(chatId, 
    //                 "Welcome! Use /report to get the latest Top20Reddit report.");
    //         }
    //         else if ("/report".equals(messageText)) {
    //             // User's subsequent request
    //             sendReport(chatId);
    //         }
    //     }
    // }
    

    // private void sendReport(long chatId) {
    //     try {
    //         byte[] reportData = reportService.generateReport();
            
    //         // Create input stream from byte array
    //         try (InputStream is = new ByteArrayInputStream(reportData)) {
    //             SendDocument document = SendDocument.builder()
    //                 .chatId(String.valueOf(chatId))
    //                 .document(new InputFile(is, "report.txt"))
    //                 .build();
    //             execute(document);
    //         }
    //     } catch (Exception e) {
    //         logger.error("Error sending report: ", e);
    //         try {
    //             SendMessage message = SendMessage.builder()
    //                 .chatId(String.valueOf(chatId))  // Convert chatId to String
    //                 .text("Error generating report. Please try again later.")
    //                 .build();
    //             execute(message);
    //         } catch (TelegramApiException ex) {
    //             logger.error("Error sending error message: ", ex);
    //         }
    //     }
    // }

}
