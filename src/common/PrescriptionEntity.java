package common;

import java.sql.*;
import java.util.*;

/**
 * Abstract class representing any entity concerned with the prescription update 
 * process.
 * @author udeyrishi
 *
 */
public abstract class PrescriptionEntity {
	private Integer ID; // Some sort of ID. Should be unique in the database
	private String name;
	private Boolean is_done; // Is the input taken from the user. If yes, is it valid?
	private String description;
	protected Connection connection;
	private UIO io;
	
	/**
	 * Constructor.
	 * @param connection The java.sql.Connection object.
	 * @param description The string description of the object.
	 */
	public PrescriptionEntity(Connection connection, UIO io, String description) {
		this.connection = connection;
		this.is_done = false;
		this.description = description;
		this.io = io;
	}
	
	/**
	 * Prompts the user for all the information needed related to the entity, and 
	 * stores it.
	 * @return The validity of the input data.
	 * @throws SQLException
	 */
	public Boolean recordInfo() throws SQLException {
		System.out.println(String.format("Please enter your %s information.", description.toLowerCase()));
		Integer option = getInfoInputMethod();
		
		while (true) {
			Boolean result = (option.equals(1)) ? getInfoUsingName() : getInfoUsingID();
			if (!result) {
				if (option.equals(1))
					System.out.println(String.format("%s name not found, or is not unique.", description));
				else
					System.out.println(String.format("%s ID not found.", description));
				String option2 = io.getInputString("Press 'q' to quit, anything else to try again.");
				if (option2.equalsIgnoreCase("q")) {
					is_done = false;
					return false;
				}
				else
					option = getInfoInputMethod();
			}
			
			else {
				is_done = true;
				return true;
			}
		}
	}
	
	/**
	 * Returns the unique ID of the entity. The input should have been successfully
	 * entered before using this method.
	 * @return The ID.
	 * @throws IllegalStateException is thrown if the method is called without 
	 * recording the data successfully first.
	 */
	public Integer getID() throws IllegalStateException {
		if (is_done)
			return ID;
		else
			throw new IllegalStateException(String.format("%s info isn't collected yet.", description));
	}
	
	/**
	 * Returns the name of the entity.
	 * @return The name of the entity.
	 @throws IllegalStateException is thrown if the method is called without 
	 * recording the data successfully first.
	 */
	public String getName() throws IllegalStateException {
		if (is_done)
			return name;
		else
			throw new IllegalStateException(String.format("%s info isn't collected yet.", description));
	}
	
	/**
	 * Returns the string description of the entity.
	 * @return The description
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Prompts the user for the input method they want to use.
	 * @return 0 if input through ID, 1 if input through name.
	 */
	private Integer getInfoInputMethod() {
		Boolean IsValid = false;
		Integer option = 0;
		while (!IsValid) {
			try {
				option = io.getInputInteger(String.format("Press 0 for entering the %s ID, 1 for %s name.", description, description));
				if (option.equals(0) || option.equals(1))
					IsValid = true;
				else
					throw new InputMismatchException();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
		
		return option;
	}
	
	/**
	 * Prompts the user to input the information using the entity name.
	 * @return The success value of the validity of the data. The data is valid only
	 * if the name is found, and it is a unique name in the database.
	 * @throws SQLException
	 */
	private Boolean getInfoUsingName() throws SQLException {
		String name = io.getInputString(String.format("Please enter %s name as it exists in the database:", description));
		if (isNameUnique(name)) {
			// Success
			this.name = name;
			this.ID = getIDFromName(name);
			return true;
		}
		else
			// Failure
			return false;
	}

	/**
	 * Prompts the user to input information using the unique entity ID.
	 * @return True if the ID is found in the database, else false.
	 */
	private Boolean getInfoUsingID() {
		Integer ID = null;
		Boolean is_valid = false;
		while(!is_valid) {
			try {
				ID = io.getInputInteger(String.format("Please enter your %s ID:", description));
				is_valid = true;
			}
			catch (InputMismatchException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
		
		try {
			this.name = getNameFromID(ID);
			this.ID = ID;
			return true;
		}
		catch (SQLException e) {
			return false;
		}
	}

	/**
	 * Static method that runs the %query using the %connection, and checks if the result
	 * set contains non zero rows.
	 * @param query The query to be run.
	 * @param connection The connection over which the query needs to be run.
	 * @return True if the result set has exactly 1 row, else false.
	 */
	public static Boolean isResultNonEmpty(String query, Connection connection) {
		ResultSet results;
		try {
			results = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
			if (results.next() && results.last()) {
				return results.getRow() > 0;
			}
			else
				return false;
		} 
		catch (SQLException e) {
			//System.out.println("Something went wrong in isNameUniqueHelper.");
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Static method that runs the %query using the %connection, and checks if the result's
	 * first row's first column is 1.
	 * @param query The query to be run.
	 * @param connection The connection over which the query needs to be run.
	 * @return True if the result set has exactly 1 row, else false.
	 */
	public static Boolean isResultOne(String query, Connection connection) {
		ResultSet results;
		try {
			results = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE).executeQuery(query);
			if (results.next() && results.last()) {
				return results.getInt(1) == 1;
			}
			else
				return false;
		} 
		catch (SQLException e) {
			//System.out.println("Something went wrong in isNameUniqueHelper.");
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Finds and returns the entity ID for the entity %name. In case multiple instances
	 * of the name exists, the ID of the first one in the result set is returned.
	 * @param name The name to lookup in the database.
	 * @return The entity ID.
	 * @throws SQLException is thrown if the Name isn't found in the database.
	 */
	public abstract Integer getIDFromName(String name) throws SQLException;
	
	/**
	 * Finds and returns the entity name for the unique entity %ID. 
	 * @param ID The ID to lookup in the database.
	 * @return The entity name.
	 * @throws SQLException is thrown if the ID isn't found in the database.
	 */
	public abstract String getNameFromID(Integer ID) throws SQLException;
	
	/**
	 * Checks if the %name is a unique name in the database.
	 * @param name The name to check.
	 * @return True if the name is unique, else false.
	 */
	public abstract Boolean isNameUnique(String name);
	
	/**
	 * Returns the string that can be printed out as the success message, if the
	 * data recording process has been successful.
	 * @return The string success message.
	 */
	public abstract String getSuccessMessage();
}
