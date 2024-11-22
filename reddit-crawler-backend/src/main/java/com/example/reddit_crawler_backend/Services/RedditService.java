package com.example.reddit_crawler_backend.Services;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.reddit_crawler_backend.Config.RedditApiConfig;
import com.example.reddit_crawler_backend.Exceptions.RedditDataParsingException;
import com.example.reddit_crawler_backend.Exceptions.RedditServiceException;
import com.example.reddit_crawler_backend.Models.RedditApiResponse;
import com.example.reddit_crawler_backend.Utils.RedditDataParser;

@Service
public class RedditService {
    
    private final RestTemplate restTemplate;
    private final RedditApiConfig config;
    private final RedditDataParser redditDataParser;
    private final Logger logger = LoggerFactory.getLogger(RedditService.class);

    @Autowired
    public RedditService(RestTemplate restTemplate, RedditApiConfig config, RedditDataParser redditDataParser) {
        this.restTemplate = restTemplate;
        this.config = config;
        this.redditDataParser = redditDataParser;
    }

    public RedditApiResponse getTop20Posts(String subreddit) throws RedditServiceException {
        try {
            String url = buildUrl(subreddit);
            logger.info("Making request to Reddit API: {}", url);

            RequestEntity<Void> request = RequestEntity
                .get(url)
                .headers(createHttpHeaders())
                .accept(MediaType.APPLICATION_JSON)
                .build();

            ResponseEntity<String> response = restTemplate.exchange(request, String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Check the rate limits after a successful response
                checkRateLimits(response.getHeaders());
                
                return redditDataParser.parseRedditResponse(response.getBody());
            } else {
                logger.error("Error response from RedditAPI. Status: {}", response.getStatusCode());
                throw new RedditServiceException("Failed to fetch reddit posts data. Status: " + response.getStatusCode());
            }
        } catch (HttpClientErrorException hcee) {
            logger.error("Client error when fetching Reddit data: Status: {}, Response: {}", hcee.getStatusCode(), hcee.getResponseBodyAsString());
            throw hcee;
        } catch (RestClientException rce) {
            logger.error("Error fetching Reddit data for subreddit: {}", subreddit, rce);
            throw new RedditServiceException("Failed to fetch reddit data", rce);
        } catch (RedditDataParsingException rdpe) {
            logger.error("Error parsing Reddit data for subreddit: {}", subreddit, rdpe);
            throw new RedditServiceException("Failed to parse Reddit data", rdpe);
        }
    }

    private String buildUrl(String subreddit) {
        return UriComponentsBuilder
            // https://www.reddit.com/r/memes/top.json?t=day
            .fromHttpUrl(config.getApiUrl() + "/r/" + subreddit + "/top.json")
            .queryParam("limit", "20")
            .queryParam("t", "day") // Currently this gives us the results for past 24 hours
            .queryParam("raw_json", "1")
            .toUriString();
    }

    private HttpHeaders createHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
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
