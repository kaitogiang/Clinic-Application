package services;

import java.sql.Connection;
import java.sql.DriverManager;

public class Database {

	public static Connection connectDB() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			Connection connect = DriverManager.getConnection("jdbc:mysql://localhost/clinic?useUnicode=true&characterEncoding=UTF-8"
					+ "","root","");
//			System.out.println("Connect successfully");
			return connect;
		} catch(Exception e) {
			System.out.println(e);
		}
		return null;
	}
}
