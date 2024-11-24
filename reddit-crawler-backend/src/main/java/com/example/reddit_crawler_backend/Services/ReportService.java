package com.example.reddit_crawler_backend.Services;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.reddit_crawler_backend.Models.RedditPost;
import com.example.reddit_crawler_backend.Utils.ReportUtils;

@Service
public class ReportService {
    
    // Show the structure for any of these components?
    // Explain the file generation process?
    // Discuss different report format options?
    // Explain how to handle the file storage/retrieval?

    /**
     * Report generation flow:
     * User clicks "Generate Report" 
     * -> Frontend makes request to backend
     * -> Backend generates report content in memory
     * -> Backend returns file as downloadable response
     * -> Browser triggers download dialog
     * -> File saves to user's downloads folder
     */

    private final Logger logger = LoggerFactory.getLogger(ReportService.class);
    private final ReportUtils reportUtils;

    @Autowired
    public ReportService(ReportUtils reportUtils) {
        this.reportUtils = reportUtils;
    }

    /**
     * Generates a report as a downloadable resource
     * Returns byte array that can be served directly to client
     */
    public byte[] generateReport(List<RedditPost> posts, String subreddit) {
        try {
            String reportContent = reportUtils.buildReportContent(posts, subreddit);
            return reportContent.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error generating report: ", e);
            throw new RuntimeException("Failed to generate report", e);
        }
    }
}
