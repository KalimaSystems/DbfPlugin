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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfProjectGeneralPropertyPage implements DbfPluginResources {

	private Button isDbfProjectCheck;

	private Button updateXmlCheck;

	private Button reloadableCheck;

	private Button redirectLoggerCheck;

	private Text ojbpathText;

	private Text dtdUrlText;

	private Text extraInfoText;

	private DbfProjectPropertyPage page;

	private static final int TEXT_FIELD_WIDTH = 200;

	public DbfProjectGeneralPropertyPage(DbfProjectPropertyPage page) {
		this.page = page;
	}

	/**
	 * returns a control which consists of the ui elements of this page
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		createIsDbfProjectGroup(composite);
		Group group = new Group(composite, SWT.NONE);
		group.setLayout(new GridLayout());
		createOjbpathGroup(group);
		createExtraInformationGroup(group);
		createDtdUrlGroup(group);

		return composite;
	}

	public void createIsDbfProjectGroup(Composite parent) {
		Composite isDbfProjectGroup = new Composite(parent, SWT.NONE);
		isDbfProjectGroup.setLayout(new GridLayout(3, false));
		isDbfProjectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// project location entry field
		isDbfProjectCheck = new Button(isDbfProjectGroup, SWT.CHECK | SWT.LEFT);
		isDbfProjectCheck.setText(PROPERTIES_PAGE_PROJECT_ISDBFPROJECT_LABEL);
		isDbfProjectCheck.setEnabled(true);

		try {
			isDbfProjectCheck.setSelection(page.getJavaProject().getProject()
					.hasNature(DbfLauncherPlugin.NATURE_ID));
		} catch (CoreException ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}
	}

	public void createOjbpathGroup(Composite parent) {
		Composite ojbpathGroup = new Composite(parent, SWT.NONE);
		ojbpathGroup.setLayout(new GridLayout(3, false));
		ojbpathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label ojbpathLabel = new Label(ojbpathGroup, SWT.NONE);
		ojbpathLabel.setText(WIZARD_PROJECT_OJBPATH_LABEL);
		ojbpathLabel.setEnabled(true);

		// project location entry field
		ojbpathText = new Text(ojbpathGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = TEXT_FIELD_WIDTH;
		// data.horizontalSpan = 2;
		ojbpathText.setLayoutData(data);
		ojbpathText.setText(this.getOjbPath());
		ojbpathText.setEnabled(true);

		// project location entry field
		updateXmlCheck = new Button(ojbpathGroup, SWT.CHECK | SWT.LEFT);
		updateXmlCheck.setText(WIZARD_PROJECT_UPDATEXML_LABEL);
		data = new GridData();
		data.horizontalSpan = 3;
		updateXmlCheck.setLayoutData(data);
		updateXmlCheck.setEnabled(true);
		updateXmlCheck.setSelection(this.getUpdateXml());

		// reloadable attribute
		reloadableCheck = new Button(ojbpathGroup, SWT.CHECK | SWT.LEFT);
		reloadableCheck.setText(WIZARD_PROJECT_RELOADABLE_LABEL);
		data = new GridData();
		data.horizontalSpan = 3;
		reloadableCheck.setLayoutData(data);
		reloadableCheck.setEnabled(true);
		reloadableCheck.setSelection(this.getReloadable());

		// reloadable attribute
		redirectLoggerCheck = new Button(ojbpathGroup, SWT.CHECK | SWT.LEFT);
		redirectLoggerCheck.setText(WIZARD_PROJECT_REDIRECTLOGGER_LABEL);
		data = new GridData();
		data.horizontalSpan = 3;
		redirectLoggerCheck.setLayoutData(data);
		redirectLoggerCheck.setEnabled(true);
		redirectLoggerCheck.setSelection(this.getRedirectLogger());
	}

	public void createDtdUrlGroup(Composite parent) {
		Composite dtdUrlGroup = new Composite(parent, SWT.NONE);
		dtdUrlGroup.setLayout(new GridLayout(2, false));
		dtdUrlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label dtdUrlLabel = new Label(dtdUrlGroup, SWT.NONE);
		dtdUrlLabel.setText(WIZARD_PROJECT_DTDURL_LABEL);
		dtdUrlLabel.setEnabled(true);

		// project location entry field
		dtdUrlText = new Text(dtdUrlGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 30;
		// data.horizontalSpan = 3;
		dtdUrlText.setLayoutData(data);
		dtdUrlText.setText(this.getDtdUrl());
		dtdUrlText.setEnabled(true);
	}

	public void createExtraInformationGroup(Composite parent) {
		Composite extraInfoGroup = new Composite(parent, SWT.NONE);
		extraInfoGroup.setLayout(new GridLayout(1, false));
		extraInfoGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// Extra information label
		Label extraInfoLabel = new Label(extraInfoGroup, SWT.NONE);
		extraInfoLabel.setText(PROPERTIES_PAGE_PROJECT_EXTRAINFO_LABEL);
		extraInfoLabel.setEnabled(true);

		// Extra information field
		extraInfoText = new Text(extraInfoGroup, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.widthHint = 500;
		data.heightHint = 100;
		// data.horizontalSpan = 3;
		extraInfoText.setLayoutData(data);
		extraInfoText.setText(this.getExtraInfo());
		extraInfoText.setEnabled(true);
	}

	protected String getOjbPath() {
		String result = "";
		try {
			DbfProject prj = page.getDbfProject();
			if (prj != null)
				result = prj.getOjbPath();
		} catch (CoreException ex) {
			// result = "";
		}
		return result;
	}

	protected String getDtdUrl() {
		String result = "/";
		try {
			DbfProject prj = page.getDbfProject();
			if (prj != null)
				result = prj.getDtdUrl();
		} catch (CoreException ex) {
			// result = "";
		}
		return result;
	}

	protected String getExtraInfo() {
		String result = "";
		try {
			DbfProject prj = page.getDbfProject();
			if (prj != null)
				result = prj.getExtraInfo();
		} catch (CoreException ex) {
			// result = "";
		}
		return result;
	}

	protected boolean getUpdateXml() {
		boolean result = true;
		try {
			DbfProject prj = page.getDbfProject();
			if (prj != null)
				result = prj.getUpdateXml();
		} catch (CoreException ex) {
			// result = false;
		}
		return result;
	}

	protected boolean getReloadable() {
		boolean result = true;
		try {
			DbfProject prj = page.getDbfProject();
			if (prj != null)
				result = prj.getReloadable();
		} catch (CoreException ex) {
		}
		return result;
	}

	protected boolean getRedirectLogger() {
		boolean result = false;
		try {
			DbfProject prj = page.getDbfProject();
			if (prj != null)
				result = prj.getRedirectLogger();
		} catch (CoreException ex) {
		}
		return result;
	}

	/**
	 * performes the ok action for this property page
	 */
	public boolean performOk() {
		try {
			if (isDbfProjectCheck.getSelection()) {
				DbfProject.addDbfNature(page.getJavaProject());
				DbfProject prj = page.getDbfProject();
				prj.updateOjbPath(ojbpathText.getText());
				prj.setUpdateXml(updateXmlCheck.getSelection());
				prj.setReloadable(reloadableCheck.getSelection());
				prj.setRedirectLogger(redirectLoggerCheck.getSelection());
				prj.setExtraInfo(extraInfoText.getText());
				prj.setDtdUrl(dtdUrlText.getText());
				prj.saveProperties();
			} else {
				DbfProject.removeDbfNature(page.getJavaProject());
			}
		} catch (Exception ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}

		return true;
	}

	public boolean performApply() {
		// FIXME mettre Ok
		try {
		} catch (Exception ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}

		return true;
	}

	public boolean isDbfProjectChecked() {
		return isDbfProjectCheck.getSelection();
	}

}
