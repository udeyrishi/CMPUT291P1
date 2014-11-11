package common;

import java.sql.*;
import java.util.Scanner;

public class ConnectionManager {
	public ConnectionManager() {}
	
    private String getUsername(Scanner in) {
        // Returns a String containing a SQL-PLUS username, as inputed by the user.

        System.out.println("Please enter your SQL Plus username.");
        return in.nextLine();
    }
    
    
    private String getPassword(Scanner in) {
        // Returns a String containing a SQL-PLUS password, as inputed by the user.

        System.out.println("Please enter your SQL Plus password.");
        return in.nextLine();
    }
    
    
    public Connection getConnection() throws SQLException {
		Scanner in = new Scanner(System.in);
    	String username = getUsername(in);
        String password = getPassword(in); // Figure out a way to hide input
        in.close();
        String url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
        String m_driverName = "oracle.jdbc.driver.OracleDriver";
        
        try {
        	Class<?> drvClass = Class.forName(m_driverName);
			DriverManager.registerDriver((Driver)drvClass.newInstance());
			DriverManager.setLoginTimeout(5);
			return DriverManager.getConnection(url, username, password);
        } 
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			// Should never reach here.
        	System.out.println("Something went wrong in the getConnection method. Please check the settings.");
        	return null;
		}        
    }
}