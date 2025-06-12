/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.List;

/**
 * Discussion model class
 *
 * @author DangPH - CE180896
 */
public class Discussion {

    private int discussionID;
    private int courseID;
    private int lessonID;
    private int userID;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isResolved;

    // Add additional fields to display
    private String userName;
    private String courseName;
    private String lessonTitle;
    private int replyCount;
    private List<DiscussionReply> replies;

    // Constructors
    public Discussion() {
    }

    public Discussion(int courseID, int lessonID, int userID, String content) {
        this.courseID = courseID;
        this.lessonID = lessonID;
        this.userID = userID;
        this.content = content;
        this.isResolved = false;
    }

    // Getters and Setters
    public int getDiscussionID() {
        return discussionID;
    }

    public void setDiscussionID(int discussionID) {
        this.discussionID = discussionID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
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

    public boolean getIsResolved() {
        return isResolved;
    }

    public void setIsResolved(boolean isResolved) {
        this.isResolved = isResolved;
    }

    // Property accessors for EL expressions
    public boolean isResolved() {
        return isResolved;
    }

    public void setResolved(boolean resolved) {
        this.isResolved = resolved;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public void setLessonTitle(String lessonTitle) {
        this.lessonTitle = lessonTitle;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    public List<DiscussionReply> getReplies() {
        return replies;
    }

    public void setReplies(List<DiscussionReply> replies) {
        this.replies = replies;
    }

    @Override
    public String toString() {
        return "Discussion{"
                + "discussionID=" + discussionID
                + ", courseID=" + courseID
                + ", lessonID=" + lessonID
                + ", userID=" + userID
                + ", isResolved=" + isResolved
                + ", createdAt=" + createdAt
                + '}';
    }
}
