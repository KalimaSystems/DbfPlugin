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

import org.compiere.mfg_scm.eclipse.db.dbInterface.DbInterfaceProperties;
import org.compiere.mfg_scm.eclipse.db.editors.DbfProjectPropertyEditor;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferencePage;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.dialogs.PropertyPage;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * provides a tab control with property pages for the settings of a dbf project.
 * 
 */
public class DbfProjectPropertyPage extends PropertyPage implements
		IWorkbenchPreferencePage, DbfPluginResources {

	private TabFolder folder;

	private DbfProjectGeneralPropertyPage generalPropertyPage;

	private DbfProjectPropertyEditor dbfProjectPropertyEditor;

	private DbfProject dbfProject = null;

	/**
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_DBSETTING = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.dbsetting");
		final String MULTIPAGE_EDITOR_PAGE_DBINTERFACE = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.dbinterface");
		final String MULTIPAGE_EDITOR_PAGE_DISPLAY = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.display");
		final String MULTIPAGE_EDITOR_PAGE_MODEL = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.model");
		final String MULTIPAGE_EDITOR_PAGE_MAPPING = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.mapping");
		final String MULTIPAGE_EDITOR_PAGE_DRIVER = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.driver");
		final String MULTIPAGE_EDITOR_PAGE_PREVIEW = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.preview");
		DbfLauncherPlugin
				.log(
						IStatus.INFO,
						DbfLauncherPlugin
								.getResourceString("DbfProjectPropertyPage createContents new TabFolder"));

		folder = new TabFolder(parent, SWT.NONE);

		// folder.setLayout(new TabFolderLayout());
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));
		// TODO findout why iprogressmonitor stuck property pages
		// iprogressmonitor = new ProgressMonitorPart(folder,
		// folder.getLayout());

		MyDefaultSelectionListener mydefaultselectionlistener = new MyDefaultSelectionListener();
		folder.addSelectionListener(mydefaultselectionlistener);

		// general property page
		generalPropertyPage = new DbfProjectGeneralPropertyPage(this);
		dbfProjectPropertyEditor = new DbfProjectPropertyEditor(this);
		// add to tab
		TabItem generalTab = new TabItem(folder, SWT.NONE);
		generalTab
				.setText(DbfPluginResources.PROPERTIES_PAGE_PROJECT_GENERAL_TAB_LABEL);
		generalTab.setControl(generalPropertyPage.createContents(folder));
		// add to tab
		TabItem dbSettingsTab = new TabItem(folder, SWT.NONE);
		dbSettingsTab.setText(MULTIPAGE_EDITOR_PAGE_DBSETTING);
		dbSettingsTab.setControl(dbfProjectPropertyEditor
				.createDBSettings(folder));
		// add to tab
		TabItem dbInterfaceSettingsTab = new TabItem(folder, SWT.NONE);
		dbInterfaceSettingsTab.setText(MULTIPAGE_EDITOR_PAGE_DBINTERFACE);
		dbInterfaceSettingsTab.setControl(dbfProjectPropertyEditor
				.createDbInterfaceSettings(folder));
		// add to tab
		TabItem displaySettingsTab = new TabItem(folder, SWT.NONE);
		displaySettingsTab.setText(MULTIPAGE_EDITOR_PAGE_DISPLAY);
		displaySettingsTab.setControl(dbfProjectPropertyEditor
				.createDisplaySettings(folder));
		// add to tab
		TabItem modelSettingsTab = new TabItem(folder, SWT.NONE);
		modelSettingsTab.setText(MULTIPAGE_EDITOR_PAGE_MODEL);
		modelSettingsTab.setControl(dbfProjectPropertyEditor
				.createModelSettings(folder));
		// add to tab
		TabItem dbMappingSettingsTab = new TabItem(folder, SWT.NONE);
		dbMappingSettingsTab.setText(MULTIPAGE_EDITOR_PAGE_MAPPING);
		dbMappingSettingsTab.setControl(dbfProjectPropertyEditor
				.createMappingSettings(folder));
		// add to tab
		TabItem dbDriverSettingsTab = new TabItem(folder, SWT.NONE);
		dbDriverSettingsTab.setText(MULTIPAGE_EDITOR_PAGE_DRIVER);
		dbDriverSettingsTab.setControl(dbfProjectPropertyEditor
				.createDriverSettings(folder));
		// add to tab
		TabItem dbPreviewTab = new TabItem(folder, SWT.NONE);
		dbPreviewTab.setText(MULTIPAGE_EDITOR_PAGE_PREVIEW);
		dbPreviewTab.setControl(dbfProjectPropertyEditor.createPreview(folder));

		return folder;
	}

	/**
	 * @see IPreferencePage#performOk()
	 */
	public boolean performOk() {
		// delegate to property pages
		if (generalPropertyPage.performOk()) {
			// check if it's a dbf project any more
			if (generalPropertyPage.isDbfProjectChecked()) {
				try {
					// dbfProjectPropertyEditor.doSave(iprogressmonitor);
					dbfProjectPropertyEditor.doSave(null);
				} catch (Exception ex) {
					DbfLauncherPlugin.log(ex);
				}
			}
		}
		return true;
	}

	/**
	 * @see IPreferencePage#performAplly()
	 */
	public void performApply() {
		// delegate to property pages
		performOk();
		return;
	}

	/**
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	/* helper methods */
	protected IJavaProject getJavaProject() throws CoreException {
		IProject project = (IProject) (this.getElement()
				.getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}

	protected DbfProject getDbfProject() throws CoreException {
		if (dbfProject == null)
			return DbfProject.create(getJavaProject());
		else
			return dbfProject;
	}

	public void showMessage(String message) {
		final String MULTIPAGE_MESSAGE_OPEN_INFO = DbfLauncherPlugin
				.getResourceString("viewer.message.open.info");
		MessageDialog.openInformation(getControl().getShell(),
				MULTIPAGE_MESSAGE_OPEN_INFO, message);
		return;
	}

	public void showError(String error) {
		final String MULTIPAGE_MESSAGE_OPEN_ERROR = DbfLauncherPlugin
				.getResourceString("viewer.message.open.error");
		MessageDialog.openError(getControl().getShell(),
				MULTIPAGE_MESSAGE_OPEN_ERROR, error);
		return;
	}

	public DbInterfaceProperties getDbInterfaceProperties() {
		DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
				.getFromFile(this.getInterfacePropertiesFile());
		return (dbInterfaceproperties);
	}

	public String getInterfacePropertiesFile() {
		try {
			return (this.getDbfProject().getInterfacePropertiesFile()
					.getAbsolutePath());
		} catch (CoreException e) {
			DbfLauncherPlugin.log(e);
		}
		return null;
	}

	class MyDefaultSelectionListener implements SelectionListener {

		MyDefaultSelectionListener() {
			return;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			int i = folder.getSelectionIndex();
			if (i == 5)
				dbfProjectPropertyEditor.printProperties();
			return;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent e) {
			// TODO Auto-generated method stub
			int i = folder.getSelectionIndex();
			return;

		}

	}

}
