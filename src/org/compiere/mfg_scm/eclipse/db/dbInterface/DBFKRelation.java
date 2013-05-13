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
import java.util.Iterator;
import java.util.Properties;

import javax.swing.tree.TreeNode;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBFKRelation implements MetadataNodeInterface, TreeNode {

	DBTable pkTable;

	DBTable fkTable;

	private boolean bAutoRetrieve = true;

	private boolean bAutoUpdate = false;

	private boolean bAutoDelete = false;

	private String strFieldName = null;

	private int id;

	private String strFieldType = null;

	private boolean enabled = true;

	boolean pkTableIsParent = true;

	java.util.ArrayList alColumnPairs = new java.util.ArrayList();

	public DBFKRelation(DBTable pPkTable, DBTable pFkTable,
			boolean ppkTableIsParent) {
		this.pkTable = pPkTable;
		this.fkTable = pFkTable;
		this.pkTableIsParent = ppkTableIsParent;
		if (this.pkTableIsParent) {
			this.strFieldName = "coll" + fkTable.getSClassName();
			this.strFieldType = "java.util.Vector";
			id = IdGeneratorSingleton.getId(getClass().getName(), pkTable
					.getFQTableName());
		} else {
			this.strFieldName = "a" + pkTable.getSClassName();
			id = IdGeneratorSingleton.getId(getClass().getName(), fkTable
					.getFQTableName());
		}
	}

	public boolean isEnabled() {
		return (this.enabled);
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public String getFieldName() {
		return (this.strFieldName);
	}

	public void setFieldName(String s) {
		this.strFieldName = s;
	}

	public String getFieldType() {
		if (this.isPkTableParent())
			return (this.strFieldType);
		else
			return (this.pkTable.getFQClassName());
	}

	public void setFieldType(String s) {
		if (!this.isPkTableParent())
			throw new UnsupportedOperationException(
					"Cannot set Field type on this type of relation");
		strFieldType = s;
	}

	public boolean isPkTableParent() {
		return (this.pkTableIsParent);
	}

	public void setAutoRetrieve(boolean b) {
		this.bAutoRetrieve = b;
	}

	public boolean getAutoRetrieve() {
		return (this.bAutoRetrieve);
	}

	public void setAutoUpdate(boolean b) {
		this.bAutoUpdate = b;
	}

	public boolean getAutoUpdate() {
		return (this.bAutoUpdate);
	}

	public void setAutoDelete(boolean b) {
		this.bAutoDelete = b;
	}

	public boolean getAutoDelete() {
		return (this.bAutoDelete);
	}

	public DBTable getFKTable() {
		return (fkTable);
	}

	public DBTable getPKTable() {
		return (pkTable);
	}

	public boolean isTreeEnabled() {
		if (this.pkTableIsParent)
			return this.pkTable.isTreeEnabled() && this.isEnabled();
		else
			return this.fkTable.isTreeEnabled() && this.isEnabled();
	}

	public void addColumnPair(DBColumn pPkColumn, DBColumn pFkColumn) {
		alColumnPairs.add(new Object[] { pPkColumn, pFkColumn });
	}

	public Iterator getColumnPairIterator() {
		return (alColumnPairs.iterator());
	}

	public Enumeration children() {
		if (pkTableIsParent)
			return (this.fkTable.children());
		else
			return (this.pkTable.children());
	}

	public boolean getAllowsChildren() {
		if (pkTableIsParent)
			return (this.fkTable.getAllowsChildren());
		else
			return (this.pkTable.getAllowsChildren());
	}

	public TreeNode getChildAt(int i) {
		if (pkTableIsParent)
			return (this.fkTable.getChildAt(i));
		else
			return (this.pkTable.getChildAt(i));
	}

	public int getChildCount() {
		if (pkTableIsParent)
			return (this.fkTable.getChildCount());
		else
			return (this.pkTable.getChildCount());
	}

	public int getIndex(TreeNode treenode) {
		if (pkTableIsParent)
			return (this.fkTable.getIndex(treenode));
		else
			return (this.pkTable.getIndex(treenode));
	}

	public TreeNode getParent() {
		if (pkTableIsParent)
			return (this.pkTable);
		else
			return (this.fkTable);
	}

	public boolean isLeaf() {
		if (pkTableIsParent)
			return (this.fkTable.isLeaf());
		else
			return (this.pkTable.isLeaf());
	}

	public String toString() {
		if (pkTableIsParent)
			return this.fkTable.toString() + " (Collection)";
		else
			return this.pkTable.toString() + " (Reference)";
	}

	private void writeXMLReference(java.io.PrintWriter pw) {
		pw.println("  <reference-descriptor");
		pw.println("    name=\"" + this.getFieldName() + "\"");
		pw.println("    class-ref=\"" + this.getPKTable().getFQClassName()
				+ "\"");
		pw.println("    auto-retrieve=\"" + this.getAutoRetrieve() + "\"");
		pw.println("    auto-update=\"" + this.getAutoUpdate() + "\"");
		pw.println("    auto-delete=\"" + this.getAutoDelete() + "\">");

		pw.print("    <foreignkey field-ref=\"");
		java.util.Iterator it = this.getColumnPairIterator();
		if (it.hasNext())
			pw.print(((DBColumn) ((Object[]) it.next())[1]).getJavaFieldName());
		pw.println("\" />");

		pw.println("  </reference-descriptor>");
	}

	private void writeXMLCollection(java.io.PrintWriter pw) {
		pw.println("  <collection-descriptor");
		pw.println("    name=\"" + this.getFieldName() + "\"");
		pw.println("    element-class-ref=\""
				+ this.getFKTable().getFQClassName() + "\"");
		pw.println("    auto-retrieve=\"" + this.getAutoRetrieve() + "\"");
		pw.println("    auto-update=\"" + this.getAutoUpdate() + "\"");
		pw.println("    auto-delete=\"" + this.getAutoDelete() + "\">");
		pw.print("    <inverse-foreignkey field-ref=\"");
		java.util.Iterator it = this.getColumnPairIterator();
		while (it.hasNext())
			pw.print(((DBColumn) ((Object[]) it.next())[1]).getJavaFieldName()
					+ " ");
		pw.println("\"    />");
		pw.println("  </collection-descriptor>");
	}

	public String getXML() {
		java.io.StringWriter sw = new java.io.StringWriter();
		writeXML(new java.io.PrintWriter(sw));
		return sw.getBuffer().toString();
	}

	public void writeXML(java.io.PrintWriter pw) {
		if (this.isPkTableParent()) {
			writeXMLCollection(pw);
		} else {
			writeXMLReference(pw);
		}
	}

	public void generateJava(File aFile, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		throw new UnsupportedOperationException(
				"Generate Java on DBFKReference is not allowed");
	}

	public void setSPackage(String packageName) {
		throw new UnsupportedOperationException(
				"Set SPackage on DBFKReference is not allowed");

	}

	public void setDPackage(String packageName) {
		throw new UnsupportedOperationException(
				"Set DPackage on DBFKReference is not allowed");

	}

	public String getJavaFieldDefinition() {
		if (this.isTreeEnabled() && this.getFKTable().isTreeEnabled()
				&& this.getPKTable().isTreeEnabled())
			return ("  private " + this.getFieldType() + " "
					+ this.getFieldName() + ";");
		else
			return ("");
	}

	public String getJavaGetterSetterDefinition() {
		if (this.isTreeEnabled() && this.getFKTable().isTreeEnabled()
				&& this.getPKTable().isTreeEnabled()) {
			String strReturn = "";
			strReturn = "  public " + this.getFieldType() + " get"
					+ this.getFieldName().substring(0, 1).toUpperCase()
					+ this.getFieldName().substring(1) + "()"
					+ System.getProperty("line.separator");

			strReturn += "  {" + System.getProperty("line.separator");
			strReturn += "     return this." + this.getFieldName() + ";"
					+ System.getProperty("line.separator");
			strReturn += "  }" + System.getProperty("line.separator");
			strReturn += "  public void set"
					+ this.getFieldName().substring(0, 1).toUpperCase()
					+ this.getFieldName().substring(1) + "("
					+ this.getFieldType() + " param)"
					+ System.getProperty("line.separator");
			strReturn += "  {" + System.getProperty("line.separator");
			strReturn += "    this." + this.getFieldName() + " = param;"
					+ System.getProperty("line.separator");
			strReturn += "  }" + System.getProperty("line.separator");
			return (strReturn);
		} else
			return ("");
	}

	public int getId() {
		return (this.id);
	}

	public void setId(int i) {
		this.id = i;
	}

	public Object getJavaModelDefinition() {
		// TODO Auto-generated method stub
		return null;
	}
}
