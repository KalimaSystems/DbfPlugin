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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class NewDbfProjectWizardPage extends WizardPage implements
		DbfPluginResources {

	private Button updateXmlCheck;

	private Text webpathText;

	private Text dtdUrlText;

	// See DbfProjectCreationWizard.getNextPage
	private boolean displayedOnce = false;

	private static final int TEXT_FIELD_WIDTH = 200;

	/**
	 * Creates a new project creation wizard page.
	 * 
	 * @param pageName
	 *            the name of this page
	 */
	public NewDbfProjectWizardPage(DbfProjectCreationWizard wizard,
			String pageName) {
		super(pageName);
		setPageComplete(true);
	}

	/*
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		createWebpathGroup(composite);
		// new Label(composite, SWT.NULL);
		createUpdateXmlGroup(composite);

		new Label(composite, SWT.NULL);
		createDtdUrlGroup(composite);

		setErrorMessage(null);
		setMessage(null);
		setControl(composite);
	}

	public void createWebpathGroup(Composite parent) {
		Composite webpathGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		webpathGroup.setLayout(layout);
		webpathGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label webpathLabel = new Label(webpathGroup, SWT.NONE);
		webpathLabel.setText(WIZARD_PROJECT_OJBPATH_LABEL);
		webpathLabel.setEnabled(true);

		// project location entry field
		webpathText = new Text(webpathGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = TEXT_FIELD_WIDTH;
		webpathText.setLayoutData(data);
		webpathText.setText(""); // see DbfProjectCreationWizard.nextPage
		webpathText.setEnabled(true);
	}

	public void createUpdateXmlGroup(Composite parent) {
		Composite updateXmlGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		updateXmlGroup.setLayout(layout);
		updateXmlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// project location entry field
		updateXmlCheck = new Button(updateXmlGroup, SWT.CHECK | SWT.LEFT);
		updateXmlCheck.setText(WIZARD_PROJECT_UPDATEXML_LABEL);
		updateXmlCheck.setEnabled(true);
		updateXmlCheck.setSelection(true);
	}

	public void createDtdUrlGroup(Composite parent) {
		Composite dtdUrlGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		dtdUrlGroup.setLayout(layout);
		dtdUrlGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// location label
		Label dtdUrlLabel = new Label(dtdUrlGroup, SWT.NONE);
		dtdUrlLabel.setText(WIZARD_PROJECT_DTDURL_LABEL);
		dtdUrlLabel.setEnabled(true);

		// project location entry field
		dtdUrlText = new Text(dtdUrlGroup, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = TEXT_FIELD_WIDTH;
		dtdUrlText.setLayoutData(data);
		dtdUrlText.setText("http://db.apache.org/torque/dtd/database_3_2.dtd"); // see
		// DbfProjectCreationWizard.nextPage
		dtdUrlText.setEnabled(true);
	}

	public String getWebpath() {
		return webpathText.getText();
	}

	public String getDtdUrl() {
		return dtdUrlText.getText();
	}

	public boolean getUpdateXml() {
		return updateXmlCheck.getSelection();
	}

	public void setWebpath(String path) {
		webpathText.setText(path);
	}

	/*
	 * @see IWizardPage#canFlipToNextPage()
	 */
	public boolean canFlipToNextPage() {
		displayedOnce = true;
		return super.canFlipToNextPage();
	}

	/**
	 * Gets the wasDisplayedOnce.
	 * 
	 * @return Returns a boolean
	 */
	public boolean wasDisplayedOnce() {
		return displayedOnce;
	}

}
