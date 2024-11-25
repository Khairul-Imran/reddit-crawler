package com.example.reddit_crawler_backend.Services;


import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.reddit_crawler_backend.Config.RedditApiConfig;
import com.example.reddit_crawler_backend.Exceptions.RedditDataParsingException;
import com.example.reddit_crawler_backend.Exceptions.RedditServiceException;
import com.example.reddit_crawler_backend.Models.RedditApiResponse;
import com.example.reddit_crawler_backend.Models.RedditPost;
import com.example.reddit_crawler_backend.Models.RedditPostData;
import com.example.reddit_crawler_backend.Utils.RedditDataParser;

@Service
public class RedditService {

    private String accessToken;
    private Instant tokenExpiry;
    
    private final RestTemplate restTemplate;
    private final RedditApiConfig config;
    private final RedditDataParser redditDataParser;
    private final RedditPostService redditPostService;
    private final Logger logger = LoggerFactory.getLogger(RedditService.class);

    // In-memory storage of latest fetch
    private List<RedditPost> latestFetchedPosts;
    private String currentSubreddit;
    private LocalDateTime lastFetchTime;

    @Autowired
    public RedditService(RestTemplate restTemplate, 
                    RedditApiConfig config, 
                    RedditDataParser redditDataParser, 
                    RedditPostService redditPostService) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.redditDataParser = redditDataParser;
        this.redditPostService = redditPostService;
    }

    public RedditApiResponse getTop20Posts(String subreddit) throws RedditServiceException {
        try {
            String token = getAccessToken();
            logger.info("Got access token, making request...");

            String url = buildUrl(subreddit);
            logger.info("Making request to Reddit API: {}", url);

            RequestEntity<Void> request = RequestEntity
                .get(url)
                .headers(createHttpHeaders(token)) // Token here
                .accept(MediaType.APPLICATION_JSON)
                .build();
                
            logger.info("Request: {}", request);
            logger.info("Using Reddit user agent: '{}'", config.getUserAgent());

            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            
            /*
             * NEW PART
             */

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Check the rate limits after a successful response
                checkRateLimits(response.getHeaders());

                RedditApiResponse redditResponse = redditDataParser.parseRedditResponse(response.getBody());

                // Store the latest fetch in memory -> for generating report
                this.latestFetchedPosts = redditResponse.getData().getChildren().stream()
                    .map(RedditPostData::getData)
                    .toList();
                this.currentSubreddit = subreddit;
                this.lastFetchTime = LocalDateTime.now();

                // Save the posts to DB
                redditPostService.processNewPosts(this.latestFetchedPosts);

                // return redditDataParser.parseRedditResponse(response.getBody());
                return redditResponse;
            } else {
                logger.error("Error response from RedditAPI. Status: {}", response.getStatusCode());
                throw new RedditServiceException("Failed to fetch reddit posts data. Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException hcee) {
            logger.error("Client error when fetching Reddit data: Status: {}, Response: {}", hcee.getStatusCode(), hcee.getResponseBodyAsString());
            // logger.error("Request headers were: {}", hcee.getRequestHeaders());
            throw hcee;
        } catch (RestClientException rce) {
            logger.error("Error fetching Reddit data for subreddit: {}", subreddit, rce);
            throw new RedditServiceException("Failed to fetch reddit data", rce);
        } catch (RedditDataParsingException rdpe) {
            logger.error("Error parsing Reddit data for subreddit: {}", subreddit, rdpe);
            throw new RedditServiceException("Failed to parse Reddit data", rdpe);
        }
    }


    private String getAccessToken() {
        try {
            if (accessToken == null || Instant.now().isAfter(tokenExpiry)) {
                // Create authentication string
                String authString = config.getClientId() + ":" + config.getClientSecret();
                String encodedAuth = Base64.getEncoder().encodeToString(authString.getBytes());

                // Set up headers
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Basic " + encodedAuth);  // Changed from setBasicAuth
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.set("User-Agent", config.getUserAgent());

                // Set up body
                MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
                body.add("grant_type", "password");
                body.add("username", config.getUsername());
                body.add("password", config.getPassword());

    
                HttpEntity<MultiValueMap<String, String>> request = 
                    new HttpEntity<>(body, headers);

                // Log the request (but mask sensitive info)
                logger.info("Requesting access token with headers: {}", 
                    headers.entrySet().stream()
                        .filter(e -> !e.getKey().equals("Authorization"))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
                );
    
                ResponseEntity<Map> response = restTemplate.postForEntity(
                    "https://oauth.reddit.com/api/v1/access_token", // Here
                    request,
                    Map.class
                );

                if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                    logger.error("Failed to get access token. Response: {}", response.getBody());
                    throw new RedditServiceException("Failed to obtain access token from Reddit");
                }
    
                accessToken = (String) response.getBody().get("access_token");
                tokenExpiry = Instant.now().plusSeconds(3600); // Token valid for 1 hour

                logger.info("New access token obtained, valid until: {}", tokenExpiry);
            }
            return accessToken;
        } catch (RestClientException e) {
            logger.error("Error obtaining Reddit access token: ", e);
            throw new RedditServiceException("Failed to obtain Reddit access token", e);
        }
    }


    // Getters for the latest fetch data
    public List<RedditPost> getLatestFetchedPosts() {
        if (latestFetchedPosts == null) {
            throw new RuntimeException("No posts have been fetched yet");
        }
        return latestFetchedPosts;
    }

    public String getCurrentSubreddit() {
        if (currentSubreddit == null) {
            throw new RuntimeException("No subreddit has been fetched yet");
        }
        return currentSubreddit;
    }

    public LocalDateTime getLastFetchTime() {
        if (lastFetchTime == null) {
            throw new RuntimeException("No fetch has been performed yet");
        }
        return lastFetchTime;
    }


    // Helper methods
    private String buildUrl(String subreddit) {
        return UriComponentsBuilder
            // https://www.reddit.com/r/memes/top.json?t=day
            // .fromHttpUrl(config.getApiUrl() + "/r/" + subreddit + "/top.json")
            .fromHttpUrl("https://oauth.reddit.com/r/" + subreddit + "/top")  // Remove .json
            .queryParam("limit", "20")
            .queryParam("t", "day") // Currently this gives us the results for past 24 hours
            .queryParam("raw_json", "1")
            .toUriString();
    }

    private HttpHeaders createHttpHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Here
        headers.set("User-Agent", config.getUserAgent());
        headers.set("Accept", "application/json");
        return headers;
    }

    // To create custom exception for the rate limits in the future.
    private void checkRateLimits(HttpHeaders headers) {
        try {

            /**
             * Note: When we access headers through Spring's HttpHeaders class, it returns List<String>
             * This is because HTTP headers can technically have multiple values for the same header name
             */
            List<String> used = headers.get("x-ratelimit-used");
            List<String> remaining = headers.get("x-ratelimit-remaining");
            List<String> reset = headers.get("x-ratelimit-reset");

            if (remaining != null && !remaining.isEmpty()) {
                double remainingCalls = Double.parseDouble(remaining.get(0));
                logger.debug("Rate limit remaining: {}", remainingCalls);

                // Warning for low rate limit
                if (remainingCalls < 15) {
                    logger.warn("Rate limit is getting low. Remaining calls: {}", remainingCalls);
                }
            }

            if (used != null && !used.isEmpty()) {
                logger.debug("Rate limit used: {}", used.get(0));
            }

            if (reset != null && !reset.isEmpty()) {
                logger.debug("Rate limit resets in: {} seconds", reset.get(0));
            }

        } catch (NumberFormatException nfe) {
            logger.warn("Error parsing rate limit headers", nfe);
        }
    }
}
