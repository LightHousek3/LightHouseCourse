/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Course model class
 *
 * @author DangPH - CE180896
 */
public class Course {

    private int courseID;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private String duration;
    private String level;
    private String approvalStatus;
    private Timestamp submissionDate;
    private Timestamp approvalDate;
    private String rejectionReason;

    // Form Edit Course cần phần số của trường duration
    private int durationNumber;

    // Relationships
    private List<Instructor> instructors;
    private List<Category> categories;
    private List<Lesson> lessons;
    private List<Video> videos;

    // Additional fields for display
    private double averageRating;
    private int ratingCount;
    private int enrollmentCount;

    // Default constructor
    public Course() {
        this.instructors = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.videos = new ArrayList<>();
    }

    public Course(String name, String description, double price, String imageUrl, String duration, String level, String approvalStatus, List<Instructor> instructors, List<Category> categories, List<Lesson> lessons, List<Video> videos) {
        this.instructors = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.lessons = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.level = level;
        this.approvalStatus = approvalStatus;
        this.instructors = instructors;
        this.categories = categories;
        this.lessons = lessons;
        this.videos = videos;
    }

    public Course(String name, String description, double price, String imageUrl, String duration, String level, String approvalStatus) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.duration = duration;
        this.level = level;
        this.approvalStatus = approvalStatus;
    }

    // Getters and setters
    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public Timestamp getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(Timestamp submissionDate) {
        this.submissionDate = submissionDate;
    }

    public Timestamp getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Timestamp approvalDate) {
        this.approvalDate = approvalDate;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public List<Instructor> getInstructors() {
        return instructors;
    }

    public void setInstructors(List<Instructor> instructors) {
        this.instructors = instructors;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    /**
     * Add an instructor to this course
     *
     * @param instructor The instructor to add
     */
    public void addInstructor(Instructor instructor) {
        if (this.instructors == null) {
            this.instructors = new ArrayList<>();
        }
        this.instructors.add(instructor);
    }

    /**
     * Get the primary instructor (first in the list) for backward compatibility
     *
     * @return The primary instructor ID or 0 if no instructors
     */
    public int getInstructorId() {
        if (this.instructors != null && !this.instructors.isEmpty()) {
            return this.instructors.get(0).getInstructorID();
        }
        return 0;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
    }

    /**
     * Add a lesson to this course
     *
     * @param lesson The lesson to add
     */
    public void addLesson(Lesson lesson) {
        if (this.lessons == null) {
            this.lessons = new ArrayList<>();
        }
        lesson.setCourseID(this.courseID);
        this.lessons.add(lesson);
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public int getEnrollmentCount() {
        return enrollmentCount;
    }

    public void setEnrollmentCount(int enrollmentCount) {
        this.enrollmentCount = enrollmentCount;
    }

    public int getDurationNumber() {
        return durationNumber;
    }

    public void setDurationNumber(int durationNumber) {
        this.durationNumber = durationNumber;
    }

    @Override
    public String toString() {
        return "Course{" + "courseID=" + courseID + ", name=" + name + ", description=" + description + ", price="
                + price + ", imageUrl=" + imageUrl + ", duration=" + duration + ", level=" + level + ", approvalStatus="
                + approvalStatus + ", submissionDate=" + submissionDate + ", approvalDate=" + approvalDate
                + ", rejectionReason=" + rejectionReason + "}";
    }
}
