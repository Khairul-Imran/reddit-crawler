package com.example.reddit_crawler_backend.Utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.OptionalDouble;

import org.springframework.stereotype.Component;

import com.example.reddit_crawler_backend.Models.RedditPost;

@Component
public class ReportUtils {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Build the report's content
     */
    public String buildReportContent(List<RedditPost> posts, String subreddit) {
        StringBuilder report = new StringBuilder();

        // Report header
        report.append("REDDIT TOP 20 REPORT\n")
              .append("===================\n\n")
              .append("Generated: ").append(LocalDateTime.now().format(FORMATTER)).append("\n")
              .append("Subreddit: r/").append(subreddit).append("\n")
              .append("Time Period: Last 24 hours\n\n");
        
        appendSummarySection(report, posts);
        appendPostsSection(report, posts);

        return report.toString();
    }

    /**
     * Add summary statistics to the report
     */
    private void appendSummarySection(StringBuilder report, List<RedditPost> posts) {
        report.append("SUMMARY\n")
              .append("-------\n");

        // Calculate statistics
        int totalPosts = posts.size();
        int totalUpvotes = posts.stream()
                               .mapToInt(RedditPost::getUpvotes)
                               .sum();
        OptionalDouble avgUpvotes = posts.stream()
                                       .mapToInt(RedditPost::getUpvotes)
                                       .average();
        // String mostActiveAuthor = findMostActiveAuthor(posts);

        // Append statistics
        report.append("Total Posts: ").append(totalPosts).append("\n")
              .append("Total Upvotes: ").append(formatNumber(totalUpvotes)).append("\n")
              .append("Average Upvotes: ").append(formatNumber(avgUpvotes.orElse(0))).append("\n");
            //   .append("Most Active Author: ").append(mostActiveAuthor).append("\n\n");
    }

    /**
     * Add individual post details to the report 
     */
    private void appendPostsSection(StringBuilder report, List<RedditPost> posts){
        report.append("TOP 20 POSTS\n")
            .append("-----------\n\n");

        for (int i = 0; i < posts.size(); i++) {
            RedditPost post = posts.get(i);
            report.append(String.format("#%d. %s\n", (i + 1), post.getTitle()))
                .append("   Author: ").append(post.getAuthorName()).append("\n")
                .append("   Upvotes: ").append(formatNumber(post.getUpvotes()))
                .append(" | Comments: ").append(formatNumber(post.getTotalNumberOfComments()))
                .append(" | Crossposts: ").append(post.getNumberOfCrossposts()).append("\n")
                .append("   Posted: ").append(formatTimestamp(post.getCreatedTime())).append("\n")
                .append("   Link: ").append(post.getPostUrl()).append("\n\n");
        }
    }
    
    /**
     * Formats numbers with commas for readability
     */
    private String formatNumber(double number) {
        return String.format("%d", (long) number);
    }

    /**
     * Formats Unix timestamp to readable date/time
     */
    private String formatTimestamp(long unixTimestamp) {
        return LocalDateTime.ofInstant(
            Instant.ofEpochSecond(unixTimestamp),
            ZoneId.systemDefault()
        ).format(FORMATTER);
    }
}
