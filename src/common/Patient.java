package common;

import java.sql.*;

/**
 * The class representing the patient to which the medical test is prescribed.
 * @author udeyrishi
 *
 */
public class Patient extends PrescriptionEntity {

	/**
	 * Constructor.
	 * @param connection The java.sql.Connection object to use.
	 */
	public Patient(Connection connection, UIO io) {
		super(connection, io, "Patient");
	}

	@Override
	public Integer getIDFromName(String name) throws SQLException {
		String query = String.format("SELECT health_care_no "
				+ "FROM patient "
				+ "WHERE name = %s", name);
		return connection.createStatement().executeQuery(query).getInt(1);
	}
	
	@Override
	public String getNameFromID(Integer ID) throws SQLException {
		String query = String.format("SELECT name "
				+ "FROM patient "
				+ "WHERE health_care_no = %d", ID);
		return connection.createStatement().executeQuery(query).getString(1);
	}

	@Override
	public Boolean isNameUnique(String name) {
		String query = String.format("SELECT COUNT(*) "
				+ "FROM patient "
				+ "WHERE name = %s", name);
		
		return isResultSingleRow(query, connection);
	}

	@Override
	public String getSuccessMessage() {
		return String.format("Patient %s with health care number %d found.", getName(), getID());
	}
}
