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

package org.compiere.mfg_scm.eclipse.db.actions;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfProject;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

abstract public class DbfProjectAbstractActionDelegate implements
		IWorkbenchWindowActionDelegate {
	private String msg;

	/*
	 * @see IWorkbenchWindowActionDelegate#dispose()
	 */
	public void dispose() {
	}

	/*
	 * @see IWorkbenchWindowActionDelegate#init(IWorkbenchWindow)
	 */
	public void init(IWorkbenchWindow window) {
	}

	/*
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		setMsgToSuccess();
		try {
			DbfProject prj = this.getCurrentSelection();
			if (prj != null) {
				this.doActionOn(prj);
			}
		} catch (DbfActionException ex) {
			setMsgToFail(ex.getMessage(), false);
		} catch (Exception ex) {
			DbfLauncherPlugin.log(ex);
			setMsgToFail(ex.getMessage(), true);
		}

		if (showMessageBox()) {
			Shell shell = DbfLauncherPlugin.getShell();
			MessageDialog.openInformation(shell, "Dbf", msg);
		}
	}

	/*
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {

	}

	protected DbfProject getCurrentSelection() {
		IWorkbenchWindow window = JavaPlugin.getActiveWorkbenchWindow();
		DbfProject result = null;
		if (window != null) {
			ISelection selection = window.getSelectionService().getSelection();
			if (selection instanceof IStructuredSelection) {
				Object project = ((IStructuredSelection) selection)
						.getFirstElement();
				if (project instanceof IProject)
					result = DbfProject.create((IProject) project);
				if (project instanceof IJavaProject)
					result = DbfProject.create((IJavaProject) project);
			}
		}
		return result;
	}

	abstract public void doActionOn(DbfProject prj) throws Exception;

	public boolean showMessageBox() {
		return true;
	};

	/**
	 * Sets the msg.
	 * 
	 * @param msg
	 *            The msg to set
	 */
	private void setMsgToFail(String detail, boolean seelog) {
		this.msg = DbfLauncherPlugin.getResourceString("msg.action.failed");
		this.msg += "\n" + detail;
		if (seelog) {
			this.msg += DbfLauncherPlugin
					.getResourceString("msg.action.seelog");
		}
	}

	/**
	 * Sets the msg.
	 * 
	 * @param msg
	 *            The msg to set
	 */
	private void setMsgToSuccess() {
		this.msg = DbfLauncherPlugin.getResourceString("msg.action.succeeded");
	}
}
