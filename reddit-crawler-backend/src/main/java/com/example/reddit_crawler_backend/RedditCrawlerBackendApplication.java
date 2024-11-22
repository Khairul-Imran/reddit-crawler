package com.example.reddit_crawler_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

// Temporarily disabling auto-configuration for the DB
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RedditCrawlerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditCrawlerBackendApplication.class, args);
	}

}
