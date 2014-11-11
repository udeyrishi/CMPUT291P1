package common;

import java.sql.*;
import java.util.Scanner;

public class ConnectionManager {
	public ConnectionManager() {}
	
    private String get_username(Scanner in) {
        // Returns a String containing a SQL-PLUS username, as inputed by the user.

        System.out.println("Please enter your SQL Plus username.");
        return in.nextLine();
    }
    
    
    private String get_password(Scanner in) {
        // Returns a String containing a SQL-PLUS password, as inputed by the user.

        System.out.println("Please enter your SQL Plus password.");
        return in.nextLine();
    }
    
    
    public Connection getConnection() throws ClassNotFoundException, SQLException, InstantiationException, IllegalAccessException {
		Scanner in = new Scanner(System.in);
    	String username = get_username(in);
        String password = get_password(in); // Figure out a way to hide input
        in.close();
        String m_driverName = "oracle.jdbc.driver.OracleDriver";
        Class<?> drvClass = Class.forName(m_driverName);
        DriverManager.registerDriver((Driver)drvClass.newInstance());        
        DriverManager.setLoginTimeout(5);
        String url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
        Connection con = DriverManager.getConnection(url, username, password);
        return con;
    }
}