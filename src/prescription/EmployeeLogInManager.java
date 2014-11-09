package prescription;

import java.sql.*;
import java.util.*;

public class EmployeeLogInManager {
	private Integer employee_number;
	private String employee_name;
	private Connection connection;
	private Boolean is_logged_in;
	
	public EmployeeLogInManager(Connection connection) {
		this.connection = connection;
		is_logged_in = false;
	}
	
	/**
	 * Prompts the user for the employee credentials and stores it.
	 * If log-in using option 1 isn't successful, makes another attempt using option 0.
	 * @return The success value of the log-in process.
	 * @throws SQLException
	 */
	public Boolean logIn() throws SQLException {
		System.out.println("Please enter your employee credentials.");
		Integer option = getEmployeeInfoInputMethod();
		
		if (option.equals(1)) {
			if (!getEmployeeInfoUsingName()) {
				// Conflict
				System.out.println("Name not found, or is not unique. Try using Employee ID.");
				option = 0;
			}
		}
		
		if (option.equals(0)) {
			if (!getEmployeeInfoUsingID()) {
				System.out.println("Employee ID not found in the database.");
				return (is_logged_in = false);
			}
		}
		
		// Successful login
		return (is_logged_in = true);
	}
	
	/**
	 * Returns the employee number of the employee logged in.
	 * @return The employee number
	 * @throws IllegalStateException Throws exception if the employee isn't logged in.
	 */
	public Integer getEmployeeNumber() throws IllegalStateException {
		if (is_logged_in)
			return employee_number;
		else
			throw new IllegalStateException("Employee isn't logged in.");
	}
	
	/**
	 * Returns the name of the employee logged in.
	 * @return The employee's name
	 * @throws IllegalStateException Throws exception if the employee isn't logged in.
	 */
	public String getEmployeeName() throws IllegalStateException {
		if (is_logged_in)
			return employee_name;
		else
			throw new IllegalStateException("Employee isn't logged in.");
	}
	
	/**
	 * Prompts user for choosing an Employee login method; 0 for employee ID, 1 for employee name.
	 * @return The login method option.
	 */
	private Integer getEmployeeInfoInputMethod() {
		Scanner in = new Scanner(System.in);
		Boolean IsValid = false;
		Integer option = 0;
		while (!IsValid) {
			try {
				System.out.println("Press 0 for entering the Employee ID, 1 for Employee Name.");
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
	 * Prompts the use for his/her name, and if it is a valid unique employee name,
	 * populates the class members employee_name and employee_no appropriately.
	 * @return Truth value of the uniqueness and validity of the entered name.
	 * @throws SQLException
	 */
	private Boolean getEmployeeInfoUsingName() throws SQLException {
		Scanner in = new Scanner(System.in);
		
		System.out.println("Please enter your name as it exists in the employee database:");
		String name = in.nextLine().trim();
		in.close();
		if (isEmployeeNameUnique(name)) {
			// Success
			this.employee_name = name;
			this.employee_number = getEmployeeNumber(name);
			return true;
		}
		else
			return false;
	}
	
	/**
	 * Returns the employee number from the database.
	 * Ensure that the employee name is unique; if it is not, the employee number
	 * of the first employee will be returned. If no such employee exists,
	 * SQLException is thrown.
	 * @param employee_name The employee_name of the employee in the database
	 * @return The employee number
	 * @throws SQLException
	 */
	private Integer getEmployeeNumber(String employee_name) throws SQLException {
		String query = String.format("SELECT d.employee_no "
				+ "FROM doctor d, patient p "
				+ "WHERE d.health_care_no = p.health_care_no "
				+ "AND p.name = %s", employee_name);
		return connection.createStatement().executeQuery(query).getInt(1);
	}
	
	/**
	 * Checks if the employee has a unique name.
	 * @param employee_name The name of the employee.
	 * @return The truth value of the uniqueness of the name.
	 */
	private Boolean isEmployeeNameUnique(String employee_name) {
		String query = String.format("SELECT COUNT(*) "
									+ "FROM doctor d, patient p "
									+ "WHERE d.health_care_no = p.health_care_no "
									+ "AND p.name = %s", employee_name);
		
		ResultSet results;
		try {
			results = connection.createStatement().executeQuery(query);
		
			results.last();
			return results.getRow() == 1;
		} catch (SQLException e) {
			System.out.println("Something went wrong in isEmployeeNameUnique.");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Prompts the use for his/her employee ID, and then
	 * populates the class members employee_name and employee_no appropriately.
	 * @return True, if the employee ID was found, else false.
	 */
	private Boolean getEmployeeInfoUsingID() {
		Scanner in = new Scanner(System.in);
		System.out.println("Please enter your employee ID:");
		Integer ID = in.nextInt();
		in.close();
		String query = String.format("SELECT p.name "
				+ "FROM doctor d, patient p "
				+ "WHERE d.health_care_no = p.health_care_no "
				+ "AND d.employee_no = %d", ID);
		try {
			this.employee_name = connection.createStatement().executeQuery(query).getString(1);
			this.employee_number = ID;
			return true;
		}
		catch (SQLException e) {
			return false;
		}
	}

}
