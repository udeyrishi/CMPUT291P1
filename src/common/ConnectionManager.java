package common;

import java.sql.*;

/**
 * Manages connection with the oracle server.
 */
public class ConnectionManager {
	private UIO io;
	
	/**
	 * Constructor.
	 * @param io The UIO object for handling user input.
	 */
	public ConnectionManager(UIO io) {
		this.io = io;
	}
    
	/**
	 * Prompts the user for their SQLPlus credentials, establishes a connection
	 * with the Oracle server, and returns the Connection object.
	 * @return The Connection object, which has been connected to the Oracle server.
	 * @throws SQLException Thrown if the connection fails.
	 */
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
        
        // These exceptions should never be encountered if the above settings are
        // correct.
        catch (InstantiationException e) {
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