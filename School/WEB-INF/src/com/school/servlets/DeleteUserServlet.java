package com.school.servlets;

import com.school.model.*;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/deleteUser")
public class DeleteUserServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String role = (String) session.getAttribute("role");

        if (!"Admin".equalsIgnoreCase(role)) {
            response.sendRedirect("Home.jsp?error=Access+Denied");
            return;
        }

        String rollNo = request.getParameter("rollNo");

        DAO dao = new DAO();
		String oktaUserId = dao.getOktaUserId(rollNo); 
		UserInfo user=dao.getUserByRollNo(rollNo);
        boolean deleted = dao.deleteUser(rollNo);

		if (deleted) {
			
			boolean oktaDeleted = dao.deleteOktaUser(oktaUserId);

			if (oktaDeleted) {
				response.sendRedirect("Home.jsp?msg=User+deleted+successfully");
			} else {
				dao.insertRollBack(user);
				response.sendRedirect("Home.jsp?error=Failed+to+delete+user+from+Okta");
			}
		} else {
			response.sendRedirect("Home.jsp?error=Failed+to+delete+user+locally");
		}

    }
}
