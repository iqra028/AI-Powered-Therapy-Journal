package org.example.seproject1;

import org.springframework.data.annotation.Id;
import java.util.Date;

public class JournalEntry {

    @Id
    private String id;

    private String type; // Type of journal (AI or Manual)
    private String content; // Journal text
    private String imageUrl; // URL of the image (if any)
    private String mood; // Mood tag (e.g., Happy, Sad, etc.)
    private Date date; // Date of the journal entry
    private Date time; // Time of the journal entry
    private Date createdAt; // Timestamp of when the entry was created
    private String privacy; // "public" or "private"

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
}
