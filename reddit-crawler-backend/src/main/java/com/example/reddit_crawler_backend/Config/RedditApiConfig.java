package com.example.reddit_crawler_backend.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RedditApiConfig {
    
    @Value("${reddit.base-url}")
    private String apiUrl;

    @Value("${reddit.user-agent}")
    private String userAgent;

    @Value("${reddit.client-id}")
    private String clientId;

    @Value("${reddit.client-secret}")
    private String clientSecret;

    @Value("${reddit.username}")
    private String username;

    @Value("${reddit.password}")
    private String password;

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

    public String getUserAgent() {
        return userAgent;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getTimeout() {
        return timeout;
    }
}
