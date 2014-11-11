package prescription;

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
	
	/**
	 * Constructor.
	 * @param connection The java.sql.Connection object.
	 * @param description The string description of the object.
	 */
	public PrescriptionEntity(Connection connection, String description) {
		this.connection = connection;
		this.is_done = false;
		this.description = description;
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
		
		if (option.equals(1)) {
			if (!getInfoUsingName()) {
				// Conflict
				System.out.println(String.format("Name not found, or is not unique. Try using %s ID.", description.toLowerCase()));
				option = 0;
			}
		}
		
		if (option.equals(0)) {
			if (!getInfoUsingID()) {
				System.out.println(String.format("%s ID not found in the database.", description));
				return (is_done = false);
			}
		}
		
		// Success
		return (is_done = true);
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
		Scanner in = new Scanner(System.in);
		Boolean IsValid = false;
		Integer option = 0;
		while (!IsValid) {
			try {
				System.out.println(String.format("Press 0 for entering the %s ID, 1 for %s name.", description, description));
				option = in.nextInt();
				if (option.equals(0) || option.equals(1))
					IsValid = true;
				else
					throw new InputMismatchException();
			} catch (InputMismatchException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
		
		in.close();
		return option;
	}
	
	/**
	 * Prompts the user to input the information using the entity name.
	 * @return The success value of the validity of the data. The data is valid only
	 * if the name is found, and it is a unique name in the database.
	 * @throws SQLException
	 */
	private Boolean getInfoUsingName() throws SQLException {
		Scanner in = new Scanner(System.in);
		
		System.out.println(String.format("Please enter %s name as it exists in the database:", description));
		String name = in.nextLine().trim();
		in.close();
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
		Scanner in = new Scanner(System.in);
		Integer ID = null;
		Boolean is_valid = false;
		while(!is_valid) {
			System.out.println(String.format("Please enter your %s ID:", description));
			try {
				ID = in.nextInt();
				is_valid = true;
			}
			catch (InputMismatchException e) {
				System.out.println("Invalid input. Try again.");
			}
		}
		in.close();
		
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
	 * set contains just 1 row (unique result).
	 * @param query The query to be run.
	 * @param connection The connection over which the query needs to be run.
	 * @return True if the result set has exactly 1 row, else false.
	 */
	protected static Boolean isResultSingleRow(String query, Connection connection) {
		ResultSet results;
		try {
			results = connection.createStatement().executeQuery(query);
			
			results.last();
			return results.getRow() == 1;
		} 
		catch (SQLException e) {
			System.out.println("Something went wrong in isNameUniqueHelper.");
			e.printStackTrace();
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
	protected abstract Integer getIDFromName(String name) throws SQLException;
	
	/**
	 * Finds and returns the entity name for the unique entity %ID. 
	 * @param ID The ID to lookup in the database.
	 * @return The entity name.
	 * @throws SQLException is thrown if the ID isn't found in the database.
	 */
	protected abstract String getNameFromID(Integer ID) throws SQLException;
	
	/**
	 * Checks if the %name is a unique name in the database.
	 * @param name The name to check.
	 * @return True if the name is unique, else false.
	 */
	protected abstract Boolean isNameUnique(String name);
	
	/**
	 * Returns the string that can be printed out as the success message, if the
	 * data recording process has been successful.
	 * @return The string success message.
	 */
	protected abstract String getSuccessMessage();
}
