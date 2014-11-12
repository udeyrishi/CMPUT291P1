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
        //Use this on lab computers:
        //String url = "jdbc:oracle:thin:@gwynne.cs.ualberta.ca:1521:CRS";
        String url = "jdbc:oracle:thin:@localhost:1525:CRS";
        String m_driverName = "oracle.jdbc.driver.OracleDriver";
        
        try {
        	Class<?> drvClass = Class.forName(m_driverName);
			DriverManager.registerDriver((Driver)drvClass.newInstance());
			DriverManager.setLoginTimeout(5);
			Connection rv = DriverManager.getConnection(url, username, password);
			rv.setAutoCommit(false);
			return rv;
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