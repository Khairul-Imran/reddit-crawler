package com.example.reddit_crawler_backend.Models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity 
@Table(name = "reddit_posts")
@Data
public class RedditPostEntity {
    @Id
    @Column(nullable = false, unique = true)
    private String postId; // Reddit's post id, used as our PK too
    
    @Column(nullable = false, length = 50)
    private String subReddit;

    @Column(nullable = false, length = 500)
    private String title;

    @Column(nullable = false)
    private String authorName;

    @Column(nullable = false)
    private Double upvoteRatio;

    @Column(nullable = false)
    private Integer upvotes;

    @Column(nullable = false)
    private Integer score;

    @Column(nullable = false)
    private Integer totalAwardsReceived;

    @Column(nullable = false)
    private Integer totalNumberOfComments;
    
    @Column
    private String photoUrl;
    
    @Column(nullable = false)
    private String postUrl;
    
    @Column(nullable = false)
    private Boolean isVideo;
    
    @Column
    private String postHint;
    
    @Column(nullable = false)
    private String domain;
    
    @Column(nullable = false)
    private Boolean stickied;
    
    @Column(nullable = false)
    private Boolean over18;
    
    @Column(nullable = false)
    private Integer numberOfCrossposts;
    
    @Column
    private String distinguished;

    // Timestamp fields
    @Column(nullable = false)
    private LocalDateTime createdAt; // When the post was created on Reddit

    @Column(nullable = false)
    private LocalDateTime firstSeenAt; // When we first discovered this post

    @Column(nullable = false)
    private LocalDateTime lastUpdatedAt; // When we last updated this post's data
}
