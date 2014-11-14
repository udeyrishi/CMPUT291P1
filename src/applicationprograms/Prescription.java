package applicationprograms;

import java.sql.*;
import java.text.*;
import java.util.*;

import prescriptionentities.Employee;
import prescriptionentities.Patient;
import prescriptionentities.PrescriptionEntity;
import prescriptionentities.Test;
import common.*;

/**
 * The class that handles the "Prescription" application program.
 * Allows a medical doctor to prescribe a medical test to a patient.
 *
 */
public class Prescription extends ApplicationProgram {
	
	private static Integer test_id = -1; // Keeps track of the test IDs already used
	private PrescriptionEntity[] data;
	
	/**
     * Constructor.
     * @param connection is a Connection object connecting
     * to the remote database.
     * @param io is a UIO object for interacting with user.
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
	 * Primary method responsible for running a loop asking
	 * for prescription information and updating the database.
	 */
	@Override
	public void run() throws SQLException {
		printWelcomeMessage();
		while (true) {
			if (promptForInput()) updateDB();
			int quit = ioproc.getInputInteger("Return to main menu? (1/0 for yes/no): ");
			if (quit == 1) break;
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
			System.out.println("The changes will be committed to the database upon exiting this application program.");
		} else {
			System.out.println(String.format("Patient %s is not allowed to take the test %s. Prescription not added.", 
							data[1].getName(), // Patient name 
							data[2].getName() // Test name
							));
		}
	}

	/**
	 * Populates all members involved in a prescription
	 * by prompting the user for appropriate inputs.
	 * @throws SQLException
	 */
	private Boolean promptForInput() throws SQLException {
		for (PrescriptionEntity entity : data) {
			if (entity.recordInfo())
				System.out.println(entity.getSuccessMessage());
			else {
				System.out.println(String.format("Info retrieval failed for %s.", entity.getDescription()));
				return false;
			}
		}
		return true;
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
		return (!PrescriptionEntity.isResultNonEmpty(query, connection));
	}

	/**
	 * Updates the database with the recorded prescription information.
	 * @throws SQLException
	 */
	private void storeTestRecords() throws SQLException {
		updateTestID();
		connection.createStatement().executeQuery(
				    "INSERT INTO test_record VALUES("
					+ test_id.toString() + ", " // Self generated test_id
					+ data[2].getID().toString() +", " // Test type id
					+ data[1].getID().toString() +", " // Patient health care number
					+ data[0].getID().toString() +", " // Doctor employee number
					+ "NULL, NULL, " // Medical lab, result
					+ "to_date('" + getCurrentDate() + "','DD-MON-YYYY'), " // today
					+ "NULL)" // test date
					);
	}

	/**
	 * Update the new entry in database with a correct test_id
	 * based on the current max in the database.
	 * @throws SQLException
	 */
	private void updateTestID() throws SQLException {
		String query = "SELECT max(test_id) FROM test_record";
		ResultSet rset = connection.createStatement().executeQuery(query);
		rset.next();
		test_id = rset.getInt(1) + 1;
	}

	/**
	 * Gets the current system date.
	 * @return The current system date.
	 */
	private String getCurrentDate() {
		return (new SimpleDateFormat("dd-MMM-yyyy")).format(Calendar.getInstance().getTime());
	}

	/**
	 * Prints the welcome message.
	 */
	private void printWelcomeMessage() {
		String Heading = "\nPRESCRIPTION APPLICATION";
		String Description1 = "This application allows you (a doctor) to prescribe a medical test to a patient.";
		String Description2 = "You will need your empoloyee (doctor) information, the information of your patient, and the test information.\n";
		StringBuffer dashes = new StringBuffer(Heading.length());
		for (int i = 0; i < Heading.length(); ++i)
			dashes.append("-");
		
		System.out.println(Heading);
		System.out.println(dashes.toString());
		System.out.println(Description1);
		System.out.println(Description2);
	}
}
