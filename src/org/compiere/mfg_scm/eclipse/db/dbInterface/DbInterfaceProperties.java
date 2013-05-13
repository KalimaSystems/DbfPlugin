/*
 * ====================================================================
 *  * Copyright 2001-2013 Andre Charles Legendre <andre.legendre@kalimasystems.org>
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfProject;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.MessageDialog;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbInterfaceProperties {

	public static IWorkspaceRoot root = ResourcesPlugin.getWorkspace()
			.getRoot();

	private String jcdAlias;

	private boolean defaultConnection;

	private String platform;

	private String jdbcLevel;

	private String driver;

	private String protocol;

	private String subprotocol;

	private String dbalias;

	private String username;

	private String password;

	private String catalog;

	private String schema;

	private boolean batchMode;

	private boolean eagerRelease;

	private String autoCommit;

	private boolean acExceptions;

	private String autoincrement;

	private String userRepositoryPath;

	private String javaRepositoryPath;

	private String includeRepositoryPath;

	private String sImport;

	private String sImplements;

	private String sExtends;

	private String sPackage;

	private String displayRepositoryPath;

	private String dImport;

	private String dImplements;

	private String dExtends;

	private String dPackage;

	private String modelRepositoryPath;

	/*
	 * private String mImport;
	 * 
	 * private String mImplements;
	 * 
	 * private String mExtends;
	 * 
	 * private String mPackage;
	 */

	private boolean copyfuction;

	private boolean useSchemaForSelect;

	private String JavaTypesARRAY;

	private String JavaTypesBIGINT;

	private String JavaTypesBINARY;

	private String JavaTypesBIT;

	private String JavaTypesBLOB;

	private String JavaTypesCHAR;

	private String JavaTypesCLOB;

	private String JavaTypesDATE;

	private String JavaTypesDECIMAL;

	private String JavaTypesDISTINCT;

	private String JavaTypesDOUBLE;

	private String JavaTypesFLOAT;

	private String JavaTypesINTEGER;

	private String JavaTypesJAVA_OBJECT;

	private String JavaTypesLONGVARBINARY;

	private String JavaTypesLONGVARCHAR;

	private String JavaTypesNULL;

	private String JavaTypesNUMERIC;

	private String JavaTypesOTHER;

	private String JavaTypesREAL;

	private String JavaTypesREF;

	private String JavaTypesSMALLINT;

	private String JavaTypesSTRUCT;

	private String JavaTypesTIME;

	private String JavaTypesTIMESTAMP;

	private String JavaTypesTINYINT;

	private String JavaTypesVARBINARY;

	private String JavaTypesVARCHAR;

	private Vector JdbcDrivers;

	public static DbInterfaceProperties getHSQLDefaults() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties.setJcdAlias("MyDB");
		dbInterfaceproperties.setDefaultConnection(true);
		dbInterfaceproperties.setPlatform("Hsqldb");
		dbInterfaceproperties.setJdbcLevel("3.0");
		dbInterfaceproperties.setDriver("org.hsqldb.jdbcDriver");
		dbInterfaceproperties.setProtocol("jdbc");
		dbInterfaceproperties.setSubprotocol("hsqldb");
		dbInterfaceproperties.setDbalias("hsql://localhost");
		dbInterfaceproperties.setUsername("sa");
		dbInterfaceproperties.setPassword("");
		dbInterfaceproperties.setCatalog("");
		dbInterfaceproperties.setSchema("");
		return (dbInterfaceproperties);
	}

	public static Properties getPropertie(
			DbInterfaceProperties dbInterfaceproperties) {
		Properties properties = new Properties();
		properties
				.setProperty("jcd-alias", dbInterfaceproperties.getJcdAlias());
		properties.setProperty("default-connection", "false");
		int i;
		if (dbInterfaceproperties.isDefaultConnection())
			properties.setProperty("default-connection", "true");

		properties.setProperty("platform", dbInterfaceproperties.getPlatform());
		properties.setProperty("jdbc-level", dbInterfaceproperties
				.getJdbcLevel());
		properties.setProperty("driver", dbInterfaceproperties.getDriver());
		properties.setProperty("protocol", dbInterfaceproperties.getProtocol());
		properties.setProperty("subprotocol", dbInterfaceproperties
				.getSubprotocol());
		properties.setProperty("dbalias", dbInterfaceproperties.getDbalias());
		properties.setProperty("username", dbInterfaceproperties.getUsername());
		properties.setProperty("password", dbInterfaceproperties.getPassword());
		properties.setProperty("catalog", dbInterfaceproperties.getCatalog());
		properties.setProperty("schema", dbInterfaceproperties.getSchema());
		properties.setProperty("batch-mode", "false");
		if (dbInterfaceproperties.isBatchMode())
			properties.setProperty("batch-mode", "true");
		if (dbInterfaceproperties.isEagerRelease())
			properties.setProperty("eager-release", "true");

		properties.setProperty("autoincrement", dbInterfaceproperties
				.getAutoincrement());
		properties.setProperty("userRepositoryPath", dbInterfaceproperties
				.getUserRepositoryPath());
		properties.setProperty("javaRepositoryPath", dbInterfaceproperties
				.getJavaRepositoryPath());
		properties.setProperty("includeRepositoryPath", dbInterfaceproperties
				.getIncludeRepositoryPath());
		properties.setProperty("package", dbInterfaceproperties.getSPackage());
		properties.setProperty("import", dbInterfaceproperties.getSImport());
		properties.setProperty("implements", dbInterfaceproperties
				.getSImplements());
		properties.setProperty("extends", dbInterfaceproperties.getSExtends());
		properties.setProperty("copyfuction", "false");
		if (dbInterfaceproperties.isCopyfuction())
			properties.setProperty("copyfuction", "true");

		properties.setProperty("useSchemaForSelect", "true");
		if (dbInterfaceproperties.isUseSchemaForSelect())
			properties.setProperty("copyfuction", "false");
		properties.setProperty("displayRepositoryPath", dbInterfaceproperties
				.getDisplayRepositoryPath());
		properties.setProperty("displaypackage", dbInterfaceproperties
				.getDPackage());
		properties.setProperty("displayimport", dbInterfaceproperties
				.getDImport());
		properties.setProperty("displayimplements", dbInterfaceproperties
				.getDImplements());
		properties.setProperty("displayextends", dbInterfaceproperties
				.getDExtends());
		properties.setProperty("modelRepositoryPath", dbInterfaceproperties
				.getXmlModelRepositoryPath());
		/*
		 * properties.setProperty("modelpackage", dbInterfaceproperties
		 * .getMPackage()); properties.setProperty("modelimport",
		 * dbInterfaceproperties .getMImport());
		 * properties.setProperty("modelimplements", dbInterfaceproperties
		 * .getMImplements()); properties.setProperty("modelextends",
		 * dbInterfaceproperties .getMExtends());
		 */

		properties.setProperty("JavaTypes.ARRAY", dbInterfaceproperties
				.getJavaTypesARRAY());
		properties.setProperty("JavaTypes.BIGINT", dbInterfaceproperties
				.getJavaTypesBIGINT());
		properties.setProperty("JavaTypes.BINARY", dbInterfaceproperties
				.getJavaTypesBINARY());
		properties.setProperty("JavaTypes.BIT", dbInterfaceproperties
				.getJavaTypesBIT());
		properties.setProperty("JavaTypes.BLOB", dbInterfaceproperties
				.getJavaTypesBLOB());
		properties.setProperty("JavaTypes.CHAR", dbInterfaceproperties
				.getJavaTypesCHAR());
		properties.setProperty("JavaTypes.CLOB", dbInterfaceproperties
				.getJavaTypesCLOB());
		properties.setProperty("JavaTypes.DATE", dbInterfaceproperties
				.getJavaTypesDATE());
		properties.setProperty("JavaTypes.DECIMAL", dbInterfaceproperties
				.getJavaTypesDECIMAL());
		properties.setProperty("JavaTypes.DISTINCT", dbInterfaceproperties
				.getJavaTypesDISTINCT());
		properties.setProperty("JavaTypes.DOUBLE", dbInterfaceproperties
				.getJavaTypesDOUBLE());
		properties.setProperty("JavaTypes.FLOAT", dbInterfaceproperties
				.getJavaTypesFLOAT());
		properties.setProperty("JavaTypes.INTEGER", dbInterfaceproperties
				.getJavaTypesINTEGER());
		properties.setProperty("JavaTypes.JAVA_OBJECT", dbInterfaceproperties
				.getJavaTypesJAVA_OBJECT());
		properties.setProperty("JavaTypes.LONGVARBINARY", dbInterfaceproperties
				.getJavaTypesLONGVARBINARY());
		properties.setProperty("JavaTypes.LONGVARCHAR", dbInterfaceproperties
				.getJavaTypesLONGVARCHAR());
		properties.setProperty("JavaTypes.NULL", dbInterfaceproperties
				.getJavaTypesNULL());
		properties.setProperty("JavaTypes.NUMERIC", dbInterfaceproperties
				.getJavaTypesNUMERIC());
		properties.setProperty("JavaTypes.OTHER", dbInterfaceproperties
				.getJavaTypesOTHER());
		properties.setProperty("JavaTypes.REAL", dbInterfaceproperties
				.getJavaTypesREAL());
		properties.setProperty("JavaTypes.REF", dbInterfaceproperties
				.getJavaTypesREF());
		properties.setProperty("JavaTypes.SMALLINT", dbInterfaceproperties
				.getJavaTypesSMALLINT());
		properties.setProperty("JavaTypes.STRUCT", dbInterfaceproperties
				.getJavaTypesSTRUCT());
		properties.setProperty("JavaTypes.TIME", dbInterfaceproperties
				.getJavaTypesTIME());
		properties.setProperty("JavaTypes.TIMESTAMP", dbInterfaceproperties
				.getJavaTypesTIMESTAMP());
		properties.setProperty("JavaTypes.TINYINT", dbInterfaceproperties
				.getJavaTypesTINYINT());
		properties.setProperty("JavaTypes.VARBINARY", dbInterfaceproperties
				.getJavaTypesVARBINARY());
		properties.setProperty("JavaTypes.VARCHAR", dbInterfaceproperties
				.getJavaTypesVARCHAR());
		for (i = 0; i < dbInterfaceproperties.getJdbcDrivers().size(); i++) {
			properties.setProperty("JDBCDriver." + i,
					(String) dbInterfaceproperties.getJdbcDrivers().get(i));
		}
		return (properties);
	}

	public String toString() {
		Date date = new Date(System.currentTimeMillis());
		String strProperty = "";
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "# Properties for Compiere MFGSCMDbInterface"
				+ System.getProperty("line.separator");
		strProperty += "# www.compiere-mfg.org"
				+ System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "# " + date + "" + System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "jcd-alias = " + getJcdAlias()
				+ System.getProperty("line.separator");
		strProperty += "default-connection = " + isDefaultConnection() + ""
				+ System.getProperty("line.separator");
		strProperty += "platform = " + getPlatform()
				+ System.getProperty("line.separator");
		strProperty += "jdbc-level = " + getJdbcLevel()
				+ System.getProperty("line.separator");
		strProperty += "driver = " + getDriver()
				+ System.getProperty("line.separator");
		strProperty += "protocol = " + getProtocol()
				+ System.getProperty("line.separator");
		strProperty += "subprotocol = " + getSubprotocol()
				+ System.getProperty("line.separator");
		strProperty += "dbalias = " + getDbalias()
				+ System.getProperty("line.separator");
		strProperty += "username = " + getUsername()
				+ System.getProperty("line.separator");
		strProperty += "password = " + getPassword()
				+ System.getProperty("line.separator");
		strProperty += "catalog = " + getCatalog()
				+ System.getProperty("line.separator");
		strProperty += "schema = " + getSchema()
				+ System.getProperty("line.separator");
		strProperty += "batch-mode = " + isBatchMode()
				+ System.getProperty("line.separator");
		strProperty += "eager-release = " + isEagerRelease()
				+ System.getProperty("line.separator");
		strProperty += "useAutoCommit = " + getAutoCommit()
				+ System.getProperty("line.separator");
		strProperty += "ignoreAutoCommitExceptions = " + isAcExceptions()
				+ System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "autoincrement = " + getAutoincrement()
				+ System.getProperty("line.separator");
		strProperty += "userRepositoryPath = "
				+ getPathNameForPropertie(getUserRepositoryPath())
				+ System.getProperty("line.separator");
		strProperty += "javaRepositoryPath = " + getJavaRepositoryPath()
				+ System.getProperty("line.separator");
		strProperty += "includeRepositoryPath = " + getIncludeRepositoryPath()
				+ System.getProperty("line.separator");
		strProperty += "package = " + getSPackage()
				+ System.getProperty("line.separator");
		strProperty += "import = " + getSImport()
				+ System.getProperty("line.separator");
		strProperty += "implements = " + getSImplements()
				+ System.getProperty("line.separator");
		strProperty += "extends = " + getSExtends()
				+ System.getProperty("line.separator");
		strProperty += "copyfuction = " + isCopyfuction()
				+ System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "useSchemaForSelect = " + isUseSchemaForSelect()
				+ System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "displayRepositoryPath = " + getDisplayRepositoryPath()
				+ System.getProperty("line.separator");
		strProperty += "displaypackage = " + getDPackage()
				+ System.getProperty("line.separator");
		strProperty += "displayimport = " + getDImport()
				+ System.getProperty("line.separator");
		strProperty += "displayimplements = " + getDImplements()
				+ System.getProperty("line.separator");
		strProperty += "displayextends = " + getDExtends()
				+ System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "modelRepositoryPath = " + getXmlModelRepositoryPath()
				+ System.getProperty("line.separator");
		/*
		 * strProperty += "modelpackage = " + getDPackage() +
		 * System.getProperty("line.separator"); strProperty += "modelimport = " +
		 * getDImport() + System.getProperty("line.separator"); strProperty +=
		 * "modelimplements = " + getDImplements() +
		 * System.getProperty("line.separator"); strProperty += "modelextends = " +
		 * getDExtends() + System.getProperty("line.separator");
		 */
		strProperty += "#" + System.getProperty("line.separator");
		strProperty += "JavaTypes.ARRAY = " + getJavaTypesARRAY()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.BIGINT = " + getJavaTypesBIGINT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.BINARY = " + getJavaTypesBINARY()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.BIT = " + getJavaTypesBIT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.BLOB = " + getJavaTypesBLOB()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.CHAR = " + getJavaTypesCHAR()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.CLOB = " + getJavaTypesCLOB()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.DATE = " + getJavaTypesDATE()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.DECIMAL = " + getJavaTypesDECIMAL()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.DISTINCT = " + getJavaTypesDISTINCT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.DOUBLE = " + getJavaTypesDOUBLE()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.FLOAT = " + getJavaTypesFLOAT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.INTEGER = " + getJavaTypesINTEGER()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.JAVA_OBJECT = " + getJavaTypesJAVA_OBJECT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.LONGVARBINARY = "
				+ getJavaTypesLONGVARBINARY()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.LONGVARCHAR = " + getJavaTypesLONGVARCHAR()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.NULL = " + getJavaTypesNULL()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.NUMERIC = " + getJavaTypesNUMERIC()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.OTHER = " + getJavaTypesOTHER()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.REAL = " + getJavaTypesREAL()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.REF = " + getJavaTypesREF()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.SMALLINT = " + getJavaTypesSMALLINT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.STRUCT = " + getJavaTypesSTRUCT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.TIME = " + getJavaTypesTIME()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.TIMESTAMP = " + getJavaTypesTIMESTAMP()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.TINYINT = " + getJavaTypesTINYINT()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.VARBINARY = " + getJavaTypesVARBINARY()
				+ System.getProperty("line.separator");
		strProperty += "JavaTypes.VARCHAR = " + getJavaTypesVARCHAR()
				+ System.getProperty("line.separator");
		strProperty += "#" + System.getProperty("line.separator");
		for (int i = 0; i < getJdbcDrivers().size(); i++) {
			strProperty += "JDBCDriver." + i + " = "
					+ getFileNameForPropertie((String) getJdbcDrivers().get(i))
					+ System.getProperty("line.separator");
		}
		return (strProperty);
	}

	public String getPathNameForPropertie(String strProperty) {
		String tmpStr = "";
		if (!strProperty.substring(strProperty.length() - 1).equals(
				System.getProperty("file.separator")))
			strProperty += System.getProperty("file.separator");

		for (int i = 0; i < strProperty.length(); i++) {
			char j = strProperty.charAt(i);
			if (j == '\\')
				tmpStr += "\\\\";

			else
				tmpStr += j;
		}
		strProperty = tmpStr;
		return (strProperty);
	}

	public String getFileNameForPropertie(String strProperty) {
		String tmpStr = "";
		for (int i = 0; i < strProperty.length(); i++) {
			char j = strProperty.charAt(i);
			if (j == '\\')
				tmpStr += "\\\\";

			else
				tmpStr += j;
		}
		strProperty = tmpStr;
		return (strProperty);
	}

	public static DbInterfaceProperties getMSSQLServerDefaults() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties.setJcdAlias("MyDB");
		dbInterfaceproperties.setDefaultConnection(true);
		dbInterfaceproperties.setPlatform("MsSQLServer");
		dbInterfaceproperties.setJdbcLevel("3.0");
		dbInterfaceproperties
				.setDriver("com.microsoft.jdbc.sqlserver.SQLServerDriver");
		dbInterfaceproperties.setProtocol("jdbc");
		dbInterfaceproperties.setSubprotocol("microsoft:sqlserver");
		dbInterfaceproperties.setDbalias("//localhost:1433;DatabaseName=java");
		dbInterfaceproperties.setUsername("java");
		dbInterfaceproperties.setPassword("java");
		dbInterfaceproperties.setCatalog("Java");
		dbInterfaceproperties.setSchema("dbo");
		return (dbInterfaceproperties);
	}

	public static DbInterfaceProperties getOracleDefaults() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties.setJcdAlias("MyDB");
		dbInterfaceproperties.setDefaultConnection(true);
		dbInterfaceproperties.setPlatform("Oracle");
		dbInterfaceproperties.setJdbcLevel("3.0");
		dbInterfaceproperties.setDriver("oracle.jdbc.driver.OracleDriver");
		dbInterfaceproperties.setProtocol("jdbc");
		dbInterfaceproperties.setSubprotocol("oracle:thin");
		dbInterfaceproperties.setDbalias("@localhost:1521:ora");
		dbInterfaceproperties.setUsername("scott");
		dbInterfaceproperties.setPassword("tiger");
		dbInterfaceproperties.setCatalog("%");
		dbInterfaceproperties.setSchema("SCOTT");
		return (dbInterfaceproperties);
	}

	public static DbInterfaceProperties getMySQLDefaults() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties.setJcdAlias("MyDB");
		dbInterfaceproperties.setDefaultConnection(true);
		dbInterfaceproperties.setPlatform("MySQL");
		dbInterfaceproperties.setJdbcLevel("3.0");
		dbInterfaceproperties.setDriver("com.mysql.jdbc.Driver");
		dbInterfaceproperties.setProtocol("jdbc");
		dbInterfaceproperties.setSubprotocol("mysql");
		dbInterfaceproperties.setDbalias("//localhost:3306/ciapl");
		dbInterfaceproperties.setUsername("root");
		dbInterfaceproperties.setPassword("");
		dbInterfaceproperties.setCatalog("");
		dbInterfaceproperties.setSchema("");
		return (dbInterfaceproperties);
	}

	public static DbInterfaceProperties getDB2Defaults() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties.setJcdAlias("MyDB");
		dbInterfaceproperties.setDefaultConnection(true);
		dbInterfaceproperties.setPlatform("Db2");
		dbInterfaceproperties.setJdbcLevel("2.0");
		dbInterfaceproperties.setDriver("COM.ibm.db2.jdbc.app.DB2Driver");
		dbInterfaceproperties.setProtocol("jdbc");
		dbInterfaceproperties.setSubprotocol("db2");
		dbInterfaceproperties.setDbalias("HELLO");
		dbInterfaceproperties.setUsername("db2admin");
		dbInterfaceproperties.setPassword("db2admin");
		dbInterfaceproperties.setCatalog("null");
		dbInterfaceproperties.setSchema("DB2ADMIN");
		return (dbInterfaceproperties);
	}

	public static DbInterfaceProperties getPostgreDefaults() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties.setJcdAlias("MyDB");
		dbInterfaceproperties.setDefaultConnection(true);
		dbInterfaceproperties.setPlatform("PostgreSQL");
		dbInterfaceproperties.setJdbcLevel("3.0");
		dbInterfaceproperties.setDriver("org.postgresql.Driver");
		dbInterfaceproperties.setProtocol("jdbc");
		dbInterfaceproperties.setSubprotocol("postgresql");
		dbInterfaceproperties.setDbalias("//localhost:5432/mfg_scm");
		dbInterfaceproperties.setUsername("postgres");
		dbInterfaceproperties.setPassword("postgres");
		dbInterfaceproperties.setCatalog("Java");
		dbInterfaceproperties.setSchema("public");
		return (dbInterfaceproperties);
	}

	public static Hashtable getDefaults() {
		Hashtable hashtable = new Hashtable();
		hashtable.put(DbInterfaceProperties.getHSQLDefaults().getPlatform(),
				DbInterfaceProperties.getHSQLDefaults());
		hashtable.put(DbInterfaceProperties.getPostgreDefaults().getPlatform(),
				DbInterfaceProperties.getPostgreDefaults());
		hashtable.put(DbInterfaceProperties.getMSSQLServerDefaults()
				.getPlatform(), DbInterfaceProperties.getMSSQLServerDefaults());
		hashtable.put(DbInterfaceProperties.getOracleDefaults().getPlatform(),
				DbInterfaceProperties.getOracleDefaults());
		hashtable.put(DbInterfaceProperties.getMySQLDefaults().getPlatform(),
				DbInterfaceProperties.getMySQLDefaults());
		hashtable.put(DbInterfaceProperties.getDB2Defaults().getPlatform(),
				DbInterfaceProperties.getDB2Defaults());
		return (hashtable);
	}

	public static String getOjbAppPath() {
		IProject[] projects = root.getProjects();

		for (int i = 0; i < projects.length; i++) {
			try {
				if (projects[i].hasNature(DbfLauncherPlugin.NATURE_ID)) {
					IFolder folder = DbfProject.create(projects[i])
							.getOjbAppFolder();
					String s_PropertieName = folder.getLocation().toFile()
							.getAbsolutePath();
					return (s_PropertieName);
				}
			} catch (CoreException e) {
				// ignore update if there is an exception
			}
		}
		MessageDialog.openInformation(null, "Error", "No Db project open");
		return null;
	}

	public static String getJavaObjectsPath() {

		IProject[] projects = root.getProjects();

		try {
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].hasNature(DbfLauncherPlugin.NATURE_ID)) {
					IFolder folder = DbfProject.create(projects[i])
							.getOjbAppFolder();
					IFolder javaFolder = folder.getFolder("src");
					String s_PropertieName = javaFolder.getLocation().toFile()
							.getAbsolutePath();
					return (s_PropertieName);
				}
			}
		} catch (CoreException e) {
			// ignore update if there is an exception
		}
		return null;
	}

	public static String getDtdUrl() {
		IProject[] projects = root.getProjects();

		try {
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].hasNature(DbfLauncherPlugin.NATURE_ID)) {
					String m_DtdUrl = DbfProject.create(projects[i])
							.getDtdUrl();
					return (m_DtdUrl);
				}
			}
		} catch (CoreException e) {
			// ignore update if there is an exception
		}
		return null;
	}

	public static boolean getUpdateXml() {
		IProject[] projects = root.getProjects();

		try {
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].hasNature(DbfLauncherPlugin.NATURE_ID)) {
					boolean m_UpdateXml = DbfProject.create(projects[i])
							.getUpdateXml();
					return (m_UpdateXml);
				}
			}
		} catch (CoreException e) {
			// ignore update if there is an exception
		}
		return false;
	}

	public static DbInterfaceProperties getFromFile() {

		IProject[] projects = root.getProjects();

		try {
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].hasNature(DbfLauncherPlugin.NATURE_ID)) {
					String m_PropertieName = DbfProject.create(projects[i])
							.getInterfacePropertiesFile().getAbsolutePath();
					return (DbInterfaceProperties.getFromFile(m_PropertieName));
				}
			}
		} catch (CoreException e) {
			// ignore update if there is an exception
		}
		return null;
	}

	public static DbInterfaceProperties getFromFile(String propertiesFileName) {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		Properties properties;
		try {
			properties = new Properties();
			properties.load(new FileInputStream(propertiesFileName));
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbInterfaceProperties Error loading propertiesFileName : "
							+ propertiesFileName);
			DbfLauncherPlugin.log(ioexception);
			return (null);
		}
		dbInterfaceproperties.setDbalias(properties
				.getProperty("jcd-alias", ""));
		dbInterfaceproperties.setDefaultConnection(false);
		if (properties.getProperty("default-connection", "false")
				.equals("true"))
			dbInterfaceproperties.setDefaultConnection(true);

		dbInterfaceproperties.setPlatform(properties
				.getProperty("platform", ""));
		dbInterfaceproperties.setJdbcLevel(properties.getProperty("jdbc-level",
				"2.0"));
		dbInterfaceproperties.setDriver(properties.getProperty("driver", ""));
		dbInterfaceproperties.setProtocol(properties.getProperty("protocol",
				"jdbc"));
		dbInterfaceproperties.setSubprotocol(properties.getProperty(
				"subprotocol", ""));
		dbInterfaceproperties.setDbalias(properties.getProperty("dbalias", ""));
		dbInterfaceproperties.setUsername(properties
				.getProperty("username", ""));
		dbInterfaceproperties.setPassword(properties
				.getProperty("password", ""));
		dbInterfaceproperties.setCatalog(properties.getProperty("catalog", ""));
		dbInterfaceproperties.setSchema(properties.getProperty("schema", ""));
		dbInterfaceproperties.setBatchMode(false);
		if (properties.getProperty("batch-mode", "false").equals("true"))
			dbInterfaceproperties.setBatchMode(true);

		dbInterfaceproperties.setAutoincrement(properties.getProperty(
				"autoincrement", ""));
		dbInterfaceproperties.setUserRepositoryPath(properties.getProperty(
				"userRepositoryPath", getOjbAppPath()));
		dbInterfaceproperties.setJavaRepositoryPath(properties.getProperty(
				"javaRepositoryPath", getJavaObjectsPath()));
		dbInterfaceproperties.setIncludeRepositoryPath(properties.getProperty(
				"includeRepositoryPath", getJavaObjectsPath()));
		dbInterfaceproperties.setXmlModelRepositoryPath(properties.getProperty(
				"modelRepositoryPath", getJavaObjectsPath()));
		dbInterfaceproperties
				.setSPackage(properties.getProperty("package", ""));
		dbInterfaceproperties.setSImport(properties.getProperty("import", ""));
		dbInterfaceproperties.setSImplements(properties.getProperty(
				"implements", ""));
		dbInterfaceproperties
				.setSExtends(properties.getProperty("extends", ""));
		dbInterfaceproperties.setCopyfuction(false);
		if (properties.getProperty("copyfuction", "false").equals("true"))
			dbInterfaceproperties.setCopyfuction(true);

		dbInterfaceproperties.setUseSchemaForSelect(true);
		if (properties.getProperty("useSchemaForSelect", "true")
				.equals("false"))
			dbInterfaceproperties.setUseSchemaForSelect(false);
		dbInterfaceproperties.setDisplayRepositoryPath(properties.getProperty(
				"displayRepositoryPath", getJavaObjectsPath()));
		dbInterfaceproperties.setDPackage(properties
				.getProperty("displaypackage",
						"org.compiere.mfg_scm.eclipse.rio.FormsUtils"));
		dbInterfaceproperties
				.setDImport(properties
						.getProperty(
								"displayimport",
								"org.eclipse.swt.SWT,org.eclipse.swt.layout.GridData,org.eclipse.swt.layout.GridLayout,org.eclipse.swt.widgets.Button,org.eclipse.swt.widgets.Combo,org.eclipse.swt.widgets.Composite,org.eclipse.swt.graphics.Font,org.eclipse.swt.widgets.Display,org.eclipse.swt.graphics.Point,org.eclipse.swt.widgets.Shell,org.eclipse.swt.layout.FillLayout,org.eclipse.swt.widgets.Label,org.eclipse.swt.widgets.Text"));
		dbInterfaceproperties.setDImplements(properties.getProperty(
				"displayimplements", ""));
		dbInterfaceproperties.setDExtends(properties.getProperty(
				"displayextends", "Composite"));

		dbInterfaceproperties.setJavaTypesARRAY(properties.getProperty(
				"JavaTypes.ARRAY", "Object[]"));
		dbInterfaceproperties.setJavaTypesBIGINT(properties.getProperty(
				"JavaTypes.BIGINT", "long"));
		dbInterfaceproperties.setJavaTypesBINARY(properties.getProperty(
				"JavaTypes.BINARY", "byte[]"));
		dbInterfaceproperties.setJavaTypesBIT(properties.getProperty(
				"JavaTypes.BIT", "byte"));
		dbInterfaceproperties.setJavaTypesBLOB(properties.getProperty(
				"JavaTypes.BLOB", "byte[]"));
		dbInterfaceproperties.setJavaTypesCHAR(properties.getProperty(
				"JavaTypes.CHAR", "String"));
		dbInterfaceproperties.setJavaTypesCLOB(properties.getProperty(
				"JavaTypes.CLOB", "String"));
		dbInterfaceproperties.setJavaTypesDATE(properties.getProperty(
				"JavaTypes.DATE", "java.sql.Date"));
		dbInterfaceproperties.setJavaTypesDECIMAL(properties.getProperty(
				"JavaTypes.DECIMAL", "long"));
		dbInterfaceproperties.setJavaTypesDISTINCT(properties.getProperty(
				"JavaTypes.DISTINCT", ""));
		dbInterfaceproperties.setJavaTypesDOUBLE(properties.getProperty(
				"JavaTypes.DOUBLE", "double"));
		dbInterfaceproperties.setJavaTypesFLOAT(properties.getProperty(
				"JavaTypes.FLOAT", "float"));
		dbInterfaceproperties.setJavaTypesINTEGER(properties.getProperty(
				"JavaTypes.INTEGER", "long"));
		dbInterfaceproperties.setJavaTypesJAVA_OBJECT(properties.getProperty(
				"JavaTypes.JAVA_OBJECT", "Object"));
		dbInterfaceproperties.setJavaTypesLONGVARBINARY(properties.getProperty(
				"JavaTypes.LONGVARBINARY", "byte[]"));
		dbInterfaceproperties.setJavaTypesLONGVARCHAR(properties.getProperty(
				"JavaTypes.LONGVARCHAR", "byte[]"));
		dbInterfaceproperties.setJavaTypesNULL(properties.getProperty(
				"JavaTypes.NULL", "Object"));
		dbInterfaceproperties.setJavaTypesNUMERIC(properties.getProperty(
				"JavaTypes.NUMERIC", "long"));
		dbInterfaceproperties.setJavaTypesOTHER(properties.getProperty(
				"JavaTypes.OTHER", "Object"));
		dbInterfaceproperties.setJavaTypesREAL(properties.getProperty(
				"JavaTypes.REAL", "long"));
		dbInterfaceproperties.setJavaTypesREF(properties.getProperty(
				"JavaTypes.REF", "Object"));
		dbInterfaceproperties.setJavaTypesSMALLINT(properties.getProperty(
				"JavaTypes.SMALLINT", "long"));
		dbInterfaceproperties.setJavaTypesSTRUCT(properties.getProperty(
				"JavaTypes.STRUCT", "Object"));
		dbInterfaceproperties.setJavaTypesTIME(properties.getProperty(
				"JavaTypes.TIME", "java.sql.Time"));
		dbInterfaceproperties.setJavaTypesTIMESTAMP(properties.getProperty(
				"JavaTypes.TIMESTAMP", "java.sql.Timestamp"));
		dbInterfaceproperties.setJavaTypesTINYINT(properties.getProperty(
				"JavaTypes.TINYINT", "long"));
		dbInterfaceproperties.setJavaTypesVARBINARY(properties.getProperty(
				"JavaTypes.VARBINARY", "byte[]"));
		dbInterfaceproperties.setJavaTypesVARCHAR(properties.getProperty(
				"JavaTypes.VARCHAR", "String"));
		for (int i = 0; properties.getProperty("JDBCDriver." + i, "").length() > 0; i++) {
			dbInterfaceproperties.addJdbcDriver(properties.getProperty(
					"JDBCDriver." + i, ""));
		}
		return (dbInterfaceproperties);
	}

	public DbInterfaceProperties() {
		jcdAlias = "default";
		defaultConnection = true;
		platform = "";
		jdbcLevel = "2.0";
		driver = "";
		protocol = "jdbc";
		subprotocol = "";
		dbalias = "";
		username = "";
		password = "";
		catalog = "";
		schema = "";
		batchMode = false;
		eagerRelease = false;
		autoCommit = "1";
		acExceptions = false;
		autoincrement = "";
		userRepositoryPath = "C:\\temp\\";
		javaRepositoryPath = "C:\\temp\\";
		includeRepositoryPath = "C:\\temp\\";
		sImport = "";
		sImplements = "";
		sExtends = "";
		sPackage = "";
		copyfuction = false;
		useSchemaForSelect = true;
		displayRepositoryPath = "C:\\temp\\";
		dImport = "";
		dImplements = "";
		dExtends = "";
		dPackage = "";
		modelRepositoryPath = "C:\\temp\\";
		/*
		 * mImport = ""; mImplements = ""; mExtends = ""; mPackage = "";
		 */
		JavaTypesARRAY = "Object[]";
		JavaTypesBIGINT = "long";
		JavaTypesBINARY = "byte[]";
		JavaTypesBIT = "byte";
		JavaTypesBLOB = "byte[]";
		JavaTypesCHAR = "String";
		JavaTypesCLOB = "String";
		JavaTypesDATE = "java.sql.Date";
		JavaTypesDECIMAL = "long";
		JavaTypesDISTINCT = "????";
		JavaTypesDOUBLE = "double";
		JavaTypesFLOAT = "float";
		JavaTypesINTEGER = "long";
		JavaTypesJAVA_OBJECT = "Object";
		JavaTypesLONGVARBINARY = "byte[]";
		JavaTypesLONGVARCHAR = "byte[]";
		JavaTypesNULL = "Object";
		JavaTypesNUMERIC = "long";
		JavaTypesOTHER = "Object";
		JavaTypesREAL = "long";
		JavaTypesREF = "Object";
		JavaTypesSMALLINT = "long";
		JavaTypesSTRUCT = "Object";
		JavaTypesTIME = "java.sql.Time";
		JavaTypesTIMESTAMP = "java.sql.Timestamp";
		JavaTypesTINYINT = "long";
		JavaTypesVARBINARY = "byte[]";
		JavaTypesVARCHAR = "String";
		JdbcDrivers = new Vector();
		return;
	}

	public String getAutoincrement() {
		return (autoincrement);
	}

	public boolean isBatchMode() {
		return (batchMode);
	}

	public boolean isEagerRelease() {
		return (eagerRelease);
	}

	public String getAutoCommit() {
		return (autoCommit);
	}

	public boolean isAcExceptions() {
		return (acExceptions);
	}

	public String getCatalog() {
		return (catalog);
	}

	public boolean isCopyfuction() {
		return (copyfuction);
	}

	public String getDbalias() {
		return (dbalias);
	}

	public boolean isDefaultConnection() {
		return (defaultConnection);
	}

	public String getDriver() {
		return (driver);
	}

	public String getJavaTypesARRAY() {
		return (JavaTypesARRAY);
	}

	public String getJavaTypesBIGINT() {
		return (JavaTypesBIGINT);
	}

	public String getJavaTypesBINARY() {
		return (JavaTypesBINARY);
	}

	public String getJavaTypesBIT() {
		return (JavaTypesBIT);
	}

	public String getJavaTypesBLOB() {
		return (JavaTypesBLOB);
	}

	public String getJavaTypesCHAR() {
		return (JavaTypesCHAR);
	}

	public String getJavaTypesCLOB() {
		return (JavaTypesCLOB);
	}

	public String getJavaTypesDATE() {
		return (JavaTypesDATE);
	}

	public String getJavaTypesDECIMAL() {
		return (JavaTypesDECIMAL);
	}

	public String getJavaTypesDISTINCT() {
		return (JavaTypesDISTINCT);
	}

	public String getJavaTypesDOUBLE() {
		return (JavaTypesDOUBLE);
	}

	public String getJavaTypesFLOAT() {
		return (JavaTypesFLOAT);
	}

	public String getJavaTypesINTEGER() {
		return (JavaTypesINTEGER);
	}

	public String getJavaTypesJAVA_OBJECT() {
		return (JavaTypesJAVA_OBJECT);
	}

	public String getJavaTypesLONGVARBINARY() {
		return (JavaTypesLONGVARBINARY);
	}

	public String getJavaTypesLONGVARCHAR() {
		return (JavaTypesLONGVARCHAR);
	}

	public String getJavaTypesNULL() {
		return (JavaTypesNULL);
	}

	public String getJavaTypesNUMERIC() {
		return (JavaTypesNUMERIC);
	}

	public String getJavaTypesOTHER() {
		return (JavaTypesOTHER);
	}

	public String getJavaTypesREAL() {
		return (JavaTypesREAL);
	}

	public String getJavaTypesREF() {
		return (JavaTypesREF);
	}

	public String getJavaTypesSMALLINT() {
		return (JavaTypesSMALLINT);
	}

	public String getJavaTypesSTRUCT() {
		return (JavaTypesSTRUCT);
	}

	public String getJavaTypesTIME() {
		return (JavaTypesTIME);
	}

	public String getJavaTypesTIMESTAMP() {
		return (JavaTypesTIMESTAMP);
	}

	public String getJavaTypesTINYINT() {
		return (JavaTypesTINYINT);
	}

	public String getJavaTypesVARBINARY() {
		return (JavaTypesVARBINARY);
	}

	public String getJavaTypesVARCHAR() {
		return (JavaTypesVARCHAR);
	}

	public String getJcdAlias() {
		return (jcdAlias);
	}

	public String getJdbcLevel() {
		return (jdbcLevel);
	}

	public String getPassword() {
		return (password);
	}

	public String getPlatform() {
		return (platform);
	}

	public String getProtocol() {
		return (protocol);
	}

	public String getSchema() {
		return (schema);
	}

	public String getSExtends() {
		return (sExtends);
	}

	public String getSImplements() {
		return (sImplements);
	}

	public String getSImport() {
		return (sImport);
	}

	public String getDExtends() {
		return (dExtends);
	}

	public String getDImplements() {
		return (dImplements);
	}

	public String getDImport() {
		return (dImport);
	}

	public String getSubprotocol() {
		return (subprotocol);
	}

	/*
	 * public String getMExtends() { return (mExtends); }
	 * 
	 * public String getMImplements() { return (mImplements); }
	 * 
	 * public String getMImport() { return (mImport); }
	 */

	public String getUsername() {
		return (username);
	}

	public String getJavaRepositoryPath() {
		return (javaRepositoryPath);
	}

	public String getIncludeRepositoryPath() {
		return (includeRepositoryPath);
	}

	public String getDisplayRepositoryPath() {
		return (displayRepositoryPath);
	}

	public String getXmlModelRepositoryPath() {
		return (modelRepositoryPath);
	}

	public String getUserRepositoryPath() {
		return (userRepositoryPath);
	}

	public void setAutoincrement(String string) {
		autoincrement = string;
		return;
	}

	public void setBatchMode(boolean i) {
		batchMode = i;
		return;
	}

	public void setEagerRelease(boolean i) {
		eagerRelease = i;
		return;
	}

	public void setAutoCommit(String string) {
		autoCommit = string;
		return;
	}

	public void setAcExceptions(boolean i) {
		acExceptions = i;
		return;
	}

	public void setCatalog(String string) {
		catalog = string;
		return;
	}

	public void setCopyfuction(boolean i) {
		copyfuction = i;
		return;
	}

	public void setDbalias(String string) {
		dbalias = string;
		return;
	}

	public void setDefaultConnection(boolean i) {
		defaultConnection = i;
		return;
	}

	public void setDriver(String string) {
		driver = string;
		return;
	}

	public void setJavaTypesARRAY(String string) {
		JavaTypesARRAY = string;
		return;
	}

	public void setJavaTypesBIGINT(String string) {
		JavaTypesBIGINT = string;
		return;
	}

	public void setJavaTypesBINARY(String string) {
		JavaTypesBINARY = string;
		return;
	}

	public void setJavaTypesBIT(String string) {
		JavaTypesBIT = string;
		return;
	}

	public void setJavaTypesBLOB(String string) {
		JavaTypesBLOB = string;
		return;
	}

	public void setJavaTypesCHAR(String string) {
		JavaTypesCHAR = string;
		return;
	}

	public void setJavaTypesCLOB(String string) {
		JavaTypesCLOB = string;
		return;
	}

	public void setJavaTypesDATE(String string) {
		JavaTypesDATE = string;
		return;
	}

	public void setJavaTypesDECIMAL(String string) {
		JavaTypesDECIMAL = string;
		return;
	}

	public void setJavaTypesDISTINCT(String string) {
		JavaTypesDISTINCT = string;
		return;
	}

	public void setJavaTypesDOUBLE(String string) {
		JavaTypesDOUBLE = string;
		return;
	}

	public void setJavaTypesFLOAT(String string) {
		JavaTypesFLOAT = string;
		return;
	}

	public void setJavaTypesINTEGER(String string) {
		JavaTypesINTEGER = string;
		return;
	}

	public void setJavaTypesJAVA_OBJECT(String string) {
		JavaTypesJAVA_OBJECT = string;
		return;
	}

	public void setJavaTypesLONGVARBINARY(String string) {
		JavaTypesLONGVARBINARY = string;
		return;
	}

	public void setJavaTypesLONGVARCHAR(String string) {
		JavaTypesLONGVARCHAR = string;
		return;
	}

	public void setJavaTypesNULL(String string) {
		JavaTypesNULL = string;
		return;
	}

	public void setJavaTypesNUMERIC(String string) {
		JavaTypesNUMERIC = string;
		return;
	}

	public void setJavaTypesOTHER(String string) {
		JavaTypesOTHER = string;
		return;
	}

	public void setJavaTypesREAL(String string) {
		JavaTypesREAL = string;
		return;
	}

	public void setJavaTypesREF(String string) {
		JavaTypesREF = string;
		return;
	}

	public void setJavaTypesSMALLINT(String string) {
		JavaTypesSMALLINT = string;
		return;
	}

	public void setJavaTypesSTRUCT(String string) {
		JavaTypesSTRUCT = string;
		return;
	}

	public void setJavaTypesTIME(String string) {
		JavaTypesTIME = string;
		return;
	}

	public void setJavaTypesTIMESTAMP(String string) {
		JavaTypesTIMESTAMP = string;
		return;
	}

	public void setJavaTypesTINYINT(String string) {
		JavaTypesTINYINT = string;
		return;
	}

	public void setJavaTypesVARBINARY(String string) {
		JavaTypesVARBINARY = string;
		return;
	}

	public void setJavaTypesVARCHAR(String string) {
		JavaTypesVARCHAR = string;
		return;
	}

	public void setJcdAlias(String string) {
		jcdAlias = string;
		return;
	}

	public void setJdbcLevel(String string) {
		jdbcLevel = string;
		return;
	}

	public void setPassword(String string) {
		password = string;
		return;
	}

	public void setPlatform(String string) {
		platform = string;
		return;
	}

	public void setProtocol(String string) {
		protocol = string;
		return;
	}

	public void setSchema(String string) {
		schema = string;
		return;
	}

	public void setSExtends(String string) {
		sExtends = string;
		return;
	}

	public void setSImplements(String string) {
		sImplements = string;
		return;
	}

	public void setSImport(String string) {
		sImport = string;
		return;
	}

	public void setDExtends(String string) {
		dExtends = string;
		return;
	}

	public void setDImplements(String string) {
		dImplements = string;
		return;
	}

	public void setDImport(String string) {
		dImport = string;
		return;
	}

	/*
	 * public void setMExtends(String string) { mExtends = string; return; }
	 * 
	 * public void setMImplements(String string) { mImplements = string; return; }
	 * 
	 * public void setMImport(String string) { mImport = string; return; }
	 */

	public void setSubprotocol(String string) {
		subprotocol = string;
		return;
	}

	public void setUsername(String string) {
		username = string;
		return;
	}

	public void setJavaRepositoryPath(String string) {
		javaRepositoryPath = string;
		return;
	}

	public void setIncludeRepositoryPath(String string) {
		includeRepositoryPath = string;
		return;
	}

	public void setDisplayRepositoryPath(String string) {
		displayRepositoryPath = string;
		return;
	}

	public void setXmlModelRepositoryPath(String string) {
		modelRepositoryPath = string;
		return;
	}

	public void setUserRepositoryPath(String string) {
		userRepositoryPath = string;
		return;
	}

	public boolean isUseSchemaForSelect() {
		return (useSchemaForSelect);
	}

	public void setUseSchemaForSelect(boolean i) {
		useSchemaForSelect = i;
		return;
	}

	public Vector getJdbcDrivers() {
		return (JdbcDrivers);
	}

	public void setJdbcDrivers(Vector vector) {
		JdbcDrivers = vector;
		return;
	}

	public void addJdbcDriver(String string) {
		JdbcDrivers.add(string);
		return;
	}

	public void removeJdbcDriver(int i) {
		JdbcDrivers.remove(i);
		return;
	}

	public void removeAllJdbcDriver() {
		JdbcDrivers.clear();
		return;
	}

	public String getSPackage() {
		return (sPackage);
	}

	public void setSPackage(String string) {
		sPackage = string;
		return;
	}

	public String getDPackage() {
		return (dPackage);
	}

	public void setDPackage(String string) {
		dPackage = string;
		return;
	}

	/*
	 * public String getMPackage() { return (mPackage); }
	 * 
	 * public void setMPackage(String string) { mPackage = string; return; }
	 */

}
