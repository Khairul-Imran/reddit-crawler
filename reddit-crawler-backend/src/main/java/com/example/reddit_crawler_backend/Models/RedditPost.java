package com.example.reddit_crawler_backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedditPost {

    private String subReddit; // Name of sub reddit
    private String title; // Title of post
    private String authorName; // Name of poster
    private double upvoteRatio; // Upvote ratio
    private int upvotes; // Upvotes
    private int score; // Score -> This might be the same as upvotes, so might not use**
    private int totalAwardsReceived;
    private int totalNumberOfComments;
    private double createdTime; // clarify what type this should be (created_utc)

    private String photoUrl; // Preview - "url"
    private String postUrl; // "permalink"

    private boolean isVideo; // (content type indicator)
    private String postHint; // (type of post)
    private String domain; // (source of content) -> not sure what this is for
    private boolean stickied; // (pinned posts)
    private boolean over18; // (NSFW flag)
    private int numberOfCrossposts; // Number of times this post has been shared to other subreddits
    private String distinguished; // Indicates if the post is marked by moderators or admins
    // null (regular post)
    // "moderator" (marked by subreddit mod)
    // "admin" (marked by Reddit admin)

}
