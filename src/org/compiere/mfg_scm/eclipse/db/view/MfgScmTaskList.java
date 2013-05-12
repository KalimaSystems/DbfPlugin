/*
 * ====================================================================
 * Copyright 2001-2005 Compiere MFG + SCM.
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

package org.compiere.mfg_scm.eclipse.db.view;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import org.compiere.mfg_scm.eclipse.db.dbInterface.DBColumn;
import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeAttribut;
import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeObject;
import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeTable;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Class that plays the role of the domain model in the TableViewerMfgScm In
 * real life, this class would access a persistent store of some kind.
 * 
 */

public class MfgScmTaskList {

	private Vector tasks;

	private Set changeListeners = new HashSet();

	private TreeTable treetable;

	private TreeObject[] treeColumns;

	// Combo box choices; Supporting all types described in torgue dtd!
	static final String[] TYPES_ARRAY = { "?", "BIT", "TINYINT", "SMALLINT",
			"INTEGER", "BIGINT", "FLOAT", "REAL", "NUMERIC", "DECIMAL", "CHAR",
			"VARCHAR", "LONGVARCHAR", "DATE", "TIME", "TIMESTAMP", "BINARY",
			"VARBINARY", "LONGVARBINARY", "NULL", "OTHER", "JAVA_OBJECT",
			"DISTINCT", "STRUCT", "ARRAY", "BLOB", "CLOB", "REF", "BOOLEANINT",
			"BOOLEANCHAR", "DOUBLE" };

	/**
	 * Constructor
	 */
	public MfgScmTaskList() {
		super();
		tasks = new Vector();
	}

	/*
	 * Initialize the table data. Create COUNT tasks and add them them to the
	 * collection of tasks
	 */
	private void initData() {
		MfgScmTask task;
		DBColumn dbColumn;
		// Generate Fields
		tasks = new Vector();
		// while (it.hasNext()) {
		for (int i = 0; i < treeColumns.length; i++) {
			/*
			 * FIXME pw.println(((DBColumn)
			 * it.next()).getJavaFieldDefinition()); pw.println();
			 */
			TreeAttribut treeColumn = (TreeAttribut) treeColumns[i];
			dbColumn = (DBColumn) treeColumn.getColumn();
			task = new MfgScmTask("Task ");
			task.setTreeColumn(treeColumn);
			task.setName(dbColumn.getColumnName());
			task.setType(dbColumn.getColumnTypeName());
			task.setSize(dbColumn.getColumnSize());
			task.setPrKey(dbColumn.isPrimaryKeyPart());
			if (dbColumn.getNullable() == 0)
				task.setNotNull(true);
			else
				task.setNotNull(false);
			task.setUnq(dbColumn.isUnq());
			task.setMUnq(dbColumn.isMUnq());
			task.setDef(dbColumn.getDef());
			task.setIdx1(dbColumn.isIdx1());
			task.setIndexU(dbColumn.isIndexU());
			tasks.add(task);
		}
	};

	/**
	 * Return the array of prkeys
	 */
	public String[] getTypes() {
		return TYPES_ARRAY;
	}

	/**
	 * Return the collection of tasks
	 */
	public Vector getTasks() {
		return tasks;
	}

	/**
	 * Add a new task to the collection of tasks
	 */
	public void addTask() {
		MfgScmTask task = new MfgScmTask("VARCHAR");
		task.setName("no_Name");
		TreeAttribut treeAttribut = (TreeAttribut) treetable.getTreeView()
				.doAddJob(treetable);
		task.setTreeColumn(treeAttribut);
		treeAttribut.getColumn().getColumnName();
		tasks.add(tasks.size(), task);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITaskListViewer) iterator.next()).addTask(task);
	}

	/**
	 * @param task
	 */
	public void removeTask(MfgScmTask task) {
		TreeAttribut treeAttribut = task.getTreeAttribut();
		treetable.getTreeView().doCut(treeAttribut);

		tasks.remove(task);
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITaskListViewer) iterator.next()).removeTask(task);
	}

	/**
	 * @param task
	 */
	public void taskChanged(MfgScmTask task) {
		Iterator iterator = changeListeners.iterator();
		while (iterator.hasNext())
			((ITaskListViewer) iterator.next()).updateTask(task);
	}

	/**
	 * @param viewer
	 */
	public void removeChangeListener(ITaskListViewer viewer) {
		changeListeners.remove(viewer);
	}

	/**
	 * @param viewer
	 */
	public void addChangeListener(ITaskListViewer viewer) {
		changeListeners.add(viewer);
	}

	public void setTreeTable(TreeTable treetable) {
		this.treetable = treetable;
		this.treeColumns = treetable.getChildren();
		this.initData();
	}

}
