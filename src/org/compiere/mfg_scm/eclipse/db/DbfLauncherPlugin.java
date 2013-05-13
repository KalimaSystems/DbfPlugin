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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.compiere.mfg_scm.eclipse.db.dbInterface.DBConnectAction;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DriverManager;
import org.compiere.mfg_scm.eclipse.db.editors.ProjectListElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * The main plugin class to be used in the desktop.
 */
public class DbfLauncherPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.compiere.mfg_scm.eclipse.db";

	public static final String NATURE_ID = PLUGIN_ID + ".dbfnature";

	static final String DBF_PREF_HOME_KEY = "dbfDir";

	static final String DBF_PREF_BASE_KEY = "dbfBase";

	static final String DBF_PREF_CREAT_KEY = "dbfCreation";

	static final String DBF_PREF_CONFIGFILE_KEY = "dbfConfigFile";

	static final String DBF_PREF_RESOURCEJAR_KEY = "dbfResourceJar";

	static final String DBF_PREF_VERSION_KEY = "dbfVersion";

	static final String DBF_PREF_JRE_KEY = "dbfJRE";

	static final String DBF_PREF_JVM_PARAMETERS_KEY = "jvmParameters";

	static final String DBF_PREF_JVM_CLASSPATH_KEY = "jvmClasspath";

	static final String DBF_PREF_JVM_BOOTCLASSPATH_KEY = "jvmBootClasspath";

	static final String DBF_PREF_COMPUTESOURCEPATH_KEY = "computeSourcePath";

	static final String DBF_PREF_PROJECTSINCP_KEY = "projectsInCp";

	static final String DBF_PREF_PROJECTSINSOURCEPATH_KEY = "projectsInSourcePath";

	static final String DBF_PREF_DEBUGMODE_KEY = "dbfDebugMode";

	static final String DBF_PREF_TARGETPERSPECTIVE = "targetPerspective";

	static final String DBF_PREF_SECURITYMANAGER = "enabledSecurityManager";

	static final String DBF_PREF_MANAGER_URL = "managerUrl";

	static final String DBF_PREF_MANAGER_USER = "managerUser";

	static final String DBF_PREF_MANAGER_PASSWORD = "managerPassword";

	static final String DBF_VERSION10 = "dbfV3";

	static final String DBF_VERSION11 = "dbfV41";

	static final String DBF_PREF_CONFMODE_KEY = "configMode";

	static final String SERVERXML_MODE = "serverXML";

	static final String CONTEXTFILES_MODE = "contextFiles";

	static final String DBF_PREF_CONTEXTSDIR_KEY = "contextsDir";

	private static final String DBF_HOME_CLASSPATH_VARIABLE = "DBF_HOME";

	// The shared instance.
	private static DbfLauncherPlugin plugin;

	// Resource bundle.
	private ResourceBundle resourceBundle;

	private DriverManager driverManager;

	private DBConnectAction dbConnectAction;

	/**
	 * The constructor.
	 */
	public DbfLauncherPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		driverManager = null;
		dbConnectAction = null;
		plugin = this;
		/*
		 * try { resourceBundle = PropertyResourceBundle.getBundle("resources"); }
		 * catch (MissingResourceException x) { resourceBundle = null; }
		 */

		this.getWorkspace()
				.addResourceChangeListener(new DbfProjectChangeListener(),
						IResourceChangeEvent.PRE_DELETE);
	}

	/**
	 * Returns the shared instance.
	 */
	public static DbfLauncherPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * Returns the active shell for this plugin.
	 */
	public static Shell getShell() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow()
				.getShell();
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = DbfLauncherPlugin.getDefault()
				.getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		try {
			resourceBundle = PropertyResourceBundle.getBundle("resources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
		return resourceBundle;
	}

	public String getDbfDir() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_HOME_KEY);
	}

	public String getDbfBase() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_BASE_KEY);
	}

	public String getConfigFile() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_CONFIGFILE_KEY);
	}

	public String getDbfResourcesJar() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_RESOURCEJAR_KEY);
	}

	public String getConfigMode() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_CONFMODE_KEY);
	}

	public String getContextsDir() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_CONTEXTSDIR_KEY);
	}

	public String getDbfVersion() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		String result = pref.getString(DBF_PREF_VERSION_KEY);
		if (result.equals(""))
			result = DBF_VERSION10;

		return result;
	}

	public String getDbfJRE() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		String result = pref.getString(DBF_PREF_JRE_KEY);
		if (result.equals(""))
			result = JavaRuntime.getDefaultVMInstall().getId();

		return result;
	}

	public DriverManager getDriverManager() {
		return (driverManager);
	}

	public DBConnectAction getDBConnectAction() {
		DbfLauncherPlugin.log(IStatus.INFO, DbfLauncherPlugin
				.getResourceString("DbfLauncherPlugin getDBConnectAction"));
		if (dbConnectAction == null)
			DbfLauncherPlugin
					.log(
							IStatus.ERROR,
							DbfLauncherPlugin
									.getResourceString("DbfLauncherPlugin getDBConnectAction null"));
		return (dbConnectAction);
	}

	public void setDBConnectAction(DBConnectAction dbConnectAction) {
		DbfLauncherPlugin.log(IStatus.INFO, DbfLauncherPlugin
				.getResourceString("DbfLauncherPlugin setDBConnectAction"));
		this.dbConnectAction = dbConnectAction;
		return;
	}

	public boolean isDebugMode() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return !pref.getBoolean(DBF_PREF_DEBUGMODE_KEY);
	}

	public String getTargetPerspective() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_TARGETPERSPECTIVE);
	}

	public boolean isSecurityManagerEnabled() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getBoolean(DBF_PREF_SECURITYMANAGER);
	}

	public String getJvmParamaters() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_JVM_PARAMETERS_KEY);
	}

	public String getJvmClasspath() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_JVM_CLASSPATH_KEY);
	}

	public String getJvmBootClasspath() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_JVM_BOOTCLASSPATH_KEY);
	}

	public DbfBootstrap getDbfBootstrap() {
		DbfBootstrap dbfBootsrap = null;

		if (getDbfVersion().equals(DBF_VERSION10)) {
			dbfBootsrap = new Dbf10Bootstrap();
		}
		if (getDbfVersion().equals(DBF_VERSION11)) {
			dbfBootsrap = new Dbf11Bootstrap();
		}

		return dbfBootsrap;
	}

	public String getManagerAppUrl() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_MANAGER_URL);
	}

	public String getManagerAppUser() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_MANAGER_USER);
	}

	public String getManagerAppPassword() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		return pref.getString(DBF_PREF_MANAGER_PASSWORD);
	}

	static public void log(String msg) {
		ILog log = DbfLauncherPlugin.getDefault().getLog();
		Status status = new Status(IStatus.ERROR, DbfLauncherPlugin
				.getDefault().getDescriptor().getUniqueIdentifier(),
				IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	static public void log(int level, String msg) {
		ILog log = DbfLauncherPlugin.getDefault().getLog();
		Status status = new Status(level, DbfLauncherPlugin.getDefault()
				.getDescriptor().getUniqueIdentifier(), level, msg + "\n", null);
		log.log(status);
	}

	static public void log(Exception ex) {
		ILog log = DbfLauncherPlugin.getDefault().getLog();
		StringWriter stringWriter = new StringWriter();
		ex.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();

		Status status = new Status(IStatus.ERROR, DbfLauncherPlugin
				.getDefault().getDescriptor().getUniqueIdentifier(),
				IStatus.ERROR, msg, null);
		log.log(status);
	}

	static public void log(Throwable ex) {
		ILog log = DbfLauncherPlugin.getDefault().getLog();

		Status status = new Status(IStatus.ERROR, DbfLauncherPlugin
				.getDefault().getDescriptor().getUniqueIdentifier(),
				IStatus.ERROR, "Error", ex);
		log.log(status);
	}

	public IPath getDbfIPath() {
		IPath dbfPath = getDbfClasspathVariable();
		if (dbfPath == null) {
			return new Path(DbfLauncherPlugin.getDefault().getDbfDir());
		} else {
			return new Path(DBF_HOME_CLASSPATH_VARIABLE);
		}
	}

	private IPath getDbfClasspathVariable() {
		IPath dbfPath = JavaCore
				.getClasspathVariable(DBF_HOME_CLASSPATH_VARIABLE);
		if (dbfPath == null) {
			this.initDbfClasspathVariable();
			dbfPath = JavaCore
					.getClasspathVariable(DBF_HOME_CLASSPATH_VARIABLE);
		}
		return dbfPath;
	}

	public void initDbfClasspathVariable() {
		try {
			JavaCore.setClasspathVariable(DBF_HOME_CLASSPATH_VARIABLE,
					new Path(DbfLauncherPlugin.getDefault().getDbfDir()), null);
		} catch (JavaModelException e) {
			log(e);
		}
	}

	public void setProjectsInCP(List projectsInCP) {
		this.saveProjectsToPreferenceStore(projectsInCP,
				DBF_PREF_PROJECTSINCP_KEY);
	}

	public List getProjectsInCP() {
		return this.readProjectsFromPreferenceStore(DBF_PREF_PROJECTSINCP_KEY);
	}

	public void setProjectsInSourcePath(List projectsInCP) {
		this.saveProjectsToPreferenceStore(projectsInCP,
				DBF_PREF_PROJECTSINSOURCEPATH_KEY);
	}

	public List getProjectsInSourcePath() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		boolean automaticallyComputed = pref
				.getBoolean(DBF_PREF_COMPUTESOURCEPATH_KEY);

		if (automaticallyComputed) {
			return computeProjectsInSourcePath();
		} else {
			return readProjectsInSourcePathFromPref();
		}
	}

	public List readProjectsInSourcePathFromPref() {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		if (!(pref.contains(DBF_PREF_PROJECTSINSOURCEPATH_KEY))) {
			// Compute source path for a new workspace
			pref.setValue(DBF_PREF_COMPUTESOURCEPATH_KEY, true);
			return computeProjectsInSourcePath();
		} else {
			return DbfLauncherPlugin
					.readProjectsFromPreferenceStore(DBF_PREF_PROJECTSINSOURCEPATH_KEY);
		}
	}

	private List computeProjectsInSourcePath() {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] allProjects = root.getProjects();
		ArrayList tempList = new ArrayList(allProjects.length);

		ArrayList alreadyAdded = new ArrayList();

		for (int i = 0; i < allProjects.length; i++) {
			IProject project = allProjects[i];
			try {
				if ((project.isOpen()) && project.hasNature(NATURE_ID)) {
					IJavaProject javaProject = (IJavaProject) project
							.getNature(JavaCore.NATURE_ID);
					if (!alreadyAdded.contains(project)) {
						tempList.add(new ProjectListElement(javaProject
								.getProject()));
						alreadyAdded.add(project);
					}
					String[] reqProjects = javaProject
							.getRequiredProjectNames();

					for (int j = 0; j < allProjects.length; j++) {
						for (int k = 0; k < reqProjects.length; k++) {
							if (allProjects[j].getName().equals(reqProjects[k])) {
								if ((allProjects[j].isOpen())
										&& allProjects[j]
												.hasNature(JavaCore.NATURE_ID)) {
									if (!alreadyAdded.contains(allProjects[j])) {
										tempList.add(new ProjectListElement(
												allProjects[j].getNature(
														JavaCore.NATURE_ID)
														.getProject()));
										alreadyAdded.add(allProjects[j]);
									}
								}
							}
						}
					}

				}
			} catch (CoreException e) {
				DbfLauncherPlugin.log(e);
			}
		}
		return tempList;
	}

	static void saveProjectsToPreferenceStore(List projectList,
			String keyInPreferenceStore) {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		StringBuffer buf = new StringBuffer();
		Iterator it = projectList.iterator();
		while (it.hasNext()) {
			ProjectListElement each = (ProjectListElement) it.next();
			buf.append(each.getID());
			buf.append(';');
		}
		pref.setValue(keyInPreferenceStore, buf.toString());
	}

	static List readProjectsFromPreferenceStore(String keyInPreferenceStore) {
		IPreferenceStore pref = DbfLauncherPlugin.getDefault()
				.getPreferenceStore();
		String stringList = pref.getString(keyInPreferenceStore);

		List projectsIdList = new ArrayList();
		StringTokenizer tokenizer = new StringTokenizer(stringList, ";");
		while (tokenizer.hasMoreElements()) {
			projectsIdList.add(tokenizer.nextToken());
		}

		return ProjectListElement.stringsToProjectsList(projectsIdList);

	}

	static public boolean checkDbfSettingsAndWarn() {
		if (!isDbfConfigured()) {
			String msg = DbfLauncherPlugin
					.getResourceString("msg.noconfiguration");
			MessageDialog.openWarning(DbfLauncherPlugin.getShell(), "Dbf", msg);
			return false;
		}
		return true;
	}

	static public boolean isDbfConfigured() {
		return !(DbfLauncherPlugin.getDefault().getDbfDir().equals(""));
	}

	public static IEditorPart getActiveEditor() {
		IWorkbenchPage iworkbenchpage0 = getActivePage();
		if (iworkbenchpage0 == null)
			return (null);

		return (iworkbenchpage0.getActiveEditor());
	}

	public static IWorkbenchPage getActivePage() {
		AbstractUIPlugin abstractuiplugin = (AbstractUIPlugin) Platform
				.getPlugin("org.eclipse.ui");
		IWorkbenchWindow iworkbenchwindow1 = abstractuiplugin.getWorkbench()
				.getActiveWorkbenchWindow();
		IWorkbenchPage iworkbenchpage;
		if (iworkbenchwindow1 == null)
			return (null);

		iworkbenchpage = iworkbenchwindow1.getActivePage();
		if (iworkbenchpage == null)
			return (null);

		return (iworkbenchpage);
	}

	public void startup() throws CoreException {
		super.startup();
		driverManager = DriverManager.getInstance();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.Plugin#initializeDefaultPluginPreferences()
	 */
	protected void initializeDefaultPluginPreferences() {
		getPreferenceStore().setDefault(
				DbfLauncherPlugin.DBF_PREF_CONFMODE_KEY,
				DbfLauncherPlugin.SERVERXML_MODE);
		super.initializeDefaultPluginPreferences();
	}
}
