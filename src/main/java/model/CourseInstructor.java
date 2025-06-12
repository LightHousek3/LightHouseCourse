/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 * Represents a mapping between courses and instructors.
 *
 * @author DangPH - CE180896
 */
public class CourseInstructor {

    private int courseID;
    private int instructorID;

    // Additional fields for display (not in database)
    private String courseName;
    private String instructorName;

    public CourseInstructor() {
    }

    public CourseInstructor(int courseID, int instructorID) {
        this.courseID = courseID;
        this.instructorID = instructorID;
    }

    public int getCourseID() {
        return courseID;
    }

    public void setCourseID(int courseID) {
        this.courseID = courseID;
    }

    public int getInstructorID() {
        return instructorID;
    }

    public void setInstructorID(int instructorID) {
        this.instructorID = instructorID;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getInstructorName() {
        return instructorName;
    }

    public void setInstructorName(String instructorName) {
        this.instructorName = instructorName;
    }

    @Override
    public String toString() {
        return "CourseInstructor{"
                + "courseID=" + courseID
                + ", instructorID=" + instructorID
                + '}';
    }
}
