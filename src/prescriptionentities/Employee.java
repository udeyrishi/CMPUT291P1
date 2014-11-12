package prescriptionentities;

import java.sql.*;

import common.UIO;

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
	public Employee(Connection connection, UIO io) {
		super(connection, io, "Employee");
	}
	
	@Override
	public Integer getIDFromName(String name) throws SQLException {
		String query = String.format("SELECT d.employee_no "
				+ "FROM doctor d, patient p "
				+ "WHERE d.health_care_no = p.health_care_no "
				+ "AND p.name = \'%s\'", name);
		ResultSet rs = connection.createStatement().executeQuery(query);
		if (rs.next())
			return rs.getInt(1);
		else
			throw new SQLException("Name not found");
	}
	
	@Override
	public String getNameFromID(Integer ID) throws SQLException {
		String query = String.format("SELECT p.name "
				+ "FROM doctor d, patient p "
				+ "WHERE d.health_care_no = p.health_care_no "
				+ "AND d.employee_no = %d", ID);
		ResultSet rs = connection.createStatement().executeQuery(query);
		if (rs.next())
			return rs.getString(1);
		else
			throw new SQLException("ID not found.");
	}
	
	@Override
	public Boolean isNameUnique(String name) {
		String query = String.format("SELECT COUNT(*) "
									+ "FROM doctor d, patient p "
									+ "WHERE d.health_care_no = p.health_care_no "
									+ "AND p.name = \'%s\'", name);
		
		return isResultOne(query, connection);
	}

	@Override
	public String getSuccessMessage() {
		return String.format("Welcome %s (Employee No.: %d)!", getName(), getID());
	}
}
