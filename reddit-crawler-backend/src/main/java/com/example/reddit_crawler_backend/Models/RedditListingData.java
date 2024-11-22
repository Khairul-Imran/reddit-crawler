package com.example.reddit_crawler_backend.Models;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedditListingData {
    private String after; // For pagination
    private Integer dist; // Number of posts returned
    private List<RedditPostData> children;  // Reddit returns posts in 'children' array
}
