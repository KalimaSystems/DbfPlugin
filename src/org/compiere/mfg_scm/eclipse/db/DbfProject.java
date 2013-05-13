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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

import org.compiere.mfg_scm.eclipse.db.editors.ProjectListElement;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfProject extends PlatformObject implements IProjectNature {

	// Persistence properties of projects
	private static final String PROPERTIES_FILENAME = ".dbfplugin";

	private static final String INTERFACE_PROPERTIES_FILENAME = ".dbfinterface";

	private static final String KEY_OJBPATH = "ojbPath";

	private static final String KEY_UPDATEXML = "updateXml";

	private static final String KEY_EXPORTSOURCE = "exportSource";

	private static final String KEY_RELOADABLE = "reloadable";

	private static final String KEY_REDIRECTLOGGER = "redirectLogger";

	private static final String KEY_DTDURL = "dtdUrl";

	private static final String KEY_EXTRAINFO = "extraInfo";

	/**
	 * The platform project this <code>DbfProject</code> is based on
	 */
	protected IProject project;

	protected IJavaProject javaProject;

	protected String ojbPath = "";

	protected String dtdUrl = "";

	protected String extraInfo = "";

	protected boolean updateXml;

	protected boolean exportSource;

	protected boolean reloadable = true;

	protected boolean redirectLogger = false;

	protected DbfClassPathEntries dbfClassPathEntries;

	protected IFolder dtdUrlFolder;

	/**
	 * Gets the project.
	 * 
	 * @return Returns a IProject
	 */
	public IProject getProject() {
		return project;
	}

	/**
	 * Sets the project.
	 * 
	 * @param project
	 *            The project to set
	 */
	public void setProject(IProject project) {
		this.project = project;
	}

	/*
	 * @see IProjectNature#configure()
	 */
	public void configure() throws CoreException {
	}

	/*
	 * @see IProjectNature#deconfigure()
	 */
	public void deconfigure() throws CoreException {
	}

	/*
	 * @see IProjectNature#getProject()
	 */
	public IJavaProject getJavaProject() {
		return javaProject;
	}

	/*
	 * @see IProjectNature#setProject(IProject)
	 */
	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
		this.setProject(javaProject.getProject());
	}

	static public void addDbfNature(IJavaProject project) {
		try {
			JDTUtil.addNatureToProject(project.getProject(),
					DbfLauncherPlugin.NATURE_ID);
		} catch (CoreException ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}
	}

	static public void removeDbfNature(IJavaProject project) {
		try {
			JDTUtil.removeNatureToProject(project.getProject(),
					DbfLauncherPlugin.NATURE_ID);
		} catch (CoreException ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}
	}

	/**
	 * Return a DbfProject if this javaProject has the dbf nature Return null if
	 * Project has not dbf nature
	 */
	static public DbfProject create(IJavaProject javaProject) {
		DbfProject result = null;
		try {
			result = (DbfProject) javaProject.getProject().getNature(
					DbfLauncherPlugin.NATURE_ID);
			if (result != null)
				result.setJavaProject(javaProject);
		} catch (CoreException ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}
		return result;
	}

	/**
	 * Return a DbfProject if this Project has the dbf nature Return null if
	 * Project has not dbf nature
	 */
	static public DbfProject create(IProject project) {

		IJavaProject javaProject = JavaCore.create(project);
		if (javaProject != null) {
			return DbfProject.create(javaProject);
		} else {
			return null;
		}
	}

	private File getPropertiesFile() {
		return (this.getProject().getLocation().append(PROPERTIES_FILENAME)
				.toFile());
	}

	private String readProperty(String key) {
		String result = null;
		try {
			result = FileUtil.readPropertyInXMLFile(getPropertiesFile(), key);
		} catch (IOException e) {
			try {
				result = getJavaProject().getCorrespondingResource()
						.getPersistentProperty(
								new QualifiedName("DbfProject", key));
			} catch (Exception e2) {
				DbfLauncherPlugin.log(e2);
			}
		}

		if (result == null) {
			result = "";
		}

		return result;
	}

	public File getInterfacePropertiesFile() {
		return (this.getProject().getLocation().append(
				INTERFACE_PROPERTIES_FILENAME).toFile());
	}

	/**
	 * Gets the dtdUrl.
	 * 
	 * @return Returns a String
	 */
	public String getDtdUrl() {
		return this.readProperty(KEY_DTDURL);
	}

	/**
	 * Sets the dtdUrl.
	 * 
	 * @param dtdUrl
	 *            The dtdUrl to set
	 */
	public void setDtdUrl(String rd) {
		this.dtdUrl = rd;
		this.dtdUrlFolder = null;
	}

	/**
	 * Gets the ojbPath.
	 * 
	 * @return Returns a String
	 */
	public String getOjbPath() {
		return this.readProperty(KEY_OJBPATH);
	}

	/**
	 * Sets the ojbPath.
	 * 
	 * @param ojbPath
	 *            The ojbPath to set
	 */
	public void setOjbPath(String wp) {
		this.ojbPath = wp;
	}

	public void updateOjbPath(String newOjbPath) throws Exception {
		setOjbPath(newOjbPath);
		if (!newOjbPath.equals(this.getOjbPath())) {
			if (getUpdateXml()) {
				// removeContext();
			}
		}
	}

	/**
	 * Gets the updateXml.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getUpdateXml() {
		return new Boolean(this.readProperty(KEY_UPDATEXML)).booleanValue();
	}

	/**
	 * Sets the updateXml.
	 * 
	 * @param updateXml
	 *            The updateXml to set
	 */
	public void setUpdateXml(boolean updateXml) {
		this.updateXml = updateXml;
	}

	/**
	 * Gets the updateXml.
	 * 
	 * @return Returns a boolean
	 */
	public boolean getExportSource() {
		return new Boolean(this.readProperty(KEY_EXPORTSOURCE)).booleanValue();
	}

	/**
	 * Sets the exportSource.
	 * 
	 * @param exportSource
	 *            The exportSource to set
	 */
	public void setExportSource(boolean exportSource) {
		this.exportSource = exportSource;
	}

	/**
	 * Gets the reloadable
	 * 
	 * @return Returns a boolean
	 */
	public boolean getReloadable() {
		String reloadableProperty = this.readProperty(KEY_RELOADABLE);
		// Set default value to true
		if (reloadableProperty.equals("")) {
			reloadableProperty = "true";
		}
		return new Boolean(reloadableProperty).booleanValue();
	}

	/**
	 * Sets the reloadable
	 * 
	 * @param reloadable
	 *            The reloadable to set
	 */
	public void setReloadable(boolean reloadable) {
		this.reloadable = reloadable;
	}

	/**
	 * Gets the reloadable
	 * 
	 * @return Returns a boolean
	 */
	public boolean getRedirectLogger() {
		String redirectLoggerProperty = this.readProperty(KEY_REDIRECTLOGGER);
		// Set default value to false
		if (redirectLoggerProperty.equals("")) {
			redirectLoggerProperty = "false";
		}
		return new Boolean(redirectLoggerProperty).booleanValue();
	}

	/**
	 * Sets the reloadable
	 * 
	 * @param reloadable
	 *            The reloadable to set
	 */
	public void setRedirectLogger(boolean redirectLogger) {
		this.redirectLogger = redirectLogger;
	}

	/**
	 * Gets the extraInfo.
	 * 
	 * @return Returns a String
	 */
	public String getExtraInfo() {
		return URLDecoder.decode(this.readProperty(KEY_EXTRAINFO));
	}

	/**
	 * Sets the extraInfo
	 * 
	 * @param extraInfo
	 *            The extraInfo to set
	 */
	public void setExtraInfo(String extra) {
		this.extraInfo = extra;
	}

	/**
	 * set the classpath entries which shall be loaded by the ojbclassloader
	 * 
	 * @param entries
	 *            List of OjbClasspathEntry objects
	 */
	public void setDbfClassPathEntries(DbfClassPathEntries entries) {
		dbfClassPathEntries = entries;
	}

	/**
	 * return the ojbclasspath entries
	 */
	public DbfClassPathEntries getDbfClassPathEntries() {
		try {
			return DbfClassPathEntries.xmlUnmarshal(FileUtil
					.readTextFile(getPropertiesFile()));
		} catch (IOException ioEx) {
			return null;
		}
	}

	/*
	 * Store exportSource in project persistent properties
	 */
	public void saveProperties() {
		try {
			StringBuffer fileContent = new StringBuffer();
			fileContent.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			fileContent.append("<dbfProjectProperties>\n");
			fileContent.append("    <dtdUrl>" + dtdUrl + "</dtdUrl>\n");
			fileContent.append("    <exportSource>" + exportSource
					+ "</exportSource>\n");
			fileContent.append("    <reloadable>" + reloadable
					+ "</reloadable>\n");
			fileContent.append("    <redirectLogger>" + redirectLogger
					+ "</redirectLogger>\n");
			fileContent
					.append("    <updateXml>" + updateXml + "</updateXml>\n");
			fileContent.append("    <extraInfo>"
					+ URLEncoder.encode(extraInfo, "UTF-8") + "</extraInfo>\n");
			fileContent.append("    <ojbPath>" + ojbPath + "</ojbPath>\n");
			if (dbfClassPathEntries != null) {
				fileContent.append(dbfClassPathEntries.xmlMarshal(4));
			}
			fileContent.append("</dbfProjectProperties>\n");
			FileUtil.toTextFile(getPropertiesFile(), fileContent.toString());
		} catch (Exception ex) {
			DbfLauncherPlugin.log(ex.getMessage());
		}
	}

	/**
	 * Run all the steps to configure a JavaProject as a DbfProject
	 */
	public void fullConfiguration() throws CoreException, IOException {
		if (!dtdUrl.equals("")) {
			this.initDtdUrlFolder(true);
		}

		this.addProjectToSourcePathPref();
		this.createOJBAPPFolder();
		this.createOJBAPPSrcFolder();
		this.createWorkFolder();
		this.createDbfinterfacefile();

		this.addOJBAPPLibJarFilesToProjectClasspath();

		this.clearDefaultSourceEntries();
		this.setClassesAsOutputFolder();
		if (classesContainsJavaFiles()) {
			this.setClassesAsSourceFolder();
		}

		this.setSrcAsSourceFolder();
		this.setOJBAPPSrcAsSourceFolder();
		this.setWorkAsSourceFolder();

	}

	public void clearDefaultSourceEntries() throws CoreException {
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		List cp = new ArrayList(entries.length + 1);
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getEntryKind() != IClasspathEntry.CPE_SOURCE) {
				cp.add(entries[i]);
			}
		}
		javaProject.setRawClasspath((IClasspathEntry[]) cp
				.toArray(new IClasspathEntry[cp.size()]), null);
	}

	/*
	 * Add all jar in OJB-APP/lib to project classpath
	 */
	public void addOJBAPPLibJarFilesToProjectClasspath() throws CoreException {
		IFolder libFolder = this.getOjbAppFolder().getFolder("lib");
		IResource[] libFiles = libFolder.members();

		IClasspathEntry[] entries = javaProject.getRawClasspath();
		List cp = new ArrayList(entries.length + 1);
		cp.addAll(Arrays.asList(entries));

		for (int i = 0; i < libFiles.length; i++) {
			if ((libFiles[i].getType() == IResource.FILE)
					&& (libFiles[i].getFileExtension().equalsIgnoreCase("jar"))) {
				cp.add(JavaCore.newLibraryEntry(libFiles[i].getFullPath(),
						null, null));
			}
		}

		javaProject.setRawClasspath((IClasspathEntry[]) cp
				.toArray(new IClasspathEntry[cp.size()]), null);
	}

	public IFolder getOjbAppFolder() {
		if (getDtdUrlFolder() == null) {
			return project.getFolder("OJB-APP");
		} else {
			return getDtdUrlFolder().getFolder("OJB-APP");
		}
	}

	public IFolder getWorkFolder() {
		return project.getFolder("work");

	}

	public IFolder getDtdUrlFolder() {
		if (dtdUrlFolder == null) {
			this.initDtdUrlFolder(false);
		}
		return dtdUrlFolder;
	}

	private IFolder initDtdUrlFolder(boolean create) {
		// FIXME Utilite de initDtdUrlFolder ???
		StringTokenizer tokenizer = new StringTokenizer(this.getDtdUrl(),
				"/\\:");
		IFolder folder = null;
		try {
			while (tokenizer.hasMoreTokens()) {
				String each = tokenizer.nextToken();
				if (folder == null) {
					folder = project.getFolder(each);
				} else {
					folder = folder.getFolder(each);
				}
				if (create) {
					this.createFolder(folder);
				}
			}
		} catch (CoreException ex) {
			DbfLauncherPlugin.log(ex);
			folder = null;
			setDtdUrl("/");
		}
		this.dtdUrlFolder = folder;
		return folder;
	}

	public void createOJBAPPFolder() throws CoreException {
		// FIXME Utilite de createOJBAPPFolder ???
		IFolder ojbappFolder = this.getOjbAppFolder();
		this.createFolder(ojbappFolder);
		this.createFolder(ojbappFolder.getFolder("classes"));
		this.createFolder(ojbappFolder.getFolder("lib"));

		// Create .cvsignore for classes
		this.createFile(ojbappFolder.getFile(".cvsignore"), "classes");
	}

	public void createOJBAPPSrcFolder() throws CoreException {
		// FIXME Utilite de createOJBAPPFolder ???
		this.createFolder(this.getOjbAppFolder().getFolder("src"));
	}

	public void createWorkFolder() throws CoreException {
		// FIXME Utilite de createWorkFolder ???
		IFolder folderHandle = this.getWorkFolder();
		this.createFolder(folderHandle);

		String dbfVersion = DbfLauncherPlugin.getDefault().getDbfVersion();
		if (dbfVersion.equals(DbfLauncherPlugin.DBF_VERSION10)
				|| dbfVersion.equals(DbfLauncherPlugin.DBF_VERSION11)) {
			folderHandle = folderHandle.getFolder("org");
			this.createFolder(folderHandle);
			folderHandle = folderHandle.getFolder("compiere");
			this.createFolder(folderHandle);
			folderHandle = folderHandle.getFolder("mfg_scm");
			this.createFolder(folderHandle);
			folderHandle = folderHandle.getFolder("dbManager");
			this.createFolder(folderHandle);
		}

		// Add a .cvsignore file in work directory
		this.createFile(project.getFile(".cvsignore"), "work");

	}

	public void createDbfinterfacefile() throws CoreException {

		this.createFile(project.getFile(".dbfinterface"), "");

	}

	public void setSrcAsSourceFolder() throws CoreException {
		// this.setFolderAsSourceEntry(project.getFolder("src"));
	}

	public void setOJBAPPSrcAsSourceFolder() throws CoreException {
		this.setFolderAsSourceEntry(this.getOjbAppFolder().getFolder("src"),
				null);
	}

	public void setClassesAsOutputFolder() throws CoreException {
		IFolder classesFolder = this.getOjbAppFolder().getFolder("classes");
		javaProject.setOutputLocation(classesFolder.getFullPath(), null);
	}

	public void setClassesAsSourceFolder() throws CoreException {
		IFolder classesFolder = this.getOjbAppFolder().getFolder("classes");
		this.setFolderAsSourceEntry(classesFolder, null);
	}

	public void setWorkAsSourceFolder() throws CoreException {
		this.setFolderAsSourceEntry(this.getWorkFolder(), this.getWorkFolder());
	}

	private void createFolder(IFolder folderHandle) throws CoreException {
		try {
			// Create the folder resource in the workspace
			folderHandle.create(false, true, null); // new
		} catch (CoreException e) {
			// If the folder already existed locally, just refresh to get
			// contents
			if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED)
				folderHandle.refreshLocal(IResource.DEPTH_INFINITE, null);
			else
				throw e;
		}
	}

	private void createFile(IFile fileHandle, String content)
			throws CoreException {
		try {
			fileHandle.create(new ByteArrayInputStream(content.getBytes()), 0,
					null);

		} catch (CoreException e) {
			// If the file already existed locally, just refresh to get contents
			if (e.getStatus().getCode() == IResourceStatus.PATH_OCCUPIED)
				fileHandle.refreshLocal(IResource.DEPTH_INFINITE, null);
			else
				throw e;
		}
	}

	/*
	 * ouput could be null (project default output will be used)
	 */
	private void setFolderAsSourceEntry(IFolder folderHandle, IFolder output)
			throws CoreException {
		IClasspathEntry[] entries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[entries.length + 1];
		System.arraycopy(entries, 0, newEntries, 0, entries.length);
		IPath outputPath = null;
		if (output != null)
			outputPath = output.getFullPath();

		IPath[] emptyPath = {};
		newEntries[entries.length] = JavaCore.newSourceEntry(folderHandle
				.getFullPath(), emptyPath, outputPath);

		javaProject.setRawClasspath(newEntries, null);
	}

	/*
	 * if OJB-APP classes contains Java files add it to source folders Otherwise
	 * Eclipse will delete all those files
	 */
	private boolean classesContainsJavaFiles() {
		IFolder ojbappFolder = this.getOjbAppFolder();
		IFolder classesFolder = ojbappFolder.getFolder("classes");
		File f = classesFolder.getLocation().toFile();

		if (!f.exists())
			return false;
		if (!f.isDirectory())
			return false;

		return FileUtil.dirContainsFiles(f, "java", true);
	}

	/*
	 * A new Dbf project should be checked by default in source path preference
	 * page
	 */
	private void addProjectToSourcePathPref() {
		List projects = DbfLauncherPlugin.getDefault()
				.getProjectsInSourcePath();
		ProjectListElement ple = new ProjectListElement(getProject());
		if (!projects.contains(ple)) {
			projects.add(ple);
			DbfLauncherPlugin.getDefault().setProjectsInSourcePath(projects);
		}
	}
}
