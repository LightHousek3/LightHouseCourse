package controller.instructor;

import dao.CourseDAO;
import dao.CustomerDAO;
import dao.InstructorDAO;
import dao.LessonDAO;
import dao.LessonItemDAO;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import dao.VideoDAO;
import dao.MaterialDAO;
import dao.QuizDAO;
import model.Course;
import model.CourseProgress;
import model.Customer;
import model.Instructor;
import model.Lesson;
import model.LessonItem;
import model.LessonItemProgress;
import model.LessonProgress;
import model.SuperUser;
import model.Video;
import model.Material;
import model.Quiz;
import service.EmailService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Servlet for handling instructor student management features.
 * 
 * @author DangPH - CE180896
 */
@WebServlet(name = "InstructorStudentServlet", urlPatterns = {"/instructor/students", "/instructor/students/progress"})
public class InstructorStudentServlet extends HttpServlet {
    
    private InstructorDAO instructorDAO;
    private CourseDAO courseDAO;
    private CustomerDAO customerDAO;
    private OrderDAO orderDAO;
    private LessonDAO lessonDAO;
    private LessonItemDAO lessonItemDAO;
    
    @Override
    public void init() throws ServletException {
        super.init();
        instructorDAO = new InstructorDAO();
        courseDAO = new CourseDAO();
        customerDAO = new CustomerDAO();
        orderDAO = new OrderDAO();
        lessonDAO = new LessonDAO();
        lessonItemDAO = new LessonItemDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        SuperUser user = (SuperUser) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        if (instructor == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Get action parameter to determine what page to show
        String action = request.getParameter("action");
        String pathInfo = request.getServletPath();
        
        request.setAttribute("avatar", user.getAvatar());
        request.setAttribute("instructor", instructor);
        
        if ("/instructor/students/progress".equals(pathInfo)) {
            viewStudentProgress(request, response, instructor);
        } else {
            // Default action: list students
            listStudents(request, response, instructor);
        }
    }
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        
        int instructorId = instructor.getInstructorID();
        
        // Get pagination parameters
        int page = 1;
        int pageSize = 10;
        
        try {
            if (request.getParameter("page") != null) {
                page = Integer.parseInt(request.getParameter("page"));
                if (page < 1) {
                    page = 1;
                }
            }
            
            if (request.getParameter("size") != null) {
                pageSize = Integer.parseInt(request.getParameter("size"));
                if (pageSize < 1) {
                    pageSize = 10;
                }
            }
        } catch (NumberFormatException e) {
            // Use default values if parsing fails
        }
        
        // Get filter and search parameters
        String searchTerm = request.getParameter("search");
        String courseFilter = request.getParameter("course");
        String progressFilter = request.getParameter("progress");
        
        // Get courses taught by instructor for filtering
        List<Course> instructorCourses = courseDAO.getCoursesByInstructorId(instructorId);
        
        // Get the students enrolled in the instructor's courses with pagination and filtering
        Map<String, Object> result = getStudentsForInstructor(
                instructorId, page, pageSize, searchTerm, courseFilter, progressFilter);
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> students = (List<Map<String, Object>>) result.get("students");
        int totalStudents = (Integer) result.get("totalStudents");
        int totalPages = (int) Math.ceil((double) totalStudents / pageSize);
        
        // Set attributes for the view
        request.setAttribute("activeMenu", "students");
        request.setAttribute("students", students);
        request.setAttribute("courses", instructorCourses);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("totalStudents", totalStudents);
        request.setAttribute("searchTerm", searchTerm);
        request.setAttribute("courseFilter", courseFilter);
        request.setAttribute("progressFilter", progressFilter);
        
        // Forward to the student list page
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-students/student-list.jsp").forward(request, response);
    }
    
    private void viewStudentProgress(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        
        int studentId = 0;
        int courseId = 0;
        
        try {
            studentId = Integer.parseInt(request.getParameter("studentId"));
            courseId = Integer.parseInt(request.getParameter("courseId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student or course ID");
            return;
        }
        
        // Verify that the instructor teaches this course
        boolean isInstructorCourse = courseDAO.isInstructorForCourse(instructor.getInstructorID(), courseId);
        if (!isInstructorCourse) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "You are not authorized to view this course's progress");
            return;
        }
        
        // Get student information
        Customer student = customerDAO.getCustomerById(studentId);
        if (student == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
            return;
        }
        
        // Get course information
        Course course = courseDAO.getCourseById(courseId);
        if (course == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Course not found");
            return;
        }
        
        // Get course progress
        CourseProgress courseProgress = courseDAO.getStudentCourseProgress(studentId, courseId);
        if (courseProgress == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "No enrollment found for this student in this course");
            return;
        }
        
        // Get lesson progress
        List<Map<String, Object>> lessonsProgress = new ArrayList<>();
        List<Lesson> lessons = lessonDAO.getLessonsByCourseId(courseId);
        
        // Initialize DAOs for different item types
        VideoDAO videoDAO = new VideoDAO();
        MaterialDAO materialDAO = new MaterialDAO();
        QuizDAO quizDAO = new QuizDAO();
        
        for (Lesson lesson : lessons) {
            Map<String, Object> lessonData = new HashMap<>();
            lessonData.put("lesson", lesson);
            
            LessonProgress lessonProgress = lessonDAO.getLessonProgress(studentId, lesson.getLessonID());
            lessonData.put("progress", lessonProgress);
            
            // Get lesson item progress
            List<Map<String, Object>> lessonItemsProgress = new ArrayList<>();
            List<LessonItem> lessonItems = lessonItemDAO.getLessonItemsByLessonId(lesson.getLessonID());
            
            for (LessonItem item : lessonItems) {
                Map<String, Object> itemData = new HashMap<>();
                
                // Load the actual item (Video, Material, Quiz) based on item type
                String itemType = item.getItemType().toLowerCase();
                Object actualItem = null;
                String itemName = "";
                
                switch (itemType) {
                    case "video":
                        Video video = videoDAO.getVideoById(item.getItemID());
                        if (video != null) {
                            actualItem = video;
                            itemName = video.getTitle();
                        }
                        break;
                    case "material":
                        Material material = materialDAO.getMaterialById(item.getItemID());
                        if (material != null) {
                            actualItem = material;
                            itemName = material.getTitle();
                        }
                        break;
                    case "quiz":
                        Quiz quiz = quizDAO.getQuizById(item.getItemID());
                        if (quiz != null) {
                            actualItem = quiz;
                            itemName = quiz.getTitle();
                        }
                        break;
                    default:
                        // Use a default name for unknown types
                        itemName = "Item #" + item.getOrderIndex();
                        break;
                }
                
                // Store the actual item for potential use in JSP
                if (actualItem != null) {
                    item.setItem(actualItem);
                }
                
                // Add a name property to the LessonItem for use in the JSP
                Map<String, Object> lessonItemWithName = new HashMap<>();
                lessonItemWithName.put("lessonItemID", item.getLessonItemID());
                lessonItemWithName.put("lessonID", item.getLessonID());
                lessonItemWithName.put("orderIndex", item.getOrderIndex());
                lessonItemWithName.put("itemType", item.getItemType());
                lessonItemWithName.put("itemID", item.getItemID());
                lessonItemWithName.put("item", item.getItem());
                lessonItemWithName.put("title", itemName);
                
                itemData.put("item", lessonItemWithName);
                
                LessonItemProgress itemProgress = lessonItemDAO.getLessonItemProgress(studentId, item.getLessonItemID());
                itemData.put("progress", itemProgress);
                
                lessonItemsProgress.add(itemData);
            }
            
            lessonData.put("items", lessonItemsProgress);
            lessonsProgress.add(lessonData);
        }
        
        // Set attributes for the view
        request.setAttribute("activeMenu", "students");
        request.setAttribute("student", student);
        request.setAttribute("course", course);
        request.setAttribute("courseProgress", courseProgress);
        request.setAttribute("lessonsProgress", lessonsProgress);
        
        // Forward to the progress detail page
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-students/view-progress.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        SuperUser user = (SuperUser) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/login");
            return;
        }
        
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        if (instructor == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        String action = request.getParameter("action");
        
        if ("send-email".equals(action)) {
            sendEmail(request, response, instructor);
        } else {
            response.sendRedirect(request.getContextPath() + "/instructor/students");
        }
    }
    
    private void sendEmail(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        
        int studentId = 0;
        
        try {
            studentId = Integer.parseInt(request.getParameter("studentId"));
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid student ID");
            return;
        }
        
        // Get student information
        Customer student = customerDAO.getCustomerById(studentId);
        if (student == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Student not found");
            return;
        }
        
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        
        if (subject == null || subject.trim().isEmpty() || message == null || message.trim().isEmpty()) {
            request.setAttribute("error", "Subject and message are required");
            doGet(request, response);
            return;
        }
        
        // Format HTML email
        String htmlMessage = "<html><body>"
                + "<h2>Message from " + instructor.getName() + "</h2>"
                + "<p>" + message.replace("\n", "<br/>") + "</p>"
                + "<hr/>"
                + "<p>This is an automated message from LightHouse Learning Platform.</p>"
                + "</body></html>";
        
        // Send email
        try {
            EmailService.sendEmail(student.getEmail(), subject, htmlMessage);
            request.setAttribute("success", "Email sent successfully to " + student.getFullName());
        } catch (Exception e) {
            request.setAttribute("error", "Failed to send email: " + e.getMessage());
        }
        
        // Redirect back to student list
        doGet(request, response);
    }
    
    /**
     * Get students enrolled in courses taught by the instructor with pagination, search and filters
     * 
     * @param instructorId   the instructor ID
     * @param page           the current page number (1-based)
     * @param pageSize       number of items per page
     * @param searchTerm     optional search term for name/email
     * @param courseFilter   optional course ID filter
     * @param progressFilter optional progress filter (completed, in-progress)
     * @return Map containing list of student data and total count
     */
    private Map<String, Object> getStudentsForInstructor(
            int instructorId, int page, int pageSize, String searchTerm, String courseFilter, String progressFilter) {
        
        List<Map<String, Object>> studentDataList = courseDAO.getStudentsForInstructor(
                instructorId, page, pageSize, searchTerm, courseFilter, progressFilter);
        
        int totalStudents = courseDAO.countStudentsForInstructor(instructorId, searchTerm, courseFilter, progressFilter);
        
        Map<String, Object> result = new HashMap<>();
        result.put("students", studentDataList);
        result.put("totalStudents", totalStudents);
        
        return result;
    }
} 