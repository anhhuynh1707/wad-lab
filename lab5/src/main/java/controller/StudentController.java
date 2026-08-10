package controller;

import dao.StudentDAO;
import model.Student;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/student")
public class StudentController extends HttpServlet {
    
    private StudentDAO studentDAO;
    
    @Override
    public void init() {
        studentDAO = new StudentDAO();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "list";
        }
        
        switch (action) {
            case "list":
                listStudents(request, response);
                break;
            case "new":
                showNewForm(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            case "delete":
                deleteStudent(request, response);
                break;
            case "search":
            	searchStudents(request, response);
            	break;
            case "filterSort":
            	filterAndSort(request, response);
            	break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null) {
            action = "insert";
        }
        
        switch (action) {
            case "insert":
                insertStudent(request, response);
                break;
            case "update":
                updateStudent(request, response);
                break;
            default:
                listStudents(request, response);
                break;
        }
    }
    
    private void listStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	// Get current page (default to 1)
    	String pageParam = request.getParameter("page");
    	int currentPage = (pageParam != null) ? Integer.parseInt(pageParam) : 1;
    	if (currentPage < 1) currentPage = 1;
    	
    	// Records per page
        int recordsPerPage = 10;
        
        // Calculate offset
        int offset = (currentPage - 1) * recordsPerPage;
        
        // Get data
        List<Student> students = studentDAO.getStudentsPaginated(offset, recordsPerPage);
        int totalRecords = studentDAO.getTotalStudents();
        
        //Calculate total pages
        int totalPages = (int) Math.ceil((double) totalRecords / recordsPerPage);
        if (currentPage > totalPages && totalPages > 0) {
            currentPage = totalPages;
        }
        //Set attributes
        request.setAttribute("students", students);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student-list.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        Student student = studentDAO.getStudentById(id);
        
        request.setAttribute("student", student);
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("/student-form.jsp");
        dispatcher.forward(request, response);
    }
    
    private void insertStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	// 1. Get parameters and create Student object
        String studentCode = request.getParameter("studentCode");
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        
        Student student = new Student(studentCode, fullName, email, major);
        
        // 2. Validate
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        // 3. If valid, proceed with insert
        if (studentDAO.addStudent(student)) {
            response.sendRedirect("student?action=list&message=Student added successfully");
        } else {
            response.sendRedirect("student?action=new&error=Failed to add student");
        }
    }
    
    private void updateStudent(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
    	// 1. Get parameters and create Student object
        int id = Integer.parseInt(request.getParameter("id"));
        String fullName = request.getParameter("fullName");
        String email = request.getParameter("email");
        String major = request.getParameter("major");
        String studentCode = request.getParameter("studentCode");
        
        Student student = new Student();
        student.setId(id);
        student.setFullName(fullName);
        student.setEmail(email);
        student.setMajor(major);
        student.setStudentCode(studentCode);
        // 2. Validate
        if (!validateStudent(student, request)) {
            request.setAttribute("student", student);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student-form.jsp");
            dispatcher.forward(request, response);
            return;
        }
        // 3. If valid, proceed with update
        if (studentDAO.updateStudent(student)) {
            response.sendRedirect("student?action=list&message=Student updated successfully");
        } else {
            response.sendRedirect("student?action=edit&id=" + id + "&error=Failed to update");
        }
    }
    
    private void deleteStudent(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        
        int id = Integer.parseInt(request.getParameter("id"));
        
        if (studentDAO.deleteStudent(id)) {
            response.sendRedirect("student?action=list&message=Student deleted successfully");
        } else {
            response.sendRedirect("student?action=list&error=Failed to delete student");
        }
    }
    private void searchStudents(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Get keyword parameter
    	String keyword = request.getParameter("keyword");
    	List<Student> students;
    	
        // 2. Decide which DAO method to call
    	try {
            if (keyword == null || keyword.trim().isEmpty()) {
            	// 3. Get the student list
                students = studentDAO.getAllStudents();
            } else {
                // Otherwise, perform search
                students = studentDAO.searchStudents(keyword);
            }

            // 4. Set request attributes
            request.setAttribute("students", students);
            request.setAttribute("keyword", keyword);

            // 5. Forward to view
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student-list.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Error searching students: " + e.getMessage());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/student-list.jsp");
            dispatcher.forward(request, response);
        }
    }
    private boolean validateStudent(Student student, HttpServletRequest request) {
        boolean isValid = true;

        // Validate student code
        String code = student.getStudentCode();
        String codePattern = "[A-Z]{2}[0-9]{3,}"; // 2 letters + 3+ digits

        if (code == null || code.trim().isEmpty()) {
            request.setAttribute("errorCode", "Student code is required.");
            isValid = false;
        } else if (!code.matches(codePattern)) {
            request.setAttribute("errorCode", "Invalid format. Use 2 uppercase letters followed by at least 3 digits. Example: SV001");
            isValid = false;
        }

        // TODO: Validate full name
        String name = student.getFullName();

        if (name == null || name.trim().isEmpty()) {
            request.setAttribute("errorName", "Full name is required.");
            isValid = false;
        } else if (name.trim().length() < 2) {
            request.setAttribute("errorName", "Full name must be at least 2 characters.");
            isValid = false;
        }

        // TODO: Validate email (only if provided)
        String email = student.getEmail();
        String emailPattern = "^[A-Za-z0-9+_.-]+@(.+)$";

        if (email != null && !email.trim().isEmpty()) {
            if (!email.matches(emailPattern)) {
                request.setAttribute("errorEmail", "Invalid email format.");
                isValid = false;
            }
        }

        // TODO: Validate major
        String major = student.getMajor();

        if (major == null || major.trim().isEmpty()) {
            request.setAttribute("errorMajor", "Major is required.");
            isValid = false;
        }

        return isValid;
    }
    private void filterAndSort(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        String major = request.getParameter("major");
        String sortBy = request.getParameter("sortBy");
        String order = request.getParameter("order");

        List<Student> students = studentDAO.getStudentsFiltered(major, sortBy, order);

        request.setAttribute("students", students);
        request.setAttribute("selectedMajor", major);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("order", order);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/student-list.jsp");
        dispatcher.forward(request, response);
    }
}