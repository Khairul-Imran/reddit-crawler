package com.example.reddit_crawler_backend.Models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RedditPost {

    private String id; // Post id

    private String subReddit; // Name of sub reddit
    private String title; // Title of post
    private String authorName; // Name of poster
    private double upvoteRatio; // Upvote ratio
    private Integer upvotes; // Upvotes
    private Integer score; // Score -> This might be the same as upvotes, so might not use**
    private Integer totalAwardsReceived;
    private Integer totalNumberOfComments;
    private Long createdTime; // (created_utc) -> Unix timestamp in seconds

    private String photoUrl; // Preview - "url"
    private String postUrl; // "permalink"

    private boolean isVideo; // (content type indicator)
    private String postHint; // (type of post)
    private String domain; // (source of content) -> not sure what this is for
    private boolean stickied; // (pinned posts)
    private boolean over18; // (NSFW flag)
    private Integer numberOfCrossposts; // Number of times this post has been shared to other subreddits
    private String distinguished; // Indicates if the post is marked by moderators or admins
    // null (regular post)
    // "moderator" (marked by subreddit mod)
    // "admin" (marked by Reddit admin)

}
