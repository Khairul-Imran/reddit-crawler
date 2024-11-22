package com.example.reddit_crawler_backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedditPostData {
    private String kind;  // Will be "t3" for posts
    private RedditPost data;  // Your actual post data
}
