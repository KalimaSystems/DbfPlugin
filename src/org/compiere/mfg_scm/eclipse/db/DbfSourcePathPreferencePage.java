/*
 * ====================================================================
 * Copyright 2001-2005 Compiere MFG + SCM
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

import org.compiere.mfg_scm.eclipse.db.editors.ProjectListEditor;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfSourcePathPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, DbfPluginResources {

	private ProjectListEditor projectListEditor;

	private BooleanFieldEditor automaticEditor;

	public DbfSourcePathPreferencePage() {
		super();
		setPreferenceStore(DbfLauncherPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout());

		// Group securityGroup = new Group(composite,SWT.NONE);
		automaticEditor = new BooleanFieldEditor(
				DbfLauncherPlugin.DBF_PREF_COMPUTESOURCEPATH_KEY,
				PREF_PAGE_COMPUTESOURCEPATH_LABEL, composite);
		this.initField(automaticEditor);

		final Group projectListGroup = new Group(composite, SWT.NULL);
		projectListGroup.setLayout(new GridLayout());
		projectListEditor = new ProjectListEditor();
		projectListEditor.setLabel(PREF_PAGE_PROJECTINSOURCEPATH_LABEL);
		final Control projectList = projectListEditor
				.getControl(projectListGroup);
		projectListGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		projectList.setLayoutData(new GridData(GridData.FILL_BOTH));

		projectListEditor.setCheckedElements(DbfLauncherPlugin.getDefault()
				.readProjectsInSourcePathFromPref());

		projectListEditor.setEnabled(!automaticEditor.getBooleanValue());
		// projectListEditor.setEnabled(false);
		automaticEditor
				.setPropertyChangeListener(new IPropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						projectListEditor.setEnabled(!automaticEditor
								.getBooleanValue());
					}
				});

		return composite;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		automaticEditor.store();
		DbfLauncherPlugin.getDefault().setProjectsInSourcePath(
				projectListEditor.getCheckedElements());
		return true;
	}

	private void initField(FieldEditor field) {
		field.setPreferenceStore(getPreferenceStore());
		field.setPreferencePage(this);
		field.load();
	}
}
