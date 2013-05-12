/*
 * ====================================================================
 * Copyright 2002-2004 The Apache Software Foundation. Parts (c) Copyright
 * 2004-2005 Compiere MFG + SCM.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 * ====================================================================
 */

package org.compiere.mfg_scm.eclipse.db.dbInterface;

import java.sql.SQLException;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DriverManager {

	private static DriverManager driverManager = null;

	private static java.sql.Connection connection = null;

	private DriverManager() {
	}

	public static DriverManager getInstance() {
		if (DriverManager.driverManager == null)
			DriverManager.driverManager = new DriverManager();

		return (DriverManager.driverManager);
	}

	public static DriverManager reloadInstance() {
		DriverManager.driverManager = null;
		return (DriverManager.getInstance());
	}

	public java.sql.Connection getConnection() throws SQLException {
		return (connection);
	}

	public java.sql.Connection getConnection(String strJDBCDriver,
			String strJDBCURL, String strUsername, String strPassword)
			throws SQLException, ClassNotFoundException {
		if (connection == null) {
			Class.forName(strJDBCDriver); // "com.informix.jdbc.IfxDriver"
			connection = java.sql.DriverManager.getConnection(strJDBCURL,
					strUsername, strPassword); // "jdbc:informix-sqli://moon:1526/bci_test:INFORMIXSERVER=ol_bci",
			// "informix", "informix"
		}
		return (connection);
	}
}
