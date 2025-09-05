package com.school.logout;

import java.io.IOException;

import com.school.login.logindao.LoginDAO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session=request.getSession(false);
		System.out.println(session);
		String uname=(String) session.getAttribute("uname");
		LoginDAO dao = new LoginDAO();
	    dao.recordLogout(uname); 
		session.removeAttribute("uname");
		
		Cookie cookie = new Cookie("JSESSIONID", "");
        cookie.setMaxAge(0); // delete immediately
        cookie.setPath(request.getContextPath()); // scope = your app
        response.addCookie(cookie);

		session.invalidate();
		System.out.println(session);
		response.sendRedirect("login.jsp");
	}
}
