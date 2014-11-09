package prescription;

import java.sql.*;

/**
 * The class that handles the "Prescription" application program.
 * Allows a medical doctor to prescribe a medical test to a patient.
 * @author udeyrishi
 *
 */
public class Prescription {
	
	private static int test_id = 0; // Keeps track of the test IDs already used
	
	private String test_name;
	private String test_type_id;
	private String patient_name;
	private int patient_health_care_number;
	private Connection connection;
	private EmployeeLogInManager employee;
	/**
	 * Constructor
	 * @param connection The java.sql.Connection object connected with the database.
	 */
	public Prescription(Connection connection) {
		this.connection = connection;
		employee = new EmployeeLogInManager(connection);
	}
	
	public void run() {
		printWelcomeMessage();
		
		try {
			if (!employee.logIn()) {
				System.out.println("Log-in failed. Returning to main menu.");
				return;
			}
		} catch (SQLException e) {
			System.out.println("Something bad happened in logInEmployee.");
			e.printStackTrace();
			return;
		}
		
		System.out.println(String.format("Welcome Dr. %s (Employee No.: %d)!", employee.getEmployeeName(), employee.getEmployeeNumber()));
		
	}
	
	/**
	 * Prints the welcome message.
	 */
	private void printWelcomeMessage() {
		String Heading = "MEDICAL TEST PRESCRIPTION MODE";
		StringBuffer dashes = new StringBuffer(Heading.length());
		for (int i = 0; i < Heading.length(); ++i)
			dashes.append("-");
		
		System.out.println("\n\n\n" + Heading);
		System.out.println(dashes.toString());
	}

}
