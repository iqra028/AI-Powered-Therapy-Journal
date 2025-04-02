package org.example.seproject1;

import org.springframework.data.annotation.Id;
import java.util.*;


public class JournalEntry {
    @Id
    private String id;
    private String userId; // Add this field to associate with user
    private String type;
    private String content;
    private String imageUrl;
    private String mood;
    private Date date;
    private Date time;
    private Date createdAt;
    private String privacy;
    private List<String> tags; // Add this if you need tags

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public Date getTime() { return time; }
    public void setTime(Date time) { this.time = time; }
    public Date getCreatedAt() { return createdAt; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public String getPrivacy() { return privacy; }
    public void setPrivacy(String privacy) { this.privacy = privacy; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}