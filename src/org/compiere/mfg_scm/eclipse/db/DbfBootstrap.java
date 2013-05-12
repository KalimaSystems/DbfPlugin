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
import java.util.Iterator;
import java.util.List;

import org.compiere.mfg_scm.eclipse.db.editors.ProjectListElement;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.sourcelookup.JavaSourceLocator;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Connect and disconnect Dbf Subclasses contains all information specific to a
 * Dbf Version
 */

public abstract class DbfBootstrap {

	private static final int RUN = 1;

	private static final int LOG = 2;

	private static final int ADD_LAUNCH = 3;

	public abstract String[] getClasspath();

	public abstract String[] getVmArgs();

	public abstract String[] getPrgArgs(String command);

	public abstract String getConnectCommand();

	public abstract String getDisconnectCommand();

	public abstract String getMainClass();

	abstract public String getLabel();

	abstract public String getContextWorkDir(String workFolder);

	/**
	 * See %DBF_HOME%/bin/startup.bat
	 */
	public void connect() throws CoreException {
		this.runDbfBootsrap(getConnectCommand(), true, RUN, true);
	}

	/**
	 * See %DBF_HOME%/bin/shutdown.bat
	 */
	public void disconnect() throws CoreException {
		this.runDbfBootsrap(getDisconnectCommand(), false, RUN, false);
	}

	/**
	 * Simply disconnect and connect
	 */
	public void reconnect() throws CoreException {
		this.disconnect();

		// Hack, need more testings
		try {
			Thread.sleep(5000);
		} catch (InterruptedException ex) {
		}

		this.connect();
	}

	/**
	 * Write dbf laucnh configuration to .metadata/.log
	 */
	public void logConfig() throws CoreException {
		this.runDbfBootsrap(getConnectCommand(), true, LOG, false);
	}

	/**
	 * Create an Eclipse launch configuration
	 */
	public void addLaunch() throws CoreException {
		this.runDbfBootsrap(getConnectCommand(), true, ADD_LAUNCH, true);
	}

	/**
	 * Launch a new JVM running Dbf Main class Set classpath, bootclasspath and
	 * environment variable
	 */
	private void runDbfBootsrap(String dbfBootOption, boolean showInDebugger,
			int action, boolean saveConfig) throws CoreException {
		String[] prgArgs = this.getPrgArgs(dbfBootOption);

		IProject[] projects = DbfLauncherPlugin.getWorkspace().getRoot()
				.getProjects();

		for (int i = 0; i < projects.length; i++) {
			if (!projects[i].isOpen())
				continue;
			DbfProject dbfProject = (DbfProject) projects[i]
					.getNature(DbfLauncherPlugin.NATURE_ID);
			if (dbfProject != null) {
				ArrayList al = new ArrayList();
				ArrayList visitedProjects = new ArrayList(); /* IMC */
				IJavaProject javaProject = (IJavaProject) projects[i]
						.getNature(JavaCore.NATURE_ID);
				DbfClassPathEntries entries = dbfProject
						.getDbfClassPathEntries();
				if (entries != null) {
					getClassPathEntries(javaProject, al, entries.getList(),
							visitedProjects);

				}
			}
		}

		String[] classpath = new String[0];
		classpath = addPreferenceJvmToClasspath(classpath);
		classpath = addPreferenceProjectListToClasspath(classpath);
		classpath = StringUtil.concatUniq(classpath, this.getClasspath());

		String[] vmArgs = this.getVmArgs();
		vmArgs = addPreferenceParameters(vmArgs);

		String[] bootClasspath = addPreferenceJvmToBootClasspath(new String[0]);

		StringBuffer programArguments = new StringBuffer();
		for (int i = 0; i < prgArgs.length; i++) {
			programArguments.append(" " + prgArgs[i]);
		}

		StringBuffer jvmArguments = new StringBuffer();
		for (int i = 0; i < vmArgs.length; i++) {
			jvmArguments.append(" " + vmArgs[i]);
		}

		if (action == RUN) {
			VMLauncherUtility.runVM(getLabel(), getMainClass(), classpath,
					bootClasspath, jvmArguments.toString(), programArguments
							.toString(), getSourceLocator(), isDebugMode(),
					showInDebugger, saveConfig);
		}
		if (action == LOG) {
			VMLauncherUtility.log(getLabel(), getMainClass(), classpath,
					bootClasspath, jvmArguments.toString(), programArguments
							.toString(), getSourceLocator(), isDebugMode(),
					showInDebugger);
		}
		if (action == ADD_LAUNCH) {
			VMLauncherUtility.createConfig(getLabel(), getMainClass(),
					classpath, bootClasspath, jvmArguments.toString(),
					programArguments.toString(), getSourceLocator(),
					isDebugMode(), showInDebugger, true);
		}

	}

	private void add(ArrayList data, IPath entry) {
		if (entry.isAbsolute() == false)
			entry = entry.makeAbsolute();
		String tmp = entry.toFile().toString();
		if (!data.contains(tmp)) {
			data.add(tmp);
		}
	}

	private void add(ArrayList data, IResource con) {
		if (con == null)
			return;
		add(data, con.getLocation());
	}

	private void getClassPathEntries(IJavaProject prj, ArrayList data,
			List selectedPaths, ArrayList visitedProjects) {
		IClasspathEntry[] entries = null;

		IPath outputPath = null;
		try {
			outputPath = prj.getOutputLocation();
			if (selectedPaths.contains(outputPath.toFile().toString().replace(
					'\\', '/'))) {
				add(data, prj.getProject().getWorkspace().getRoot().findMember(
						outputPath));
			}
			entries = prj.getRawClasspath();
		} catch (JavaModelException e) {
			DbfLauncherPlugin.log(e);
		}
		if (entries == null)
			return;
		for (int i = 0; i < entries.length; i++) {
			IClasspathEntry entry = entries[i];
			IPath path = entry.getPath();
			if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
				path = entry.getOutputLocation();
				if (path == null)
					continue;
			}
			if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
				String prjName = entry.getPath().lastSegment();
				if (!visitedProjects.contains(prjName)) {
					visitedProjects.add(prjName);
					getClassPathEntries(prj.getJavaModel().getJavaProject(
							prjName), data, selectedPaths, visitedProjects);
				}
				continue;
			} else if (!selectedPaths.contains(path.toFile().toString()
					.replace('\\', '/')))
				continue;

			IClasspathEntry[] tmpEntry = null;
			if (entry.getEntryKind() == IClasspathEntry.CPE_CONTAINER) {
				try {
					tmpEntry = JavaCore.getClasspathContainer(path, prj)
							.getClasspathEntries();
				} catch (JavaModelException e1) {
					DbfLauncherPlugin.log(e1);
					continue;
				}
			} else {
				tmpEntry = new IClasspathEntry[1];
				tmpEntry[0] = JavaCore.getResolvedClasspathEntry(entry);
			}

			for (int j = 0; j < tmpEntry.length; j++) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
					IResource res = prj.getProject().getWorkspace().getRoot()
							.findMember(tmpEntry[j].getPath());
					if (res != null)
						add(data, res);
					else
						add(data, tmpEntry[j].getPath());
				} else if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath srcPath = entry.getOutputLocation();
					if (srcPath != null && !srcPath.equals(outputPath)) {
						add(data, prj.getProject().getWorkspace().getRoot()
								.findMember(srcPath));
					}
				} else {
					add(data, tmpEntry[j].getPath());
				}
			}
		}
	}

	private boolean isDebugMode() {
		return DbfLauncherPlugin.getDefault().isDebugMode();
	}

	/**
	 * Set dbf process source locator. Initial implementation is to return all
	 * Java Projects This could decrease debugger performance (I don't know,
	 * have to ask to eclipse team) Todo after 2.0 is released : add
	 * sourceLocator only for allDbfProjects
	 */

	private ISourceLocator getSourceLocator() throws CoreException {
		ArrayList tempList = new ArrayList();

		List projects = DbfLauncherPlugin.getDefault()
				.getProjectsInSourcePath();
		for (Iterator iter = projects.iterator(); iter.hasNext();) {
			IProject project = ((ProjectListElement) iter.next()).getProject();

			if ((project.isOpen()) && project.hasNature(JavaCore.NATURE_ID)) {
				tempList.add(project.getNature(JavaCore.NATURE_ID));
			}
		}

		ISourceLocator sourceLocator = null;
		if (!tempList.isEmpty()) {
			IJavaProject[] javaProjects = (IJavaProject[]) tempList
					.toArray(new IJavaProject[1]);
			sourceLocator = new JavaSourceLocator(javaProjects, true);
		}

		return sourceLocator;
	}

	protected String getDbfDir() {
		return DbfLauncherPlugin.getDefault().getDbfDir();
	}

	protected String getDbfBase() {
		return DbfLauncherPlugin.getDefault().getDbfBase();
	}

	private String[] addPreferenceProjectListToClasspath(String[] previouscp) {
		List projectsList = DbfLauncherPlugin.getDefault().getProjectsInCP();
		String[] result = previouscp;
		Iterator it = projectsList.iterator();
		while (it.hasNext()) {
			try {
				ProjectListElement ple = (ProjectListElement) it.next();
				IJavaProject jproject = JavaCore.create(ple.getProject());
				result = this.addProjectToClasspath(result, jproject);
			} catch (Exception e) {
				// nothing will be added to classpath
			}
		}

		return result;

	}

	private String[] addProjectToClasspath(String[] previouscp,
			IJavaProject project) throws CoreException {
		// IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
		// IJavaProject project =
		// JavaCore.getJavaCore().create(root.getProject(projectName));
		if ((project != null) && (project.exists() && project.isOpen())) {
			String[] projectcp = JavaRuntime
					.computeDefaultRuntimeClassPath(project);
			return StringUtil.concatUniq(projectcp, previouscp);
		} else {
			return previouscp;
		}
	}

	private String[] addPreferenceParameters(String[] previous) {
		String[] prefParams = StringUtil.cutString(DbfLauncherPlugin
				.getDefault().getJvmParamaters(),
				DbfPluginResources.PREF_PAGE_LIST_SEPARATOR);
		return StringUtil.concat(previous, prefParams);
	}

	private String[] addPreferenceJvmToClasspath(String[] previous) {
		String[] prefClasspath = StringUtil.cutString(DbfLauncherPlugin
				.getDefault().getJvmClasspath(),
				DbfPluginResources.PREF_PAGE_LIST_SEPARATOR);
		return StringUtil.concatUniq(previous, prefClasspath);
	}

	private String[] addPreferenceJvmToBootClasspath(String[] previous) {
		String[] prefBootClasspath = StringUtil.cutString(DbfLauncherPlugin
				.getDefault().getJvmBootClasspath(),
				DbfPluginResources.PREF_PAGE_LIST_SEPARATOR);
		return StringUtil.concatUniq(previous, prefBootClasspath);
	}

}
