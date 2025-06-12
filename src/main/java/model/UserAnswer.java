/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * UserAnswer model class
 *
 * @author DangPH - CE180896
 */
public class UserAnswer {

    private int userAnswerID;
    private int attemptID;
    private int questionID;
    private int answerID;
    private Boolean isCorrect;

    // Additional information
    private String questionContent;
    private String answerContent;
    private int points;

    // Constructors
    public UserAnswer() {
    }

    public UserAnswer(int attemptID, int questionID, int answerID) {
        this.attemptID = attemptID;
        this.questionID = questionID;
        this.answerID = answerID;
    }

    // Getters and Setters
    public int getUserAnswerID() {
        return userAnswerID;
    }

    public void setUserAnswerID(int userAnswerID) {
        this.userAnswerID = userAnswerID;
    }

    public int getAttemptID() {
        return attemptID;
    }

    public void setAttemptID(int attemptID) {
        this.attemptID = attemptID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getAnswerID() {
        return answerID;
    }

    public void setAnswerID(int answerID) {
        this.answerID = answerID;
    }

    public Boolean getCorrect() {
        return isCorrect;
    }

    public void setCorrect(Boolean correct) {
        isCorrect = correct;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public Boolean getIsCorrect() {
        return isCorrect;
    }

    public String getAnswerContent() {
        return answerContent;
    }

    public int getPoints() {
        return points;
    }

    public void setIsCorrect(Boolean isCorrect) {
        this.isCorrect = isCorrect;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "UserAnswer{" + "userAnswerID=" + userAnswerID + ", attemptID=" + attemptID + ", questionID=" + questionID + ", answerID=" + answerID + ", isCorrect=" + isCorrect + ", questionContent=" + questionContent + ", answerContent=" + answerContent + ", points=" + points + '}';
    }

}
