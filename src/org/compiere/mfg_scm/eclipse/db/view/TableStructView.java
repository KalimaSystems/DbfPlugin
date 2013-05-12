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

import org.compiere.mfg_scm.eclipse.db.dbInterface.DBMeta;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBTable;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DbInterfaceProperties;
import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeTable;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.*;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * This sample class demonstrates how to use the TableViewerMfgScm inside a
 * workbench view. The view is essentially a wrapper for the TableViewerMfgScm.
 * It handles the Selection event for the close button.
 */

public class TableStructView extends ViewPart {
	private TableViewerMfgScm viewer;

	private DbInterfaceProperties dbInterfaceProperties;

	private DBMeta dbMeta;

	private DBTable dbTable;

	private String m_table;

	/**
	 * The constructor.
	 */
	public TableStructView() {
		super();
		dbInterfaceProperties = null;
		dbMeta = null;
		dbTable = null;
		m_table = "";
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
		viewer = new TableViewerMfgScm(parent);
		viewer.getCloseButton().addSelectionListener(new SelectionAdapter() {

			// Close the view i.e. dispose of the composite's parent
			public void widgetSelected(SelectionEvent e) {
				handleDispose();
			}
		});
	}

	public void setInput(TreeTable treetable) {
		this.dbTable = treetable.getTable();
		viewer.setTreeTable(treetable);
		m_table = treetable.getName();
		return;
	}

	public void setDbInterfaceProperties(
			DbInterfaceProperties dbInterfaceProperties) {
		this.dbInterfaceProperties = dbInterfaceProperties;
	}

	public void setDbMeta(DBMeta dbMeta) {
		this.dbMeta = dbMeta;
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * Handle a 'close' event by disposing of the view
	 */

	public void handleDispose() {
		this.getSite().getPage().hideView(this);
	}
}
