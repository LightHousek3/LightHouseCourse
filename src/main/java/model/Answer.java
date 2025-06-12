/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Answer model class
 *
 * @author DangPH - CE180896
 */
public class Answer {
    private int answerID;
    private int questionID;
    private String content;
    private boolean isCorrect;
    private int orderIndex;

    // Default constructor
    public Answer() {
    }

    // Constructor with parameters
    public Answer(int answerID, int questionID, String content, boolean isCorrect, int orderIndex) {
        this.answerID = answerID;
        this.questionID = questionID;
        this.content = content;
        this.isCorrect = isCorrect;
        this.orderIndex = orderIndex;
    }

    // Getters and setters
    public int getAnswerID() {
        return answerID;
    }

    public void setAnswerID(int answerID) {
        this.answerID = answerID;
    }

    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    @Override
    public String toString() {
        return "Answer{" +
                "answerID=" + answerID +
                ", questionID=" + questionID +
                ", content='" + content + '\'' +
                ", isCorrect=" + isCorrect +
                ", orderIndex=" + orderIndex +
                '}';
    }
}