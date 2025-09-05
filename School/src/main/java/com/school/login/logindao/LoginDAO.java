package com.school.login.logindao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginDAO {
	String url="jdbc:mysql://localhost:3306/school";
	String user="root";
	String pass="Dinesh@2004";
	public boolean verifyUser(String uname,String password) {
		try {
			String sql="Select * from person where name=? and pass=?";
			Class.forName("com.mysql.jdbc.Driver");
			Connection con=DriverManager.getConnection(url,user,pass);
			PreparedStatement st=con.prepareStatement(sql);
			st.setString(1, uname);
			st.setString(2, password);
			ResultSet rs=st.executeQuery();
			if(rs.next())
				return true;
			return false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void recordLogin(String uname) {
	    String sql = "INSERT INTO login_history (username, login_time) VALUES (?, NOW())";
	    try (Connection con = DriverManager.getConnection(url, user, pass);
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

	    try (Connection con = DriverManager.getConnection(url, user, pass);
	         PreparedStatement st = con.prepareStatement(sql)) {

	        st.setString(1, uname);
	        st.executeUpdate();

	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



}
