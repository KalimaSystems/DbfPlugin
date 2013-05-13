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

package org.compiere.mfg_scm.eclipse.db;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfProjectChangeListener implements IResourceChangeListener,
		DbfPluginResources {

	/*
	 * @see IResourceChangeListener#resourceChanged(IResourceChangeEvent)
	 */
	public void resourceChanged(IResourceChangeEvent event) {
		if (event.getResource() instanceof IProject) {
			final DbfProject project = DbfProject.create((IProject) event
					.getResource());
			if (project != null) {

				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						IWorkbenchWindow window = PlatformUI.getWorkbench()
								.getActiveWorkbenchWindow();

						String[] labels = { IDialogConstants.OK_LABEL,
								IDialogConstants.CANCEL_LABEL };
						MessageDialog dialog = new MessageDialog(window
								.getShell(), WIZARD_PROJECT_REMOVE_TITLE, null,
								WIZARD_PROJECT_REMOVE_DESCRIPTION,
								MessageDialog.QUESTION, labels, 1);

						if (dialog.open() == MessageDialog.OK) {
							try {
								//
							} catch (Exception ex) {
								DbfLauncherPlugin.log(ex.getMessage());
							}
						}
					}
				});

			}
		}
	}

}
