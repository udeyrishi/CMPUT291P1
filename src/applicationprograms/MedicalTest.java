package applicationprograms;

import java.util.Date;
import java.sql.*;

import prescriptionentities.Employee;
import prescriptionentities.Patient;
import prescriptionentities.Test;
import common.*;

public class MedicalTest extends ApplicationProgram {

	private int health_care_number;
	private int employee_number;
	private int type_id;
	
	private String lab_name;
	private String results;
	private Date test_date;
	
	public MedicalTest(Connection connection, UIO io){
		super(connection, io);
	}
	
	@Override
	public void run(){
		try {
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
	
	private void printSuccess() {
		System.out.println("Successfully updated medical test");
	}
	
	private void printFailure() {
		System.out.println("Update failed");
	}

	private void getVerificationInfo() {
		health_care_number = Integer.valueOf(ioproc.getInputString("Enter patient number: "));
		employee_number = Integer.valueOf(ioproc.getInputString("Enter doctor number: "));
		type_id = Integer.valueOf(ioproc.getInputString("Enter test type number: "));
	}
	
	private void verifyTest() throws SQLException {
		/*
		  	private int health_care_number;
			private int employee_number;
			private int type_id;
			
			private String lab_name;
			private String results;
			private Date test_date;
		 */
		verifyPatientExists();
		verifyEmployeeExists();
		verifyTestExists();
		verifyLabExists();
		verifyPrescriptionExists();
		verifyTestDate();
	}
	
	private void verifyPatientExists() throws SQLException {
		Patient patient = new Patient(connection, ioproc);
		patient.getNameFromID(health_care_number);
	}
	
	private void verifyEmployeeExists() throws SQLException {
		Employee emp = new Employee(connection, ioproc);
		emp.getNameFromID(employee_number);
	}
	
	private void verifyTestExists() throws SQLException {
		Test test = new Test(connection, ioproc);
		test.getNameFromID(type_id);
	}
	
	private void verifyLabExists() throws SQLException {
		String query = String.format("SELECT * "
				+ "FROM medical_lab ml " 
				+ "WHERE ml.lab_name = '%s'",
				lab_name);
		
		if (!connection.createStatement().executeQuery(query).next())
			throw new IllegalArgumentException("Medical lab not valid");
	}
	
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
			throw new IllegalArgumentException("Test date after prescribe date not possible");
	}
	
	private void verifyPrescriptionExists() throws SQLException {
		// Check if health_care_number was assigned a type_id by employee_number
		// and that no test was previously done, i.e. results are null.
		// If this is not the case, throw exception.
		String query = String.format("SELECT * "
								+ "FROM test_record tr " 
								+ "WHERE tr.type_id = %d AND "
								+ "tr.patient_no = %d AND "
								+ "tr.employee_no = %d AND " 
								+ "tr.result IS NULL",
								type_id, health_care_number, employee_number);
		
		if (!connection.createStatement().executeQuery(query).next())
			throw new IllegalArgumentException("Prescription not valid");
	}
	
	private void updateDB() throws SQLException {
		// Update rows with lab_name, results, and test_date.
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
	
	private void getResultsInfo() {
		lab_name = ioproc.getInputString("Enter lab name: ");
		results = ioproc.getInputString("Enter results: ");
		test_date = ioproc.getInputDate("Enter test date: ");
	}
	
}
