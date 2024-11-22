package com.example.reddit_crawler_backend.Utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.reddit_crawler_backend.Exceptions.RedditDataParsingException;
import com.example.reddit_crawler_backend.Models.RedditApiResponse;
import com.example.reddit_crawler_backend.Models.RedditListingData;
import com.example.reddit_crawler_backend.Models.RedditPost;
import com.example.reddit_crawler_backend.Models.RedditPostData;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonException;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

@Component
public class RedditDataParser {
    
    private static final Logger logger = LoggerFactory.getLogger(RedditDataParser.class);

    // Remember to throw exception later
    public RedditApiResponse parseRedditResponse(String jsonPayload) throws RedditDataParsingException {

        try (JsonReader jsonReader = Json.createReader(new StringReader(jsonPayload))) {
            JsonObject jsonObject = jsonReader.readObject();
            
            // Create RedditListingData
            RedditListingData listingData = new RedditListingData();
            listingData.setAfter(parseAfter(jsonObject));
            listingData.setDist(parseDist(jsonObject));

            // Parse children array into list of RedditPostData
            List<RedditPostData> posts = new ArrayList<>();
            JsonArray children = jsonObject.getJsonObject("data").getJsonArray("children");
            
            for (JsonValue child : children) {
                JsonObject postObject = child.asJsonObject(); // Represents each data-Json Object inside the "children array"
                RedditPostData postData = new RedditPostData(); // Create RedditPostData
                
                // Set kind ("t3")
                postData.setKind(postObject.getString("kind", ""));
                
                // Create and set individual RedditPost objects
                RedditPost post = new RedditPost();
                JsonObject data = postObject.getJsonObject("data"); // Accesses the "data" object in each reddit post
                
                post.setId(parseId(data));
                post.setSubReddit(parseSubReddit(data));
                post.setTitle(parseTitle(data));
                post.setAuthorName(parseAuthorName(data));
                post.setUpvoteRatio(parseUpvoteRatio(data));
                post.setUpvotes(parseUpvotes(data));
                post.setScore(parseScore(data));
                post.setTotalAwardsReceived(parseTotalAwardsReceived(data));
                post.setTotalNumberOfComments(parseTotalNumberOfComments(data));
                post.setCreatedTime(parseCreatedTime(data));
                post.setPhotoUrl(parsePhotoUrl(data));
                post.setPostUrl(parsePostUrl(data));
                post.setVideo(parseIsVideo(data));
                post.setPostHint(parsePostHint(data));
                post.setDomain(parseDomain(data));
                post.setStickied(parseStickied(data));
                post.setOver18(parseOver18(data));
                post.setNumberOfCrossposts(parseNumberOfCrossposts(data));
                post.setDistinguished(parseDistinguished(data));
                
                postData.setData(post);
                posts.add(postData); // Add to the list

                // Then repeat for the remaining posts inside the children array
            }

            listingData.setChildren(posts); // List of RedditPostData

            // Create and return RedditApiResponse
            RedditApiResponse response = new RedditApiResponse();
            response.setData(listingData);
            return response;
        } catch (JsonException je) {
            logger.error("Error parsing JSON data", je);
            throw new RedditDataParsingException("Error parsing JSON data", je);
        } catch (Exception e) {
            logger.error("Unexpected error while parsing reddit data", e);
            throw new RedditDataParsingException("Unexpected error while parsing reddit data", e);
        }
    }

    // Parsing methods for RedditListingData
    private String parseAfter(JsonObject jsonObject) {
        return jsonObject.getJsonObject("data").getString("after", "");
    }
    
    private Integer parseDist(JsonObject jsonObject) {
        return jsonObject.getJsonObject("data").getInt("dist");
    }
    
    // Parsing methods for RedditPost
    private String parseId(JsonObject data) {
        return data.getString("id", "");
    }
    
    private String parseSubReddit(JsonObject data) {
        return data.getString("subreddit", "");
    }
    
    private String parseTitle(JsonObject data) {
        return data.getString("title", "");
    }
    
    private String parseAuthorName(JsonObject data) {
        return data.getString("author", "");
    }
    
    private double parseUpvoteRatio(JsonObject data) {
        return data.getJsonNumber("upvote_ratio").doubleValue();
    }
    
    private Integer parseUpvotes(JsonObject data) {
        return data.getInt("ups");
    }
    
    private Integer parseScore(JsonObject data) {
        return data.getInt("score");
    }
    
    private Integer parseTotalAwardsReceived(JsonObject data) {
        return data.getInt("total_awards_received", 0);
    }
    
    private Integer parseTotalNumberOfComments(JsonObject data) {
        return data.getInt("num_comments", 0);
    }
    
    private Long parseCreatedTime(JsonObject data) {
        return data.getJsonNumber("created_utc").longValue();
    }
    
    private String parsePhotoUrl(JsonObject data) {
        return data.getString("url", "");
    }
    
    private String parsePostUrl(JsonObject data) {
        return "https://reddit.com" + data.getString("permalink", "");
    }
    
    private boolean parseIsVideo(JsonObject data) {
        return data.getBoolean("is_video", false);
    }
    
    private String parsePostHint(JsonObject data) {
        return data.getString("post_hint", "");
    }
    
    private String parseDomain(JsonObject data) {
        return data.getString("domain", "");
    }
    
    private boolean parseStickied(JsonObject data) {
        return data.getBoolean("stickied", false);
    }
    
    private boolean parseOver18(JsonObject data) {
        return data.getBoolean("over_18", false);
    }
    
    private Integer parseNumberOfCrossposts(JsonObject data) {
        return data.getInt("num_crossposts", 0);
    }
    
    private String parseDistinguished(JsonObject data) {
        return data.containsKey("distinguished") && !data.isNull("distinguished") ? 
            data.getString("distinguished") : null;
    }
}
