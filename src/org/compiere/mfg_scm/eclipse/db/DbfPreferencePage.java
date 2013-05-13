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

import java.io.File;

import org.compiere.mfg_scm.eclipse.db.editors.DbfFileFieldEditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, DbfPluginResources {

	private RadioGroupFieldEditor version;

	private DirectoryFieldEditor home;

	private DbfFileFieldEditor resourceJar;

	private String oldVersion;

	public DbfPreferencePage() {
		super();
		setPreferenceStore(DbfLauncherPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));

		oldVersion = DbfLauncherPlugin.getDefault().getDbfVersion();
		version = new RadioGroupFieldEditor(
				DbfLauncherPlugin.DBF_PREF_VERSION_KEY,
				PREF_PAGE_CHOOSEVERSION_LABEL, 1, new String[][] {
						{ PREF_PAGE_VERSION1_0_LABEL,
								DbfLauncherPlugin.DBF_VERSION10 },
						{ PREF_PAGE_VERSION1_1_LABEL,
								DbfLauncherPlugin.DBF_VERSION11 } }, composite,
				true);

		Group homeGroup = new Group(composite, SWT.NONE);
		home = new DirectoryFieldEditor(DbfLauncherPlugin.DBF_PREF_HOME_KEY,
				PREF_PAGE_HOME_LABEL, homeGroup);

		new Label(composite, SWT.NULL); // blank
		resourceJar = new DbfFileFieldEditor(
				DbfLauncherPlugin.DBF_PREF_RESOURCEJAR_KEY,
				PREF_PAGE_RESOURCEJAR_LABEL, homeGroup);

		home.setPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				if (event.getProperty().equals(home.VALUE)) {
					//
				}
			}
		});

		new Label(composite, SWT.NULL); // blank

		initLayoutAndData(homeGroup, 3);

		this.initField(version);
		version.setPropertyChangeListener(new IPropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent event) {
				// String value = (String) event.getNewValue();
				// versionChanged(composite, value);
			}
		});

		this.initField(home);
		this.initField(resourceJar);
		if (resourceJar.getStringValue().length() == 0) {
			computeResourceJar();
		}

		return parent;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		version.store();
		home.store();
		resourceJar.store();
		DbfLauncherPlugin.getDefault().initDbfClasspathVariable();
		DbfLauncherPlugin.getDefault().savePluginPreferences();

		if (!oldVersion.equals(DbfLauncherPlugin.getDefault().getDbfVersion())) {
			this.updateDbfProjectsBuildPath();
		}
		return true;
	}

	private void updateDbfProjectsBuildPath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		try {
			for (int i = 0; i < projects.length; i++) {
				if (projects[i].hasNature(DbfLauncherPlugin.NATURE_ID)) {
					// DbfProject.create(projects[i])
					// .addDbfJarToProjectClasspath();
				}
			}
		} catch (CoreException e) {
			// ignore update if there is an exception
		}

	}

	private void initField(FieldEditor field) {
		field.setPreferenceStore(getPreferenceStore());
		field.setPreferencePage(this);
		field.load();
	}

	private void computeResourceJar() {
		resourceJar.setStringValue(home.getStringValue() + File.separator
				+ "lib" + File.separator + "dbfResource.jar");
	}

	private void versionChanged(final Composite composite, String value) {
	}

	private void initLayoutAndData(Composite aGroup, int spanH, int spanV,
			int numColumns) {
		GridLayout gl = new GridLayout(numColumns, false);
		aGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = spanH;
		gd.verticalSpan = spanV;
		aGroup.setLayoutData(gd);
	}

	private void initLayoutAndData(Composite aGroup, int numColumns) {
		GridLayout gl = new GridLayout(numColumns, false);
		aGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		gd.widthHint = 400;
		aGroup.setLayoutData(gd);
	}

}
