package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbUtil{
	private static final String DRIVER_NAME="com.mysql.cj.jdbc.Driver";
	private static final String URL="jdbc:mysql://localhost:3306/steganography_info?useLegacyDatetimeCode=false&serverTimezone=UTC";
	private static final String USERNAME="root";
	private static final String PASSWORD="";
	
	public static Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName(DRIVER_NAME);
		Connection con=DriverManager.getConnection(URL,USERNAME,PASSWORD);
		return con;		
}
}