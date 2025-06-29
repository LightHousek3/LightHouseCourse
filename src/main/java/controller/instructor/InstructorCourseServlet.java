/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.instructor;

import dao.CategoryDAO;
import dao.CourseDAO;
import dao.InstructorDAO;
import dao.LessonDAO;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.Answer;
import model.Category;
import model.Course;
import model.Instructor;
import model.Lesson;
import model.Material;
import model.Question;
import model.Quiz;
import model.SuperUser;
import model.Video;
import util.FileUploadUtil;

/**
 *
 * @author Pham Quoc Tu - CE181513
 */
@WebServlet(name = "InstructorCourseServlet", urlPatterns = {"/instructor/courses", "/instructor/courses/create"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024, maxFileSize = 20 * 1024 * 1024, maxRequestSize = 60 * 1024 * 1024)
public class InstructorCourseServlet extends HttpServlet {

    private CourseDAO courseDAO;
    private LessonDAO lessonDAO;
    private InstructorDAO instructorDAO;
    private CategoryDAO categoryDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        courseDAO = new CourseDAO();
        lessonDAO = new LessonDAO();
        instructorDAO = new InstructorDAO();
        categoryDAO = new CategoryDAO();
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        // Check if user is logged in
        HttpSession session = request.getSession();
        // Test account exist
        SuperUser superUser = new SuperUser();
        superUser.setSuperUserID(4);
        superUser.setAvatar("/assets/imgs/avatars/instructor1.png");
        session.setAttribute("user", superUser);
        // End test
        SuperUser user = (SuperUser) session.getAttribute("user");

        // Get instructor information using getInstructorBySuperUserId
        Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
        // Add instructor to request attributes
        request.setAttribute("avatar", user.getAvatar());
        request.setAttribute("instructor", instructor);
        if (instructor == null) {
            // If no instructor record exists, redirect to 404 page
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        // Handle different URL patterns
        if (path.equals("/instructor/courses") && pathInfo == null) {
            // Show instructor courses list
            listCourses(request, response, instructor);
        } else if (path.equals("/instructor/courses/create") && pathInfo == null) {
            // Show form instructor create
            showFormCreateCourse(request, response, instructor);
        }
        // Test success message
        // request.setAttribute("message", "Success");
        // request.setAttribute("error", "Error");
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();
        String pathInfo = request.getPathInfo();

        if (path.equals("/instructor/courses/create")) {
            createCourse(request, response);
        }
    }

    private void listCourses(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {
        // Get courses by instructor ID
        List<Course> courses = courseDAO.getCoursesByInstructorId(instructor.getInstructorID());
        request.setAttribute("courses", courses);
        // Forward to dashboard page
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/courses.jsp").forward(request, response);
    }

    private void showFormCreateCourse(HttpServletRequest request, HttpServletResponse response, Instructor instructor)
            throws ServletException, IOException {

        List<Category> categories = categoryDAO.getAllCategories();
        System.out.println("debug: " + categories);
        List<Instructor> instructors = instructorDAO.getAll();
        Instructor inst = null;
        for (Instructor instructor1 : instructors) {
            if (instructor1.getInstructorID() == instructor.getInstructorID()) {
                inst = instructor1;
            }
        }
        instructors.remove(inst);
        System.out.println("debug: " + instructors);
        request.setAttribute("categories", categories);
        request.setAttribute("instructorList", instructors);
        // Forward to dashboard page
        request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/create-course.jsp").forward(request, response);
    }

    private void createCourse(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, String> errors = new HashMap<>();

        String name = request.getParameter("name");
        if (name == null || name.trim().isEmpty()) {
            errors.put("name", "Course name is required.");
        }
        System.out.println("debug: name: " + name);

        String description = request.getParameter("description");
        if (description == null || description.trim().isEmpty()) {
            errors.put("description", "Description is required.");
        }
        System.out.println("debug: description: " + description);

        String priceStr = request.getParameter("price");
        if (priceStr == null || priceStr.trim().isEmpty()) {
            errors.put("price", "Price is required.");
        } else {
            try {
                BigDecimal price = new BigDecimal(priceStr.trim());
                if (price.compareTo(BigDecimal.ZERO) < 0) {
                    errors.put("price", "Price must be greater or equal to 0.");
                }
            } catch (NumberFormatException ex) {
                errors.put("price", "Price must be a valid number.");
            }
        }
        System.out.println("debug: price: " + priceStr);

        String durationStr = request.getParameter("duration");
        if (durationStr == null || durationStr.trim().isEmpty()) {
            errors.put("duration", "Duration is required.");
        } else {
            try {
                int duration = Integer.parseInt(durationStr.trim());
                if (duration < 1) {
                    errors.put("duration", "Duration must be at least 1 hour.");
                }
            } catch (NumberFormatException ex) {
                errors.put("duration", "Duration must be a number.");
            }
        }
        System.out.println("debug: duration: " + durationStr);

        String level = request.getParameter("level");
        if (level == null || level.trim().isEmpty()) {
            errors.put("level", "Level is required.");
        }
        System.out.println("debug: level: " + level);

        String action = request.getParameter("action"); // "draft" or "pending"
        System.out.println("debug: action: " + action);

        // 2. Đọc instructor và category từ input hidden (chuỗi id, phân tách dấu phẩy)
        String instructorIdsStr = request.getParameter("instructorIds");
        if (instructorIdsStr == null || instructorIdsStr.trim().isEmpty()) {
            errors.put("instructorIds", "Please select at least one instructor.");
        }
        System.out.println("debug: instructorIds: " + instructorIdsStr);

        String categoryIdsStr = request.getParameter("categoryIds");
        if (categoryIdsStr == null || categoryIdsStr.trim().isEmpty()) {
            errors.put("categoryIds", "Please select at least one category.");
        }
        System.out.println("debug: categoryIds: " + categoryIdsStr);

        List<Integer> instructorIds = new ArrayList<>();
        List<Integer> categoryIds = new ArrayList<>();

        // Validate instructor & category
        if (instructorIdsStr != null && !instructorIdsStr.trim().isEmpty()) {
            for (String id : instructorIdsStr.split(",")) {
                if (id != null && !id.trim().isEmpty()) {
                    instructorIds.add(Integer.parseInt(id.trim()));
                }
            }
        }
        if (categoryIdsStr != null && !categoryIdsStr.trim().isEmpty()) {
            for (String id : categoryIdsStr.split(",")) {
                if (id != null && !id.trim().isEmpty()) {
                    categoryIds.add(Integer.parseInt(id.trim()));
                }
            }
        }

        // Video
        String[] videoLessonIds = request.getParameterValues("videoLessonId[]");
        String[] videoTitleArr = request.getParameterValues("videoTitle[]");
        String[] videoDescriptionArr = request.getParameterValues("videoDescription[]");
        String[] videoDurationArr = request.getParameterValues("videoDuration[]");
        if (videoLessonIds == null || videoLessonIds.length == 0) {
            errors.put("lessons", "You must add at least one video.");
        } else {
            for (int i = 0; i < videoTitleArr.length; i++) {
                if (videoTitleArr[i] == null || videoTitleArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All video titles are required.");
                    break;
                }
                if (videoDescriptionArr[i] == null || videoDescriptionArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All video descriptions are required.");
                    break;
                }
                if (videoDurationArr[i] == null || videoDurationArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All video durations are required.");
                    break;
                }
                try {
                    int duration = Integer.parseInt(videoDurationArr[i]);
                    if (duration <= 0) {
                        errors.put("lessons", "Video duration must be greater than 0.");
                        break;
                    }
                } catch (NumberFormatException ex) {
                    errors.put("lessons", "Video duration must be a valid number.");
                    break;
                }
            }
        }
        System.out.println("debug: videoLessonId: " + videoLessonIds);
        System.out.println("debug: videoTitle: " + videoTitleArr);
        System.out.println("debug: videoDescription: " + videoDescriptionArr);
        System.out.println("debug: videoDuration: " + videoDurationArr);

        // Material
        String[] materialLessonIds = request.getParameterValues("materialLessonId[]");
        String[] materialTitleArr = request.getParameterValues("materialTitle[]");
        String[] materialDescriptionArr = request.getParameterValues("materialDescription[]");
        String[] materialContentArr = request.getParameterValues("materialContent[]");
        if (materialLessonIds == null || materialLessonIds.length == 0) {
            errors.put("lessons", "You must add at least one material.");
        } else {
            for (int i = 0; i < materialTitleArr.length; i++) {
                if (materialTitleArr[i] == null || materialTitleArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All material titles are required.");
                    break;
                }
                if (materialDescriptionArr[i] == null || materialDescriptionArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All material descriptions are required.");
                    break;
                }
                // Nếu yêu cầu nội dung
                if (materialContentArr[i] == null || materialContentArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All material content is required.");
                    break;
                }
            }
        }
        System.out.println("debug: materialLessonId: " + materialLessonIds);
        System.out.println("debug: materialTitle: " + materialTitleArr);
        System.out.println("debug: materialDescription: " + materialDescriptionArr);
        System.out.println("debug: materialContent: " + materialContentArr);

        // Answer
        String[] answerQuizLessonIdArr = request.getParameterValues("answerQuizLessonId[]");
        String[] answerQuizIndexArr = request.getParameterValues("answerQuizIndex[]");
        String[] answerQuestionIndexArr = request.getParameterValues("answerQuestionIndex[]");
        String[] answerIndexArr = request.getParameterValues("answerIndex[]");
        String[] answerContentArr = request.getParameterValues("answerContent[]");
        Map<String, Boolean> questionHasCorrect = new HashMap<>(); // Validate answer đúng duy nhất cho mỗi câu hỏi
        Map<String, Integer> answerCountPerQuestion = new HashMap<>(); // Thêm map này để đếm số đáp án mỗi câu hỏi

        if (answerContentArr == null || answerContentArr.length == 0) {
            errors.put("lessons", "You must add at least one answer for a question.");
        } else {
            for (int i = 0; i < answerContentArr.length; i++) {
                if (answerContentArr[i] == null || answerContentArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All answer content is required.");
                    break;
                }
                String questionKey = answerQuizLessonIdArr[i] + "_" + answerQuizIndexArr[i] + "_" + answerQuestionIndexArr[i];

                // Đếm số đáp án cho từng câu hỏi
                answerCountPerQuestion.put(questionKey, answerCountPerQuestion.getOrDefault(questionKey, 0) + 1);

                String radioName = "answerIsCorrect_" + answerQuizLessonIdArr[i] + "_" + answerQuizIndexArr[i] + "_" + answerQuestionIndexArr[i];
                String correctVal = request.getParameter(radioName);
                if (correctVal != null && correctVal.equals(answerIndexArr[i])) {
                    questionHasCorrect.put(questionKey, true);
                }
            }
            // Kiểm tra từng câu hỏi đã có đáp án đúng và đủ số lượng đáp án chưa
            for (String key : answerCountPerQuestion.keySet()) {
                if (answerCountPerQuestion.get(key) < 2) {
                    errors.put("lessons", "Each question must have at least 2 answers.");
                    break;
                }
                if (!questionHasCorrect.getOrDefault(key, false)) {
                    errors.put("lessons", "Each question must have one correct answer.");
                    break;
                }
            }
        }
        System.out.println("debug: answerQuizLessonId: " + answerQuizLessonIdArr);
        System.out.println("debug: answerQuizIndex: " + answerQuizIndexArr);
        System.out.println("debug: answerQuestionIndex: " + answerQuestionIndexArr);
        System.out.println("debug: answerIndex: " + answerIndexArr);
        System.out.println("debug: answerContent: " + answerContentArr);

        // Question
        String[] questionQuizLessonIdArr = request.getParameterValues("questionQuizLessonId[]");
        String[] questionQuizIndexArr = request.getParameterValues("questionQuizIndex[]");
        String[] questionIndexArr = request.getParameterValues("questionIndex[]");
        String[] questionContentArr = request.getParameterValues("questionContent[]");
        String[] questionPointsArr = request.getParameterValues("questionPoints[]");
        if (questionQuizLessonIdArr == null || questionQuizLessonIdArr.length == 0) {
            errors.put("lessons", "You must add at least one question.");
        } else {
            for (int i = 0; i < questionQuizLessonIdArr.length; i++) {
                if (questionContentArr[i] == null || questionContentArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All question content is required.");
                    break;
                }
                if (questionPointsArr[i] == null || questionPointsArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All question points required.");
                    break;
                }
            }
        }
        System.out.println("debug: questionQuizLessonId: " + questionQuizLessonIdArr);
        System.out.println("debug: questionQuizIndex: " + questionQuizIndexArr);
        System.out.println("debug: questionIndex: " + questionIndexArr);
        System.out.println("debug: questionContent: " + questionContentArr);
        System.out.println("debug: questionPoints: " + questionPointsArr);

        // Quiz
        String[] quizLessonIds = request.getParameterValues("quizLessonId[]");
        String[] quizIndexArr = request.getParameterValues("quizIndex[]");
        String[] quizTitleArr = request.getParameterValues("quizTitle[]");
        String[] quizDescriptionArr = request.getParameterValues("quizDescription[]");
        String[] quizTimeLimitArr = request.getParameterValues("quizTimeLimit[]");
        String[] quizPassingScoreArr = request.getParameterValues("quizPassingScore[]");
        if (quizLessonIds == null || quizLessonIds.length == 0) {
            errors.put("lessons", "You must add at least one quiz.");
        } else {
            for (int i = 0; i < quizLessonIds.length; i++) {
                if (quizTitleArr[i] == null || quizTitleArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All quiz titles are required.");
                    break;
                }
                if (quizTimeLimitArr[i] == null || quizTimeLimitArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All quiz time limit required.");
                    break;
                }
                if (quizPassingScoreArr[i] == null || quizPassingScoreArr[i].trim().isEmpty()) {
                    errors.put("lessons", "All quiz passing score required.");
                    break;
                }
            }
        }
        System.out.println("debug: quizLessonId: " + quizLessonIds);
        System.out.println("debug: quizIndex: " + quizIndexArr);
        System.out.println("debug: quizTitle: " + quizTitleArr);
        System.out.println("debug: quizDescription: " + quizDescriptionArr);
        System.out.println("debug: quizTimeLimit: " + quizTimeLimitArr);
        System.out.println("debug: quizPassingScore: " + quizPassingScoreArr);

        // Lesson
        String[] lessonTempIds = request.getParameterValues("lessonTempId[]");
        String[] lessonTitles = request.getParameterValues("lessonTitle[]");
        String[] lessonDescriptions = request.getParameterValues("lessonDescription[]");
        if (lessonTempIds == null || lessonTempIds.length == 0) {
            errors.put("lessons", "You must add at least one lesson.");
        } else {
            for (int i = 0; i < lessonTitles.length; i++) {
                if (lessonTitles[i] == null || lessonTitles[i].trim().isEmpty()) {
                    errors.put("lessons", "All lesson titles are required.");
                    break;
                }
                if (lessonDescriptions[i] == null || lessonDescriptions[i].trim().isEmpty()) {
                    errors.put("lessons", "All lesson descriptions are required.");
                    break;
                }
            }
        }
        System.out.println("debug: lessonTempId: " + lessonTempIds);
        System.out.println("debug: lessonTitle: " + lessonTitles);
        System.out.println("debug: lessonDescription: " + lessonDescriptions);

        if (!errors.isEmpty()) {

            // Build lại list lessons từ param
            List<Map<String, Object>> oldLessons = new ArrayList<>();
            // Loop qua từng lesson
            if (lessonTitles != null) {
                for (int i = 0; i < lessonTitles.length; i++) {
                    Map<String, Object> lesson = new HashMap<>();
                    String tempId = (lessonTempIds != null && i < lessonTempIds.length) ? lessonTempIds[i] : String.valueOf(i);

                    lesson.put("tempId", tempId);
                    lesson.put("title", lessonTitles[i]);
                    lesson.put("description", (lessonDescriptions != null && i < lessonDescriptions.length) ? lessonDescriptions[i] : "");

                    // --- Quizzes ---
                    List<Map<String, Object>> quizzes = new ArrayList<>();
                    if (quizLessonIds != null && quizIndexArr != null) {
                        for (int q = 0; q < quizLessonIds.length; q++) {
                            // Đúng lesson
                            if (quizLessonIds[q].equals(tempId)) {
                                String quizIndex = quizIndexArr[q];
                                Map<String, Object> quiz = new HashMap<>();
                                quiz.put("quizIndex", quizIndex);
                                quiz.put("title", quizTitleArr != null && q < quizTitleArr.length ? quizTitleArr[q] : "");
                                quiz.put("description", quizDescriptionArr != null && q < quizDescriptionArr.length ? quizDescriptionArr[q] : "");
                                quiz.put("timeLimit", quizTimeLimitArr != null && q < quizTimeLimitArr.length ? quizTimeLimitArr[q] : "");
                                quiz.put("passingScore", quizPassingScoreArr != null && q < quizPassingScoreArr.length ? quizPassingScoreArr[q] : "");

                                // --- Questions ---
                                List<Map<String, Object>> questions = new ArrayList<>();
                                if (questionQuizLessonIdArr != null && questionQuizIndexArr != null && questionIndexArr != null) {
                                    for (int qq = 0; qq < questionQuizLessonIdArr.length; qq++) {
                                        if (questionQuizLessonIdArr[qq].equals(tempId)
                                                && questionQuizIndexArr[qq].equals(quizIndex)) {
                                            String questionIndex = questionIndexArr[qq];
                                            Map<String, Object> question = new HashMap<>();
                                            question.put("questionIndex", questionIndex);
                                            question.put("content", questionContentArr != null && qq < questionContentArr.length ? questionContentArr[qq] : "");
                                            question.put("points", questionPointsArr != null && qq < questionPointsArr.length ? questionPointsArr[qq] : "");

                                            // --- Answers ---
                                            List<Map<String, Object>> answers = new ArrayList<>();
                                            if (answerQuizLessonIdArr != null && answerQuizIndexArr != null && answerQuestionIndexArr != null && answerIndexArr != null) {
                                                for (int a = 0; a < answerQuizLessonIdArr.length; a++) {
                                                    if (answerQuizLessonIdArr[a].equals(tempId)
                                                            && answerQuizIndexArr[a].equals(quizIndex)
                                                            && answerQuestionIndexArr[a].equals(questionIndex)) {
                                                        Map<String, Object> answer = new HashMap<>();
                                                        answer.put("answerIndex", answerIndexArr[a]);
                                                        answer.put("content", answerContentArr != null && a < answerContentArr.length ? answerContentArr[a] : "");
                                                        // Xác định đúng đáp án
                                                        String radioName = "answerIsCorrect_" + tempId + "_" + quizIndex + "_" + questionIndex;
                                                        String correctVal = request.getParameter(radioName);
                                                        boolean isCorrect = (correctVal != null && correctVal.equals(answerIndexArr[a]));
                                                        answer.put("isCorrect", isCorrect); // Đây là Boolean
                                                        answers.add(answer);
                                                    }
                                                }
                                            }
                                            question.put("answers", answers);
                                            questions.add(question);
                                        }
                                    }
                                }
                                quiz.put("questions", questions);
                                quizzes.add(quiz);
                            }
                        }
                    }
                    lesson.put("quizzes", quizzes);

                    // --- Materials ---
                    List<Map<String, String>> materials = new ArrayList<>();
                    if (materialLessonIds != null) {
                        int matCount = 0;
                        for (int m = 0; m < materialLessonIds.length; m++) {
                            if (materialLessonIds[m].equals(tempId)) {
                                Map<String, String> mat = new HashMap<>();
                                mat.put("title", materialTitleArr != null && matCount < materialTitleArr.length ? materialTitleArr[matCount] : "");
                                mat.put("description", materialDescriptionArr != null && matCount < materialDescriptionArr.length ? materialDescriptionArr[matCount] : "");
                                mat.put("content", materialContentArr != null && matCount < materialContentArr.length ? materialContentArr[matCount] : "");
                                // Không map file (file input không giữ lại)
                                materials.add(mat);
                                matCount++;
                            }
                        }
                    }
                    lesson.put("materials", materials);

                    // --- Videos ---
                    List<Map<String, String>> videos = new ArrayList<>();
                    if (videoLessonIds != null) {
                        int vidCount = 0;
                        for (int v = 0; v < videoLessonIds.length; v++) {
                            if (videoLessonIds[v].equals(tempId)) {
                                Map<String, String> vid = new HashMap<>();
                                vid.put("title", videoTitleArr != null && vidCount < videoTitleArr.length ? videoTitleArr[vidCount] : "");
                                vid.put("description", videoDescriptionArr != null && vidCount < videoDescriptionArr.length ? videoDescriptionArr[vidCount] : "");
                                vid.put("duration", videoDurationArr != null && vidCount < videoDurationArr.length ? videoDurationArr[vidCount] : "");
                                // Không map file (file input không giữ lại)
                                videos.add(vid);
                                vidCount++;
                            }
                        }
                    }
                    lesson.put("videos", videos);

                    oldLessons.add(lesson);
                }
            }
            HttpSession session = request.getSession();
            SuperUser user = (SuperUser) session.getAttribute("user");
            // Get instructor information using getInstructorBySuperUserId
            Instructor instructor = instructorDAO.getInstructorBySuperUserId(user.getSuperUserID());
            // Add instructor to request attributes
            request.setAttribute("avatar", user.getAvatar());
            request.setAttribute("instructor", instructor);
            List<Category> categories = categoryDAO.getAllCategories();
            System.out.println("debug: " + categories);
            List<Instructor> instructors = instructorDAO.getAll();
            Instructor inst = null;
            for (Instructor instructor1 : instructors) {
                if (instructor1.getInstructorID() == instructor.getInstructorID()) {
                    inst = instructor1;
                }
            }
            instructors.remove(inst);
            System.out.println("debug: " + instructors);
            request.setAttribute("categories", categories);
            request.setAttribute("instructorList", instructors);

            request.setAttribute("oldLessons", oldLessons);
            // Nếu có lỗi sau khi upload (do file), trả lại form
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/create-course.jsp").forward(request, response);
            return;
        }

        try {
            Collection<Part> parts = request.getParts();
            // 1. Upload Video Files
            String videoPath = request.getServletContext().getRealPath("/assets/videos");
            List<String> videoFileUrls = new ArrayList<>();
            List<Part> videoFileParts = new ArrayList<>();
            for (Part part : parts) {
                if ("videoFile[]".equals(part.getName())) {
                    videoFileParts.add(part);
                }
            }
            for (int i = 0; i < videoFileParts.size(); i++) {
                Part videoFilePart = videoFileParts.get(i);
                if (videoFilePart != null && videoFilePart.getSize() > 0) {
                    String fileName = videoFilePart.getSubmittedFileName().toLowerCase();
                    if (!(fileName.endsWith(".mp4") || fileName.endsWith(".avi") || fileName.endsWith(".mov") || fileName.endsWith(".wmv"))) {
                        errors.put("lessons", "Video file must be mp4, avi, mov, or wmv.");
                        break;
                    }
                    if (videoFilePart.getSize() > 200 * 1024 * 1024) {
                        errors.put("lessons", "Video file is too large (max 200MB).");
                        break;
                    }
                    String url = FileUploadUtil.saveFile(videoFilePart, videoPath, "/assets/videos");
                    videoFileUrls.add(url);
                } else {
                    videoFileUrls.add("");
                }
            }
            if (videoTitleArr != null) {
                for (int i = 0; i < videoTitleArr.length; i++) {
                    if ((videoTitleArr[i] != null && !videoTitleArr[i].trim().isEmpty())
                            || (videoDescriptionArr != null && videoDescriptionArr[i] != null && !videoDescriptionArr[i].trim().isEmpty())
                            || (videoDurationArr != null && videoDurationArr[i] != null && !videoDurationArr[i].trim().isEmpty())) {
                        // Nếu đã nhập thông tin thì phải có file
                        if (videoFileUrls.size() <= i || videoFileUrls.get(i) == null || videoFileUrls.get(i).isEmpty()) {
                            errors.put("lessons", "Each video must have a file uploaded.");
                            break;
                        }
                    }
                }
            }
            System.out.println("debug videoFile: " + videoFileUrls);

            // 2. Upload Material Files
            String materialPath = request.getServletContext().getRealPath("/assets/materials");
            List<String> materialFileUrls = new ArrayList<>();
            List<Part> materialFileParts = new ArrayList<>();
            for (Part part : parts) {
                if ("materialFile[]".equals(part.getName())) {
                    materialFileParts.add(part);
                }
            }
            for (int i = 0; i < materialFileParts.size(); i++) {
                Part materialFilePart = materialFileParts.get(i);
                if (materialFilePart != null && materialFilePart.getSize() > 0) {
                    String fileName = materialFilePart.getSubmittedFileName().toLowerCase();
                    if (!(fileName.endsWith(".zip") || fileName.endsWith(".pdf") || fileName.endsWith(".doc") || fileName.endsWith(".docx"))) {
                        errors.put("lessons", "Material file must be zip, pdf, doc, or docx.");
                        break;
                    }
                    if (materialFilePart.getSize() > 15 * 1024 * 1024) {
                        errors.put("lessons", "Material file is too large (max 15MB).");
                        break;
                    }
                    String url = FileUploadUtil.saveFile(materialFilePart, materialPath, "/assets/materials");
                    materialFileUrls.add(url);
                } else {
                    materialFileUrls.add(""); // Không upload file, giữ trống
                }
            }
            if (materialTitleArr != null) {
                for (int i = 0; i < materialTitleArr.length; i++) {
                    if ((materialTitleArr[i] != null && !materialTitleArr[i].trim().isEmpty())
                            || (materialDescriptionArr != null && materialDescriptionArr[i] != null && !materialDescriptionArr[i].trim().isEmpty())
                            || (materialContentArr != null && materialContentArr[i] != null && !materialContentArr[i].trim().isEmpty())) {
                        // Nếu đã nhập thông tin thì phải có file
                        if (materialFileUrls.size() <= i || materialFileUrls.get(i) == null || materialFileUrls.get(i).isEmpty()) {
                            errors.put("lessons", "Each material must have a file uploaded.");
                            break;
                        }
                    }
                }
            }
            System.out.println("debug materialFile: " + materialFileUrls);

            // 3. Xử lý upload ảnh khoá học
            Part imagePart = request.getPart("imageFile");
            if (imagePart == null || imagePart.getSize() == 0) {
                errors.put("imageFile", "Course image is required.");
            } else {
                String fileName = imagePart.getSubmittedFileName().toLowerCase();
                if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif"))) {
                    errors.put("imageFile", "Course image must be JPG, PNG, JPEG or GIF.");
                }
                if (imagePart.getSize() > 5 * 1024 * 1024) {
                    errors.put("imageFile", "Image file is too large (max 5MB).");
                }
            }
            String imgPath = request.getServletContext().getRealPath("/assets/imgs/courses");
            String imgUrl = null;
            if (imagePart != null && imagePart.getSize() > 0) {
                imgUrl = FileUploadUtil.saveFile(imagePart, imgPath, "/assets/imgs/courses");
            }
            System.out.println("debug imageFile: " + imgUrl);

            List<Instructor> listInstructor = new ArrayList<>();
            for (Integer instructorId : instructorIds) {
                listInstructor.add(new Instructor(instructorId, ""));
            }

            List<Category> listCategory = new ArrayList<>();
            for (Integer categoryId : categoryIds) {
                listCategory.add(new Category(categoryId, ""));
            }
            Course course = new Course();
            course.setName(name);
            course.setDescription(description);
            course.setPrice(new Double(priceStr));
            course.setDuration(durationStr + " weeks");
            course.setLevel(level);
            course.setImageUrl(imgUrl == null ? "" : imgUrl.substring(1));
            course.setInstructors(listInstructor);
            course.setCategories(listCategory);
            course.setApprovalStatus(action);

            List<Lesson> listLesson = new ArrayList<>();
            for (int i = 0; i < lessonTitles.length; i++) {
                Lesson lesson = new Lesson();
                int lessonIndex = Integer.parseInt(lessonTempIds[i]) + 1;
                lesson.setOrderIndex(lessonIndex);
                lesson.setTitle(lessonTitles[i]);
                System.out.println("lesson: " + lesson);
                listLesson.add(lesson);
            }

            for (Lesson lesson : listLesson) {
                List<Video> lessonVideos = new ArrayList<>();
                for (int j = 0; j < videoLessonIds.length; j++) {
                    if (lesson.getOrderIndex() == (Integer.parseInt(videoLessonIds[j]) + 1)) {
                        Video video = new Video();
                        video.setTitle(videoTitleArr[j]);
                        video.setDescription(videoDescriptionArr[j]);
                        video.setDuration(Integer.parseInt(videoDurationArr[j]));
                        video.setVideoUrl(videoFileUrls.get(j)); // Phải build list videoFileUrls cùng thứ tự
                        lessonVideos.add(video);
                    }
                }
                lesson.setVideos(lessonVideos);
            }

            for (Lesson lesson : listLesson) {
                List<Material> lessonMaterials = new ArrayList<>();
                for (int j = 0; j < materialLessonIds.length; j++) {
                    if (lesson.getOrderIndex() == (Integer.parseInt(materialLessonIds[j]) + 1)) {
                        Material mat = new Material();
                        mat.setTitle(materialTitleArr[j]);
                        mat.setDescription(materialDescriptionArr[j]);
                        mat.setContent(materialContentArr[j]);
                        mat.setFileUrl(materialFileUrls.get(j)); // build đúng thứ tự
                        lessonMaterials.add(mat);
                    }
                }
                lesson.setMaterials(lessonMaterials);
            }

            for (Lesson lesson : listLesson) {
                List<Quiz> lessonQuizzes = new ArrayList<>();
                for (int q = 0; q < quizLessonIds.length; q++) {
                    if (lesson.getOrderIndex() == (Integer.parseInt(quizLessonIds[q]) + 1)) {
                        Quiz quiz = new Quiz();
                        int quizIndex = Integer.parseInt(quizIndexArr[q]) + 1;
                        quiz.setTitle(quizTitleArr[q]);
                        quiz.setDescription(quizDescriptionArr[q]);
                        quiz.setTimeLimit(Integer.parseInt(quizTimeLimitArr[q]));
                        quiz.setPassingScore(Integer.parseInt(quizPassingScoreArr[q]));

                        // --- Questions ---
                        List<Question> quizQuestions = new ArrayList<>();
                        for (int k = 0; k < questionQuizLessonIdArr.length; k++) {
                            if ((Integer.parseInt(questionQuizLessonIdArr[k]) + 1) == lesson.getOrderIndex()
                                    && (Integer.parseInt(questionQuizIndexArr[k] + 1) == quizIndex)) {
                                Question question = new Question();
                                int questionIndex = Integer.parseInt(questionIndexArr[k]) + 1;
                                question.setOrderIndex(questionIndex);
                                question.setContent(questionContentArr[k]);
                                question.setPoints(Integer.parseInt(questionPointsArr[k]));

                                // --- Answers ---
                                List<Answer> questionAnswers = new ArrayList<>();
                                for (int a = 0; a < answerQuizLessonIdArr.length; a++) {
                                    if ((Integer.parseInt(answerQuizLessonIdArr[a]) + 1) == lesson.getOrderIndex()
                                            && (Integer.parseInt(answerQuizIndexArr[a]) + 1) == quizIndex
                                            && (Integer.parseInt(answerQuestionIndexArr[a]) + 1) == questionIndex) {
                                        Answer ans = new Answer();
                                        ans.setOrderIndex(Integer.parseInt(answerIndexArr[a]) + 1);
                                        ans.setContent(answerContentArr[a]);
                                        // xác định đúng sai
                                        String radioName = "answerIsCorrect_" + lesson.getOrderIndex() + "_" + quizIndex + "_" + questionIndex;
                                        String correctVal = request.getParameter(radioName);
                                        ans.setCorrect(correctVal != null && correctVal.equals(answerIndexArr[a]));
                                        questionAnswers.add(ans);
                                    }
                                }
                                question.setAnswers(questionAnswers);
                                quizQuestions.add(question);
                            }
                        }
                        quiz.setQuestions(quizQuestions);
                        lessonQuizzes.add(quiz);
                    }
                }
                lesson.setQuizs(lessonQuizzes);
            }
            course.setLessons(listLesson);
            System.out.println(course);
            for (Lesson lesson : course.getLessons()) {
                System.out.println(lesson);

            }
            courseDAO.insertFullCourse(course);

        } catch (ServletException | IOException ex) {
            errors.put("general", "File upload failed: " + ex.getMessage());
            request.setAttribute("errors", errors);
            request.getRequestDispatcher("/WEB-INF/views/instructor/manage-courses/create-course.jsp").forward(request, response);
            return;
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
