/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Question model class
 *
 * @author DangPH - CE180896
 */
public class Question {

    private int questionID;
    private int quizID;
    private String content;
    private int points;
    private int orderIndex;
    private String type; // MCQ, TRUE_FALSE, etc.

    // Non-DB fields
    private List<Answer> answers;
    private int correctAnswerID;

    // Default constructor
    public Question() {
        answers = new ArrayList<>();
    }

    // Constructor with parameters
    public Question(int questionID, int quizID, String content, int points, int orderIndex, String questionType) {
        this.questionID = questionID;
        this.quizID = quizID;
        this.content = content;
        this.points = points;
        this.orderIndex = orderIndex;
        this.type = questionType;
        this.answers = new ArrayList<>();
    }

    // Getters and setters
    public int getQuestionID() {
        return questionID;
    }

    public void setQuestionID(int questionID) {
        this.questionID = questionID;
    }

    public int getQuizID() {
        return quizID;
    }

    public void setQuizID(int quizID) {
        this.quizID = quizID;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public int getCorrectAnswerID() {
        return correctAnswerID;
    }

    public void setCorrectAnswerID(int correctAnswerID) {
        this.correctAnswerID = correctAnswerID;
    }

    // Add an answer to the question
    public void addAnswer(Answer answer) {
        this.answers.add(answer);
    }

    @Override
    public String toString() {
        return "Question{"
                + "questionID=" + questionID
                + ", quizID=" + quizID
                + ", content='" + content + '\''
                + ", points=" + points
                + ", orderIndex=" + orderIndex
                + ", questionType='" + type + '\''
                + ", answers=" + (answers != null ? answers.size() : 0)
                + '}';
    }
}
