package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import org.mindrot.jbcrypt.BCrypt;

@WebServlet("/change-password")
public class ChangePasswordController extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    /**
     * Show the change password form
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check login
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        request.getRequestDispatcher("/change-password.jsp").forward(request, response);
    }

    /**
     * Process change password submission
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String currentPassword = request.getParameter("currentPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        // 1. Check empty fields
        if (currentPassword == null || newPassword == null || confirmPassword == null ||
            currentPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {

            request.setAttribute("error", "All fields are required.");
            request.getRequestDispatcher("/change-password.jsp").forward(request, response);
            return;
        }

        // 2. Validate current password
        if (!BCrypt.checkpw(currentPassword, user.getPassword())) {
            request.setAttribute("error", "Current password is incorrect.");
            request.getRequestDispatcher("/change-password.jsp").forward(request, response);
            return;
        }

        // 3. Validate new password length
        if (newPassword.length() < 8) {
            request.setAttribute("error", "New password must be at least 8 characters long.");
            request.getRequestDispatcher("/change-password.jsp").forward(request, response);
            return;
        }

        // 4. Confirm new password matches
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "New password and confirm password do not match.");
            request.getRequestDispatcher("/change-password.jsp").forward(request, response);
            return;
        }

        // 5. Hash new password
        String newHashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());

        // 6. Update DB
        boolean updated = userDAO.updatePassword(user.getId(), newHashedPassword);

        if (updated) {
            // Also update in session
            user.setPassword(newHashedPassword);
            session.setAttribute("user", user);

            request.setAttribute("success", "Password updated successfully!");
        } else {
            request.setAttribute("error", "Something went wrong. Try again.");
        }

        request.getRequestDispatcher("/change-password.jsp").forward(request, response);
    }
}