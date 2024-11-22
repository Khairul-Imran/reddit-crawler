package com.example.reddit_crawler_backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class RedditApiConfig {
    
    @Value("${reddit.base-url}")
    private String apiUrl;

    // @Value("${weatherapi.key}")
    // private String apiKey;

    @Value("${reddit.user-agent}")
    private String userAgent;

    @Value("${redditapi.timeout}")
    private int timeout;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);
        restTemplate.setRequestFactory(requestFactory);

        return restTemplate;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    // public String getApiKey() {
    //     return apiKey;
    // }

    public String getUserAgent() {
        return userAgent;
    }

    public int getTimeout() {
        return timeout;
    }

}
