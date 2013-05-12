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

import java.util.Properties;

import org.apache.torque.engine.database.model.Database;
import org.apache.torque.engine.database.transform.XmlToAppData;
import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfPluginResources;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBConnectAction {

	private Properties theProperties;

	private StringBuffer m_strBuf;

	private java.sql.DatabaseMetaData sqlMeta = null;;

	private DBMeta dbMeta = null;

	private java.sql.Connection conn = null;

	private org.apache.torque.engine.database.transform.XmlToAppData xmlToAppData = null;

	private Database torqueDatabase;

	public DBConnectAction(Properties properties) {
		m_strBuf = new StringBuffer();
		theProperties = properties;
	}

	public java.sql.Connection actionConnect() {
		System.out.println("actionConnect");
		if (conn == null) {
			System.out.println("actionConnect null");
			String strJDBCURL = "";
			strJDBCURL += theProperties.getProperty("protocol", "") + ":";
			strJDBCURL += theProperties.getProperty("subprotocol", "") + (":");
			strJDBCURL += theProperties.getProperty("dbalias", "");
			conn = connectToDB(theProperties.getProperty("driver", ""),
					strJDBCURL, theProperties.getProperty("username", ""),
					theProperties.getProperty("password", ""));
			DbfLauncherPlugin.getDefault().setDBConnectAction(this);
		}
		return (conn);
	}

	public java.sql.Connection actionReConnect() {
		String strJDBCURL = "";
		strJDBCURL += theProperties.getProperty("protocol", "") + ":";
		strJDBCURL += theProperties.getProperty("subprotocol", "") + (":");
		strJDBCURL += theProperties.getProperty("dbalias", "");
		conn = connectToDB(theProperties.getProperty("driver", ""), strJDBCURL,
				theProperties.getProperty("username", ""), theProperties
						.getProperty("password", ""));
		DbfLauncherPlugin.getDefault().setDBConnectAction(this);
		return (conn);
	}

	public String getMessages() {
		return (m_strBuf.toString());
	}

	private java.sql.Connection connectToDB(String strJDBCDriver,
			String strJDBCURL, String strUsername, String strPassword) {
		try {
			DbfLauncherPlugin
					.log(IStatus.INFO, "Use Driver:\t" + strJDBCDriver);
			DbfLauncherPlugin
					.log(IStatus.INFO, "Connect to DB:\t" + strJDBCURL);
			m_strBuf.append("Use Driver:\t" + strJDBCDriver + "\n");
			m_strBuf.append("Connect to DB:\t" + strJDBCURL + "\n\n");
			java.sql.Connection connection = null;
			if (conn == null) {
				DbfLauncherPlugin.log(IStatus.INFO,
						"DriverManager.getInstance().getConnection");
				connection = DriverManager.getInstance().getConnection(
						strJDBCDriver, strJDBCURL, strUsername, strPassword);
			} else {
				DbfLauncherPlugin.log(IStatus.INFO,
						"DriverManager.reloadInstance().getConnection");
				connection = DriverManager.reloadInstance().getConnection(
						strJDBCDriver, strJDBCURL, strUsername, strPassword);
			}
			return (connection);
		} catch (java.sql.SQLException sqlEx) {
			java.sql.SQLException currentSqlEx = sqlEx;
			DbfLauncherPlugin.log(sqlEx);
			while (currentSqlEx.getNextException() != null) {
				currentSqlEx = currentSqlEx.getNextException();
				DbfLauncherPlugin.log(currentSqlEx);
			}
			return (null);
		} catch (Throwable throwable) {
			DbfLauncherPlugin.log(throwable);
			m_strBuf.append(String.valueOf(throwable.getMessage()) + "\n\n\n");
		}
		return (null);
	}

	public java.sql.DatabaseMetaData getSqlMetaData() {
		if (sqlMeta == null) {
			try {
				conn = actionConnect();
				sqlMeta = conn.getMetaData();
			} catch (Exception exception) {
				DbfLauncherPlugin.log(IStatus.ERROR, "getSqlMetaData Error");
				DbfLauncherPlugin.log(exception);
			}
		}
		return (sqlMeta);
	}

	public DBMeta getDbMetaData(DbInterfaceProperties dbInterfaceproperties) {
		if (dbMeta == null) {
			DbfLauncherPlugin.log("getDbMetaData null");
			try {
				// OJB
				dbMeta = new DBMeta(getSqlMetaData());
				if (xmlToAppData == null) {
					String modelDir = dbInterfaceproperties
							.getXmlModelRepositoryPath();
					xmlToAppData = new XmlToAppData(dbInterfaceproperties
							.getPlatform());
					torqueDatabase = xmlToAppData.parseFile(modelDir
							+ System.getProperty("file.separator")
							+ "model.xml");
					if (torqueDatabase == null)
						System.out.println("TORQUEDATABASE null");
					dbMeta.setTorqueDatabase(torqueDatabase);
				}
				if (dbInterfaceproperties.getCatalog().equals("null"))
					dbMeta.setCatalog(null);

				else
					dbMeta.setCatalog(dbInterfaceproperties.getCatalog());

				if (dbInterfaceproperties.getSchema().equals("null"))
					dbMeta.setSchema(null);

				else {
					dbMeta.setSchema(dbInterfaceproperties.getSchema());
				}
				dbMeta.read();
				dbMeta.generateReferences();
				dbMeta.setSPackage(dbInterfaceproperties.getSPackage());
				dbMeta.setDPackage(dbInterfaceproperties.getDPackage());
				// FIXME why close ???
				// conn.close();
			} catch (Exception exception) {
				DbfLauncherPlugin.log(IStatus.ERROR, "DBConnectAction "
						+ DbfPluginResources.VIEWER_EXCEPTION_DB_CONNECT);
				DbfLauncherPlugin.log(exception);
			}
		}
		return (dbMeta);
	}
}
