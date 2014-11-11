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
				+ "WHERE test_name = %s", name);
		return connection.createStatement().executeQuery(query).getInt(1);
	}

	@Override
	public String getNameFromID(Integer ID) throws SQLException {
		String query = String.format("SELECT test_name "
				+ "FROM test_type "
				+ "WHERE type_id = %d", ID);
		return connection.createStatement().executeQuery(query).getString(1);
	}
	
	@Override
	public Boolean isNameUnique(String name) {
		String query = String.format("SELECT COUNT(*) "
				+ "FROM test_type "
				+ "WHERE p.name = %s", name);

		return isResultSingleRow(query, connection);
	}

	@Override
	public String getSuccessMessage() {
		return String.format("Test with name %s and ID %d found.", getName(), getID());
	}
	
	
}
