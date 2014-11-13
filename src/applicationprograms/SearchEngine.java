package applicationprograms;

import java.sql.*;
import java.util.Date;

import prescriptionentities.Employee;
import prescriptionentities.Patient;
import prescriptionentities.Test;
import common.*;

/**
 * 
 * SearchEngine is one of the four main modules of this
 * program. Search is responsible for searching three
 * different queries.
 *
 */
public class SearchEngine extends ApplicationProgram {
	
	/**
     * Constructor.
     * @param connection is a Connection object connecting
     * to the remote database.
     * @param io is a UIO object for interacting with user.
     */
	public SearchEngine(Connection connection, UIO io) {
		super(connection, io);
	}

	/**
	 * Primary method responsible for running a loop asking
	 * for search number and processing.
	 */
	@Override
	public void run() {
		printInstructionHelp();
		while (true) {
			int quit = ioproc.getInputInteger("");
			if (quit == 4) break;
			handleInput(quit);
		}
	}

	/**
	 * Print information when search module is started.
	 */
	private void printInstructionHelp() {
		System.out.println("Enter:");
		System.out.println("1 to get a patient's test history");
		System.out.println("2 to get a doctor's prescription history");
		System.out.println("3 to get at risk patients for a test type");
		System.out.println("4 to quit");
	}

	/**
	 * This method is responsible for executing the
	 * correct query based on choice.
	 * @param choice
	 */
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

	/**
	 * Get patient name or ID for the first query 
	 * and print query results. Print a patients 
	 * test history.
	 * @throws NumberFormatException
	 * @throws SQLException
	 */
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

	/**
	 * Return a ResultSet containing the results of the first
	 * query based on the patient_num specified.
	 * @param patient_num
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Print the rset containing the results of the first
	 * query.
	 * @param rset
	 * @throws SQLException
	 */
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

	/**
	 * Prompts user for employee number or name and prints
	 * the results after querying the database. The second
	 * query is to return a doctors prescriptions in a certain
	 * time range.
	 * @throws SQLException
	 */
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

	/**
	 * Return a ResultSet containing the results of the second
	 * query based on the doc_id specified.
	 * @param doc_id
	 * @return
	 * @throws SQLException
	 */
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

	/**
	 * Print the rset containing the results of the second
	 * query.
	 * @param rset
	 * @throws SQLException
	 */
	private void printPrescriptions(ResultSet rset) throws SQLException {
		// tr.patient_no, p.name, tt.test_name, tr.prescribe_date
		while (rset.next()) {
			System.out.println(rset.getInt(1) + ", " +
							 	rset.getString(2) + ", " +
								rset.getString(3) + ", " +
								rset.getDate(4));
		}
	}

	/**
	 * Prompts user for test_type and prints 
	 * the results after querying the database. The third
	 * query is to return the patients that have reached 
	 * the alarming age for a given test_type.
	 * @throws SQLException
	 */
	public void getAtRiskPatientsForTest() throws SQLException {
		String input_test = ioproc.getInputString("Please enter test name: ");
		Test test = new Test(connection, ioproc);
		printAtRiskPatients(queryAtRiskPatientsForTest(test.getIDFromName(input_test)));
	}

	/**
	 * Return a ResultSet containing the results of the third
	 * query based on the test_type specified.
	 * 
	 * NOTE: QUERY IS MOSTLY EXACTLY AS ASSIGN2 SOLUTIONS
	 * @param test_type
	 * @return
	 * @throws SQLException
	 */
	private ResultSet queryAtRiskPatientsForTest(int test_type) throws SQLException {
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

	/**
	 * Print the rset containing the results of the third
	 * query.
	 * @param rset
	 * @throws SQLException
	 */
	private void printAtRiskPatients(ResultSet rset) throws SQLException {
		// p.health_care_no, p.name, p.address, p.phone
		while (rset.next()) {
			System.out.println(rset.getInt(1) + ", " +
							 	rset.getString(2) + ", " +
								rset.getString(3) + ", " +
								rset.getString(4));
		}
	}

	/**
	 * Returns a "tuple" of dates where the first
	 * date is before the second date. 
	 * @return
	 */
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

	/**
	 * Wrapper around Integer.valueOf that makes
	 * it more convenient to check if a string can
	 * be converted to an Integer.
	 */
	private Boolean isInteger(String whatisit) {
		try {
			Integer.valueOf(whatisit);
			return true;
		} catch (NumberFormatException ne) {
			return false;
		}
	}
}