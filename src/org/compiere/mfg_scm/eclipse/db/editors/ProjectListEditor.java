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

package org.compiere.mfg_scm.eclipse.db.editors;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfPluginResources;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbench;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class ProjectListEditor implements DbfPluginResources {

	private CheckedListDialogField fProjectsList;

	private String[] fExcludedNatures;

	public ProjectListEditor() {
		this(new String[0]);
	}

	public ProjectListEditor(String[] excludedNatures) {
		this.fExcludedNatures = excludedNatures;
		String[] buttonLabels = new String[] { PREF_PAGE_SELECTALL_LABEL,
				PREF_PAGE_UNSELECTALL_LABEL };

		fProjectsList = new CheckedListDialogField(null, buttonLabels,
				new MyLabelProvider());
		fProjectsList.setCheckAllButtonIndex(0);
		fProjectsList.setUncheckAllButtonIndex(1);
		updateProjectsList();
		// fProjectsList.setViewerSorter(new CPListElementSorter());
	}

	public void setEnabled(boolean enabled) {
		fProjectsList.setEnabled(enabled);
	}

	public void init(IJavaProject jproject) {
		updateProjectsList();
	}

	public void setLabel(String label) {
		fProjectsList.setLabelText(label);
	}

	private void updateProjectsList() {
		try {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IProject[] projects = root.getProjects();

			List projectsList = new ArrayList(projects.length);

			for (int i = 0; i < projects.length; i++) {
				IProject proj = projects[i];
				if (projects[i].isOpen()) {
					boolean accept = true;
					for (int j = 0; j < fExcludedNatures.length; j++) {
						if (proj.hasNature(fExcludedNatures[j]))
							accept = false;
					}
					if (accept) {
						projectsList.add(new ProjectListElement(proj));
					}
				}
			}

			// Remove Dbf project for preference Store (for compatibility
			// between dbf plugin versions V2.x and V3)
			List oldProjectsInCP = DbfLauncherPlugin.getDefault()
					.getProjectsInCP();
			List newProjectsInCP = new ArrayList();
			for (Iterator iter = oldProjectsInCP.iterator(); iter.hasNext();) {
				ProjectListElement element = (ProjectListElement) iter.next();
				boolean accept = true;
				for (int j = 0; j < fExcludedNatures.length; j++) {
					if (element.getProject().hasNature(fExcludedNatures[j]))
						accept = false;
				}
				if (accept) {
					newProjectsInCP.add(element);
				}
			}

			/*
			 * Quick hack : Using reflection for compatability with Eclipse 2.1
			 * and 3.0 M9
			 * 
			 * Old code : fProjectsList.setElements(projectsList);
			 * fProjectsList.setCheckedElements(DbfLauncherPlugin.getDefault().getProjectsInCP());
			 */
			this.invokeForCompatibility("setElements", projectsList);
			this.invokeForCompatibility("setCheckedElements", newProjectsInCP);

		} catch (Exception e) {
			/*
			 * Old code : fProjectsList.setElements(new ArrayList(5));
			 */
			this.invokeForCompatibility("setElements", new ArrayList(5));
		}

	}

	// -------- UI creation ---------

	public Control getControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		// fProjectsList.doFillIntoGrid(composite,3);
		LayoutUtil.doDefaultLayout(composite,
				new DialogField[] { fProjectsList }, true, 0, 0);

		return composite;
	}

	public List getCheckedElements() {
		return (List) fProjectsList.getCheckedElements();
	}

	public void setCheckedElements(List projects) {
		/*
		 * Old code : fProjectsList.setCheckedElements(projects);
		 */
		this.invokeForCompatibility("setCheckedElements", projects);
	}

	private class MyLabelProvider extends LabelProvider {

		/*
		 * @see ILabelProvider#getImage(Object)
		 */
		public Image getImage(Object element) {
			IWorkbench workbench = JavaPlugin.getDefault().getWorkbench();
			return workbench.getSharedImages().getImage(
					ISharedImages.IMG_OBJ_PROJECT);
		}

		/*
		 * @see ILabelProvider#getText(Object)
		 */
		public String getText(Object element) {
			return super.getText(element);
		}

	}

	/*
	 * Quick hack : Using reflection for compatability with Eclipse 2.1 and 3.0
	 * M9
	 */

	private void invokeForCompatibility(String methodName, List projects) {
		Class clazz = fProjectsList.getClass();
		Class[] collectionParameter = { Collection.class };
		try {
			Method method = clazz.getMethod(methodName, collectionParameter);
			Object[] args = { projects };
			method.invoke(fProjectsList, args);
		} catch (Exception e) {
			Class[] listParameter = { List.class };
			try {
				Method method = clazz.getMethod(methodName, listParameter);
				Object[] args = { projects };
				method.invoke(fProjectsList, args);
			} catch (Exception ex) {
				DbfLauncherPlugin.log(ex);
			}
		}

	}

}
