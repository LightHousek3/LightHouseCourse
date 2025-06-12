/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents a mapping between courses and categories.
 *
 * @author DangPH - CE180896
 */
public class CourseCategory {

    private int courseID;
    private int categoryID;

    // Additional fields for display (not in database)
    private String courseName;
    private String categoryName;

    public CourseCategory() {
    }

    public CourseCategory(int courseID, int categoryID) {
        this.courseID = courseID;
        this.categoryID = categoryID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public String toString() {
        return "CourseCategory{"
                + "courseID=" + courseID
                + ", categoryID=" + categoryID
                + '}';
    }
}
