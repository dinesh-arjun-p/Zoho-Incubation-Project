package com.school.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.OutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

@WebServlet("/createUser")
public class CreateUserServlet extends HttpServlet {

    // Okta details
    private static final String OKTA_DOMAIN = "https://trial-3599609.okta.com";
    private static final String API_TOKEN = "00ORTu-qzJWpZOQKmWZxcpJYW-b4yXlVGHkxwBpOAC"; 

    // Group IDs
    private static final String ADMIN_GROUP_ID = "00gvak756w9bKkWYG697";
    private static final String TEACHER_GROUP_ID = "00gvak9np7jMPgp2x697";
    private static final String STUDENT_GROUP_ID = "00gvakamixXcbpU0D697";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, java.io.IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null ||
                !"admin".equalsIgnoreCase((String) session.getAttribute("role"))) {
            response.sendRedirect("Home.jsp?error=Access+Denied");
            return;
        }

        String name = request.getParameter("name"); 
        String email = request.getParameter("email");
        String password = request.getParameter("pass");
        String roleIdStr = request.getParameter("role_id");

        try {
            int roleId = Integer.parseInt(roleIdStr);

            // 1. Save in your local DB
            DAO dao = new DAO();
            boolean success = dao.createUser(name, password, roleId, email);

            if (!success) {
                response.sendRedirect("Home.jsp?error=Failed+to+create+user+in+DB");
                return;
            }

            // 2. Create user in Okta
            String jsonBody = "{"
                    + "\"profile\": {"
                    + "\"email\": \"" + email + "\","
                    + "\"login\": \"" + email + "\","
                    + "\"name\": \"" + name + "\""
                    + "},"
                    + "\"credentials\": {"
                    + "\"password\": { \"value\": \"" + password + "\" }"
                    + "}"
                    + "}";

            URL url = new URI(OKTA_DOMAIN + "/api/v1/users?activate=true").toURL();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "SSWS " + API_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonBody.getBytes(StandardCharsets.UTF_8));
            }

            if (conn.getResponseCode() == 200 || conn.getResponseCode() == 201) {
                // Read response JSON
                String jsonResponse;
                try (InputStream is = conn.getInputStream()) {
                    jsonResponse = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                }
                JSONObject obj = new JSONObject(jsonResponse);
                String userId = obj.getString("id");

                // Assign user to group
                String groupId = switch (roleId) {
                    case 1 -> ADMIN_GROUP_ID;
                    case 2 -> TEACHER_GROUP_ID;
                    case 3 -> STUDENT_GROUP_ID;
                    default -> null;
                };

                if (groupId != null) {
                    URL groupUrl = new URI(OKTA_DOMAIN + "/api/v1/groups/" + groupId + "/users/" + userId).toURL();
                    HttpURLConnection groupConn = (HttpURLConnection) groupUrl.openConnection();
                    groupConn.setRequestMethod("PUT");
                    groupConn.setRequestProperty("Authorization", "SSWS " + API_TOKEN);
                    groupConn.getResponseCode(); // trigger the call
                }

                response.sendRedirect("Home.jsp?msg=User+created+successfully");
            } else {
                String errorResponse;
				try (InputStream es = conn.getErrorStream()) {
					errorResponse = new String(es.readAllBytes(), StandardCharsets.UTF_8);
				}
				System.err.println("Okta User Creation Failed: " + errorResponse);

				response.sendRedirect("Home.jsp?error=Okta+failed:+"
                          + java.net.URLEncoder.encode(errorResponse, StandardCharsets.UTF_8));
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("Home.jsp?error=" + e.getMessage());
        }
    }
}
		