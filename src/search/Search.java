package search;

import java.sql.*;
import java.util.Date;

import prescription.Employee;
import prescription.Patient;
import prescription.Test;
import common.UIO;

public class Search {

	public Search(Connection connection) {
		ioproc = new UIO();
	}

	public void main() {
		String prompt = "MedicalTest: ";
		printInstructionHelp();
		handleInput(ioproc.getInputInteger(prompt));
	}

	private void printInstructionHelp() {
		System.out.println("Enter:");
		System.out.println("1 to get a patient's test history");
		System.out.println("2 to get a doctor's prescription history");
		System.out.println("3 to get at risk patients for a test type");
	}

	private void handleInput(int choice) {
		try {
			if (choice == 1) System.out.println(getPatientTestHistory());
			else if (choice == 2) System.out.println(getDoctorsPrescriptionsWithinRange());
			else if (choice == 3) System.out.println(getAtRiskPatientsForTest());
			else throw new SearchException("Invalid choice");
		} catch (SearchException se) {
			System.out.println(se.toString());
		}
	}

	Connection connection;
	UIO ioproc;


	public String getPatientTestHistory() throws SearchException {
		String input_patient = ioproc.getInputString("Please enter patient name or number: ");
		Patient patient = new Patient(connection);

		try {
			if (isInteger(input_patient)) {
				patient.getNameFromID(Integer.valueOf(input_patient));
				return queryPatientHistory(Integer.valueOf(input_patient));
			}
			return queryPatientHistory(patient.getIDFromName(input_patient));
		} catch (SQLException se) {
			throw new SearchException("Patient doesn't exist");
		}
	}

	private String queryPatientHistory(Integer patient_num) throws SearchException {
		String query = String.format("SELECT tr.patient_no, p.name, "
				+ "tt.test_name, tr.test_date, tr.result"
				+ "FROM test_record tr, patient p, test_type tt"
				+ "WHERE tr.patient_no = %d AND"
				+ "tr.patient_no = p.health_care_no AND"
				+ "tr.type_id = tt.type_id",
				patient_num);
		try {
			return connection.createStatement().executeQuery(query).toString();
		} catch (SQLException se) {
			throw new SearchException("SQL error --> please try again");
		}
	}

	public String getDoctorsPrescriptionsWithinRange() throws SearchException {
		String input_doc = ioproc.getInputString("Please enter employee name or number: ");
		Employee doctor = new Employee(connection);

		try {
			if (isInteger(input_doc)) {
				doctor.getNameFromID(Integer.valueOf(input_doc));
				return queryDocPrescriptionsWithinRange(Integer.valueOf(input_doc));
			}
			return queryDocPrescriptionsWithinRange(doctor.getIDFromName(input_doc));
		} catch (SQLException se) {
			throw new SearchException("Employee doesn't exist");
		}
	}

	private String queryDocPrescriptionsWithinRange(Integer doc_id) throws SearchException {
		Date[] date_range = getDateRange();
		String query = String.format("SELECT tr.patient_no, p.name, "
				+ "tt.test_name, tr.prescribe_date"
				+ "FROM test_record tr, patient p, test_type tt, doctor d"
				+ "WHERE tr.employee_no = %d AND"
				+ "tr.employee_no = d.employee_no AND"
				+ "tr.patient_no = p.health_care_no AND"
				+ "tr.type_id = tt.type_id AND"
				+ "tr.prescribe_date BETWEEN %s AND %s",
				doc_id, ioproc.getTestDateInSQLDateStringForm(date_range[0]),
				ioproc.getTestDateInSQLDateStringForm(date_range[1]));
		try {
			return connection.createStatement().executeQuery(query).toString();
		} catch (SQLException se) {
			throw new SearchException("SQL error --> please try again");
		}
	}

	public String getAtRiskPatientsForTest() throws SearchException {
		String input_test = ioproc.getInputString("Please enter test name: ");
		Test test = new Test(connection);

		try {
			test.getIDFromName(input_test);
			return queryAtRiskPatientsForTest(input_test);
		} catch (SQLException se) {
			throw new SearchException("Test doesn't exist");
		}
	}

	private String queryAtRiskPatientsForTest(String input_test) throws SearchException {
		String query = String.format("SELECT tr.patient_no, p.name, "
				+ "tt.test_name, tr.prescribe_date"
				+ "FROM test_record tr, patient p, test_type tt, doctor d"
				+ "WHERE tr.employee_no = %d AND"
				+ "tr.employee_no = d.employee_no AND"
				+ "tr.patient_no = p.health_care_no AND"
				+ "tr.type_id = tt.type_id AND"
				+ "tr.prescribe_date BETWEEN %s AND %s",
				input_test);
		try {
			return connection.createStatement().executeQuery(query).toString();
		} catch (SQLException se) {
			throw new SearchException("SQL error --> please try again");
		}
	}

	private Date[] getDateRange() {
		Date[] range = new Date[2];
		range[0] = ioproc.getInputDate("From: ");
		range[1] = ioproc.getInputDate("To: ");
		return range;
	}

	private Boolean isInteger(String whatisit) {
		try {
			Integer.valueOf(whatisit);
			return true;
		} catch (NumberFormatException ne) {
			return false;
		}
	}
}

/*


Display the health_care_no, name, address,
and phone number of all patients,
who have reached the alarming age of the given test type
but have never taken a test of that type
by requesting the test type name.

List the
health_care_no, patient name, test type name,
prescribing date of all tests
prescribed by a given doctor during a specified time period.
The user needs to enter the name or employee_no of the doctor,
and the starting and ending dates between which tests are prescribed.

List the
health_care_no, patient name, test type name,
testing date, and test result of all test records
by inputing health_care_no or a patient name
*/
