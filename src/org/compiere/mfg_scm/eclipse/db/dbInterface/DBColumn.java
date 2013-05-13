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
import java.util.Enumeration;
import java.util.Properties;

import javax.swing.tree.TreeNode;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBColumn implements MetadataNodeInterface, TreeNode {

	private DBTable aTable;

	private String strColumnName;

	private String strJavaFieldName;

	private String strJavaFieldType;

	private int iColumnType;

	private String strColumnTypeName;

	// the column value; if the value is SQL NULL, the value returned is 0 else
	// the size
	private int iColumnSize;

	// the column value; 1 if Nullable 0 if not
	private int iNullable;

	private String iColumnDef;

	private boolean isPrimaryKeyPart = false;

	private boolean bAutoIncrement = false;

	private boolean enabled = true;

	private int id;

	private boolean idx1;

	private boolean indexU;

	private boolean unq;

	private boolean mUnq;

	private org.apache.torque.engine.database.model.Column torqueColumn;

	public DBColumn() {
	}

	public DBColumn(java.sql.DatabaseMetaData pdbMeta, DBTable paTable,
			String pstrColumnName, int piColumnType, String pstrColumnTypeName,
			int piColumnSize, int piNullable, String piColumnDef) {
		this.aTable = paTable;
		this.strColumnName = pstrColumnName;
		this.id = IdGeneratorSingleton.getId(getClass().getName(), aTable
				.getFQTableName());
		this.iColumnType = piColumnType;
		this.strJavaFieldType = Utilities.hmJDBCTypeToJavaType.get(
				new Integer(iColumnType)).toString();
		this.strColumnTypeName = pstrColumnTypeName;
		this.iColumnSize = piColumnSize;
		this.iNullable = piNullable;
		this.iColumnDef = piColumnDef;
		this.iColumnDef = piColumnDef;
		this.idx1 = false;
		this.indexU = false;
		this.unq = false;
		this.mUnq = false;
		if (paTable.getTorqueTable() != null) {
			this.torqueColumn = (paTable.getTorqueTable())
					.getColumn(pstrColumnName);
		} else {
			this.torqueColumn = null;
		}
		if (this.torqueColumn != null) {
			this.strJavaFieldName = this.torqueColumn.getJavaName();
		} else {
			this.strJavaFieldName = Namer.nameField(this.strColumnName);
		}
	}

	public int getId() {
		return (this.id);
	}

	public boolean getAutoIncrement() {
		return (this.bAutoIncrement);
	}

	public void setAutoIncrement(boolean b) {
		this.bAutoIncrement = b;
	}

	public boolean isEnabled() {
		return (this.enabled);
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public int getColumnType() {
		return (this.iColumnType);
	}

	public String getJavaFieldName() {
		return (this.strJavaFieldName);
	}

	public void setJavaFieldName(String s) {
		this.strJavaFieldName = s;
	}

	public String getJavaFieldType() {
		return (this.strJavaFieldType);
	}

	public void setJavaFieldType(String s) {
		this.strJavaFieldType = s;
	}

	public void setColumnType(int i) {
		this.iColumnType = i;
		setColumnTypeName(Utilities.getTypeNameFromJDBCType(this.iColumnType));
	}

	public void setColumnType(String s) {
		Integer i = (Integer) Utilities.mJDBCNameToType.get(s);
		if (i != null)
			this.iColumnType = i.intValue();
		setColumnTypeName(Utilities.getTypeNameFromJDBCType(this.iColumnType));
	}

	public String getColumnTypeName() {
		return (this.strColumnTypeName);
	}

	public void setColumnTypeName(String s) {
		this.strColumnTypeName = s;
	}

	public int getColumnSize() {
		return (this.iColumnSize);
	}

	public void setColumnSize(int piColumnSize) {
		this.iColumnSize = piColumnSize;
	}

	public int getNullable() {
		return (this.iNullable);
	}

	public void setNullable(int piNullable) {
		this.iNullable = piNullable;
	}

	public DBTable getDBTable() {
		return (this.aTable);
	}

	public boolean isTreeEnabled() {
		return this.aTable.isTreeEnabled() && this.isEnabled();
	}

	public void read() throws java.sql.SQLException {
	}

	public void generateReferences() throws java.sql.SQLException {
	}

	public void setPrimaryKeyPart(boolean b) {
		this.isPrimaryKeyPart = b;
	}

	public boolean isPrimaryKeyPart() {
		return (this.isPrimaryKeyPart);
	}

	public String getColumnName() {
		return (this.strColumnName);
	}

	public void setColumnName(String columnName) {
		this.strColumnName = columnName;
		// FIXME replac by torque model info or namer result
		// this.strJavaFieldName = Namer.nameField(this.strColumnName);
	}

	public void setTorqueColumn(
			org.apache.torque.engine.database.model.Column torqueColumn) {
		this.torqueColumn = torqueColumn;
		return;
	}

	public org.apache.torque.engine.database.model.Column getTorqueColumn() {
		return (this.torqueColumn);
	}

	public Enumeration children() {
		return null;
	}

	public boolean getAllowsChildren() {
		return (false);
	}

	public TreeNode getChildAt(int param) {
		return (null);
	}

	public int getChildCount() {
		return (0);
	}

	public int getIndex(TreeNode treenode) {
		return (0);
	}

	public TreeNode getParent() {
		return (this.aTable);
	}

	public boolean isLeaf() {
		return (true);
	}

	public String toString() {
		if (this.isPrimaryKeyPart)
			return (strColumnName + " (PK)");

		return (this.strColumnName);
	}

	public String getXML() {
		if (this.isTreeEnabled()) {
			java.io.StringWriter sw = new java.io.StringWriter();
			writeXML(new java.io.PrintWriter(sw));
			return sw.getBuffer().toString();
		} else
			return "";

	}

	public void writeXML(java.io.PrintWriter pw) {
		String columnXml = "	<field-descriptor ";
		columnXml = columnXml + " name=\"" + this.strJavaFieldName + "\"";
		columnXml = columnXml + " column=\"" + this.strColumnName.toUpperCase()
				+ "\"";
		columnXml = columnXml + " jdbc-type=\""
				+ Utilities.hmJDBCTypeToName.get(new Integer(this.iColumnType))
				+ "\"";
		// timestamp must be readonly and nullable flag is not required
		if (Utilities.hmJDBCTypeToName.get(new Integer(this.iColumnType))
				.equals("TIMESTAMP")) {
			columnXml = columnXml + " access=\"readonly\"";
		} else if (this.getNullable() == 0) {
			columnXml = columnXml + " nullable=\"false\"";
		}
		if (this.isPrimaryKeyPart())
			columnXml = columnXml + " primarykey=\"true\"";
		if (this.getAutoIncrement())
			columnXml = columnXml + " <autoincrement>true</autoincrement>";
		columnXml = columnXml + "/>";
		pw.println(columnXml);
	}

	public void generateJava(File aFile, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		throw new UnsupportedOperationException(
				"Generate Java on DBColumn is not allowed");
	}

	public void setSPackage(String packageName) {
		throw new UnsupportedOperationException(
				"Set SPackage on DBColumn is not allowed");

	}

	public void setDPackage(String packageName) {
		throw new UnsupportedOperationException(
				"Set DPackage on DBColumn is not allowed");
	}

	public String getJavaStaticFields() {
		String strReturn = "";
		if (this.isTreeEnabled()) {
			String description = this.getDef();
			if (description != null) {
				strReturn += "	/** " + description.replace('&', '_') + " */\n";
			}
			strReturn += "	public static final String "
					+ this.getJavaFieldName().toUpperCase() + " = \""
					+ this.getJavaFieldName() + "\";";
		}
		return strReturn;
	}

	public String getJavaSizeFields() {
		String strReturn = "";
		if (this.isTreeEnabled()) {
			String colTypeName = this.getJavaFieldType();
			if ((colTypeName.toUpperCase()).equals("STRING")) {
				strReturn += "	public static final int "
						+ this.getJavaFieldName().toUpperCase() + "_SIZE = "
						+ getColumnSize() + ";";
			}
		}
		return strReturn;
	}

	public String getJavaFieldDefinition() {
		if (this.isTreeEnabled())
			return ("	public " + this.getJavaFieldType() + " "
					+ this.getJavaFieldName() + ";");
		else
			return ("");
	}

	// FIXME adapt....
	public String getDisplayFieldDefinition() {
		if (this.isTreeEnabled())
			return ("	private " + this.getJavaFieldType() + " "
					+ this.getJavaFieldName() + ";");
		else
			return ("");
	}

	public String getJavaGetterSetterDefinition() {
		if (this.isTreeEnabled()) {
			String strReturn = "";
			strReturn = "	public " + this.getJavaFieldType() + " get"
					+ this.getJavaFieldName().substring(0, 1).toUpperCase()
					+ this.getJavaFieldName().substring(1) + "()";

			strReturn += " {" + System.getProperty("line.separator");
			strReturn += "		return this." + this.getJavaFieldName() + ";"
					+ System.getProperty("line.separator");
			strReturn += "	}" + System.getProperty("line.separator");
			strReturn += "	public void set"
					+ this.getJavaFieldName().substring(0, 1).toUpperCase()
					+ this.getJavaFieldName().substring(1) + "("
					+ this.getJavaFieldType() + " param)";
			strReturn += " {" + System.getProperty("line.separator");
			strReturn += "		this." + this.getJavaFieldName() + " = param;"
					+ System.getProperty("line.separator");
			strReturn += "	}" + System.getProperty("line.separator");
			return strReturn;
		} else
			return "";
	}

	public String getXmlModelDefinition() {
		if (this.isTreeEnabled()) {
			String strReturn = "    <column name='" + this.getColumnName()
					+ "' javaName=\"" + this.getJavaFieldName() + "\"";
			strReturn += " primaryKey='" + this.isPrimaryKeyPart() + "'";
			if (this.getNullable() == 0)
				strReturn += " required='true'";
			else
				strReturn += " required='false'";
			String colTypeName = getColumnTypeName();
			if (colTypeName.equals("float4"))
				colTypeName = "float";
			if (colTypeName.equals("float8"))
				colTypeName = "double";
			if (colTypeName.equals("FLOAT4"))
				colTypeName = "FLOAT";
			if (colTypeName.equals("FLOAT8"))
				colTypeName = "DOUBLE";
			strReturn += " type='" + colTypeName.toUpperCase() + "'";
			strReturn += " size='" + getColumnSize() + "'";
			strReturn += " autoIncrement='" + this.getAutoIncrement() + "'";
			String description = this.getDef();
			if (description != null) {
				strReturn += " description=\"" + description.replace('&', '_')
						+ "\"";
			}
			strReturn += "/>";
			return strReturn;
		} else
			return "";
	}

	public void setIdx1(boolean b) {
		this.idx1 = b;

	}

	public void setIndexU(boolean isUnique) {
		this.indexU = isUnique;

	}

	public boolean isIdx1() {
		return this.idx1;
	}

	public boolean isIndexU() {
		return this.indexU;
	}

	public void setUnq(boolean b) {
		this.unq = b;
	}

	public void setMUnq(boolean b) {
		this.mUnq = b;
	}

	public boolean isUnq() {
		return this.unq;
	}

	public boolean isMUnq() {
		return this.mUnq;
	}

	public String getDef() {
		return this.iColumnDef;
	}

	public void setDef(String def) {
		iColumnDef = def;
	}
}
