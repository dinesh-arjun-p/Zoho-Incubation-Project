<%@ page import="java.util.List" %>
<%@ page import="com.school.model.RequestAccess" %>

<h2>Welcome Student, <%= session.getAttribute("uname") %></h2>

<!-- Request Form -->
<form action="requestAccess" method="post">
    Request Date: <input type="date" name="request_date" required><br>
    Department: <input type="text" name="department" required><br>
    <button type="submit">Request</button>
</form>

<!-- Show Past Requests -->
<h3>Your Requests</h3>
<table border="1">
    <tr>
        <th>ID</th>
        <th>Date</th>
        <th>Department</th>
        <th>Status</th>
        <th>Reviewed By</th>
    </tr>
    <%
        List<RequestAccess> requests = (List<RequestAccess>) request.getAttribute("requests");
        if (requests != null) {
            for (RequestAccess r : requests) {
    %>
        <tr>
            <td><%= r.getRequestId() %></td>
            <td><%= r.getRequestDate() %></td>
            <td><%= r.getDepartment() %></td>
            <td><%= r.getStatus() %></td>
            <td><%= r.getReviewedBy() == null ? "Pending" : r.getReviewedBy() %></td>
        </tr>
    <%
            }
        }
    %>
</table>
