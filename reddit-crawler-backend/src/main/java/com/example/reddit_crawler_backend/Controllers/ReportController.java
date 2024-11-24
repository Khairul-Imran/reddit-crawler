package com.example.reddit_crawler_backend.Controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.reddit_crawler_backend.Services.RedditService;
import com.example.reddit_crawler_backend.Services.ReportService;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    
    private final ReportService reportService;
    private final RedditService redditService;
    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    public ReportController(ReportService reportService, RedditService redditService) {
        this.reportService = reportService;
        this.redditService = redditService;
    }

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateReport() {
        byte[] report = reportService.generateReport();

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String filename = String.format("%s_%s_report.txt", 
            redditService.getCurrentSubreddit(), 
            timestamp
        );

        logger.info("Received request to generate report for subreddit - {}, at time: {}", redditService.getCurrentSubreddit(), timestamp);

        // Headers for profile download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.setContentDispositionFormData("attachment", filename);

        return new ResponseEntity<>(report, headers, HttpStatus.OK);
    }
}
