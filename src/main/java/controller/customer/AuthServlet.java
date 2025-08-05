/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller.customer;

import dao.CustomerDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import dao.SuperUserDAO;
import dao.InstructorDAO;
import jakarta.servlet.http.Cookie;
import model.Customer;
import model.Instructor;
import model.SuperUser;
import service.FacebookService;
import service.GoogleService;
import util.PasswordEncrypt;

/**
 *
 * @author admin
 */
@WebServlet(name = "AuthServlet", urlPatterns = { "/login", "/logout" })
public class AuthServlet extends HttpServlet {

    private SuperUserDAO superUserDAO;
    private CustomerDAO customerDAO;

    @Override
    public void init() throws ServletException {
        super.init();
        superUserDAO = new SuperUserDAO();
        customerDAO = new CustomerDAO();
    }

    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();
        String code = request.getParameter("code");
        String url;
        switch (servletPath) {
            case "/login":
                String method = request.getParameter("method") != null ? request.getParameter("method") : "";

                switch (method) {
                    case "googleLogin":
                        // Send a request to google
                        url = "https://accounts.google.com/o/oauth2/auth?client_id=" + GoogleService.getCLIENT_ID()
                                + "&redirect_uri=" + GoogleService.getREDIRECT_URI()
                                + "&response_type=code"
                                + "&scope=email profile"
                                + "&access_type=offline"
                                + "&prompt=select_account";
                        response.sendRedirect(url);
                        break;
                    case "google":
                        // Handle login with google
                        handleGoogleLogin(code, request, response);
                        break;
                    case "facebookLogin":
                        // Send a request to facebook
                        url = "https://www.facebook.com/v21.0/dialog/oauth?client_id="
                                + FacebookService.getFACEBOOK_CLIENT_ID()
                                + "&redirect_uri=" + FacebookService.getFACEBOOK_REDIRECT_URI()
                                + "&scope=email";
                        response.sendRedirect(url);
                        break;
                    case "facebook":
                        // Handle login with facebook
                        handleFacebookLogin(code, request, response);
                        break;
                    default:
                        // Check remember-me cookie for login page
                        checkRememberMeCookie(request);

                        // Display login form
                        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                }
                break;
            case "/register":
                // Display registration form
                request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp").forward(request, response);
                break;
            case "/logout":
                // Process logout
                logout(request, response);
                break;
            default:
                break;
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String servletPath = request.getServletPath();

        switch (servletPath) {
            case "/login":
                processLogin(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/home");
                break;
        }
    }

    /**
     * Process user login.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void processLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String rememberMe = request.getParameter("remember-me");

        // Handle remember-me cookies regardless of authentication result
        handleRememberMeCookies(request, response, rememberMe, username);

        // Validate input
        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            request.setAttribute("error", "Username and password are required");
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            return;
        }

        // Encrypt password for comparison
        String encryptedPassword = PasswordEncrypt.encryptSHA256(password);

        // First try to authenticate as SuperUser (admin or instructor)
        SuperUser superUser = superUserDAO.authenticate(username, encryptedPassword);

        if (superUser != null) {
            // Create session and add superuser
            HttpSession session = request.getSession();
            session.setAttribute("user", superUser);

            // Set session timeout if remember me is checked
            if (rememberMe != null && rememberMe.equals("on")) {
                // Set session timeout to 7 days (in seconds)
                session.setMaxInactiveInterval(7 * 24 * 60 * 60);
            }

            // Redirect based on role
            if (superUser.isAdmin()) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else if (superUser.isInstructor()) {
                // Verify instructor exists in Instructors table
                InstructorDAO instructorDAO = new InstructorDAO();
                Instructor instructor = instructorDAO.findByUserId(superUser.getSuperUserID());

                if (instructor != null) {
                    response.sendRedirect(request.getContextPath() + "/instructor/dashboard");
                } else {
                    // User has instructor role but no record in Instructors table
                    response.sendRedirect(request.getContextPath() + "/home");
                }
            }
            return;
        }

        // If not a SuperUser, try to authenticate as a Customer
        Customer customer = customerDAO.authenticate(username, encryptedPassword);

        if (customer != null) {
            // Create session and add customer
            HttpSession session = request.getSession();
            session.setAttribute("user", customer);

            // Set session timeout if remember me is checked
            if (rememberMe != null && rememberMe.equals("on")) {
                // Set session timeout to 7 days (in seconds)
                session.setMaxInactiveInterval(7 * 24 * 60 * 60);
            }

            // Redirect to referer if available, otherwise to home
            String referer = request.getHeader("Referer");
            if (referer != null && !referer.contains("/login") && !referer.contains("/register")) {
                response.sendRedirect(referer);
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }

        // Authentication failed
        request.setAttribute("error", "Invalid username or password");
        request.setAttribute("savedUsername", username);
        request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
    }

    /**
     * Handle remember-me cookies management
     * 
     * @param request    The servlet request
     * @param response   The servlet response
     * @param rememberMe The remember-me parameter value
     * @param username   The username for remember-me cookie
     */
    private void handleRememberMeCookies(HttpServletRequest request, HttpServletResponse response, String rememberMe,
            String username) {
        // Clear existing cookies if remember-me is not checked
        if (rememberMe == null || !rememberMe.equals("on")) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("username") || cookie.getName().equals("isRemember")) {
                        cookie.setValue("");
                        cookie.setMaxAge(0);
                        response.addCookie(cookie);
                    }
                }
            }
        } else {
            // Set remember-me cookies when checkbox is checked (regardless of login
            // success)
            Cookie usernameCookie = new Cookie("username", username);
            Cookie rememberCookie = new Cookie("isRemember", "checked");

            // Set cookie expiration to 7 days
            int maxAge = 7 * 24 * 60 * 60; // 7 days
            usernameCookie.setMaxAge(maxAge);
            rememberCookie.setMaxAge(maxAge);

            response.addCookie(usernameCookie);
            response.addCookie(rememberCookie);
        }
    }

    /**
     * Process user logout.
     *
     * @param request  servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    private void logout(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Invalidate session
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Clear all remember-me cookies
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username") || cookie.getName().equals("isRemember")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                }
            }
        }

        // Redirect to home page
        response.sendRedirect(request.getContextPath() + "/home?logoutSuccess=true");
    }

    /**
     * Handle Google login process.
     *
     * @param code     The authorization code from Google
     * @param request  The servlet request
     * @param response The servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void handleGoogleLogin(String code, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            GoogleService googleService = new GoogleService();
            String token = googleService.getToken(code);
            Customer account = googleService.getInfoUser(token);

            if (account != null) {
                // Set authentication provider details
                account.setAuthProvider("google");

                // Make sure we have the provider ID from Google
                if (account.getAuthProviderId() == null) {
                    request.setAttribute("error", "Could not retrieve user ID from Google");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                    return;
                }

                account.setActive(true);

                // Process social login (find or create user)
                Customer customer = customerDAO.processocialLogin(account);

                if (customer != null) {
                    // Create session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", customer);

                    // Redirect to home
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    request.setAttribute("error",
                            "Your account has been disabled. Please contact support for assistance.");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Could not retrieve user information from Google");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Google login failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    /**
     * Handle Facebook login process.
     *
     * @param code     The authorization code from Facebook
     * @param request  The servlet request
     * @param response The servlet response
     * @throws ServletException If a servlet-specific error occurs
     * @throws IOException      If an I/O error occurs
     */
    private void handleFacebookLogin(String code, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            FacebookService facebookService = new FacebookService();
            String token = facebookService.getToken(code);
            Customer account = facebookService.getUserInfo(token);

            if (account != null) {
                // Set authentication provider details
                account.setAuthProvider("facebook");

                // Make sure we have the provider ID from Facebook
                if (account.getAuthProviderId() == null) {
                    request.setAttribute("error", "Could not retrieve user ID from Facebook");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                    return;
                }

                account.setActive(true);

                // Process social login (find or create user)
                Customer customer = customerDAO.processocialLogin(account);

                if (customer != null) {
                    // Create session
                    HttpSession session = request.getSession();
                    session.setAttribute("user", customer);

                    // Redirect to home
                    response.sendRedirect(request.getContextPath() + "/home");
                } else {
                    request.setAttribute("error",
                            "Your account has been disabled. Please contact support for assistance.");
                    request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
                }
            } else {
                request.setAttribute("error", "Could not retrieve user information from Facebook");
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Facebook login failed: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp").forward(request, response);
        }
    }

    /**
     * Check and process remember-me cookie for login form
     * 
     * @param request The servlet request
     */
    private void checkRememberMeCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String savedUsername = null;
            boolean isRemembered = false;

            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) {
                    savedUsername = cookie.getValue();
                }
                if (cookie.getName().equals("isRemember") && cookie.getValue().equals("checked")) {
                    isRemembered = true;
                }
            }

            if (savedUsername != null && isRemembered) {
                // Set username as a request attribute to be displayed in the form
                request.setAttribute("savedUsername", savedUsername);
                request.setAttribute("isRemembered", "checked");
            } else {
                // Clear these attributes if not remembered
                request.setAttribute("savedUsername", "");
                request.setAttribute("isRemembered", "");
            }
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Authentication Servlet";
    }
}
