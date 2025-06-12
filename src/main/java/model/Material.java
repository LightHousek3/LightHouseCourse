/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents a learning material in the course.
 *
 * @author DangPH - CE180896
 */
public class Material {

    private int materialID;
    private int lessonID;
    private String title;
    private String description;
    private String content;
    private String fileUrl;

    // Constructors
    public Material() {
    }

    public Material(int materialID, int lessonID, String title, String description, String content, String fileUrl) {
        this.materialID = materialID;
        this.lessonID = lessonID;
        this.title = title;
        this.description = description;
        this.content = content;
        this.fileUrl = fileUrl;
    }

    // Getters and Setters
    public int getMaterialID() {
        return materialID;
    }

    public void setMaterialID(int materialID) {
        this.materialID = materialID;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    @Override
    public String toString() {
        return "Material{" + "materialID=" + materialID + ", lessonID=" + lessonID + ", title=" + title + ", description=" + description + ", content=" + content + ", fileUrl=" + fileUrl + '}';
    }

}
