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
    private int userID;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isInstructorReply;
    private boolean isAcceptedAnswer;

    // Additional fields not in database
    private String userName;

    // Constructors
    public DiscussionReply() {
    }

    public DiscussionReply(int discussionID, int userID, String content, boolean isInstructorReply,
            boolean isAcceptedAnswer) {
        this.discussionID = discussionID;
        this.userID = userID;
        this.content = content;
        this.isInstructorReply = isInstructorReply;
        this.isAcceptedAnswer = isAcceptedAnswer;
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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

    public boolean getIsInstructorReply() {
        return isInstructorReply;
    }

    public void setIsInstructorReply(boolean isInstructorReply) {
        this.isInstructorReply = isInstructorReply;
    }

    // Property accessors for EL expressions
    public boolean isInstructorReply() {
        return isInstructorReply;
    }

    public void setInstructorReply(boolean instructorReply) {
        this.isInstructorReply = instructorReply;
    }

    public boolean getIsAcceptedAnswer() {
        return isAcceptedAnswer;
    }

    public void setIsAcceptedAnswer(boolean isAcceptedAnswer) {
        this.isAcceptedAnswer = isAcceptedAnswer;
    }

    // Property accessors for EL expressions
    public boolean isAcceptedAnswer() {
        return isAcceptedAnswer;
    }

    public void setAcceptedAnswer(boolean acceptedAnswer) {
        this.isAcceptedAnswer = acceptedAnswer;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "DiscussionReply{"
                + "replyID=" + replyID
                + ", discussionID=" + discussionID
                + ", userID=" + userID
                + ", isInstructorReply=" + isInstructorReply
                + ", isAcceptedAnswer=" + isAcceptedAnswer
                + ", createdAt=" + createdAt
                + '}';
    }
}
