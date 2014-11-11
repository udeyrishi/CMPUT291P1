package medicaltest;

import java.util.Date;
import java.sql.*;

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
		} catch (NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
		}
	}
	
	private void getVerificationInfo() {
		health_care_number = Integer.valueOf(ioproc.getInputString("Enter patient number: "));
		employee_number = Integer.valueOf(ioproc.getInputString("Enter doctor number: "));
		type_id = Integer.valueOf(ioproc.getInputString("Enter test type number: "));
	}
	
	private void verifyTest() throws SQLException {
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
		
		if (connection.createStatement().executeQuery(query).getRow() == 0)
			throw new IllegalArgumentException("Prescription not valid");
	}
	
	private void updateDB() throws SQLException {
		// Update rows with lab_name, results, and test_date.
		String query = String.format("UPDATE test_record set"
					+ "medical_lab = %s,"
					+ "result = %s,"
					+ "test_date = %s,"
					+ "WHERE type_id = %d AND"
					+ "patient_no = %d AND"
					+ "employee_no = %d",
					lab_name, results, ioproc.getTestDateInSQLDateStringForm(test_date), 
					type_id, health_care_number, employee_number);

		connection.createStatement().executeQuery(query).getRow();
	}
	
	private void getResultsInfo() {
		lab_name = ioproc.getInputString("Enter lab name: ");
		results = ioproc.getInputString("Enter results: ");
		test_date = ioproc.getInputDate("Enter test date: ");
	}
	
}
