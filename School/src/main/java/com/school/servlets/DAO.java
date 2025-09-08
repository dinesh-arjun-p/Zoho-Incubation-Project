package com.school.servlets;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.school.model.RequestAccess;
import com.school.utils.*;
import java.util.*;

public class DAO {
	
	public boolean verifyUser(String uname, String password) {
	    String sql = "SELECT * FROM person WHERE name=? AND pass=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.setString(2, password);
	        System.out.println("Hello"); // Debug log

	        try (ResultSet rs = st.executeQuery()) {
	            return rs.next(); // true if at least one row
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}


	
	
	public void recordLogin(String uname) {
	    String sql = "INSERT INTO login_history (username, login_time) VALUES (?, NOW())";
	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	public void recordLogout(String uname) {
	    String sql = "UPDATE login_history SET logout_time = NOW() " +
	                 "WHERE username=? ORDER BY id DESC LIMIT 1";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	
	public UserInfo getUserInfo(String uname, String password) {
	    UserInfo userInfo = null;

	    String sql = "SELECT p.roll_no, p.name, p.pass, r.role_name " +
	                 "FROM person p JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.name=? AND p.pass=?";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.setString(2, password);
	        ResultSet rs = st.executeQuery();

	        if (rs.next()) {
	            userInfo = new UserInfo();
	            userInfo.setRollNo(rs.getString("roll_no"));
	            userInfo.setName(rs.getString("name"));
	            userInfo.setPassword(rs.getString("pass"));
	            userInfo.setRole(rs.getString("role_name"));
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return userInfo;
	}


	
	public boolean createUser(String uname, String password, int roleId) {
	    String sql = "INSERT INTO person (roll_no, name, pass, role_id) VALUES (?, ?, ?, ?)";
	    try (Connection con = (Connection) DBUtil.getConnection()) {
	        
	        // Generate roll_no first
	        String rollNo = generateRollNo(roleId, con);

	        try (PreparedStatement st = con.prepareStatement(sql)) {
	            st.setString(1, rollNo);
	            st.setString(2, uname);
	            st.setString(3, password);
	            st.setInt(4, roleId);

	            int rows = st.executeUpdate();
	            return rows > 0;
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	
	private String generateRollNo(int roleId, Connection con) throws Exception {
	    String prefix = "";
	    if (roleId == 1) prefix = "zohoAdmin";
	    else if (roleId == 2) prefix = "zohoTeacher";
	    else if (roleId == 3) prefix = "zohoStudent";

	    String sql = "SELECT roll_no FROM person WHERE role_id=? AND roll_no LIKE ? " +
	                 "ORDER BY CAST(SUBSTRING(roll_no, LENGTH(?) + 1) AS UNSIGNED) DESC LIMIT 1";

	    try (PreparedStatement st = con.prepareStatement(sql)) {
	        st.setInt(1, roleId);
	        st.setString(2, prefix + "%");
	        st.setString(3, prefix);
	        ResultSet rs = st.executeQuery();

	        if (rs.next()) {
	            String lastRoll = rs.getString("roll_no");
	            int num = Integer.parseInt(lastRoll.substring(prefix.length()));
	            return prefix + (num + 1);
	        } else {
	            return prefix + "1";
	        }
	    }
	}

	
	public List<UserInfo> getAllUsers() {
	    List<UserInfo> users = new ArrayList<>();
	    String sql = "SELECT p.roll_no, p.name, r.role_name " +
	                 "FROM person p JOIN role r ON p.role_id = r.role_id";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql);
	         ResultSet rs = st.executeQuery()) {

	        while (rs.next()) {
	            UserInfo u = new UserInfo();
	            u.setRollNo(rs.getString("roll_no"));
	            u.setName(rs.getString("name"));
	            u.setRole(rs.getString("role_name"));
	            users.add(u);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return users;
	}
	
	public UserInfo getUserByRollNo(String rollNo) {
	    UserInfo user = null;

	    String sql = "SELECT p.roll_no, p.name, p.pass, r.role_name " +
	                 "FROM person p " +
	                 "JOIN role r ON p.role_id = r.role_id " +
	                 "WHERE p.roll_no = ?";

	    try (Connection con = (Connection) DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, rollNo);

	        try (ResultSet rs = st.executeQuery()) {
	            if (rs.next()) {
	                user = new UserInfo();
	                user.setRollNo(rs.getString("roll_no"));
	                user.setName(rs.getString("name"));
	                user.setPassword(rs.getString("pass"));
	                user.setRole(rs.getString("role_name"));

	               
	            }
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return user;
	}
	
	public boolean deleteUser(String rollNo) {
	    String sql = "DELETE FROM person WHERE roll_no=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {
	        st.setString(1, rollNo);
	        return st.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	public boolean createRequest(String department, String rollNo, String date) {
	    String sql = "INSERT INTO request_access (request_date, department, requested_by) VALUES (?, ?, ?)";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement st = con.prepareStatement(sql)) {
	    	st.setString(1,date);
	        st.setString(2, department);
	        st.setString(3, rollNo);
	        return st.executeUpdate() > 0;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}
	
	public List<RequestAccess> getRequestedByStudent(String rollNo) {
        List<RequestAccess> requests = new ArrayList<>();
        String sql = "SELECT * FROM request_access WHERE requested_by = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
        	System.out.println(rollNo);
            ps.setString(1, rollNo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RequestAccess req = new RequestAccess();
                    req.setRequestId(rs.getInt("request_id"));
                    req.setRequestDate(rs.getDate("request_date"));
                    req.setDepartment(rs.getString("department"));
                    req.setRequestedBy(rs.getString("requested_by"));
                    req.setStatus(rs.getString("status"));
                    req.setReviewedBy(rs.getString("reviewed_by"));
                    requests.add(req);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }
	
	public List<RequestAccess> getPendingRequests() {
	    List<RequestAccess> requests = new ArrayList<>();
	    String sql = "SELECT * FROM request_access WHERE status = 'Pending'";

	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            RequestAccess req = new RequestAccess();
	            req.setRequestId(rs.getInt("request_id"));
	            req.setRequestDate(rs.getDate("request_date"));
	            req.setDepartment(rs.getString("department"));
	            req.setRequestedBy(rs.getString("requested_by"));
	            req.setStatus(rs.getString("status"));
	            req.setReviewedBy(rs.getString("reviewed_by"));

	            requests.add(req);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return requests;
	}
	
	
	public boolean updateRequestStatus(int requestId, String status, String reviewedBy) {
	    String sql = "UPDATE request_access SET status=?, reviewed_by=? WHERE request_id=?";
	    try (Connection con = DBUtil.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {

	        ps.setString(1, status); // "Approved" or "Rejected"
	        ps.setString(2, reviewedBy);
	        ps.setInt(3, requestId);

	        return ps.executeUpdate() > 0;

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}



}
