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
				+ "WHERE name = \'%s\'", name);
		ResultSet rs = connection.createStatement().executeQuery(query);
		if (rs.next())
			return rs.getInt(1);
		else
			throw new SQLException("Name not found.");
	}
	
	@Override
	public String getNameFromID(Integer ID) throws SQLException {
		String query = String.format("SELECT name "
				+ "FROM patient "
				+ "WHERE health_care_no = %d", ID);
		ResultSet rs = connection.createStatement().executeQuery(query);
		if (rs.next())
			return rs.getString(1);
		else
			throw new SQLException("ID not found.");
	}

	@Override
	public Boolean isNameUnique(String name) {
		String query = String.format("SELECT COUNT(*) "
				+ "FROM patient "
				+ "WHERE name = \'%s\'", name);
		
		return isResultOne(query, connection);
	}

	@Override
	public String getSuccessMessage() {
		return String.format("Patient %s with health care number %d found.", getName(), getID());
	}
}
