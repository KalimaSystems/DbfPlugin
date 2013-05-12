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

import java.util.Vector;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class SQLDialog extends Dialog {

	private Text txt;

	private String sql;

	private Combo com;

	private Vector shortList;

	public SQLDialog(Shell shell) {
		super(shell);
		txt = null;
		sql = "";
		com = null;
		shortList = new Vector();
		return;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayout(getDlgLayout());
		txt = new Text(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.CHECK);
		txt.setSize(600, 200);
		txt.setText(sql);
		txt.addDisposeListener(new MyDisposeListener());
		com = new Combo(composite, SWT.BORDER);
		com.removeAll();
		com.removeAll();
		FormData formdata;
		for (int i = 0; i < shortList.size(); i++)
			com.add((String) shortList.get(i));
		com.addSelectionListener(new MySelectionListener());
		formdata = new FormData();
		formdata.left = new FormAttachment(0, 5);
		formdata.top = new FormAttachment(0, 5);
		formdata.right = new FormAttachment(100, -5);
		formdata.width = 200;
		com.setLayoutData(formdata);
		formdata = new FormData();
		formdata.top = new FormAttachment(com, 5);
		formdata.left = new FormAttachment(0, 5);
		formdata.right = new FormAttachment(100, -5);
		formdata.bottom = new FormAttachment(100, -5);
		txt.setLayoutData(formdata);
		return (composite);
	}

	protected void configureShell(Shell shell) {
		final String MULTIPAGE_EDITOR_LABEL_DEFAULT = DbfLauncherPlugin
				.getResourceString("sql.dialog.label.query");
		super.configureShell(shell);
		shell.setText(MULTIPAGE_EDITOR_LABEL_DEFAULT);
		return;
	}

	private Layout getDlgLayout() {
		FormLayout formlayout = new FormLayout();
		return (formlayout);
	}

	public String getSQL() {
		return (sql);
	}

	public void setSQL(String string) {
		sql = string;
		return;
	}

	public Vector getShortList() {
		return (shortList);
	}

	public void setShortList(Vector vector) {
		shortList = vector;
		return;
	}

	class MyDisposeListener implements DisposeListener {

		public MyDisposeListener() {
			return;
		}

		public void widgetDisposed(DisposeEvent disposeevent) {
			sql = txt.getText();
			return;
		}

	}

	class MySelectionListener implements SelectionListener {

		MySelectionListener() {
			return;
		}

		public void widgetDefaultSelected(SelectionEvent selectionevent) {
			return;
		}

		public void widgetSelected(SelectionEvent selectionevent) {
			txt.setText(com.getText());
			return;
		}

	}
}
