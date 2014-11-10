package medicaltest;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

import common.UIO;

public class MedicalTest {

	public MedicalTest(Connection connection){
		ioproc = new UIO();
		this.connection = connection;
	}
	
	UIO ioproc;
	Connection connection;
	
	int health_care_number;
	int employee_number;
	int type_id;
	
	String lab_name;
	String results;
	Date test_date;
	
	
	public void process(){
		try {
			getResultsInfo();
			getVerificationInfo();
			verifyTest();
			updateDB();
		} catch (MedicalTestException mte) {
			System.out.println("Process failed: " + mte.toString());
		}
	}
	
	private void getVerificationInfo() throws MedicalTestException {
		try {
			health_care_number = Integer.valueOf(ioproc.getInputString("Enter patient number: "));
			employee_number = Integer.valueOf(ioproc.getInputString("Enter doctor number: "));
			type_id = Integer.valueOf(ioproc.getInputString("Enter test type number: "));
		} catch (NumberFormatException nfe) {
			throw new MedicalTestException("Please enter numbers");
		}
	}
	
	private void verifyTest() throws MedicalTestException{
		// Check if health_care_number was assigned a type_id by employee_number
		// and that no test was previously done, i.e. results are null.
		// If this is not the case, throw exception.
		String query = String.format("SELECT *"
								+ "FROM test_record tr" 
								+ "WHERE tr.type_id = %d AND"
								+ "tr.patient_no = %d AND"
								+ "tr.employee_no = %d AND" 
								+ "tr.result IS NULL",
								type_id, health_care_number, employee_number);
		
		try {
			if (connection.createStatement().executeQuery(query).getRow() == 0)
				throw new MedicalTestException("Prescription not valid");
		} catch (SQLException se) {
			throw new MedicalTestException("Prescription not valid");
		}
	}
	
	private void updateDB() throws MedicalTestException {
		// Update rows with lab_name, results, and test_date.
		String query = String.format("UPDATE test_record set"
					+ "medical_lab = %s,"
					+ "result = %s,"
					+ "test_date = %s,"
					+ "WHERE type_id = %d AND"
					+ "patient_no = %d AND"
					+ "employee_no = %d",
					lab_name, results, getTestDateInSQLDateStringForm(), 
					type_id, health_care_number, employee_number);

		try {
			connection.createStatement().executeQuery(query).getRow();
		} catch (SQLException se) {
			throw new MedicalTestException("Test not updated");
		}
	}
	
	private void getResultsInfo() throws MedicalTestException {
		try {
			lab_name = ioproc.getInputString("Enter lab name: ");
			results = ioproc.getInputString("Enter results: ");
			test_date = ioproc.getInputDate("Enter test date: ");
		} catch (IllegalArgumentException iae) {
			throw new MedicalTestException(iae.toString());
		}
	}
	
	/* Inspired by Picasso and Prescription.java */
	private String getTestDateInSQLDateStringForm() {
		return (new SimpleDateFormat("dd-MMM-YYYY")).format(test_date);
	}
}
