package search;

import java.sql.*;
import java.util.Date;

import common.*;

public class SearchEngine extends ApplicationProgram {

	public SearchEngine(Connection connection, UIO io) {
		super(connection, io);
	}

	@Override
	public void run() {
		String prompt = "Search: ";
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
			if (choice == 1) getPatientTestHistory();
			else if (choice == 2) getDoctorsPrescriptionsWithinRange();
			else if (choice == 3) getAtRiskPatientsForTest();
			else System.out.println("Invalid choice");
		} catch (NumberFormatException nfe) {
			System.out.println(nfe.getMessage());
		} catch (IllegalArgumentException iae) {
			System.out.println(iae.getMessage());
		} catch (SQLException sqle) {
			System.out.println(sqle.getMessage());
		}
	}

	public void getPatientTestHistory() throws NumberFormatException, SQLException {
		String input_patient = ioproc.getInputString("Please enter patient name or number: ");
		Patient patient = new Patient(connection, ioproc);

		if (isInteger(input_patient)) {
			patient.getNameFromID(Integer.valueOf(input_patient));
			printPatientHistory(queryPatientHistory(Integer.valueOf(input_patient)));
		} else {
			printPatientHistory(queryPatientHistory(patient.getIDFromName(input_patient)));
		}
	}

	private ResultSet queryPatientHistory(Integer patient_num) throws SQLException {
		String query = String.format("SELECT tr.patient_no, p.name, "
				+ "tt.test_name, tr.test_date, tr.result "
				+ "FROM test_record tr, patient p, test_type tt "
				+ "WHERE tr.patient_no = %d AND "
				+ "tr.patient_no = p.health_care_no AND "
				+ "tr.type_id = tt.type_id",
				patient_num);

		return connection.createStatement().executeQuery(query);
	}

	private void printPatientHistory(ResultSet rset) throws SQLException {
		// tr.patient_no, p.name, tt.test_name, tr.test_date, tr.result
		while (rset.next()) {
			System.out.println(rset.getInt(1) + ", " +
							 	rset.getString(2) + ", " +
								rset.getString(3) + ", " +
								rset.getDate(4) + ", " +
								rset.getString(5));
		}
	}

	public void getDoctorsPrescriptionsWithinRange() throws SQLException {
		String input_doc = ioproc.getInputString("Please enter employee name or number: ");
		Employee doctor = new Employee(connection, ioproc);

		if (isInteger(input_doc)) {
			doctor.getNameFromID(Integer.valueOf(input_doc));
			printPrescriptions(queryDocPrescriptionsWithinRange(Integer.valueOf(input_doc)));
		} else {
			printPrescriptions(queryDocPrescriptionsWithinRange(doctor.getIDFromName(input_doc)));
		}
	}

	private ResultSet queryDocPrescriptionsWithinRange(Integer doc_id) throws SQLException {
		Date[] date_range = getDateRange();
		String query = String.format("SELECT tr.patient_no, p.name, "
				+ "tt.test_name, tr.prescribe_date "
				+ "FROM test_record tr, patient p, test_type tt, doctor d "
				+ "WHERE tr.employee_no = %d AND "
				+ "tr.employee_no = d.employee_no AND "
				+ "tr.patient_no = p.health_care_no AND "
				+ "tr.type_id = tt.type_id AND "
				+ "tr.prescribe_date BETWEEN '%s' AND '%s'",
				doc_id, ioproc.getTestDateInSQLDateStringForm(date_range[0]),
				ioproc.getTestDateInSQLDateStringForm(date_range[1]));

		return connection.createStatement().executeQuery(query);
	}

	private void printPrescriptions(ResultSet rset) throws SQLException {
		// tr.patient_no, p.name, tt.test_name, tr.prescribe_date
		while (rset.next()) {
			System.out.println(rset.getInt(1) + ", " +
							 	rset.getString(2) + ", " +
								rset.getString(3) + ", " +
								rset.getDate(4));
		}
	}

	public void getAtRiskPatientsForTest() throws SQLException {
		String input_test = ioproc.getInputString("Please enter test name: ");
		Test test = new Test(connection, ioproc);
		printAtRiskPatients(queryAtRiskPatientsForTest(test.getIDFromName(input_test)));
	}

	private ResultSet queryAtRiskPatientsForTest(int test_type) throws SQLException {
		/*
		Display the health_care_no, name, address,
		and phone number of all patients,
		who have reached the alarming age of the given test type
		but have never taken a test of that type
		by requesting the test type name.

		NOTE: QUERY IS MOSTLY EXACTLY AS ASSIGN2 SOLUTIONS
		*/
		String query = String.format("SELECT distinct p.health_care_no, p.name, p.address, p.phone "
									+ "FROM test_record tr, patient p "
									+ "WHERE tr.patient_no = p.health_care_no and "
									+ "trunc(months_between(sysdate,p.birth_day)/12) >= ( "
									+ "SELECT min(c1.age) "
									+ "FROM  "
									+ "       (SELECT   t1.type_id, count(distinct t1.patient_no)/count(distinct t2.patient_no) ab_rate "
									+ "        FROM     test_record t1, test_record t2 "
									+ "        WHERE    t1.result <> 'normal' AND t1.type_id = t2.type_id "
									+ "        GROUP BY t1.type_id "
									+ "        ) r, "
									+ "       (SELECT   t1.type_id,age,COUNT(distinct p1.health_care_no) AS ab_cnt "
									+ "        FROM     patient p1,test_record t1, "
									+ "                 (SELECT DISTINCT trunc(months_between(sysdate,p1.birth_day)/12) AS age FROM patient p1) "
									+ "        WHERE    trunc(months_between(sysdate,p1.birth_day)/12)>=age "
									+ "                 AND p1.health_care_no=t1.patient_no "
									+ "                 AND t1.result<>'normal' "
									+ "        GROUP BY age,t1.type_id "
									+ "        ) c1, "
									+ "        (SELECT  t1.type_id,age,COUNT(distinct p1.health_care_no) AS cnt "
									+ "         FROM    patient p1, test_record t1, "
									+ "                 (SELECT DISTINCT trunc(months_between(sysdate,p1.birth_day)/12) AS age FROM patient p1) "
									+ "         WHERE trunc(months_between(sysdate,p1.birth_day)/12)>=age "
									+ "               AND p1.health_care_no=t1.patient_no "
									+ "         GROUP BY age,t1.type_id "
									+ "        ) c2 "
									+ "  WHERE  c1.age = c2.age AND c1.type_id = c2.type_id AND c1.type_id = r.type_id "
									+ "         AND c1.ab_cnt/c2.cnt>=2*r.ab_rate and c1.type_id = %d "
									+ "  GROUP BY c1.type_id,ab_rate)", test_type);

		return connection.createStatement().executeQuery(query);
	}

	private void printAtRiskPatients(ResultSet rset) throws SQLException {
		// p.health_care_no, p.name, p.address, p.phone
		while (rset.next()) {
			System.out.println(rset.getInt(1) + ", " +
							 	rset.getString(2) + ", " +
								rset.getString(3) + ", " +
								rset.getString(4));
		}
	}

	private Date[] getDateRange() {
		Date[] range = new Date[2];
		range[0] = ioproc.getInputDate("From: ");
		range[1] = ioproc.getInputDate("To: ");
		if (range[0].after(range[1])) {
			System.out.println("From must be before To");
			return getDateRange();
		}
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
