/*
 * ====================================================================
 * Copyright 2002-2004 The Apache Software Foundation. Parts (c) Copyright 2004-2005 Compiere MFG + SCM.
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.tree.TreeNode;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBCatalog implements MetadataNodeInterface, TreeNode {

	private DBMeta parsedMetaData;

	private java.sql.DatabaseMetaData dbMeta;

	private boolean enabled = true;

	private String strCatalogName;

	private java.util.TreeMap hmSchemas = new java.util.TreeMap();

	private org.apache.torque.engine.database.model.Database torqueDatabase;

	public DBCatalog(java.sql.DatabaseMetaData pdbMeta, DBMeta paMeta,
			String pstrCatalogName) {
		this.dbMeta = pdbMeta;
		this.parsedMetaData = paMeta;
		this.strCatalogName = pstrCatalogName;
		this.torqueDatabase = paMeta.getTorqueDatabase();
	}

	public boolean isEnabled() {
		return (this.enabled);
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public DBSchema getSchema(String strSchemaName) {
		return ((DBSchema) this.hmSchemas.get(strSchemaName));
	}

	public TreeMap getSchemas() {
		return (this.hmSchemas);
	}

	public void putSchema(String key, DBSchema dbschema) {
		this.hmSchemas.put(key, dbschema);
	}

	public String getCatalogName() {
		return (this.strCatalogName);
	}

	public DBMeta getDBMeta() {
		return (parsedMetaData);
	}

	public boolean isTreeEnabled() {
		return getDBMeta().isEnabled() && this.isEnabled();
	}

	public void generateReferences() throws java.sql.SQLException {
		dbMeta.getConnection().setCatalog(this.getCatalogName());
		Iterator it = this.hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).generateReferences();
	}

	public void read() throws java.sql.SQLException {
		// Set the catalog name of the connection before accessing the metadata
		// object
		java.sql.ResultSet rs = dbMeta.getSchemas();
		int count = 0;
		while (rs.next()) {
			count++;
			String strSchemaName = rs.getString("TABLE_SCHEM");
			// Fix for IBM Informix JDBC 2.21JC2; Schema is padded with spaces,
			// needs to be trimmed
			strSchemaName = strSchemaName.trim();

			DBSchema dbschema = this.getSchema(strSchemaName);
			if (dbschema == null) {
				dbschema = new DBSchema(dbMeta, this, strSchemaName);
				this.putSchema(strSchemaName, dbschema);
			}
		}
		// Fix for MySQL: Create an empty schema
		if (count == 0) {
			this.hmSchemas.put("", new DBSchema(dbMeta, this, ""));
		}

		rs.close();
		Iterator it = hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).read();
	}

	public void addTable(String strSchemaName, String strTableName,
			String strTableType) throws java.sql.SQLException {
		DBSchema aDBSchema = this.getSchema(strSchemaName);
		if (aDBSchema == null) {
			aDBSchema = new DBSchema(dbMeta, this, strSchemaName);
			this.putSchema(strSchemaName, aDBSchema);
			aDBSchema.read();
		}
		aDBSchema.addTable(strTableName, strTableType);
	}

	public void addColumn(String strSchemaName, String strTableName,
			String strColumnName, int iDataType, String strTypeName,
			int iColumnSize, int iNullable, String iColumnDef) {
		DBSchema aDBSchema = this.getSchema(strSchemaName);
		if (aDBSchema != null)
			aDBSchema.addColumn(strTableName, strColumnName, iDataType,
					strTypeName, iColumnSize, iNullable, iColumnDef);
	}

	public void addPrimaryKeyColumn(String strSchemaName, String strTableName,
			String strColumnName) {
		DBSchema aDBSchema = this.getSchema(strSchemaName);
		if (aDBSchema != null)
			aDBSchema.addPrimaryKeyColumn(strTableName, strColumnName);
	}

	public Enumeration children() {
		return (Collections.enumeration(this.hmSchemas.values()));
	}

	public boolean getAllowsChildren() {
		return (true);
	}

	public TreeNode getChildAt(int param) {
		TreeNode treeNode = (TreeNode) this.hmSchemas.values().toArray()[param];
		return (treeNode);
	}

	public int getChildCount() {
		return (this.hmSchemas.size());
	}

	public int getIndex(TreeNode treenode) {
		int indexOf = new Vector(this.hmSchemas.values()).indexOf(treenode);
		return (indexOf);
	}

	public TreeNode getParent() {
		return (this.parsedMetaData);
	}

	public boolean isLeaf() {
		return (false);
	}

	public String toString() {
		if (this.strCatalogName == null
				|| this.strCatalogName.trim().length() == 0)
			return ("<empty catalog>");

		return (this.strCatalogName);
	}

	public String getXML() {
		java.io.StringWriter swr = new java.io.StringWriter();
		writeXML(new java.io.PrintWriter(swr));
		return swr.getBuffer().toString();
	}

	public void writeXML(java.io.PrintWriter pw) {
		Iterator i = this.hmSchemas.values().iterator();
		while (i.hasNext()) {
			((DBSchema) i.next()).writeXML(pw);
		}
	}

	public void generateJava(java.io.File packageDir, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		Iterator it = this.hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).generateJava(packageDir, properties,
					strHeader, strFooter);
	}

	public void generateDisplay(java.io.File packageDir, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		Iterator it = this.hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).generateDisplay(packageDir, properties,
					strHeader, strFooter);
	}

	public void generateXmlModel(java.io.PrintWriter pw)
			throws FileNotFoundException, IOException {
		Iterator it = this.hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).generateXmlModel(pw);

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
		Iterator it = this.hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).setSPackage(packageName);
	}

	public void setDPackage(String packageName) {
		Iterator it = this.hmSchemas.values().iterator();
		while (it.hasNext())
			((DBSchema) it.next()).setDPackage(packageName);
	}
}
