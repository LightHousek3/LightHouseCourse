/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 * Model class for LessonItemProgress
 *
 * @author DangPH - CE180896
 */
public class LessonItemProgress {

    private int progressID;
    private int userID;
    private int lessonItemID;
    private boolean isCompleted;
    private Timestamp completionDate;
    private Timestamp lastAccessDate;

    public LessonItemProgress() {
    }

    public LessonItemProgress(int userID, int lessonItemID) {
        this.userID = userID;
        this.lessonItemID = lessonItemID;
        this.isCompleted = false;
        this.lastAccessDate = new Timestamp(System.currentTimeMillis());
    }

    public LessonItemProgress(int progressID, int userID, int lessonItemID, boolean isCompleted,
            Timestamp completionDate, Timestamp lastAccessDate) {
        this.progressID = progressID;
        this.userID = userID;
        this.lessonItemID = lessonItemID;
        this.isCompleted = isCompleted;
        this.completionDate = completionDate;
        this.lastAccessDate = lastAccessDate;
    }

    /**
     * @return the progressID
     */
    public int getProgressID() {
        return progressID;
    }

    /**
     * @param progressID the progressID to set
     */
    public void setProgressID(int progressID) {
        this.progressID = progressID;
    }

    /**
     * @return the userID
     */
    public int getUserID() {
        return userID;
    }

    /**
     * @param userID the userID to set
     */
    public void setUserID(int userID) {
        this.userID = userID;
    }

    /**
     * @return the lessonItemID
     */
    public int getLessonItemID() {
        return lessonItemID;
    }

    /**
     * @param lessonItemID the lessonItemID to set
     */
    public void setLessonItemID(int lessonItemID) {
        this.lessonItemID = lessonItemID;
    }

    /**
     * @return the isCompleted
     */
    public boolean isCompleted() {
        return isCompleted;
    }

    /**
     * @param isCompleted the isCompleted to set
     */
    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    /**
     * @return the completionDate
     */
    public Timestamp getCompletionDate() {
        return completionDate;
    }

    /**
     * @param completionDate the completionDate to set
     */
    public void setCompletionDate(Timestamp completionDate) {
        this.completionDate = completionDate;
    }

    /**
     * @return the lastAccessDate
     */
    public Timestamp getLastAccessDate() {
        return lastAccessDate;
    }

    /**
     * @param lastAccessDate the lastAccessDate to set
     */
    public void setLastAccessDate(Timestamp lastAccessDate) {
        this.lastAccessDate = lastAccessDate;
    }

    public boolean isIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "LessonItemProgress{" + "progressID=" + progressID + ", userID=" + userID + ", lessonItemID=" + lessonItemID + ", isCompleted=" + isCompleted + ", completionDate=" + completionDate + ", lastAccessDate=" + lastAccessDate + '}';
    }
    
}
