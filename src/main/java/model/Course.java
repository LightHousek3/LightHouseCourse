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
    private String requirements;
    private int instructorId; // Single instructor ID for backward compatibility

    // Relationships
    private List<Instructor> instructors;
    private List<Category> categories;
    private List<Video> videos;
    private List<Lesson> lessons;

    // Additional fields for display
    private double averageRating;
    private int ratingCount;

    // Default constructor
    public Course() {
        this.instructors = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.videos = new ArrayList<>();
        this.lessons = new ArrayList<>();
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

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public int getInstructorId() {
        return instructorId;
    }

    public void setInstructorId(int instructorId) {
        this.instructorId = instructorId;
    }

    // For backward compatibility with existing code
    public String getInstructor() {
        if (instructors == null || instructors.isEmpty()) {
            return "No instructor assigned";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < instructors.size(); i++) {
            sb.append(instructors.get(i).getName());
            if (i < instructors.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    public void setInstructor(String instructor) {
        // This is a compatibility method, it doesn't actually set anything
        // since we now use a list of instructor objects
    }

    public String getRequirements() {
        return requirements;
    }

    public void setRequirements(String requirements) {
        this.requirements = requirements;
    }

    @Override
    public String toString() {
        return "Course{" + "courseID=" + courseID + ", name=" + name + ", description=" + description + ", price=" + price + ", imageUrl=" + imageUrl + ", duration=" + duration + ", level=" + level + ", approvalStatus=" + approvalStatus + ", submissionDate=" + submissionDate + ", approvalDate=" + approvalDate + ", rejectionReason=" + rejectionReason + ", requirements=" + requirements + ", instructorId=" + instructorId + ", instructors=" + instructors + ", categories=" + categories + ", videos=" + videos + ", lessons=" + lessons + ", averageRating=" + averageRating + ", ratingCount=" + ratingCount + '}';
    }

    

}
