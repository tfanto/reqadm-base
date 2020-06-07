package pdmf.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pdmf.sys.Db;

public class SystemService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SystemService.class);

	public Map<String, String> getConstantsForCountryCode(String lncd) {

		if (lncd == null || lncd.trim().length() < 1) {
			lncd = "se";
		}

		String theSQL = "select cst,value from constant where lncd = ?";

		Map<String, String> ret = new HashMap<>();
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			connection = Db.open();
			if (connection != null) {
				stmt = connection.prepareStatement(theSQL);
				stmt.setString(1, lncd);
				rs = stmt.executeQuery();
				while (rs.next()) {
					String cst = rs.getString("cst");
					String value = rs.getString("value");
					ret.put(cst, value);
				}
				return ret;
			}
		} catch (SQLException e) {
			LOGGER.error(e.toString(), e);
		} finally {
			Db.close(rs);
			Db.close(stmt);
			Db.close(connection);
		}
		return ret;
	}

}
