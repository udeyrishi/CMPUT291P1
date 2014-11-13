package applicationprograms;

import java.sql.*;

import common.UIO;

/**
 * An abstract class representing an application program (sub-app) that the
 * main app will run. 
 *
 */
public abstract class ApplicationProgram {
	protected Connection connection;
	protected UIO ioproc;
	
	/**
	 * Constructor.
	 * @param connection The Connection object connected to the Oracle server.
	 * @param io The UIO object for user input.
	 * @throws IllegalArgumentException Thrown if connection is null, or is not connected
	 * with the Oracle server.
	 */
	public ApplicationProgram(Connection connection, UIO io) throws IllegalArgumentException {
		if (connection == null)
			throw new IllegalArgumentException("Null connection object passed to ApplicationProgram.");
		try {
			if (!connection.isValid(5))
				throw new IllegalArgumentException("Connection to server not established.");;
		}
		catch (SQLException e) {
			throw new IllegalArgumentException("SQLException was thrown while trying to use the Connection object. Invalid connection object passed.");
		}
		
		// Everything is valid
		this.connection = connection;
		this.ioproc = io;
	}
	
	/**
	 * Abstract method that runs that application program logic.
	 * @throws SQLException Thrown if the connection encounters an issue.
	 */
	public abstract void run() throws SQLException;
}
