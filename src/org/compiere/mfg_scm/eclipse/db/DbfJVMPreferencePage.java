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

import java.util.ArrayList;

import org.compiere.mfg_scm.eclipse.db.editors.ClasspathFieldEditor;
import org.compiere.mfg_scm.eclipse.db.editors.ComboFieldEditor;
import org.compiere.mfg_scm.eclipse.db.editors.ListFieldEditor;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfJVMPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, DbfPluginResources {

	static final private int FIELD_WIDTH = 50;

	private ComboFieldEditor jvmChoice;

	private ListFieldEditor jvmParamaters;

	private ClasspathFieldEditor jvmClasspath;

	private ClasspathFieldEditor jvmBootClasspath;

	private BooleanFieldEditor debugModeEditor;

	public DbfJVMPreferencePage() {
		super();
		setPreferenceStore(DbfLauncherPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		final ScrolledComposite scrolledComposite = new ScrolledComposite(
				parent, SWT.V_SCROLL);
		Composite composite = new Composite(scrolledComposite, SWT.NULL);
		scrolledComposite.setContent(composite);
		composite.setLayout(new GridLayout(2, false));

		// Collect all JREs
		ArrayList allVMs = new ArrayList();
		IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; i++) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				allVMs.add(vms[j]);
			}
		}

		String[][] namesAndValues = new String[allVMs.size()][2];
		for (int i = 0; i < allVMs.size(); i++) {
			namesAndValues[i][0] = ((IVMInstall) allVMs.get(i)).getName();
			namesAndValues[i][1] = ((IVMInstall) allVMs.get(i)).getId();
		}

		jvmChoice = new ComboFieldEditor(DbfLauncherPlugin.DBF_PREF_JRE_KEY,
				PREF_PAGE_JRE_LABEL, namesAndValues, composite);

		debugModeEditor = new BooleanFieldEditor(
				DbfLauncherPlugin.DBF_PREF_DEBUGMODE_KEY,
				PREF_PAGE_DEBUGMODE_LABEL, composite);
		this.initField(debugModeEditor);

		new Label(composite, SWT.NULL);
		Composite group = new Composite(composite, SWT.NULL);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		group.setLayout(new GridLayout(2, false));
		Button btAddLaunch = new Button(group, SWT.PUSH);
		btAddLaunch.setText(PREF_PAGE_CREATE_LAUNCH_LABEL);
		btAddLaunch.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					DbfLauncherPlugin.getDefault().getDbfBootstrap()
							.addLaunch();
				} catch (Exception ex) {
					DbfLauncherPlugin
							.log("Failed to create launch configuration/n");
					DbfLauncherPlugin.log(ex);
				}
			}
		});
		Button btLog = new Button(group, SWT.PUSH);
		btLog.setText(PREF_PAGE_DUMP_CONFIG_LABEL);
		btLog.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					DbfLauncherPlugin.getDefault().getDbfBootstrap()
							.logConfig();
				} catch (Exception ex) {
					DbfLauncherPlugin
							.log("Failed to create launch configuration/n");
					DbfLauncherPlugin.log(ex);
				}
			}
		});

		jvmParamaters = new ListFieldEditor(
				DbfLauncherPlugin.DBF_PREF_JVM_PARAMETERS_KEY,
				PREF_PAGE_PARAMETERS_LABEL, composite);
		jvmClasspath = new ClasspathFieldEditor(
				DbfLauncherPlugin.DBF_PREF_JVM_CLASSPATH_KEY,
				PREF_PAGE_CLASSPATH_LABEL, composite);
		jvmBootClasspath = new ClasspathFieldEditor(
				DbfLauncherPlugin.DBF_PREF_JVM_BOOTCLASSPATH_KEY,
				PREF_PAGE_BOOTCLASSPATH_LABEL, composite);

		this.initField(jvmChoice);
		this.initField(jvmParamaters);
		this.initField(jvmClasspath);
		this.initField(jvmBootClasspath);

		composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		return scrolledComposite;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		jvmChoice.store();
		jvmBootClasspath.store();
		jvmClasspath.store();
		jvmParamaters.store();
		debugModeEditor.store();

		DbfLauncherPlugin.getDefault().savePluginPreferences();
		return true;
	}

	private void initField(FieldEditor field) {
		field.setPreferenceStore(getPreferenceStore());
		field.setPreferencePage(this);
		field.load();
	}

}
