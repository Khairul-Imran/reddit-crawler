package com.example.reddit_crawler_backend.Exceptions;

public class RedditServiceException extends RuntimeException {

    public RedditServiceException(String message) {
        super(message);
    }

    public RedditServiceException(String message, Throwable cause) {
        super(message, cause);
    }    
}
