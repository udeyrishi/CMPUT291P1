package prescription;

import java.sql.*;
import java.text.*;
import java.util.*;
import common.*;

/**
 * The class that handles the "Prescription" application program.
 * Allows a medical doctor to prescribe a medical test to a patient.
 * @author udeyrishi
 *
 */
public class Prescription extends ApplicationProgram {
	
	private static Integer test_id = -1; // Keeps track of the test IDs already used
	private PrescriptionEntity[] data;
	
	/**
	 * 
	 * Constructor
	 * @param connection The java.sql.Connection object. The connection to the remote
	 * server should have been established.
	 * @throws IllegalArgumentException is thrown if connection to the server is not established.
	 */
	public Prescription(Connection connection, UIO io) {
		super(connection, io);
		data = new PrescriptionEntity[3];
		// In the correct order of processing
		data[0] = new Employee(connection, io);
		data[1] = new Patient(connection, io);
		data[2] = new Test(connection, io);
	}
	
	/**
	 * Takes the prescription input, and updates the database.
	 * @throws SQLException
	 */
	@Override
	public void run() throws SQLException {
		printWelcomeMessage();
		Boolean cont = true;
		while(cont) {
			promptForInput();
			updateDB();
			String input = ioproc.getInputString("Press 'm' to return to main menu; any other key to add another prescription.");
			if (input.equalsIgnoreCase("m"))
				cont = false;
		}
	}
	
	/**
	 * Using the data in %data, checks if the given test can be prescribed to the patient.
	 * If yes, updates the database; if not, doesn't do anything.
	 * @throws SQLException
	 */
	private void updateDB() throws SQLException {
		if (checkAllowed()) {
			storeTestRecords();
			System.out.println("Prescription added.");
		}
		else
			System.out.println(String.format("Patient %s is not allowed to take the test %s. Prescription not added.", 
					data[1].getName(), // Patient name 
					data[2].getName() // Test name
					));
		
	}

	/**
	 * Populates all members of the data array by prompting the user for appropriate
	 * inputs.
	 * @throws SQLException
	 */
	private void promptForInput() throws SQLException {
		for (PrescriptionEntity entity : data) {
			if (entity.recordInfo())
				System.out.println(entity.getSuccessMessage());
			else {
				System.out.println(String.format("Info retrieval failed for %s.", entity.getDescription()));
				return;
			}
		}
	}
	
	/**
	 * Checks if the recorded patient can take the recorded test.
	 * @return True, if test is allowed, else false.
	 */
	private Boolean checkAllowed() {
		String query = String.format("SELECT * "
						+ "FROM not_allowed "
						+ "WHERE health_care_no = %d "
						+ "AND test_id = %d", data[1].getID(), data[2].getID());
		// Single row is sufficient as both health_care_no and test_id are primary
		// keys (hence, unique) in their respective tables.
		return (!PrescriptionEntity.isResultSingleRow(query, connection));
	}

	/**
	 * Updates the database with the recorded prescription information.
	 * @throws SQLException
	 */
	private void storeTestRecords() throws SQLException {
		connection.createStatement().executeQuery(
				    "INSERT INTO test_record VALUES("
					+ (++test_id).toString() + ", " // Self generated test_id
					+ data[2].getID().toString() +", " // Test type id
					+ data[1].getID().toString() +", " // Patient health care number
					+ data[0].getID().toString() +", " // Doctor employee number
					+ "NULL, NULL, " // Medical lab, result
					+ "to_date('" + getCurrentDate() + "','DD-MON-YYYY'), " // today
					+ "NULL)" // test date
					);
	}

	/**
	 * Gets the current system date.
	 * @return The current system date.
	 */
	private String getCurrentDate() {
		return (new SimpleDateFormat("dd-MMM-YYYY")).format(Calendar.getInstance().getTime());
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
