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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.tree.TreeNode;

import org.apache.torque.engine.database.model.Column;
import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.MfgScmSetElement;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @author <a href="mailto:bfl@florianbruckner.com">Florian Bruckner </a>
 * @mfgscm <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBTable implements MetadataNodeInterface, TreeNode {

	private java.sql.DatabaseMetaData dbMeta;

	private DBSchema aSchema;

	private java.util.HashMap hmReferences = new java.util.HashMap(0);

	private java.util.HashMap hmCollections = new java.util.HashMap(0);

	private java.util.HashMap hmIndex = new java.util.HashMap(0);

	private HashMap hmIndexU = new java.util.HashMap(0);

	private java.util.TreeMap tmColumns = new java.util.TreeMap();

	private java.util.Vector vSubTreeNodes = new java.util.Vector();

	private String strTableName;

	private String sClassName;

	private String dClassName;

	// private String mClassName;

	private String sPackageName = ""; // In default package by default ;-)

	private String dPackageName = ""; // In default package by default ;-)

	// private String mPackageName = ""; // In default package by default ;-)

	private String strConversionStrategyClass = "";

	private boolean dynamicProxy = false;

	private boolean enabled = true;

	private int id;

	private Hashtable importedKeysList = null;

	private org.apache.torque.engine.database.model.Table torqueTable;

	private List torqueColumns = null;

	public DBTable() {
	}

	public DBTable(java.sql.DatabaseMetaData pdbMeta, DBSchema paSchema,
			String pstrTableName) {
		this.dbMeta = pdbMeta;
		this.strTableName = pstrTableName;
		if (paSchema.getTorqueDatabase() != null) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"paSchema.getTorqueDatabase() != null pstrTableName "
							+ pstrTableName);
			this.torqueTable = paSchema.getTorqueDatabase().getTable(
					pstrTableName);
			this.torqueColumns = this.torqueTable.getColumns();
			if (torqueColumns != null) {
				for (Iterator iter = torqueColumns.iterator(); iter.hasNext();) {
					Column col = (Column) iter.next();
				}
			} else {
				DbfLauncherPlugin.log(IStatus.INFO,
						"paSchema.getTorqueDatabase() torqueColumns=null "
								+ pstrTableName);
			}
		} else {
			this.torqueTable = null;
		}
		// this.sClassName = Namer.nameClass(this.strTableName);
		this.sClassName = this.torqueTable.getJavaName();
		this.dClassName = this.sClassName.concat("Display");
		// this.mClassName = this.sClassName.concat("Model");
		id = IdGeneratorSingleton.getId(getClass().getName(), getClass()
				.getName());
		this.aSchema = paSchema;
	}

	public boolean hasDynamicProxy() {
		return (dynamicProxy);
	}

	public TreeMap getColums() {
		return (this.tmColumns);
	}

	public HashMap getReferences() {
		return (this.hmReferences);
	}

	public HashMap getIndexes() {
		return (this.hmIndex);
	}

	public HashMap getCollection() {
		return (this.hmCollections);
	}

	public void setDynamicProxy(boolean b) {
		dynamicProxy = b;
	}

	public String getConversionStrategyClass() {
		return (this.strConversionStrategyClass);
	}

	public void setConversionStrategyClass(String s) {
		this.strConversionStrategyClass = s;
	}

	public boolean isEnabled() {
		return (this.enabled);
	}

	public void setEnabled(boolean b) {
		this.enabled = b;
	}

	public boolean isTreeEnabled() {
		return this.aSchema.isTreeEnabled() && this.isEnabled();
	}

	public DBColumn getColumn(String colName) {
		return ((DBColumn) tmColumns.get(colName));
	}

	public String getTableName() {
		return (strTableName);
	}

	public void setTableName(String tableName) {
		this.strTableName = tableName;
	}

	public String getFQTableName() {
		String strReturn = null;
		if (aSchema.getDBCatalog().getDBMeta()
				.getSupportsCatalogsInTableDefinitions()) {
			if (aSchema.getDBCatalog().getDBMeta().getIsCatalogAtStart())
				strReturn = aSchema.getDBCatalog().getCatalogName()
						+ aSchema.getDBCatalog().getDBMeta()
								.getCatalogSeparator()
						+ aSchema.getSchemaName() + "." + this.getTableName();

			else
				strReturn = strReturn = aSchema.getSchemaName()
						+ "."
						+ this.getTableName()
						+ aSchema.getDBCatalog().getDBMeta()
								.getCatalogSeparator()
						+ aSchema.getDBCatalog().getCatalogName();

		} else if (aSchema.getSchemaName() == null
				&& aSchema.getSchemaName().length() > 0)
			strReturn = aSchema.getSchemaName() + "." + this.getTableName();

		else
			strReturn = getTableName();

		return (strReturn);
	}

	public String getSClassName() {
		return (sClassName);
	}

	public void setSClassName(String s) {
		this.sClassName = s;
	}

	public String getDClassName() {
		return (dClassName);
	}

	public void setDClassName(String s) {
		this.dClassName = s;
	}

	/*
	 * public String getMClassName() { return (mClassName); }
	 * 
	 * public void setMClassName(String s) { this.mClassName = s; }
	 */
	public String getSPackageName() {
		return (sPackageName);
	}

	public void setSPackageName(String s) {
		this.sPackageName = s;
	}

	public String getDPackageName() {
		return (dPackageName);
	}

	public void setDPackageName(String s) {
		this.dPackageName = s;
	}

	/*
	 * public String getMPackageName() { return (mPackageName); }
	 * 
	 * public void setMPackageName(String s) { this.mPackageName = s; }
	 */
	public String getFQClassName() {
		if (this.getSPackageName() != null
				&& this.getSPackageName().trim().length() > 0)
			return this.getSPackageName() + "." + this.getSClassName();
		else
			return this.getSClassName();
	}

	public DBSchema getDBSchema() {
		return (this.aSchema);
	}

	public void read() throws SQLException {
	}

	public DBColumn addColumn(String strColumnName, int iDataType,
			String strTypeName, int iColumnSize, int iNullable,
			String iColumnDef) {
		DbfLauncherPlugin.log(IStatus.INFO, "DBTable addColumn "
				+ strColumnName + " iDataType " + iDataType + " strTypeName "
				+ strTypeName + " iColumnSize " + iColumnSize + " iNullable "
				+ iNullable + " Def " + iColumnDef);
		DBColumn aDBColumn = new DBColumn(this.dbMeta, this, strColumnName,
				iDataType, strTypeName, iColumnSize, iNullable, iColumnDef);
		this.tmColumns.put(strColumnName, aDBColumn);
		return aDBColumn;
	}

	public void addPrimaryKeyColumn(String strColumnName) {
		DBColumn aDBColumn = this.getColumn(strColumnName);
		if (aDBColumn != null) {
			aDBColumn.setPrimaryKeyPart(true);
		}
	}

	public void changeColumn(String strColumnName, DBColumn changedObject) {
		this.tmColumns.remove(strColumnName);
		this.tmColumns.put(changedObject.getColumnName(), changedObject);
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBTable changeColumn strColumnName " + strColumnName + " new "
						+ changedObject.getColumnName());
	}

	public void removeColumn(String strColumnName) {
		this.tmColumns.remove(strColumnName);
	}

	/**
	 * Determines the indices for the indicated table.
	 * 
	 * @return no
	 */
	private void getIndexInfo() throws SQLException {
		ResultSet indexData = null;
		try {
			indexData = dbMeta.getIndexInfo(this.aSchema.getDBCatalog()
					.getCatalogName(), this.aSchema.getSchemaName(),
					strTableName, false, false);

			Set availableColumns = determineAvailableColumns(indexData);

			while (indexData.next()) {
				String strColumnName = getValueAsString(indexData,
						"COLUMN_NAME", availableColumns, null);
				String indexName = getValueAsString(indexData, "INDEX_NAME",
						availableColumns, null);
				boolean isUnique = !getValueAsBoolean(indexData, "NON_UNIQUE",
						availableColumns, true);
				DBColumn dbcol = (DBColumn) tmColumns.get(strColumnName);
				if (dbcol != null) {
					dbcol.setIdx1(true);
					dbcol.setIndexU(isUnique);
				}
				Set colSet;
				if (hmIndex.containsKey(indexName)) {
					colSet = (Set) hmIndex.get(indexName);
				} else {
					colSet = new HashSet();
				}
				colSet.add(strColumnName);
				this.hmIndex.put(indexName, colSet);
				if (isUnique) {
					Set colSetU;
					if (hmIndexU.containsKey(indexName)) {
						colSetU = (Set) hmIndexU.get(indexName);
					} else {
						colSetU = new HashSet();
					}
					colSetU.add(strColumnName);
					this.hmIndexU.put(indexName, colSetU);
				}
			}
		} catch (SQLException ex) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"Could determine the indices for the table " + strTableName
							+ " " + ex);
		} finally {
			if (indexData != null) {
				indexData.close();
			}
		}
	}

	/**
	 * Determines the columns available in the given result set, and returns
	 * them (in upper case) in a set.
	 * 
	 * @param data
	 *            The result set
	 * @return The columns present in the result set
	 */
	private Set determineAvailableColumns(ResultSet data) throws SQLException {
		Set result = new HashSet();
		ResultSetMetaData metaData = data.getMetaData();

		for (int idx = 1; idx <= metaData.getColumnCount(); idx++) {
			result.add(metaData.getColumnName(idx).toUpperCase());
		}
		return result;
	}

	/**
	 * Retrieves the value of the specified column as a string. If the column is
	 * not present, then the default value is returned.
	 * 
	 * @param data
	 *            The data
	 * @param columnName
	 *            The name of the column
	 * @param availableColumns
	 *            The available columns
	 * @param defaultValue
	 *            The default value to use if the column is not present
	 * @return The value
	 */
	private String getValueAsString(ResultSet data, String columnName,
			Set availableColumns, String defaultValue) throws SQLException {
		return availableColumns.contains(columnName) ? data
				.getString(columnName) : defaultValue;
	}

	/**
	 * Retrieves the value of the specified column as a boolean value. If the
	 * column is not present, then the default value is returned.
	 * 
	 * @param data
	 *            The data
	 * @param columnName
	 *            The name of the column
	 * @param availableColumns
	 *            The available columns
	 * @param defaultValue
	 *            The default value to use if the column is not present
	 * @return The value
	 */
	private boolean getValueAsBoolean(ResultSet data, String columnName,
			Set availableColumns, boolean defaultValue) throws SQLException {
		return availableColumns.contains(columnName) ? data
				.getBoolean(columnName) : defaultValue;
	}

	public void generateReferences() throws SQLException {
		Iterator it = tmColumns.values().iterator();
		boolean hasNoPrimaryKey = true;
		while (hasNoPrimaryKey && it.hasNext())
			if (((DBColumn) it.next()).isPrimaryKeyPart())
				hasNoPrimaryKey = false;

		if (hasNoPrimaryKey)
			readPrimaryKeys();

		// FIXME stuck
		// Now generate References and Collections
		/*
		 * generateFKCollections(); generateFKReferences();
		 * System.out.println("aatmColumns");
		 * vSubTreeNodes.addAll(this.tmColumns.values());
		 * System.out.println("aatmCollection");
		 * vSubTreeNodes.addAll(this.hmCollections.values());
		 * System.out.println("aatmReference");
		 * vSubTreeNodes.addAll(this.hmReferences.values());
		 */
		getIndexInfo();
	}

	public Enumeration children() {
		return (vSubTreeNodes.elements());
	}

	public boolean getAllowsChildren() {
		return (true);
	}

	public TreeNode getChildAt(int i) {
		TreeNode treenode = (TreeNode) vSubTreeNodes.elementAt(i);
		return (treenode);
	}

	public int getChildCount() {
		return (this.vSubTreeNodes.size());
	}

	public int getIndex(TreeNode treenode) {
		return (this.vSubTreeNodes.indexOf(treenode));
	}

	public TreeNode getParent() {
		return (this.aSchema);
	}

	public boolean isLeaf() {
		if (this.vSubTreeNodes.size() == 0)
			return true;
		else
			return false;
	}

	public String toString() {
		return (this.strTableName);
	}

	private void readPrimaryKeys() throws SQLException {
		java.sql.ResultSet rs = null;
		int munq = 0;
		try {
			rs = dbMeta.getPrimaryKeys(this.aSchema.getDBCatalog()
					.getCatalogName(), this.aSchema.getSchemaName(),
					strTableName);
			/*
			 * while (rs.next()) { String strCatalogName =
			 * rs.getString("TABLE_CAT"); String strSchemaName =
			 * rs.getString("TABLE_SCHEM"); if ((strSchemaName == null &&
			 * this.aSchema.getSchemaName() == null || strSchemaName
			 * .equals(this.aSchema.getSchemaName())) && (strCatalogName == null &&
			 * this.aSchema.getDBCatalog().getCatalogName() == null ||
			 * strCatalogName .equals(this.aSchema.getDBCatalog()
			 * .getCatalogName()))) { String strColumnName =
			 * rs.getString("COLUMN_NAME"); String pkName =
			 * rs.getString("PK_NAME"); DBColumn dbcol = (DBColumn)
			 * tmColumns.get(strColumnName); if (dbcol != null)
			 * dbcol.setPrimaryKeyPart(true); } }
			 */
			Set availableColumns = determineAvailableColumns(rs);

			while (rs.next()) {
				String strColumnName = getValueAsString(rs, "COLUMN_NAME",
						availableColumns, null);
				String pkName = getValueAsString(rs, "PK_NAME",
						availableColumns, null);
				DBColumn dbcol = (DBColumn) tmColumns.get(strColumnName);
				if (dbcol != null) {
					dbcol.setPrimaryKeyPart(true);
					dbcol.setUnq(true);
					if (munq++ > 0)
						dbcol.setMUnq(true);
				}
			}
		} catch (SQLException sqlEx) {
			// Ignore excpetions here.
		} finally {
			try {
				rs.close();
			} catch (Throwable t) {
			} // Ignore this exception
		}
	}

	private void generateImportedKeys() throws SQLException {
		java.sql.ResultSet importedKeys = null;
		try {
			// System.out.println("generateImportedKeys getImportedKeys");
			importedKeys = dbMeta.getImportedKeys(this.getDBSchema()
					.getDBCatalog().getCatalogName(), this.getDBSchema()
					.getSchemaName(), strTableName);
			String strPKTableName;
			String strPKColumnName;
			String strFKColumnName;
			while (importedKeys.next()) {
				strPKTableName = importedKeys.getString("PKTABLE_NAME");
				strPKColumnName = importedKeys.getString("PKCOLUMN_NAME");
				strFKColumnName = importedKeys.getString("FKCOLUMN_NAME");
				// System.out.println("generateImportedKeys : " +
				// strFKColumnName + " strPKTableName " + strPKTableName + "
				// strPKColumnName " + strPKColumnName);
				importedKeysList.put((String) strFKColumnName,
						new MfgScmSetElement(strPKTableName, strPKColumnName));
			}
		} catch (SQLException sqlEx) {
			DbfLauncherPlugin.log(sqlEx);
		}
		try {
			importedKeys.close();
		} catch (Throwable t) {
		}
	}

	public Hashtable getImportedKeysList() throws SQLException {
		if (importedKeysList == null)
			;
		{
			importedKeysList = new Hashtable();
			// System.out.println("GENERATEFK");
			generateImportedKeys();
			// System.out.println("GENERATECOLL");
		}
		return importedKeysList;
	}

	private void generateFKReferences() throws SQLException {
		java.sql.ResultSet rs = null;
		try {
			rs = dbMeta.getImportedKeys(this.getDBSchema().getDBCatalog()
					.getCatalogName(), this.getDBSchema().getSchemaName(),
					strTableName);
			while (rs.next()) {
				String strFKSchemaName = rs.getString("FKTABLE_SCHEM");
				String strFKCatalogName = rs.getString("FKTABLE_CAT");

				if ((strFKCatalogName == null
						&& this.aSchema.getDBCatalog().getCatalogName() == null || strFKCatalogName
						.equals(this.aSchema.getDBCatalog().getCatalogName()))
						&& (strFKSchemaName == null
								&& this.aSchema.getSchemaName() == null || strFKSchemaName
								.equals(this.aSchema.getSchemaName()))) {
					String strPKCatalogName = rs.getString("PKTABLE_CAT");
					String strPKSchemaName = rs.getString("PKTABLE_SCHEM");
					String strPKTableName = rs.getString("PKTABLE_NAME");
					String strPKColumnName = rs.getString("PKCOLUMN_NAME");
					String strFKTableName = rs.getString("FKTABLE_NAME");
					String strFKColumnName = rs.getString("FKCOLUMN_NAME");

					// Resolove the primaryKey column
					DBCatalog dbPKCatalog = this.aSchema.getDBCatalog()
							.getDBMeta().getCatalog(strPKCatalogName);
					if (dbPKCatalog != null) {
						DBSchema dbPKSchem = dbPKCatalog
								.getSchema(strPKSchemaName);
						if (dbPKSchem != null) {
							DBTable dbPKTable = dbPKSchem
									.getTable(strPKTableName);
							if (dbPKTable != null) {
								DBColumn dbPKColumn = dbPKTable
										.getColumn(strPKColumnName);
								// The FK column is always from this table.
								DBColumn dbFKColumn = getColumn(strFKColumnName);

								// Now retrieve the FKReference to this table
								// from the collection
								DBFKRelation aFKRef = (DBFKRelation) this.hmReferences
										.get(dbPKSchem.getSchemaName() + "."
												+ dbPKTable.getTableName());
								if (aFKRef == null) {
									aFKRef = new DBFKRelation(dbPKTable, this,
											false);
									this.hmReferences.put(dbPKSchem
											.getSchemaName()
											+ "." + dbPKTable.getTableName(),
											aFKRef);
								}
								aFKRef.addColumnPair(dbPKColumn, dbFKColumn);
							}
						}
					}
				}
			}
		} catch (SQLException sqlEx) {
			DbfLauncherPlugin.log(sqlEx);
		}
		try {
			rs.close();
		} catch (Throwable t) {
		}
	}

	private void generateFKCollections() throws SQLException {
		java.sql.ResultSet rs = null;
		try {
			rs = dbMeta.getExportedKeys(this.getDBSchema().getDBCatalog()
					.getCatalogName(), this.getDBSchema().getSchemaName(),
					strTableName);
			while (rs.next()) {
				String strPKSchemaName = rs.getString("PKTABLE_SCHEM");
				String strPKCatalogName = rs.getString("PKTABLE_CAT");

				if ((strPKSchemaName == null
						&& this.aSchema.getSchemaName() == null || strPKSchemaName
						.equals(this.aSchema.getSchemaName()))
						&& (strPKCatalogName == null
								&& this.aSchema.getDBCatalog().getCatalogName() == null || strPKCatalogName
								.equals(this.aSchema.getDBCatalog()
										.getCatalogName()))) {
					String strPKTableName = rs.getString("PKTABLE_NAME");
					String strPKColumnName = rs.getString("PKCOLUMN_NAME");
					String strFKCatalogName = rs.getString("FKTABLE_CAT");
					String strFKTableName = rs.getString("FKTABLE_NAME");
					String strFKColumnName = rs.getString("FKCOLUMN_NAME");
					String strFKSchemaName = rs.getString("FKTABLE_SCHEM");

					// Resolve the FK column. If catalog is supported in the
					// index
					// definition, resolve the catalog of the FK column,
					// otherwise
					// assume the current catalog (Note: This is needed for
					// Informix,
					// because the driver reports null for the catalog in this
					// case.
					DBCatalog dbFKCatalog = null;
					if (this.aSchema.getDBCatalog().getDBMeta()
							.getSupportsCatalogsInIndexDefinitions()) {
						dbFKCatalog = this.aSchema.getDBCatalog().getDBMeta()
								.getCatalog(strFKCatalogName);
					} else {
						dbFKCatalog = this.aSchema.getDBCatalog();
					}
					if (dbFKCatalog != null) {
						DBSchema dbFKSchema = dbFKCatalog
								.getSchema(strFKSchemaName);
						if (dbFKSchema != null) {
							DBTable dbFKTable = dbFKSchema
									.getTable(strFKTableName);
							if (dbFKTable != null) {
								DBColumn dbFKColumn = dbFKTable
										.getColumn(strFKColumnName);
								// The PK column is always from this table
								DBColumn dbPKColumn = getColumn(strPKColumnName);
								// Retrieve the FK Reference for the FK Table
								DBFKRelation aFKRef = (DBFKRelation) this.hmCollections
										.get(dbFKSchema.getSchemaName() + "."
												+ dbFKTable.getTableName());
								if (aFKRef == null) {
									aFKRef = new DBFKRelation(this, dbFKTable,
											true);
									this.hmCollections.put(dbFKSchema
											.getSchemaName()
											+ "." + dbFKTable.getTableName(),
											aFKRef);
								}
								aFKRef.addColumnPair(dbPKColumn, dbFKColumn);
							}
						}
					}
				}
			}
		} catch (SQLException sqlEx) {
			DbfLauncherPlugin.log(sqlEx);
		}
		try {
			rs.close();
		} catch (Throwable t) {
		}
	}

	public String getXML() {
		java.io.StringWriter sw = new java.io.StringWriter();
		writeXML(new java.io.PrintWriter(sw));
		return sw.getBuffer().toString();
	}

	public void writeXML(java.io.PrintWriter pw) {
		// Only generate a Classdescriptor if this table is enabled
		if (this.isTreeEnabled()) {
			java.util.Iterator it = tmColumns.values().iterator();
			if (it.hasNext()) {
				// Generate the class descriptor
				String tableXml = "<class-descriptor";
				tableXml = tableXml + " class=\"" + this.getFQClassName()
						+ "\"";
				tableXml = tableXml + " table=\"" + this.getFQTableName()
						+ "\">";
				if (this.hasDynamicProxy())
					tableXml = tableXml + " <class.proxy>dynamic</class.proxy>";
				if (this.getConversionStrategyClass() != null
						&& this.getConversionStrategyClass().trim().length() > 0)
					tableXml = tableXml + " <conversionStrategy>"
							+ this.getConversionStrategyClass()
							+ "</conversionStrategy>";

				pw.println(tableXml);
				while (it.hasNext()) {
					((DBColumn) it.next()).writeXML(pw);
				}

				// Generate references
				it = this.hmReferences.values().iterator();
				while (it.hasNext()) {
					((DBFKRelation) it.next()).writeXML(pw);
				}
				// Generate collections
				it = this.hmCollections.values().iterator();
				while (it.hasNext()) {
					((DBFKRelation) it.next()).writeXML(pw);
				}
				pw.println("</class-descriptor>");
			}
		}
	}

	public void generateJava(File aFile, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		if (isTreeEnabled()) {
			String dirName = getSPackageName().replace('.', File.separatorChar);
			File packageDir;
			if (getSPackageName() != null
					&& getSPackageName().trim().length() > 0)
				packageDir = new File(aFile, dirName);

			else
				packageDir = aFile;

			if (!packageDir.exists())
				packageDir.mkdirs();

			File javaFile = new File(packageDir, new StringBuffer(String
					.valueOf(getSClassName())).append(".java").toString());
			if (!javaFile.exists())
				javaFile.createNewFile();

			PrintWriter pw = new PrintWriter(new FileOutputStream(javaFile));
			pw.println(strHeader);
			pw.println("// Generated by compiere MFG + SCM");
			pw.println("// www.compiere-mfg.org info@compiere-mfg.org");
			pw.println();
			if (this.getSPackageName().trim().length() > 0) {
				pw.println("package " + this.getSPackageName() + ";");
				pw.println();
			}
			pw.println();
			// Generate Imports
			printImports(pw, properties.getProperty("import", ""));
			pw.println();
			// Generate Class Declaration
			pw.println("public class " + getSClassName() + " "
					+ getExtends(properties.getProperty("extends", "")) + " "
					+ getImplements(properties.getProperty("implements", "")));
			pw.println("{");

			// Generate Static Fields
			java.util.Iterator it = this.tmColumns.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBColumn) it.next()).getJavaStaticFields());
				pw.println();
			}

			pw
					.println("	// ---------------------------------------------------------------------------");
			pw.println();

			// Generate Size Fields
			java.util.Iterator it1 = this.tmColumns.values().iterator();
			while (it1.hasNext()) {
				String sizeStr = ((DBColumn) it1.next()).getJavaSizeFields();
				if (sizeStr != null && sizeStr != "") {
					pw.println(sizeStr);
					pw.println();
				}
			}

			pw
					.println("	// ---------------------------------------------------------------------------");
			pw.println();

			// Generate Fields
			java.util.Iterator it2 = this.tmColumns.values().iterator();
			while (it2.hasNext()) {
				pw.println(((DBColumn) it2.next()).getJavaFieldDefinition());
				pw.println();
			}

			it = this.hmReferences.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBFKRelation) it.next()).getJavaFieldDefinition());
				pw.println();
			}

			it = this.hmCollections.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBFKRelation) it.next()).getJavaFieldDefinition());
				pw.println();
			}

			// Generate Constructors
			String includeDirName = properties.getProperty(
					"includeRepositoryPath", "");
			File includeDir = new File(includeDirName);

			if (!includeDir.exists())
				includeDir.mkdirs();
			File f = new File(includeDir, new StringBuffer(String
					.valueOf(getSClassName())).append(".ext").toString());
			if (f.exists()) {
				pw
						.println("	// ---------------------------------------------------------------------------");
				pw.println();
				BufferedReader in = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					pw.println(inputLine);
				}
				pw.println();

				in.close();
			}
			// Generate Getter/Setter
			it = this.tmColumns.values().iterator();
			while (it.hasNext()) {
				pw
						.println("	// ---------------------------------------------------------------------------");
				pw.println();
				pw.println(((DBColumn) it.next())
						.getJavaGetterSetterDefinition());
			}

			it = this.hmReferences.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBFKRelation) it.next())
						.getJavaGetterSetterDefinition());
				pw.println();
			}

			it = this.hmCollections.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBFKRelation) it.next())
						.getJavaGetterSetterDefinition());
				pw.println();
			}

			pw.println("}");
			pw.println(strFooter);
			pw.close();
		}
	}

	public void generateDisplay(File aFile, Properties properties,
			String strHeader, String strFooter) throws IOException,
			FileNotFoundException {
		if (isTreeEnabled()) {
			// System.out.println("generateDisplay");
			String dirName = getDPackageName().replace('.', File.separatorChar);
			// System.out.println("generateDisplay dirName " + dirName);
			File packageDir;
			if (getDPackageName() != null
					&& getDPackageName().trim().length() > 0)
				packageDir = new File(aFile, dirName);

			else
				packageDir = aFile;

			if (!packageDir.exists())
				packageDir.mkdirs();

			File javaFile = new File(packageDir, new StringBuffer(String
					.valueOf(getDClassName())).append(".java").toString());
			if (!javaFile.exists())
				javaFile.createNewFile();

			PrintWriter pw = new PrintWriter(new FileOutputStream(javaFile));
			pw.println(strHeader);
			pw.println("// Generated by compiere MFG + SCM");
			pw.println("// www.compiere-mfg.org info@compiere-mfg.org");
			pw.println();
			if (this.getDPackageName().trim().length() > 0) {
				pw.println("package " + this.getDPackageName() + ";");
				pw.println();
			}
			pw.println();
			// Generate Imports
			printImports(pw, properties.getProperty("displayimport", ""));
			pw.println();
			// Generate Class Declaration
			pw.println("public class "
					+ getDClassName()
					+ " "
					+ getExtends(properties.getProperty("displayextends", ""))
					+ " "
					+ getImplements(properties.getProperty("displayimplements",
							"")));
			pw.println("{");

			// Generate Fields
			pw.println("	private Section section = null;");
			pw.println("");
			pw.println("	private Composite composite = null;");
			pw.println("	// New Enreg in which we will store this object");
			pw.println("	private Object newRecord = null;");

			java.util.Iterator it = this.tmColumns.values().iterator();
			while (it.hasNext()) {
				DBColumn dbColumn = (DBColumn) it.next();
				pw.println(dbColumn.getDisplayFieldDefinition());
				pw.println();
			}

			/*
			 * it = this.hmReferences.values().iterator(); while (it.hasNext()) {
			 * pw.println(((DBFKRelation) it.next()).getJavaFieldDefinition());
			 * pw.println(); }
			 * 
			 * it = this.hmCollections.values().iterator(); while (it.hasNext()) {
			 * pw.println(((DBFKRelation) it.next()).getJavaFieldDefinition());
			 * pw.println(); }
			 */

			// Generate Constructor
			pw.println("public " + getDClassName()
					+ "(Composite parent, int style) {");
			pw.println("	super(parent, style);");
			pw.println("	initialize();");
			pw.println("}");

			// Generate initialize method
			pw.println("	/**");
			pw.println("	 * This method initializes this");
			pw.println("	 * ");
			pw.println("	 */");
			pw.println("	private void initialize() {");
			pw.println("        this.setText(\"My form title\");");
			pw.println("        this.setLayout(new GridLayout());");
			pw.println("");
			pw.println("        createSection();");
			pw.println("");
			pw.println("	}");

			// Generate Getter/Setter
			it = this.tmColumns.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBColumn) it.next())
						.getJavaGetterSetterDefinition());
				pw.println();
			}

			it = this.hmReferences.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBFKRelation) it.next())
						.getJavaGetterSetterDefinition());
				pw.println();
			}

			it = this.hmCollections.values().iterator();
			while (it.hasNext()) {
				pw.println(((DBFKRelation) it.next())
						.getJavaGetterSetterDefinition());
				pw.println();
			}

			pw.println("}");
			pw.println(strFooter);
			pw.close();
		}
	}

	public void generateXmlModel(java.io.PrintWriter pw) {
		if (isTreeEnabled()) {
			java.util.Iterator it = tmColumns.values().iterator();
			pw.println("  <table name='" + getTableName() + "' javaName=\""
					+ torqueTable.getJavaName() + "\">");
			if (it.hasNext()) {
				// Generate Columns
				while (it.hasNext()) {
					pw.println(((DBColumn) it.next()).getXmlModelDefinition());
					// pw.println();
				}
				// Generate indexes
				Iterator names = this.hmIndex.keySet().iterator();
				String iname;
				String cname;
				while (names.hasNext()) {
					iname = (String) names.next();
					pw.println("  <index name='" + iname + "'>");
					Set colSet = (Set) hmIndex.get(iname);
					Iterator cols = colSet.iterator();
					while (cols.hasNext()) {
						cname = (String) cols.next();
						pw.println("  <index-column name='" + cname + "'/>");
					}
					pw.println("   </index>");
				}
				// Generate unique indexes
				Iterator namesU = this.hmIndexU.keySet().iterator();
				while (namesU.hasNext()) {
					iname = (String) namesU.next();
					pw.println("  <unique name='" + iname + "'>");
					Set colSetU = (Set) hmIndexU.get(iname);
					Iterator cols = colSetU.iterator();
					while (cols.hasNext()) {
						cname = (String) cols.next();
						pw.println("  <unique-column name='" + cname + "'/>");
					}
					pw.println("   </unique>");
				}
				/*
				 * FIXME see if required // Generate references it =
				 * this.hmReferences.values().iterator(); while (it.hasNext()) {
				 * pw.println(((DBFKRelation) it.next())
				 * .getXmlModelDefinition()); pw.println(); } // Generate
				 * collections it = this.hmCollections.values().iterator();
				 * while (it.hasNext()) { pw.println(((DBFKRelation) it.next())
				 * .getXmlModelDefinition()); pw.println(); }
				 */
			}
			pw.println("  </table>");
		}
	}

	public void setTorqueTable(
			org.apache.torque.engine.database.model.Table torqueTable) {
		this.torqueTable = torqueTable;
		return;
	}

	public org.apache.torque.engine.database.model.Table getTorqueTable() {
		return (this.torqueTable);
	}

	public void setSPackage(String packageName) {
		setSPackageName(packageName);
		return;
	}

	public void setDPackage(String packageName) {
		setDPackageName(packageName);
		return;
	}

	public int getId() {
		return (id);
	}

	private void printImports(PrintWriter printwriter, String strImports) {
		if (strImports == null)
			return;

		if (strImports.length() < 1)
			return;

		String[] Imports = strImports.split(",");
		if (Imports.length > 0) {
			for (int i = 0; i < Imports.length; i++) {
				printwriter.println("import " + Imports[i] + ";");
			}
		}
	}

	private String getImplements(String string) {
		if (string == null)
			return ("");

		if (string.length() < 1)
			return ("");

		return ("implements " + string);
	}

	private String getExtends(String string) {
		if (string == null)
			return ("");

		if (string.length() < 1)
			return ("");

		return ("extends " + string);
	}

	private String getPackage(String string) {
		if (string == null)
			return ("");

		if (string.length() < 1)
			return ("");

		return ("package " + string + ";");
	}

	/*
	 * private Vector getFormElements() { Vector vector = new Vector();
	 * MfgScmSetElement mfgScmSetElement; Field field; Method method; treeObject =
	 * (TreeObject) page.getTreeObject(); //FIXME adapt this.clazz =
	 * MFG_Plant.class; this.parentClazz = MFG_WorkCenter.class; Name =
	 * treeObject.getName(); //FIXME control if (parentClazz != null) {
	 * Collection tmpList = null; try { tmpList = (Collection)
	 * mfgPersistenceBroker .mfgGetCollectionByQuery("name," + Name,
	 * parentClazz, -1, 12800); } catch (RemoteException e) {
	 * RioLauncherPlugin.log(e); } RioLauncherPlugin.log(IStatus.INFO,
	 * "MfgScmFormSection createMfgTree"); for (Iterator it1 =
	 * tmpList.iterator(); it1.hasNext();) { parentRecord = it1.next(); } if
	 * (treeObject instanceof TreePlant) { parentId = ((MFG_Plant)
	 * parentRecord).getId(); } else if (treeObject instanceof TreeLine) {
	 * parentId = ((MFG_ProductionLine) parentRecord).getId(); } else if
	 * (treeObject instanceof TreeWorkCenter) { parentId = ((MFG_WorkCenter)
	 * parentRecord).getId(); } else if (treeObject instanceof TreeCapacity) {
	 * parentId = ((MFG_Capacity) parentRecord).getId(); } else if (treeObject
	 * instanceof TreeZone) { parentId = ((MGT_Zone) parentRecord).getId(); }
	 * ((TreeObject) treeObject).setId(parentId); ((TreeObject)
	 * treeObject).setRecord(parentRecord); } // Fill tableColumns from table
	 * object methods RioLauncherPlugin.log(IStatus.INFO, "MfgScmFormSection
	 * getFormElements getMethods Name " + Name); //FIXME ADAPTER TreeMap
	 * columns = getMethods(); try { Iterator it =
	 * tableColumns.values().iterator(); while (it.hasNext()) { column =
	 * (DBColumn) it.next(); String strColumnName = column.getColumnName();
	 * RioLauncherPlugin.log(IStatus.INFO, "MfgScmFormSection getFormElements
	 * strColumnName " + strColumnName); //FIXME Ajouter dans DBField
	 * isSequence, isPreset, isHidden //typeList doit se constituer d'apres la
	 * liste des Column ayant la propriete hidden //lie au champs State a
	 * analyser //FIXME rcuperer valeurs depuis druid????????????/
	 * pw.println("setSequence(false);"); pw.println("setPreset(false);");
	 * pw.println("setHidden(false);"); pw.println("setPrKey(false);");
	 * pw.println("setUnq(false);"); //FIXME INverser false true ???
	 * pw.println("setNotN(" + column.getNullable() + ");");
	 * pw.println("setMUnq(false);"); //FIXME Adapter : positioner setDef,
	 * setCommentsizes pw.println("setDef(\"Value of column \" + strColumnName + \"
	 * for table \" + Name);"); pw.println("setIdx1(false);");
	 * pw.println("setIndex_U(false);"); pw.println("setComment(\"Value of
	 * column \" + strColumnName + \" for table \" + Name);");
	 * pw.println("setAutoIncrement(" + column.getAutoIncrement() + ");");
	 * pw.println("setEnabled(" + column.isEnabled() + ");"); //voir comment ca
	 * se passe si pas def dans la base getColumnSize donne 0 ???
	 * pw.println("setColumnSize(" + column.getColumnSize() + ");");
	 * pw.println("setColumnTypeName(\"" + column.getColumnTypeName() + "\");");
	 * pw.println("setJavaFieldName(\"" + column.getJavaFieldName() + "\");");
	 * pw.println("setJavaFieldType(" + column.getJavaFieldType() + ");");
	 * RioLauncherPlugin.log(IStatus.INFO, "MfgScmFormSection getFormElements
	 * Value of column " + strTypeName + " " + strColumnName + " for table " +
	 * Name); if (importedKeysList.containsKey(strColumnName)) { //ajout
	 * setRefTable(refTable) et setRefField(refField) pour les foreignKey
	 * RioLauncherPlugin.log(IStatus.INFO, "MfgScmFormSection getFormElements
	 * Combo : " + strColumnName); mfgScmSetElement = (MfgScmSetElement)
	 * importedKeysList .get(strColumnName);
	 * pw.println("setRefTable(mfgScmSetElement.getTableName());");
	 * RioLauncherPlugin.log(IStatus.INFO, "MfgScmFormSection getFormElements
	 * strPKTableName : " + strPKTableName);
	 * pw.println("setRefField(mfgScmSetElement.getColumnName());");
	 * RioLauncherPlugin.log(IStatus.INFO, "MfgScmFormSection getFormElements
	 * strPKColumnName : " + strPKColumnName);
	 * pw.println("setOnDelete(false);"); pw.println("setOnUpdate(false);"); }
	 * else { } RioLauncherPlugin.log(IStatus.ERROR, "MfgScmFormSection add : " +
	 * strColumnName); } } catch (Exception exception) {
	 * RioLauncherPlugin.log(IStatus.ERROR, "MfgScmFormSection Error: " +
	 * exception.getMessage()); } return (vector); }
	 */
}
