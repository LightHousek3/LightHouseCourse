/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a quiz in the course.
 *
 * @author DangPH - CE180896
 */
public class Quiz {
    private int quizID;
    private int lessonID;
    private String title;
    private String description;
    private Integer timeLimit; // in minutes, nullable
    private int passingScore;
    private boolean isActive;

    // Non-DB fields
    private int totalQuestions;
    private List<Question> questions;

    private int totalPoints;

    // Constructors
    public Quiz() {
        questions = new ArrayList<>();
        this.isActive = true; // Default to active
    }

    public Quiz(int quizID, int lessonID, String title, String description, Integer timeLimit, int passingScore,
            boolean isActive, Timestamp createdAt) {
        this.quizID = quizID;
        this.lessonID = lessonID;
        this.title = title;
        this.description = description;
        this.timeLimit = timeLimit;
        this.passingScore = passingScore;
        this.isActive = isActive;
        this.questions = new ArrayList<>();
    }

    // Getters and Setters
    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
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

    public Integer getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(Integer timeLimit) {
        this.timeLimit = timeLimit;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    public void addQuestion(Question question) {
        this.questions.add(question);
        this.totalQuestions = this.questions.size();
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    @Override
    public String toString() {
        return "Quiz{" + "quizID=" + quizID + ", lessonID=" + lessonID + ", title=" + title + ", description=" + description + ", timeLimit=" + timeLimit + ", passingScore=" + passingScore + ", isActive=" + isActive + ", totalQuestions=" + totalQuestions + ", questions=" + questions + ", totalPoints=" + totalPoints + '}';
    }

}