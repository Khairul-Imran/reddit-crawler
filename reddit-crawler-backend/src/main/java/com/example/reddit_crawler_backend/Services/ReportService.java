package com.example.reddit_crawler_backend.Services;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reddit_crawler_backend.Models.RedditPost;
import com.example.reddit_crawler_backend.Utils.ReportUtils;

@Service
public class ReportService {
    
    // Look at other report formats in the future

    /**
     * Report generation flow:
     * User clicks "Generate Report" 
     * -> Frontend makes request to backend
     * -> Backend generates report content...
     * -> Backend returns file as downloadable response
     * -> Browser triggers download dialog
     * -> File saves to user's downloads folder
     */

    private final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportUtils reportUtils;
    private final RedditService redditService;

    @Autowired
    public ReportService(ReportUtils reportUtils, RedditService redditService) {
        this.reportUtils = reportUtils;
        this.redditService = redditService;
    }

    /**
     * Generates a report as a downloadable resource
     * Returns byte array that can be served directly to client
     * Report generated will be based on the top20 posts (for that subreddit)
     * This report should give us the same results as what is shown on the frontend
     */
    public byte[] generateReport() {
        try {
            List<RedditPost> posts = redditService.getLatestFetchedPosts();
            String subreddit = redditService.getCurrentSubreddit();

            if (posts == null || posts.isEmpty()) {
                throw new RuntimeException("No posts available for report generation.");
            }

            String reportContent = reportUtils.buildReportContent(posts, subreddit);
            return reportContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error generating report: ", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }
}
