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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.wizards.NewElementWizard;
import org.eclipse.jdt.ui.wizards.NewJavaProjectWizardPage;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfProjectCreationWizard extends NewElementWizard implements
		IExecutableExtension, DbfPluginResources {

	public static final String NEW_PROJECT_WIZARD_ID = "org.eclipse.jdt.ui.wizards.NewProjectCreationWizard"; //$NON-NLS-1$

	private NewDbfProjectWizardPage fDbfPage;

	private NewJavaProjectWizardPage fJavaPage;

	private WizardNewProjectCreationPage fMainPage;

	private IConfigurationElement fConfigElement;

	public DbfProjectCreationWizard() {
		super();
		DbfLauncherPlugin.checkDbfSettingsAndWarn();
		ImageDescriptor banner = this.getBannerImg();
		if (banner != null)
			setDefaultPageImageDescriptor(banner);
		setDialogSettings(JavaPlugin.getDefault().getDialogSettings());
		setWindowTitle(WIZARD_PROJECT_TITLE);
	}

	public boolean canFinish() {
		return DbfLauncherPlugin.isDbfConfigured();

	}

	private ImageDescriptor getBannerImg() {
		try {
			URL prefix = new URL(DbfLauncherPlugin.getDefault().getDescriptor()
					.getInstallURL(), "icons/");
			return ImageDescriptor.createFromURL(new URL(prefix,
					"newjprj_ojb.gif"));
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/*
	 * @see Wizard#addPages
	 */
	public void addPages() {
		super.addPages();
		fMainPage = new WizardNewProjectCreationPage("Page 1");
		fMainPage.setTitle(WIZARD_PROJECT_MAINPAGE_TITLE);
		fMainPage.setDescription(WIZARD_PROJECT_MAINPAGE_DESCRIPTION);
		addPage(fMainPage);

		fDbfPage = new NewDbfProjectWizardPage(this, "NewDbfProjectPage");
		fDbfPage.setTitle(WIZARD_PROJECT_DBFPAGE_TITLE);
		fDbfPage.setDescription(WIZARD_PROJECT_DBFPAGE_DESCRIPTION);
		addPage(fDbfPage);

		IWorkspaceRoot root = JavaPlugin.getWorkspace().getRoot();
		fJavaPage = new NewJavaProjectWizardPage(root, fMainPage);
		// addPage(fJavaPage);

	}

	/*
	 * @see Wizard#performFinish
	 */
	public boolean performFinish() {
		IRunnableWithProgress op = new WorkspaceModifyDelegatingOperation(
				fJavaPage.getRunnable());
		try {
			getContainer().run(false, true, op);
			DbfProject.addDbfNature(fJavaPage.getNewJavaProject());
			DbfProject dbfPrj = DbfProject
					.create(fJavaPage.getNewJavaProject());
			dbfPrj.setOjbPath(fDbfPage.getWebpath());
			dbfPrj.setUpdateXml(fDbfPage.getUpdateXml());
			dbfPrj.setDtdUrl(fDbfPage.getDtdUrl());
			dbfPrj.saveProperties();
			dbfPrj.fullConfiguration();
		} catch (InvocationTargetException e) {
			/*
			 * FIXME
			 * 
			 * String title = NewWizardMessages
			 * .getString("NewProjectCreationWizard.op_error.title");
			 * //$NON-NLS-1$ String message = NewWizardMessages
			 * .getString("NewProjectCreationWizard.op_error.message");
			 * //$NON-NLS-1$ ExceptionHandler.handle(e, getShell(), title,
			 * message);
			 */
			return false;
		} catch (InterruptedException e) {
			return false;
		} catch (CoreException e) {
			return false;
		} catch (IOException e) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(fConfigElement);
		selectAndReveal(fJavaPage.getNewJavaProject().getProject());
		return true;
	}

	/*
	 * Stores the configuration element for the wizard. The config element will
	 * be used in <code> performFinish </code> to set the result perspective.
	 */
	public void setInitializationData(IConfigurationElement cfig,
			String propertyName, Object data) {
		fConfigElement = cfig;
	}

	/*
	 * @see IWizard#getNextPage(IWizardPage)
	 */
	public IWizardPage getNextPage(IWizardPage page) {
		// initialize Dbf Wizard page webpath field
		// Default value is / + projectName
		if (page instanceof WizardNewProjectCreationPage) {
			if (!fDbfPage.wasDisplayedOnce()) {
				fDbfPage.setWebpath("/" + fMainPage.getProjectName());
			}
		}

		return super.getNextPage(page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#finishPage(org.eclipse.core.runtime.IProgressMonitor)
	 */
	protected void finishPage(IProgressMonitor monitor)
			throws InterruptedException, CoreException {
		// for Eclipse 3.0 compatibility
	}

	/**
	 * Getters for subclasses
	 */
	protected IConfigurationElement getFConfigElement() {
		return fConfigElement;
	}

	protected NewJavaProjectWizardPage getFJavaPage() {
		return fJavaPage;
	}

	protected WizardNewProjectCreationPage getFMainPage() {
		return fMainPage;
	}

	protected NewDbfProjectWizardPage getFDbfPage() {
		return fDbfPage;
	}

	public IJavaElement getCreatedElement() {
		// TODO Control if OK
		return (IJavaElement) this;
	}

}
