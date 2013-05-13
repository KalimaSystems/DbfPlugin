/*
 * ====================================================================
 * Copyright 2001-2013 Andre Charles Legendre <andre.legendre@kalimasystems.org>
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

import java.util.Arrays;

import org.compiere.mfg_scm.eclipse.db.dbInterface.DBTable;
import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeTable;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * The TableViewerMfgScm class is meant to be a fairly complete example of the
 * use of the org.eclipse.jface.viewers.TableViewer class to implement an
 * editable table with text, combobox and image editors.
 * 
 * The example application metaphor consists of a table to view and edit tasks
 * in a task list. It is by no means a complete or truly usable application.
 * 
 * This example draws from sample code in the Eclipse
 * org.eclipse.ui.views.tasklist.TaskList class and some sample code in SWT
 * fragments from the eclipse.org web site.
 * 
 * Known issue: We were not able to get the images to be center aligned in the
 * checkbox column.
 * 
 * @author Laurent Gauthier
 * @created Apr 2, 2003
 */

public class TableViewerMfgScm {
	/**
	 * @param parent
	 */
	public TableViewerMfgScm(Composite parent) {

		this.addChildControls(parent);
	}

	// private Shell shell;
	private Table table;

	private TableViewer tableViewer;

	private Button closeButton;

	private DBTable dbTable;

	private TreeTable treeTable;

	// Create a MfgScmTaskList and assign it to an instance variable
	private MfgScmTaskList taskList = new MfgScmTaskList();

	// Set the table column property names
	private final String NAME_COLUMN = "Name"; // Text

	private final String TYPE_COLUMN = "Type"; // Text + Combo

	private final String SIZE_COLUMN = "Size"; // Text wit digit only

	private final String PRKEY_COLUMN = "PrKey"; // CheckBox

	private final String UNQ_COLUMN = "Unq"; // CheckBox

	private final String NOTNULL_COLUMN = "NotN"; // CheckBox

	private final String MUNQ_COLUMN = "MUnq"; // CheckBox

	private final String DEF_COLUMN = "Def"; // Text

	private final String IDX1_COLUMN = "Idx1"; // CheckBox

	private final String INDEXU_COLUMN = "Index-U"; // CheckBox

	// Set column names
	private String[] columnNames = new String[] { NAME_COLUMN, TYPE_COLUMN,
			SIZE_COLUMN, PRKEY_COLUMN, UNQ_COLUMN, NOTNULL_COLUMN, MUNQ_COLUMN,
			DEF_COLUMN, IDX1_COLUMN, INDEXU_COLUMN };

	/**
	 * Main method to launch the window
	 */
	public static void main(String[] args) {

		Shell shell = new Shell();
		shell.setText("Task List - TableViewer MfgScm");

		// Set layout for shell
		GridLayout layout = new GridLayout();
		shell.setLayout(layout);

		// Create a composite to hold the children
		Composite composite = new Composite(shell, SWT.NONE);
		final TableViewerMfgScm tableViewerMfgScm = new TableViewerMfgScm(
				composite);

		tableViewerMfgScm.getControl().addDisposeListener(
				new DisposeListener() {

					public void widgetDisposed(DisposeEvent e) {
						tableViewerMfgScm.dispose();
					}

				});

		// Ask the shell to display its content
		shell.open();
		tableViewerMfgScm.run(shell);
	}

	/**
	 * Run and wait for a close event
	 * 
	 * @param shell
	 *            Instance of Shell
	 */
	private void run(Shell shell) {

		// Add a listener for the close button
		closeButton.addSelectionListener(new SelectionAdapter() {

			// Close the view i.e. dispose of the composite's parent
			public void widgetSelected(SelectionEvent e) {
				table.getParent().getParent().dispose();
			}
		});

		Display display = shell.getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
	}

	/**
	 * Release resources
	 */
	public void dispose() {

		// Tell the label provider to release its ressources
		tableViewer.getLabelProvider().dispose();
	}

	/**
	 * Create a new shell, add the widgets, open the shell
	 * 
	 * @return the shell that was created
	 */
	private void addChildControls(Composite composite) {

		// Create a composite to hold the children
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.FILL_BOTH);
		composite.setLayoutData(gridData);

		// Set numColumns to 3 for the buttons
		GridLayout layout = new GridLayout(3, false);
		layout.marginWidth = 4;
		composite.setLayout(layout);

		// Create the table
		createTable(composite);

		// Create and setup the TableViewer
		createTableViewer();
		tableViewer.setContentProvider(new MfgScmContentProvider());
		tableViewer.setLabelProvider(new MfgScmLabelProvider());

		// Add the buttons
		createButtons(composite);
	}

	/**
	 * Create the Table
	 */
	private void createTable(Composite parent) {
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 9;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with task Type
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(NAME_COLUMN);
		column.setWidth(300);
		// Add listener to column so tasks are sorted by name when clicked
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new MfgScmTaskSorter(
						MfgScmTaskSorter.NAME));
			}
		});

		// 2nd column with task Type
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(TYPE_COLUMN);
		column.setWidth(80);
		// Add listener to column so tasks are sorted by type when clicked
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new MfgScmTaskSorter(
						MfgScmTaskSorter.TYPE));
			}
		});

		// 3rd column with task SIZE_COLUMN
		column = new TableColumn(table, SWT.LEFT, 2);
		column.setText(SIZE_COLUMN);
		column.setWidth(80);
		// Add listener to column so tasks are sorted by prkey when clicked
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new MfgScmTaskSorter(
						MfgScmTaskSorter.PRKEY));
			}
		});

		// 4th column with task PRKEY_COLUMN
		column = new TableColumn(table, SWT.CENTER, 3);
		column.setText(PRKEY_COLUMN);
		column.setWidth(40);

		// 5th column with task Unq
		column = new TableColumn(table, SWT.CENTER, 4);
		column.setText(UNQ_COLUMN);
		column.setWidth(40);

		// 6th column with task NotNull
		column = new TableColumn(table, SWT.CENTER, 5);
		column.setText(NOTNULL_COLUMN);
		column.setWidth(40);

		// 7th column with task MUnq
		column = new TableColumn(table, SWT.CENTER, 6);
		column.setText(MUNQ_COLUMN);
		column.setWidth(40);

		// 8th column with task Def
		column = new TableColumn(table, SWT.LEFT, 7);
		column.setText(DEF_COLUMN);
		column.setWidth(300);
		// Add listener to column so tasks are sorted by prkey when clicked
		column.addSelectionListener(new SelectionAdapter() {

			public void widgetSelected(SelectionEvent e) {
				tableViewer
						.setSorter(new MfgScmTaskSorter(MfgScmTaskSorter.DEF));
			}
		});

		// 8th column with task Idx1
		column = new TableColumn(table, SWT.CENTER, 8);
		column.setText(IDX1_COLUMN);
		column.setWidth(40);

		// 9th column with task IndexU
		column = new TableColumn(table, SWT.CENTER, 9);
		column.setText(INDEXU_COLUMN);
		column.setWidth(40);
	}

	/**
	 * Create the TableViewer
	 */
	private void createTableViewer() {

		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);

		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];

		// Column 1 : Name (Free text)
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[0] = textEditor;

		// Column 2 : Type (Combo Box)
		editors[1] = new ComboBoxCellEditor(table, taskList.getTypes(),
				SWT.READ_ONLY);

		// Column 3 : Size (Text with digits only)
		textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).addVerifyListener(

		new VerifyListener() {
			public void verifyText(VerifyEvent e) {
				// Here, we could use a RegExp such as the following
				// if using JRE1.4 such as e.doit = e.text.matches("[\\-0-9]*");
				// TODO : improve

				for (int i = 0; i < e.text.length(); i++) {
					char currentChar = e.text.charAt(i);
					if ("0123456789".indexOf(currentChar) < 0) {
						e.doit = false;
						break;
					}
				}
				// e.doit = "0123456789".indexOf(e.text) >= 0;
			}
		});
		editors[2] = textEditor;

		// Column 4 : PRKEY_COLUMN (Checkbox)
		editors[3] = new CheckboxCellEditor(table);

		// Column 5 : Unq (Checkbox)
		editors[4] = new CheckboxCellEditor(table);

		// Column 6 : NotNull (Checkbox)
		editors[5] = new CheckboxCellEditor(table);

		// Column 7 : MUnq (Checkbox)
		editors[6] = new CheckboxCellEditor(table);

		// Column 8 : Def (Free text)
		textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(60);
		editors[7] = textEditor;

		// Column 9 : Idx1 (Checkbox)
		editors[8] = new CheckboxCellEditor(table);

		// Column 10 : IndexU (Checkbox)
		editors[9] = new CheckboxCellEditor(table);

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new MfgScmCellModifier(this));
		// Set the default sorter for the viewer
		tableViewer.setSorter(new MfgScmTaskSorter(MfgScmTaskSorter.TYPE));
	}

	/*
	 * Close the window and dispose of resources
	 */
	public void close() {
		Shell shell = table.getShell();

		if (shell != null && !shell.isDisposed())
			shell.dispose();
	}

	/**
	 * InnerClass that acts as a proxy for the MfgScmTaskList providing content
	 * for the Table. It implements the ITaskListViewer interface since it must
	 * register changeListeners with the MfgScmTaskList
	 */
	class MfgScmContentProvider implements IStructuredContentProvider,
			ITaskListViewer {
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((MfgScmTaskList) newInput).addChangeListener(this);
			if (oldInput != null)
				((MfgScmTaskList) oldInput).removeChangeListener(this);
		}

		public void dispose() {
			taskList.removeChangeListener(this);
		}

		// Return the tasks as an array of Objects
		public Object[] getElements(Object parent) {
			return taskList.getTasks().toArray();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ITaskListViewer#addTask(MfgScmTask)
		 */
		public void addTask(MfgScmTask task) {
			tableViewer.add(task);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ITaskListViewer#removeTask(MfgScmTask)
		 */
		public void removeTask(MfgScmTask task) {
			tableViewer.remove(task);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see ITaskListViewer#updateTask(MfgScmTask)
		 */
		public void updateTask(MfgScmTask task) {
			tableViewer.update(task, null);
		}
	}

	/**
	 * Return the array of choices for a multiple choice cell
	 */
	public String[] getChoices(String property) {
		if (TYPE_COLUMN.equals(property))
			return taskList.getTypes(); // The MfgScmTaskList knows about the
		// choice of prkeys
		else
			return new String[] {};
	}

	/**
	 * Add the "Add", "Delete" and "Close" buttons
	 * 
	 * @param parent
	 *            the parent composite
	 */
	private void createButtons(Composite parent) {

		// Create and configure the "Add" button
		Button add = new Button(parent, SWT.PUSH | SWT.CENTER);
		add.setText("Add");

		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		add.setLayoutData(gridData);
		add.addSelectionListener(new SelectionAdapter() {

			// Add a task to the MfgScmTaskList and refresh the view
			public void widgetSelected(SelectionEvent e) {
				taskList.addTask();
			}
		});

		// Create and configure the "Delete" button
		Button delete = new Button(parent, SWT.PUSH | SWT.CENTER);
		delete.setText("Delete");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gridData.widthHint = 80;
		delete.setLayoutData(gridData);

		delete.addSelectionListener(new SelectionAdapter() {

			// Remove the selection and refresh the view
			public void widgetSelected(SelectionEvent e) {
				MfgScmTask task = (MfgScmTask) ((IStructuredSelection) tableViewer
						.getSelection()).getFirstElement();
				if (task != null) {
					taskList.removeTask(task);
				}
			}
		});

		// Create and configure the "Close" button
		closeButton = new Button(parent, SWT.PUSH | SWT.CENTER);
		closeButton.setText("Close");
		gridData = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gridData.widthHint = 80;
		closeButton.setLayoutData(gridData);
	}

	/**
	 * Return the column names in a collection
	 * 
	 * @return List containing column names
	 */
	public java.util.List getColumnNames() {
		return Arrays.asList(columnNames);
	}

	/**
	 * @return currently selected item
	 */
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	/**
	 * Return the MfgScmTaskList
	 */
	public MfgScmTaskList getTaskList() {
		return taskList;
	}

	/**
	 * Return the parent composite
	 */
	public Control getControl() {
		return table.getParent();
	}

	/**
	 * Return the 'close' Button
	 */
	public Button getCloseButton() {
		return closeButton;
	}

	public void setTreeTable(TreeTable treetable) {
		this.treeTable = treetable;
		// The input for the table viewer is the instance of MfgScmTaskList
		this.dbTable = treetable.getTable();
		taskList = new MfgScmTaskList();
		taskList.setTreeTable(treetable);
		tableViewer.setInput(taskList);
	}

	public TreeTable getTreeTable() {
		return treeTable;
	}
}
