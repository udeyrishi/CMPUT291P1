package common;

import java.sql.*;

public class ConnectionManager {
	private UIO io;
	
	public ConnectionManager(UIO io) {
		this.io = io;
	}
    
    public Connection getConnection() throws SQLException {
    	String username = io.getInputString("Please enter your SQL Plus username: ");
        String password = io.getInputString("Please enter your SQL Plus password: ");
        String url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
        String m_driverName = "oracle.jdbc.driver.OracleDriver";
        
        try {
        	Class<?> drvClass = Class.forName(m_driverName);
			DriverManager.registerDriver((Driver)drvClass.newInstance());
			DriverManager.setLoginTimeout(5);
			return DriverManager.getConnection(url, username, password);
        } 
        catch (InstantiationException e) {
			// Should never reach here.
        	System.out.println("Something went wrong in the getConnection method. Please check the settings.");
        	return null;
		} 
        
        catch (IllegalAccessException e) {
        	System.out.println("Something went wrong in the getConnection method. Please check the settings.");
        	return null;
        }
        
        catch (ClassNotFoundException e) {
        	System.out.println("Something went wrong in the getConnection method. Please check the settings.");
        	return null;
        }
    }
}