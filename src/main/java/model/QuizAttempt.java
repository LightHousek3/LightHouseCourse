/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * QuizAttempt model class
 *
 * @author DangPH - CE180896
 */
public class QuizAttempt {

    private int attemptID;
    private int quizID;
    private int customerID;
    private Timestamp startTime;
    private Timestamp endTime;
    private Integer score; // Can be null if not completed
    private Boolean isPassed; // Can be null if not completed

    // Additional information
    private String quizTitle;
    private int totalQuestions;
    private int answeredQuestions;
    private List<UserAnswer> userAnswers = new ArrayList<>();
    private Customer customer;
    private Quiz quiz;

    // Constructors
    public QuizAttempt() {
    }

    public QuizAttempt(int quizID, int customerID) {
        this.quizID = quizID;
        this.customerID = customerID;
        this.startTime = new Timestamp(System.currentTimeMillis());
    }

    // Getters and Setters
    public int getAttemptID() {
        return attemptID;
    }

    public void setAttemptID(int attemptID) {
        this.attemptID = attemptID;
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    // For backward compatibility
    public int getUserID() {
        return customerID;
    }

    public void setUserID(int userID) {
        this.customerID = userID;
    }

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Boolean getPassed() {
        return isPassed;
    }

    public void setPassed(Boolean passed) {
        isPassed = passed;
    }

    public String getQuizTitle() {
        return quizTitle;
    }

    public void setQuizTitle(String quizTitle) {
        this.quizTitle = quizTitle;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getAnsweredQuestions() {
        return answeredQuestions;
    }

    public void setAnsweredQuestions(int answeredQuestions) {
        this.answeredQuestions = answeredQuestions;
    }

    public List<UserAnswer> getUserAnswers() {
        return userAnswers;
    }

    public void setUserAnswers(List<UserAnswer> userAnswers) {
        this.userAnswers = userAnswers;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void addUserAnswer(UserAnswer userAnswer) {
        this.userAnswers.add(userAnswer);
    }

    // Helper method to check if attempt is completed
    public boolean isCompleted() {
        return endTime != null;
    }

    // Helper method to calculate time spent in minutes
    public long getTimeSpentMinutes() {
        if (startTime == null) {
            return 0;
        }

        Timestamp end = endTime != null ? endTime : new Timestamp(System.currentTimeMillis());
        return (end.getTime() - startTime.getTime()) / (60 * 1000);
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    @Override
    public String toString() {
        return "QuizAttempt{" + "attemptID=" + attemptID + ", quizID=" + quizID + ", customerID=" + customerID +
                ", startTime=" + startTime + ", endTime=" + endTime + ", score=" + score +
                ", isPassed=" + isPassed + ", quizTitle=" + quizTitle + ", totalQuestions=" + totalQuestions +
                ", answeredQuestions=" + answeredQuestions + "}";
    }
}
