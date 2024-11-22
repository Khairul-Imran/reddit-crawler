package com.example.reddit_crawler_backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedditApiResponse {

    private String after; // For pagination -> todo later
    private int numberOfPosts; // "dist"
    private RedditPost[] redditPosts;
    
}
