package com.example.reddit_crawler_backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.reddit_crawler_backend.Models.RedditPostEntity;

@Repository
public interface RedditPostRepository extends JpaRepository<RedditPostEntity, String> {

    /*
     * Custom queries can be added in the future depending on what you want to find:
     * -> Find posts by subreddit
     * -> Find posts from last 24 hours
     * -> Find posts by subreddit and time range
     * -> Find trending posts (high engagement in a short period of time)
     * -> Find posts with significant metric changes
     */
}
