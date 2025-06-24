/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import db.DBContext;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Instructor;
import model.SuperUser;

/**
 * Data access object for Instructor model
 * 
 * @author DangPH - CE180896
 */
public class InstructorDAO extends DBContext {
    private Connection conn = null;
    private PreparedStatement ps = null;
    private ResultSet rs = null;

    /**
     * Get list of course IDs taught by an instructor
     * 
     * @param instructorId The ID of the instructor
     * @return List of course IDs
     */
    public List<Integer> getInstructorCourseIds(int instructorId) {
        List<Integer> courseIds = new ArrayList<>();
        try {
            String query = "SELECT CourseID FROM CourseInstructors WHERE InstructorID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, instructorId);
            rs = ps.executeQuery();

            while (rs.next()) {
                courseIds.add(rs.getInt("CourseID"));
            }
        } catch (SQLException ex) {
            System.out.println("Error getting instructor course IDs: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return courseIds;
    }

    /**
     * Find an instructor by their user ID
     * 
     * @param superUserId The ID of the super user who is an instructor
     * @return Instructor object or null if not found
     */
    public Instructor getInstructorBySuperUserId(int superUserId) {
        try {
            String query = "SELECT i.InstructorID, i.SuperUserID, i.Biography, i.Specialization, "
                    + "i.ApprovalDate, su.FullName, su.Email "
                    + "FROM Instructors i "
                    + "JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID "
                    + "WHERE i.SuperUserID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setInt(1, superUserId);
            rs = ps.executeQuery();

            if (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setInstructorID(rs.getInt("InstructorID"));
                instructor.setSuperUserID(rs.getInt("SuperUserID"));
                instructor.setBiography(rs.getString("Biography"));
                instructor.setSpecialization(rs.getString("Specialization"));
                instructor.setApprovalDate(rs.getTimestamp("ApprovalDate"));
                instructor.setName(rs.getString("FullName"));
                instructor.setEmail(rs.getString("Email"));

                // Get statistical information
                instructor.setTotalCourses(countCoursesForInstructor(instructor.getInstructorID()));
                instructor.setTotalStudents(countStudentsForInstructor(instructor.getInstructorID()));

                return instructor;
            }
        } catch (SQLException ex) {
            System.out.println("Error finding instructor by user ID: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return null;
    }

    /**
     * Get all instructors from the database
     * 
     * @return List of instructors
     */
    public List<Instructor> getAll() {
        List<Instructor> instructors = new ArrayList<>();
        try {
            String query = "SELECT i.InstructorID, i.SuperUserID, i.Biography, i.Specialization, "
                    + "i.ApprovalDate, su.FullName, su.Email "
                    + "FROM Instructors i "
                    + "JOIN SuperUsers su ON i.SuperUserID = su.SuperUserID";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            rs = ps.executeQuery();

            while (rs.next()) {
                Instructor instructor = new Instructor();
                instructor.setInstructorID(rs.getInt("InstructorID"));
                instructor.setSuperUserID(rs.getInt("SuperUserID"));
                instructor.setBiography(rs.getString("Biography"));
                instructor.setSpecialization(rs.getString("Specialization"));
                instructor.setApprovalDate(rs.getTimestamp("ApprovalDate"));
                instructor.setName(rs.getString("FullName"));
                instructor.setEmail(rs.getString("Email"));

                instructors.add(instructor);
            }
        } catch (SQLException ex) {
            System.out.println("Error getting all instructors: " + ex.getMessage());
        } finally {
            closeResources(rs, ps, conn);
        }
        return instructors;
    }

    /**
     * Update instructor information in the database
     * 
     * @param instructor The instructor to update
     * @return True if successful, false otherwise
     */
    public boolean update(Instructor instructor) {
        try {
            String query = "UPDATE Instructors "
                    + "SET Biography = ?, Specialization = ? "
                    + "WHERE InstructorID = ?";

            conn = getConnection();
            ps = conn.prepareStatement(query);
            ps.setString(1, instructor.getBiography());
            ps.setString(2, instructor.getSpecialization());
            ps.setInt(3, instructor.getInstructorID());

            int affectedRows = ps.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating instructor: " + ex.getMessage());
            return false;
        } finally {
            closeResources(rs, ps, conn);
        }
    }

    /**
     * Count courses for an instructor
     * 
     * @param instructorId The instructor ID
     * @return Number of courses
     */
    private int countCoursesForInstructor(int instructorId) {
        int count = 0;
        try {
            String query = "SELECT COUNT(*) as total FROM CourseInstructors WHERE InstructorID = ?";
            PreparedStatement countPs = conn.prepareStatement(query);
            countPs.setInt(1, instructorId);
            ResultSet countRs = countPs.executeQuery();

            if (countRs.next()) {
                count = countRs.getInt("total");
            }

            countRs.close();
            countPs.close();
        } catch (SQLException ex) {
            System.out.println("Error counting courses for instructor: " + ex.getMessage());
        }
        return count;
    }

    /**
     * Count students enrolled in an instructor's courses
     * 
     * @param instructorId The instructor ID
     * @return Number of students
     */
    private int countStudentsForInstructor(int instructorId) {
        int count = 0;
        try {
            // Count unique customers who have made progress in courses taught by this
            // instructor
            String query = "SELECT COUNT(DISTINCT cp.CustomerID) as total "
                    + "FROM CourseProgress cp "
                    + "JOIN CourseInstructors ci ON cp.CourseID = ci.CourseID "
                    + "WHERE ci.InstructorID = ?";

            PreparedStatement countPs = conn.prepareStatement(query);
            countPs.setInt(1, instructorId);
            ResultSet countRs = countPs.executeQuery();

            if (countRs.next()) {
                count = countRs.getInt("total");
            }

            countRs.close();
            countPs.close();
        } catch (SQLException ex) {
            System.out.println("Error counting students for instructor: " + ex.getMessage());
        }
        return count;
    }
}