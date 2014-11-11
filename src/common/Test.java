package common;

import java.sql.*;

/**
 * The class representing the medical test to be prescribed.
 * @author udeyrishi
 *
 */
public class Test extends PrescriptionEntity {

	/**
	 * Constructor.
	 * @param connection The java.sql.Connection object to use.
	 */
	public Test(Connection connection, UIO io) {
		super(connection, io, "Test");
	}

	@Override
	public Integer getIDFromName(String name) throws SQLException {
		String query = String.format("SELECT type_id "
				+ "FROM test_type "
				+ "WHERE test_name = \'%s\'", name);
		ResultSet rs = connection.createStatement().executeQuery(query);
		if (rs.next())
			return rs.getInt(1);
		else
			throw new SQLException("Name not found.");
	}

	@Override
	public String getNameFromID(Integer ID) throws SQLException {
		String query = String.format("SELECT test_name "
				+ "FROM test_type "
				+ "WHERE type_id = %d", ID);
		ResultSet rs = connection.createStatement().executeQuery(query);
		if (rs.next())
			return rs.getString(1);
		else
			throw new SQLException("ID not found");
	}
	
	@Override
	public Boolean isNameUnique(String name) {
		String query = String.format("SELECT COUNT(*) "
				+ "FROM test_type "
				+ "WHERE test_name = \'%s\'", name);

		return isResultOne(query, connection);
	}

	@Override
	public String getSuccessMessage() {
		return String.format("Test with name %s and ID %d found.", getName(), getID());
	}
	
	
}
