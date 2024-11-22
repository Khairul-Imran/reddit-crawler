package com.example.reddit_crawler_backend.Controllers;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import com.example.reddit_crawler_backend.Exceptions.RedditServiceException;
import com.example.reddit_crawler_backend.Models.RedditApiResponse;
import com.example.reddit_crawler_backend.Services.RedditService;

@RestController
@RequestMapping("/api/posts")
public class RedditController {
    
    private final RedditService redditService;
    private static final Logger logger = LoggerFactory.getLogger(RedditController.class);

    @Autowired
    public RedditController(RedditService redditService) {
        this.redditService = redditService;
    }

    @GetMapping("/{subreddit}")
    public ResponseEntity<?> getTopPosts(@PathVariable String subreddit) {
        logger.info("Received request for top reddit posts from subreddit: {}", subreddit);

        try {
            RedditApiResponse redditApiResponse = redditService.getTop20Posts(subreddit);
            logger.info("Successfully retrieved top post data for subreddit: {}", subreddit);
            return ResponseEntity.ok(redditApiResponse);
            
        } catch (HttpClientErrorException hcee) {
            logger.error("HttpClientErrorException - Status: {}, Raw response: {}", 
                hcee.getStatusCode(), 
                hcee.getResponseBodyAsString());

            return ResponseEntity
                .status(hcee.getStatusCode())
                .body(new HashMap<String, Object>() {{
                    put("error", "Reddit API Error");
                    put("message", hcee.getStatusText());
                    put("status", hcee.getStatusCode().value());
            }});
        
        } catch (RedditServiceException rse) {
            logger.error("RedditServiceException while fetching data: {}", rse.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, Object>() {{
                put("error", "Reddit Service Error");
                put("message", rse.getMessage());
                put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }});

        } catch (Exception e) {
            logger.error("Unexpected error while fetching top reddit posts data", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new HashMap<String, Object>() {{
                put("error", "Internal Server Error");
                put("message", "An unexpected error occurred");
                put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }});
        }
    }
}
