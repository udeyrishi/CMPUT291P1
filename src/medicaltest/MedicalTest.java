package medicaltest;

import java.util.Date;

import common.UIO;

public class MedicalTest {

	public MedicalTest(){
		ioproc = new UIO();
	}
	
	UIO ioproc;
	
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
			System.out.println(mte.toString());
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
	
	private void verifyTest() {
		// Check if health_care_number was assigned a type_id by employee_number
		// and that no test was previously done, i.e. results are null.
		// If this is not the case, throw exception.
	}
	
	private void updateDB() {
		// Update rows with lab_name, results, and test_date.
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

}
