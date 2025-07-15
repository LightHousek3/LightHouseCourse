/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents a video in the course.
 *
 * @author DangPH - CE180896
 */
public class Video {

    private int videoID;
    private int lessonID;
    private String title;
    private String description;
    private String videoUrl;
    private int duration;

    // Constructors
    public Video() {
    }

    public Video(int videoID, int lessonID, String title, String description, String videoUrl, int duration) {
        this.videoID = videoID;
        this.lessonID = lessonID;
        this.title = title;
        this.description = description;
        this.videoUrl = videoUrl;
        this.duration = duration;
    }

    public Video(int lessonID, String title, String description, int duration, String videoUrl) {
        this.lessonID = lessonID;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.videoUrl = videoUrl;
    }
    // Getters and setters
    public int getVideoID() {
        return videoID;
    }

    public void setVideoID(int videoID) {
        this.videoID = videoID;
    }

    public int getLessonID() {
        return lessonID;
    }

    public void setLessonID(int lessonID) {
        this.lessonID = lessonID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * Get formatted duration in MM:SS format
     *
     * @return String containing formatted duration
     */
    public String getFormattedDuration() {
        int minutes = duration / 60;
        int seconds = duration % 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    // For backward compatibility with existing code
    public int getCourseID() {
        return lessonID; // This is not correct but maintains API compatibility
    }

    public void setCourseID(int courseID) {
        this.lessonID = courseID; // This is not correct but maintains API compatibility
    }

    @Override
    public String toString() {
        return "Video{" + "videoID=" + videoID + ", lessonID=" + lessonID + ", title=" + title + ", description=" + description + ", videoUrl=" + videoUrl + ", duration=" + duration + '}';
    }

}
