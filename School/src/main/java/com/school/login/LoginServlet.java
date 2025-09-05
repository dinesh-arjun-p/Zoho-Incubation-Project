package com.school.login;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import com.school.login.logindao.LoginDAO;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String uname=request.getParameter("uname");
		String pass=request.getParameter("pass");
		LoginDAO ld=new LoginDAO();
		
		if(ld.verifyUser(uname, pass)) {
		    HttpSession session = request.getSession();
		    session.setAttribute("uname", uname);

		    ld.recordLogin(uname);

		    response.sendRedirect("Main.jsp");
		} else {
		    response.sendRedirect("login.jsp");
		}

	}

}
