/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a lesson in a course
 *
 * @author DangPH - CE180896
 */
public class Lesson {

    private int lessonID;
    private String title;
    private int courseID;
    private int orderIndex;

    // Collection for lesson items (ordered resources)
    private List<LessonItem> lessonItems = new ArrayList<>();

    private List<Video> videos = new ArrayList<>();
    private List<Quiz> quizs = new ArrayList<>();
    private List<Material> materials = new ArrayList<>();

// Additional information
    private boolean completed;

    // Constructors
    public Lesson() {
    }

    public Lesson(int lessonID, String title, String type, double duration, int courseID,
            int orderIndex) {
        this.lessonID = lessonID;
        this.title = title;
        this.courseID = courseID;
        this.orderIndex = orderIndex;
        this.completed = false;
    }

    // Getters and Setters
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

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public List<LessonItem> getLessonItems() {
        return lessonItems;
    }

    public void setLessonItems(List<LessonItem> lessonItems) {
        this.lessonItems = lessonItems;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public void setQuizs(List<Quiz> quizs) {
        this.quizs = quizs;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public List<Quiz> getQuizs() {
        return quizs;
    }

    public List<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(List<Material> materials) {
        this.materials = materials;
    }

    @Override
    public String toString() {
        return "Lesson{" + "lessonID=" + lessonID + ", title=" + title + ", courseID=" + courseID + ", orderIndex=" + orderIndex + ", lessonItems=" + lessonItems + ", videos=" + videos + ", quizs=" + quizs + ", materials=" + materials + ", completed=" + completed + '}';
    }
    

}
