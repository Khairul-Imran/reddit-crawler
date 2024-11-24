package com.example.reddit_crawler_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

// Temporarily disabling auto-configuration for the DB
// @SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.reddit_crawler_backend.Repository")
@EntityScan(basePackages = "com.example.reddit_crawler_backend.Models")
public class RedditCrawlerBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedditCrawlerBackendApplication.class, args);
	}

}
