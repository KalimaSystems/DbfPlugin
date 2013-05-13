/*
 * ====================================================================
 * Copyright 2002-2004 The Apache Software Foundation. Parts (c)  * Copyright 2001-2013 Andre Charles Legendre <andre.legendre@kalimasystems.org>
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBMeta implements MetadataNodeInterface, TreeNode {

	private boolean enabled = true;

	private java.sql.DatabaseMetaData dbMeta;

	private java.util.HashMap hmCatalogs = new java.util.HashMap();

	private String catalogTerm;

	private String catalogSeparator;

	private boolean isCatalogAtStart;

	private boolean supportsCatalogsInDataManipulation;

	private boolean supportsCatalogsInIndexDefinitions;

	private boolean supportsCatalogsInPrivilegeDefinitions;

	private boolean supportsCatalogsInProcedureCalls;

	private boolean supportsCatalogsInTableDefinitions;

	private String schemaTerm;

	private String sCatalog = null;

	private String sSchema = null;

	private boolean withSystemTables = false;

	private String databaseProductName = null;

	private String databaseProductVersion = null;

	private StringBuffer m_strBuf = new StringBuffer();

	private org.apache.torque.engine.database.model.Database torqueDatabase;

	public DBMeta(java.sql.DatabaseMetaData pDbMeta)
			throws java.sql.SQLException {
		this.dbMeta = pDbMeta;
		this.torqueDatabase = null;
	}

	public String getMessages() {
		return (this.m_strBuf.toString());
	}

	public void setCatalog(String s) {
		this.sCatalog = s;
	}

	public void setSchema(String s) {
		this.sSchema = s;
	}

	public String getSchema() {
		return this.sSchema;
	}

	public void setWithSystemTables(boolean b) {
		this.withSystemTables = b;
	}

	public boolean isEnabled() {
		return (this.enabled);
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public String getDatabaseProductVersion() {
		return (this.databaseProductVersion);
	}

	public String getDatabaseProductName() {
		return (this.databaseProductName);
	}

	public boolean getSupportsCatalogsInIndexDefinitions() {
		return (this.supportsCatalogsInIndexDefinitions);
	}

	public boolean getSupportsCatalogsInDataManipulation() {
		return (this.supportsCatalogsInDataManipulation);
	}

	public boolean getSupportsCatalogsInPrivilegeDefinitions() {
		return (this.supportsCatalogsInPrivilegeDefinitions);
	}

	public boolean getSupportsCatalogsInProcedureCalls() {
		return (this.supportsCatalogsInProcedureCalls);
	}

	public boolean getSupportsCatalogsInTableDefinitions() {
		return (this.supportsCatalogsInTableDefinitions);
	}

	public String getCatalogTerm() {
		return (this.catalogTerm);
	}

	public String getSchemaTerm() {
		return (this.schemaTerm);
	}

	public String getCatalogSeparator() {
		return (this.catalogSeparator);
	}

	public boolean getIsCatalogAtStart() {
		return (this.isCatalogAtStart);
	}

	public DBCatalog getCatalog(String catalogName) {
		return ((DBCatalog) this.hmCatalogs.get(catalogName));
	}

	public void read() throws java.sql.SQLException {
		this.databaseProductName = dbMeta.getDatabaseProductName();
		this.databaseProductVersion = dbMeta.getDatabaseProductVersion();
		catalogTerm = dbMeta.getCatalogTerm();
		catalogSeparator = dbMeta.getCatalogSeparator();
		isCatalogAtStart = dbMeta.isCatalogAtStart();
		supportsCatalogsInDataManipulation = dbMeta
				.supportsCatalogsInDataManipulation();
		supportsCatalogsInIndexDefinitions = dbMeta
				.supportsCatalogsInIndexDefinitions();
		supportsCatalogsInPrivilegeDefinitions = dbMeta
				.supportsCatalogsInPrivilegeDefinitions();
		supportsCatalogsInProcedureCalls = dbMeta
				.supportsCatalogsInProcedureCalls();
		supportsCatalogsInTableDefinitions = dbMeta
				.supportsCatalogsInTableDefinitions();
		schemaTerm = dbMeta.getSchemaTerm();
		java.sql.ResultSet rs = dbMeta.getTables(sCatalog, sSchema, "%", null);
		String strTableType;
		String strTableName;
		DBCatalog aDBCatalog = null;
		String strSchemaName;
		int count = 0;
		while (rs.next()) {
			count++;
			String strCatalogName = rs.getString("TABLE_CAT");
			if (strCatalogName == null)
				if (sCatalog != null)
					strCatalogName = sCatalog;
				else
					strCatalogName = "";

			strSchemaName = rs.getString("TABLE_SCHEM");
			if (strSchemaName == null)
				strSchemaName = "";

			strTableName = rs.getString("TABLE_NAME");
			strTableType = rs.getString("TABLE_TYPE");
			if (strTableType.equals("SYSTEM TABLE") && !withSystemTables)
				continue;
			DbfLauncherPlugin.log(IStatus.INFO, "DBMeta read catalog "
					+ strCatalogName + " strSchemaName " + strSchemaName
					+ " strTableName " + strTableName + " strTableType "
					+ strTableType);
			if (aDBCatalog == null) {
				DbfLauncherPlugin.log(IStatus.INFO, "DBMeta read catalog NULL"
						+ strCatalogName + " sCatalog " + sCatalog
						+ " strSchemaName " + strSchemaName + " strTableName "
						+ strTableName + " strTableType " + strTableType);
				aDBCatalog = new DBCatalog(dbMeta, this, strCatalogName);
				this.hmCatalogs.put(strCatalogName, aDBCatalog);
			}

		}
		rs.close();
		if (count == 0) {
			aDBCatalog = new DBCatalog(dbMeta, this, null);
			this.hmCatalogs.put(null, aDBCatalog);
		}

		// Read after closing recordset.
		Iterator it = hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).read();
	}

	public void generateReferences() throws java.sql.SQLException {
		Iterator it = this.hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).generateReferences();

		return;
	}

	public void debug() {
	}

	public Enumeration children() {
		return (Collections.enumeration(this.hmCatalogs.values()));
	}

	public boolean getAllowsChildren() {
		return (true);
	}

	public TreeNode getChildAt(int i) {
		return ((TreeNode) this.hmCatalogs.values().toArray()[i]);
	}

	public int getChildCount() {
		return (this.hmCatalogs.size());
	}

	public int getIndex(TreeNode treenode) {
		return (new Vector(this.hmCatalogs.values()).indexOf(treenode));
	}

	public TreeNode getParent() {
		return (null);
	}

	public boolean isLeaf() {
		return (false);
	}

	public String toString() {
		return ("DBMeta");
	}

	public String getXML() {
		java.io.StringWriter sw = new java.io.StringWriter();
		writeXML(new java.io.PrintWriter(sw));
		return sw.getBuffer().toString();
	}

	public void writeXML(java.io.PrintWriter pw) {

		Iterator i = this.hmCatalogs.values().iterator();
		while (i.hasNext()) {
			((DBCatalog) i.next()).writeXML(pw);
		}
	}

	public void generateJava(File packageDir, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		Iterator it = this.hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).generateJava(packageDir, properties,
					strHeader, strFooter);

		return;
	}

	public void generateDisplay(File packageDir, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		Iterator it = this.hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).generateDisplay(packageDir, properties,
					strHeader, strFooter);

		return;
	}

	public void generateXmlModel(java.io.PrintWriter pw)
			throws FileNotFoundException, IOException {
		Iterator it = this.hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).generateXmlModel(pw);

		return;

	}

	public void setTorqueDatabase(
			org.apache.torque.engine.database.model.Database torqueDatabase) {
		this.torqueDatabase = torqueDatabase;
		return;
	}

	public org.apache.torque.engine.database.model.Database getTorqueDatabase() {
		return (this.torqueDatabase);
	}

	public void setSPackage(String packageName) {
		Iterator it = this.hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).setSPackage(packageName);

		return;
	}

	public void setDPackage(String packageName) {
		Iterator it = this.hmCatalogs.values().iterator();
		while (it.hasNext())
			((DBCatalog) it.next()).setDPackage(packageName);

		return;
	}

	public HashMap getCatalogs() {
		return (this.hmCatalogs);
	}

	public DBTable getTable(String strTableName) {
		DBCatalog dbCatalog = getCatalog(this.sCatalog);
		DBSchema dbSchema = dbCatalog.getSchema(this.sSchema);
		if (dbSchema != null)
			return (dbSchema.getTable(strTableName));
		return (null);
	}

	public DBTable addTable(String tableName) throws SQLException {
		DbfLauncherPlugin.log(IStatus.INFO, "DBMeta addTable = [" + tableName
				+ "] catalog = [" + this.sCatalog + "]");

		DBCatalog dbCatalog = getCatalog(this.sCatalog);
		if (dbCatalog == null) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBMeta addTable dbCatalog == null " + tableName);
			dbCatalog = new DBCatalog(dbMeta, this, this.sCatalog);
			this.hmCatalogs.put(this.sCatalog, dbCatalog);

		}

		DBSchema dbSchema = dbCatalog.getSchema(this.sSchema);
		if (dbSchema == null) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBMeta addTable dbSchema == null " + tableName);
			dbSchema = new DBSchema(dbMeta, dbCatalog, this.sSchema);
			dbCatalog.putSchema(this.sSchema, dbSchema);
		}
		DBTable dbtable = dbSchema.addTable(tableName, "TABLE");
		DbfLauncherPlugin.log(IStatus.INFO, "DBMeta addTable OK " + tableName
				+ " catalog " + this.sCatalog);
		return (dbtable);
	}

	public DBColumn addColumn(String strTableName, String strColumnName,
			int iDataType, String strTypeName, int iColumnSize, int iNullable,
			String iColumnDef) {
		DBCatalog dbCatalog = getCatalog(this.sCatalog);
		DBSchema dbSchema = dbCatalog.getSchema(this.sSchema);

		if (dbSchema != null)
			return (dbSchema.addColumn(strTableName, strColumnName, iDataType,
					strTypeName, iColumnSize, iNullable, iColumnDef));
		return (null);
	}

	public void changeTable(String tableName, DBTable changedObject) {
		DbfLauncherPlugin.log(IStatus.INFO, "DBMeta changeTable " + tableName
				+ " new " + changedObject.getTableName());
		DBCatalog dbCatalog = getCatalog(this.sCatalog);
		DBSchema dbSchema = dbCatalog.getSchema(this.sSchema);
		dbSchema.changeTable(tableName, changedObject);
	}

	public void changeColumn(String strTableName, String columnName,
			DBColumn changedObject) {
		DBCatalog dbCatalog = getCatalog(this.sCatalog);
		DbfLauncherPlugin.log(IStatus.INFO, "DBMeta changeColumn " + columnName
				+ " new " + changedObject.getColumnName());
		DBSchema dbSchema = dbCatalog.getSchema(this.sSchema);
		if (dbSchema != null)
			dbSchema.changeColumn(strTableName, columnName, changedObject);
	}
}
