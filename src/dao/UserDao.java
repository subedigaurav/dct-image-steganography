package dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import dto.User;
import util.DbUtil;

public class UserDao {
	PreparedStatement ps=null;
	
	public boolean checkUser(User user) throws ClassNotFoundException, SQLException{
		String sql="select * from user_details where username=? and password=?";
		ps=DbUtil.getConnection().prepareStatement(sql);
		ps.setString(1, user.getUsername());
		ps.setString(2, user.getPassword());
		ResultSet rs=ps.executeQuery();
		if (rs.next()) return true;
		else return false;
	}
	
	public boolean saveUser(User user) throws ClassNotFoundException {
		String sql="insert into user_details(username,password)values(?,?)";
		try {
			ps=DbUtil.getConnection().prepareStatement(sql);
			ps.setString(1, user.getUsername());
			ps.setString(2, user.getPassword());
			ps.executeUpdate();
		} catch (SQLException e) {
			if(e.getErrorCode()==1062) return false;
		}
		return true;
	}
}
