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
    private int authorID;
    private String authorType;
    private String content;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private boolean isResolved;

    // Add additional fields to display
    private String authorName;
    private String courseName;
    private String lessonTitle;
    private int replyCount;
    private List<DiscussionReply> replies;

    // Constructors
    public Discussion() {
    }

    public Discussion(int courseID, int lessonID, int authorID, String authorType, String content) {
        this.courseID = courseID;
        this.lessonID = lessonID;
        this.authorID = authorID;
        this.authorType = authorType;
        this.content = content;
        this.isResolved = false;
    }

    // Legacy constructor for backward compatibility
    public Discussion(int courseID, int lessonID, int userID, String content) {
        this.courseID = courseID;
        this.lessonID = lessonID;
        this.authorID = userID;
        this.authorType = "customer"; // Default to customer for backward compatibility
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
                + ", authorID=" + authorID
                + ", authorType='" + authorType + '\''
                + ", isResolved=" + isResolved
                + ", createdAt=" + createdAt
                + '}';
    }
}
