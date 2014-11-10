package prescription;

import java.sql.*;

/**
 * The class representing an employee (i.e., a doctor) who is prescribing the test.
 * @author udeyrishi
 *
 */
public class Employee extends PrescriptionEntity {

	/**
	 * Constructor
	 * @param connection The java.sql.Connection object to use.
	 */
	public Employee(Connection connection) {
		super(connection, "Employee");
	}
	
	@Override
	protected Integer getIDFromName(String name) throws SQLException {
		String query = String.format("SELECT d.employee_no "
				+ "FROM doctor d, patient p "
				+ "WHERE d.health_care_no = p.health_care_no "
				+ "AND p.name = %s", name);
		return connection.createStatement().executeQuery(query).getInt(1);
	}
	
	@Override
	protected String getNameFromID(Integer ID) throws SQLException {
		String query = String.format("SELECT p.name "
				+ "FROM doctor d, patient p "
				+ "WHERE d.health_care_no = p.health_care_no "
				+ "AND d.employee_no = %d", ID);
		return connection.createStatement().executeQuery(query).getString(1);
	}
	
	@Override
	protected Boolean isNameUnique(String name) {
		String query = String.format("SELECT COUNT(*) "
									+ "FROM doctor d, patient p "
									+ "WHERE d.health_care_no = p.health_care_no "
									+ "AND p.name = %s", name);
		
		return isResultSingleRow(query, connection);
	}

	@Override
	protected String getSuccessMessage() {
		return String.format("Welcome Dr. %s (Employee No.: %d)!", getName(), getID());
	}
}
