package com.example.reddit_crawler_backend.Exceptions;

public class RedditDataParsingException extends Exception {
    public RedditDataParsingException(String message) {
        super(message);
    }

    public RedditDataParsingException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedditDataParsingException(Throwable cause) {
        super(cause);
    }

    public RedditDataParsingException(String message, Throwable cause, boolean enableSupression, boolean writableStackTrace) {
        super(message, cause, enableSupression, writableStackTrace);
    }
}
