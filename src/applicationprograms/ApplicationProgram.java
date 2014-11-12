package applicationprograms;

import java.sql.*;

import common.UIO;

public abstract class ApplicationProgram {
	protected Connection connection;
	protected UIO ioproc;
	
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
	
	public abstract void run() throws SQLException;
}
