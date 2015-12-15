package org.cycleourcity.cyclelourcity_web_server.database;

import java.sql.SQLException;

public interface TestDriver {
	
	public void clearTable(String tablename) throws SQLException;

}
