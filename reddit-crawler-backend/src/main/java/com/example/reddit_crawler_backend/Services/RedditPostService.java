package com.example.reddit_crawler_backend.Services;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reddit_crawler_backend.Models.RedditPost;
import com.example.reddit_crawler_backend.Models.RedditPostEntity;
import com.example.reddit_crawler_backend.Repository.RedditPostRepository;

@Service
public class RedditPostService {

    private final RedditPostRepository redditPostRepository;
    private final Logger logger = LoggerFactory.getLogger(RedditPostService.class);

    @Autowired
    public RedditPostService(RedditPostRepository redditPostRepository) {
        this.redditPostRepository = redditPostRepository;
    }

    /*
     * Inserts / Updates posts into the DB
     */
    public void processNewPosts(List<RedditPost> newPosts) {
        logger.info("Inserting posts into the DB!: {}", newPosts);
        
        for (RedditPost post : newPosts) {
            // Look for existing posts
            Optional<RedditPostEntity> existingPost = redditPostRepository.findById(post.getId());

            if (existingPost.isPresent()) {
                // Update the existing post with the new metrics
                RedditPostEntity entity = existingPost.get();
                updatePostMetrics(entity, post);
                entity.setLastUpdatedAt(LocalDateTime.now());
                redditPostRepository.save(entity); // Saves the updated entity
            } else {
                // Create and save new post entity if it doesn't already exist
                RedditPostEntity newEntity = createNewPostEntity(post);
                redditPostRepository.save(newEntity);
            }
        }
    }

    private void updatePostMetrics(RedditPostEntity entity, RedditPost newPost) {
        // Updating only the metrics that have changed
        entity.setUpvotes(newPost.getUpvotes());
        entity.setUpvoteRatio(newPost.getUpvoteRatio());
        entity.setScore(newPost.getScore());
        entity.setTotalAwardsReceived(newPost.getTotalAwardsReceived());
        entity.setTotalNumberOfComments(newPost.getTotalNumberOfComments());
        entity.setNumberOfCrossposts(newPost.getNumberOfCrossposts());
    }

    private RedditPostEntity createNewPostEntity(RedditPost post) {
        RedditPostEntity entity = new RedditPostEntity();

         // Convert Unix timestamp (seconds) to LocalDateTime
        LocalDateTime createdDateTime = LocalDateTime.ofInstant(
            Instant.ofEpochSecond(post.getCreatedTime()),
            ZoneId.systemDefault()
        );

        // Set all fields
        entity.setPostId(post.getId());
        entity.setSubReddit(post.getSubReddit());
        entity.setTitle(post.getTitle());
        entity.setAuthorName(post.getAuthorName());
        entity.setUpvoteRatio(post.getUpvoteRatio());
        entity.setUpvotes(post.getUpvotes());
        entity.setScore(post.getScore());
        entity.setTotalAwardsReceived(post.getTotalAwardsReceived());
        entity.setTotalNumberOfComments(post.getTotalNumberOfComments());
        entity.setPhotoUrl(post.getPhotoUrl());
        entity.setPostUrl(post.getPostUrl());
        entity.setIsVideo(post.getIsVideo());
        entity.setPostHint(post.getPostHint());
        entity.setDomain(post.getDomain());
        entity.setStickied(post.getStickied());
        entity.setOver18(post.getOver18());
        entity.setNumberOfCrossposts(post.getNumberOfCrossposts());
        entity.setDistinguished(post.getDistinguished());

        // Set timestamps
        entity.setCreatedAt(createdDateTime);
        entity.setFirstSeenAt(LocalDateTime.now());
        entity.setLastUpdatedAt(LocalDateTime.now());

        return entity;
    }
}
