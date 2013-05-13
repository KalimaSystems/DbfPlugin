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
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.tree.TreeNode;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBSchema implements MetadataNodeInterface, TreeNode {

	private java.sql.DatabaseMetaData dbMeta;

	private DBCatalog aDBCatalog;

	private boolean enabled = true;

	private TreeMap tmTables = new TreeMap();

	private String m_strSchemaName;

	private org.apache.torque.engine.database.model.Database torqueDatabase;

	public DBSchema(java.sql.DatabaseMetaData pdbMeta, DBCatalog paDBCatalog,
			String pstrSchemaName) {
		aDBCatalog = paDBCatalog;
		dbMeta = pdbMeta;
		m_strSchemaName = pstrSchemaName;
		this.torqueDatabase = paDBCatalog.getTorqueDatabase();
	}

	public boolean isEnabled() {
		return (enabled);
	}

	public void setEnabled(boolean b) {
		enabled = b;
	}

	public DBCatalog getDBCatalog() {
		return (aDBCatalog);
	}

	public String getSchemaName() {
		return (this.m_strSchemaName);
	}

	public boolean isTreeEnabled() {
		return getDBCatalog().isEnabled() && this.isEnabled();
	}

	public void read() throws java.sql.SQLException {
		java.sql.ResultSet rs = dbMeta.getTables(this.getDBCatalog()
				.getCatalogName(), this.getSchemaName(), "%", null);
		while (rs.next()) {
			String strTableCat = rs.getString("TABLE_CAT");
			String strSchemaName = rs.getString("TABLE_SCHEM");
			String strTableName = rs.getString("TABLE_NAME");
			String strTableType = rs.getString("TABLE_TYPE");
			if (strTableType.equals("TABLE"))
				this.addTable(strTableName, strTableType);
		}
		rs.close();

		rs = dbMeta.getColumns(this.getDBCatalog().getCatalogName(), this
				.getSchemaName(), "%", "%");
		while (rs.next()) {
			String strSchemaName = rs.getString("TABLE_SCHEM");
			String strTableName = rs.getString("TABLE_NAME");
			String strColumnName = rs.getString("COLUMN_NAME");
			String strColumnDef = rs.getString("REMARKS");
			int iDataType = rs.getInt("DATA_TYPE");
			String strTypeName = rs.getString("TYPE_NAME");
			int iColumnSize = rs.getInt("COLUMN_SIZE");
			int iNullable = rs.getInt("NULLABLE");
			// System.out.println(strTableName + " " + strColumnName + " " +
			// iDataType + " " + strTypeName + " " + iColumnSize);
			this.addColumn(strTableName, strColumnName, iDataType, strTypeName,
					iColumnSize, iNullable, strColumnDef);
		}
		rs.close();
	}

	public DBTable addTable(String strTableName, String strTableType)
			throws java.sql.SQLException {
		DbfLauncherPlugin.log(IStatus.INFO, "DBSchema addTable " + strTableName
				+ " strTableType " + strTableType);
		DBTable aDBTable = new DBTable(dbMeta, this, strTableName);
		this.tmTables.put(strTableName, aDBTable);
		aDBTable.read();
		return aDBTable;
	}

	public DBColumn addColumn(String strTableName, String strColumnName,
			int iDataType, String strTypeName, int iColumnSize, int iNullable,
			String iColumnDef) {
		DBTable aDBTable = this.getTable(strTableName);
		if (aDBTable != null)
			return (aDBTable.addColumn(strColumnName, iDataType, strTypeName,
					iColumnSize, iNullable, iColumnDef));
		return null;
	}

	public void changeTable(String strTableName, DBTable changedObject) {
		this.tmTables.remove(strTableName);
		this.tmTables.put(changedObject.getTableName(), changedObject);
		DbfLauncherPlugin.log(IStatus.INFO, "DSchema changeTable strTableName "
				+ strTableName + " new " + changedObject.getTableName());
	}

	public void changeColumn(String strTableName, String columnName,
			DBColumn changedObject) {
		DBTable aDBTable = this.getTable(strTableName);
		if (aDBTable != null)
			aDBTable.changeColumn(columnName, changedObject);
	}

	public void addPrimaryKeyColumn(String strTableName, String strColumnName) {
		DBTable aDBTable = this.getTable(strTableName);
		if (aDBTable != null)
			aDBTable.addPrimaryKeyColumn(strColumnName);
	}

	public DBTable getTable(String strTableName) {
		return ((DBTable) this.tmTables.get(strTableName));
	}

	public TreeMap getTables() {
		return (this.tmTables);
	}

	public void generateReferences() throws java.sql.SQLException {
		Iterator it = this.tmTables.values().iterator();
		while (it.hasNext())
			((DBTable) it.next()).generateReferences();
	}

	public Enumeration children() {
		return (Collections.enumeration(this.tmTables.values()));
	}

	public boolean getAllowsChildren() {
		return (true);
	}

	public TreeNode getChildAt(int param) {
		TreeNode treenode = (TreeNode) this.tmTables.values().toArray()[param];
		return (treenode);
	}

	public int getChildCount() {
		return (tmTables.size());
	}

	public int getIndex(TreeNode treenode) {
		int indexOf = new Vector(this.tmTables.values()).indexOf(treenode);
		return (indexOf);
	}

	public TreeNode getParent() {
		return (this.aDBCatalog);
	}

	public boolean isLeaf() {
		return (false);
	}

	public String toString() {
		if (m_strSchemaName == null || m_strSchemaName.trim().length() == 0)
			return ("<empty  schema>");
		else
			return (this.m_strSchemaName);
	}

	public String getXML() {
		java.io.StringWriter swr = new java.io.StringWriter();
		writeXML(new java.io.PrintWriter(swr));
		return swr.getBuffer().toString();
	}

	public void writeXML(java.io.PrintWriter pw) {
		Iterator i = this.tmTables.values().iterator();
		while (i.hasNext()) {
			((DBTable) i.next()).writeXML(pw);
		}
	}

	public void generateJava(File packageDir, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		Iterator it = this.tmTables.values().iterator();
		while (it.hasNext())
			((DBTable) it.next()).generateJava(packageDir, properties,
					strHeader, strFooter);
	}

	public void generateDisplay(File packageDir, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		Iterator it = this.tmTables.values().iterator();
		while (it.hasNext())
			((DBTable) it.next()).generateDisplay(packageDir, properties,
					strHeader, strFooter);
	}

	public void generateXmlModel(java.io.PrintWriter pw)
			throws FileNotFoundException, IOException {
		Iterator it = this.tmTables.values().iterator();
		while (it.hasNext())
			((DBTable) it.next()).generateXmlModel(pw);

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
		Iterator it = tmTables.values().iterator();
		while (it.hasNext())
			((DBTable) it.next()).setSPackage(packageName);
	}

	public void setDPackage(String packageName) {
		Iterator it = tmTables.values().iterator();
		while (it.hasNext())
			((DBTable) it.next()).setDPackage(packageName);
	}
}
