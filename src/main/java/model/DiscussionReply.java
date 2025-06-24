/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * DiscussionReply model class
 *
 * @author DangPH - CE180896
 */
public class DiscussionReply {

    private int replyID;
    private int discussionID;
    private int authorID;
    private String authorType; // "customer" or "instructor"
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Additional fields not in database
    private String authorName;

    // Constructors
    public DiscussionReply() {
    }

    public DiscussionReply(int discussionID, int authorID, String authorType, String content) {
        this.discussionID = discussionID;
        this.authorID = authorID;
        this.authorType = authorType;
        this.content = content;
    }

    // Getters and setters
    public int getReplyID() {
        return replyID;
    }

    public void setReplyID(int replyID) {
        this.replyID = replyID;
    }

    public int getDiscussionID() {
        return discussionID;
    }

    public void setDiscussionID(int discussionID) {
        this.discussionID = discussionID;
    }

    public int getAuthorID() {
        return authorID;
    }

    public void setAuthorID(int authorID) {
        this.authorID = authorID;
    }

    public String getAuthorType() {
        return authorType;
    }

    public void setAuthorType(String authorType) {
        this.authorType = authorType;
    }

    // Legacy methods for backward compatibility
    public int getUserID() {
        return authorID;
    }

    public void setUserID(int userID) {
        this.authorID = userID;
        if (this.authorType == null) {
            this.authorType = "customer"; // Default to customer for backward compatibility
        }
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Updated from userName to authorName
    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    // Legacy methods for backward compatibility
    public String getUserName() {
        return authorName;
    }

    public void setUserName(String userName) {
        this.authorName = userName;
    }

    @Override
    public String toString() {
        return "DiscussionReply{" + "replyID=" + replyID + ", discussionID=" + discussionID + ", authorID=" + authorID + ", authorType=" + authorType + ", content=" + content + ", createdAt=" + createdAt + ", updatedAt=" + updatedAt + ", authorName=" + authorName + '}';
    }

}
