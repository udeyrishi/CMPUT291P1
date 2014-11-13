package applicationprograms;

import java.util.Date;
import java.sql.*;

import prescriptionentities.Employee;
import prescriptionentities.Patient;
import prescriptionentities.Test;
import common.*;

/**
 * 
 * MedicalTest is one of the four main modules of this
 * program. MedicalTest is responsible for filling in
 * the lab results after a test is conducted for a given
 * prescription.
 *
 */
public class MedicalTest extends ApplicationProgram {

	private int health_care_number;
	private int employee_number;
	private int type_id;
	
	private String lab_name;
	private String results;
	private Date test_date;
	
	/**
     * Constructor.
     * @param connection is a Connection object connecting
     * to the remote database.
     * @param io is a UIO object for interacting with user.
     */
	public MedicalTest(Connection connection, UIO io){
		super(connection, io);
	}
	
	/**
	 * A simple loop wrapper on process() that creates 
	 * a simple menu.
	 */
	@Override
	public void run() {
		while (true) {
			process();
			int quit = ioproc.getInputInteger("Return to main menu? (1/0 for yes/no): ");
			if (quit == 1) break;
		}
	}
	 
	 /**
	 * Primary method responsible for asking
	 * for lab results, lab name, etc. and updating the database.
	 */
	private void process() {
		try {
			System.out.println("\nMEDICAL TEST APPLICATION");
			System.out.println("This application allows you to add the results of a (prescribed) medical test to the database.\n");
			getResultsInfo();
			getVerificationInfo();
			verifyTest();
			updateDB();
			printSuccess();
		} catch (NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
			printFailure();
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
			printFailure();
		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
			printFailure();
		}
	}
	
	/**
	 * Prints a message after a successful update.
	 */
	private void printSuccess() {
		System.out.println("Successfully updated medical test.");
	}
	
	/**
	 * Prints a message after a failed update.
	 */
	private void printFailure() {
		System.out.println("Update failed.");
	}

	/**
	 * Gets the information about a prescription prescribed
	 * by an employee in the past. 
	 */
	private void getVerificationInfo() {
		health_care_number = Integer.valueOf(ioproc.getInputString("Please enter the patient's health care number: "));
		employee_number = Integer.valueOf(ioproc.getInputString("Please enter the doctor's employee number: "));
		type_id = Integer.valueOf(ioproc.getInputString("Please enter the test type number: "));
	}
	
	/**
	 * Gets the information to be updated into a test record
	 * after a successful lab test. 
	 */
	private void getResultsInfo() {
		lab_name = ioproc.getInputString("Please enter the name of the lab where the test was conducted: ");
		results = ioproc.getInputString("Please enter the results of the test: ");
		test_date = ioproc.getInputDate("Please enter the date the test was conducted (YYYY-MM-DD): ");
	}
	
	/**
	 * Verify that various information collected from user
	 * actually exist in database and cause no conflicts.
	 * @throws SQLException if a step fails.
	 */
	private void verifyTest() throws SQLException {
		verifyPatientExists();
		verifyEmployeeExists();
		verifyTestExists();
		verifyLabExists();
		verifyPrescriptionExists();
		verifyTestDate();
		verifyCanConduct();
	}
	
	/**
	 * Verify that a patient exists in database.
	 * @throws SQLException if doesn't exist.
	 */
	private void verifyPatientExists() throws SQLException {
		Patient patient = new Patient(connection, ioproc);
		patient.getNameFromID(health_care_number);
	}
	
	/**
	 * Verify that an employee exists in database.
	 * @throws SQLException if doesn't exist.
	 */
	private void verifyEmployeeExists() throws SQLException {
		Employee emp = new Employee(connection, ioproc);
		emp.getNameFromID(employee_number);
	}
	
	/**
	 * Verify that a test exists in database.
	 * @throws SQLException if doesn't exist.
	 */
	private void verifyTestExists() throws SQLException {
		Test test = new Test(connection, ioproc);
		test.getNameFromID(type_id);
	}
	
	/**
	 * Verify that a lab exists in database.
	 * @throws SQLException if doesn't exist.
	 */
	private void verifyLabExists() throws SQLException {
		String query = String.format("SELECT * "
				+ "FROM medical_lab ml " 
				+ "WHERE ml.lab_name = '%s'",
				lab_name);
		
		if (!connection.createStatement().executeQuery(query).next())
			throw new IllegalArgumentException("Medical lab not valid. ");
	}
	
	/**
	 * Verify that the prescribe date was before the test date.
	 * @throws SQLException if doesn't exist.
	 */
	private void verifyTestDate() throws SQLException {
		String query = String.format("SELECT tr.prescribe_date "
				+ "FROM test_record tr " 
				+ "WHERE tr.type_id = %d AND "
				+ "tr.patient_no = %d AND "
				+ "tr.employee_no = %d AND " 
				+ "tr.result IS NULL",
				type_id, health_care_number, employee_number);
		
		ResultSet rset = connection.createStatement().executeQuery(query);
		rset.next();
		Date prescribed_on = rset.getDate(1);
		if (prescribed_on.after(test_date))
			throw new IllegalArgumentException("Test date after prescribe date not possible. ");
	}
	
	/**
	 * Verify that a prescription was a
	 * Check if health_care_number was assigned a type_id by 
	 * employee_number and that no test was previously done, 
	 * i.e. results are null. If this is not the case, throw 
	 * SQLException.
	 * @throws SQLException if doesn't exist.
	 */
	private void verifyPrescriptionExists() throws SQLException {
		String query = String.format("SELECT * "
								+ "FROM test_record tr " 
								+ "WHERE tr.type_id = %d AND "
								+ "tr.patient_no = %d AND "
								+ "tr.employee_no = %d AND " 
								+ "tr.result IS NULL",
								type_id, health_care_number, employee_number);
		
		if (!connection.createStatement().executeQuery(query).next())
			throw new IllegalArgumentException("Prescription not valid. ");
	}
	
	/**
	 * Verify that the given lab can actually conduct
	 * the test. 
	 * @throws SQLException
	 */
	private void verifyCanConduct() throws SQLException {
		String query = String.format("SELECT * "
				+ "FROM can_conduct " 
				+ "WHERE lab_name = '%s' AND "
				+ "test_id = %d",
				lab_name, type_id);
		
		if (!connection.createStatement().executeQuery(query).next())
			throw new IllegalArgumentException("Test cannot be conducted at this lab. ");
	}
	
	/**
	 * If all the verification has been successful, the database
	 * will be updated with the new test results.
	 * @throws SQLException if database cannot be updated.
	 */
	private void updateDB() throws SQLException {
		String query = String.format("UPDATE test_record set "
					+ "medical_lab = '%s', "
					+ "result = '%s', "
					+ "test_date = '%s' "
					+ "WHERE type_id = %d AND "
					+ "patient_no = %d AND "
					+ "employee_no = %d ",
					lab_name, results, ioproc.getTestDateInSQLDateStringForm(test_date), 
					type_id, health_care_number, employee_number);

		connection.createStatement().executeUpdate(query);
	}

	
}