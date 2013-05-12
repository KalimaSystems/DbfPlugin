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

package org.compiere.mfg_scm.eclipse.db.actions;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DisconnectActionDelegate implements IWorkbenchWindowActionDelegate {
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
		if (DbfLauncherPlugin.checkDbfSettingsAndWarn()) {
			DbfLauncherPlugin.log(DbfLauncherPlugin
					.getResourceString("msg.disconnect"));
			try {
				DbfLauncherPlugin.getDefault().getDbfBootstrap().disconnect();
			} catch (Exception ex) {
				String msg = DbfLauncherPlugin
						.getResourceString("msg.disconnect.failed");
				DbfLauncherPlugin.log(msg + "/n");
				DbfLauncherPlugin.log(ex);
			}
		}
	}

	/*
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
