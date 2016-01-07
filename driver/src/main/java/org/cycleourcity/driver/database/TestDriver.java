package org.cycleourcity.driver.database;

import java.sql.SQLException;

public interface TestDriver {
	
	public void clearTable(String tablename) throws SQLException;

}
