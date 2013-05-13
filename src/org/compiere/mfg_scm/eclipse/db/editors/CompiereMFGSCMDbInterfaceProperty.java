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

package org.compiere.mfg_scm.eclipse.db.editors;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFileState;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.jobs.ISchedulingRule;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class CompiereMFGSCMDbInterfaceProperty implements IFile {

	public static String m_PropertieName = System.getProperty("user.home")
			+ System.getProperty("file.separator")
			+ "CompiereMFGSCMDbInterface.properties";

	public CompiereMFGSCMDbInterfaceProperty() {
		return;
	}

	public void appendContents(InputStream inputstream, boolean i, boolean j,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void appendContents(InputStream inputstream, int i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void create(InputStream inputstream, boolean i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void create(InputStream inputstream, int i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void delete(boolean i, boolean j, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public InputStream getContents() throws CoreException {
		try {
			return (new FileInputStream(
					CompiereMFGSCMDbInterfaceProperty.m_PropertieName));
		} catch (FileNotFoundException filenotfoundexception) {
			return (null);
		}
	}

	public InputStream getContents(boolean i) throws CoreException {
		try {
			return (new FileInputStream(
					CompiereMFGSCMDbInterfaceProperty.m_PropertieName));
		} catch (FileNotFoundException filenotfoundexception) {
			return (null);
		}
	}

	public int getEncoding() throws CoreException {
		return (0);
	}

	public IPath getFullPath() {
		String string = System.getProperty("user.home");
		Path path = new Path(string);
		return (path);
	}

	public IFileState[] getHistory(IProgressMonitor iprogressmonitor)
			throws CoreException {
		return (null);
	}

	public String getName() {
		return (null);
	}

	public boolean isReadOnly() {
		return (false);
	}

	public void move(IPath ipath, boolean i, boolean j,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void setContents(InputStream inputstream, boolean i, boolean j,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void setContents(IFileState ifilestate, boolean i, boolean j,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void setContents(InputStream inputstream, int i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void setContents(IFileState ifilestate, int i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void accept(IResourceVisitor iresourcevisitor) throws CoreException {
		return;
	}

	public void accept(IResourceVisitor iresourcevisitor, int i, boolean j)
			throws CoreException {
		return;
	}

	public void accept(IResourceVisitor iresourcevisitor, int i, int j)
			throws CoreException {
		return;
	}

	public void clearHistory(IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void copy(IProjectDescription iprojectdescription, boolean i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void copy(IPath ipath, boolean i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void copy(IProjectDescription iprojectdescription, int i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void copy(IPath ipath, int i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public IMarker createMarker(String string) throws CoreException {
		return (null);
	}

	public void delete(boolean i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void delete(int i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void deleteMarkers(String string, boolean i, int j)
			throws CoreException {
		return;
	}

	public boolean exists() {
		try {
			InputStream inputstream = getContents();
			if (inputstream != null) {
				inputstream.close();
				return (true);
			}
		} catch (CoreException coreexception) {
		} catch (IOException ioexception) {
		}
		return (false);
	}

	public IMarker findMarker(long i) throws CoreException {
		return (null);
	}

	public IMarker[] findMarkers(String string, boolean i, int j)
			throws CoreException {
		return (null);
	}

	public String getFileExtension() {
		return ("CompiereMFGSCMDbInterface.properties");
	}

	public IPath getLocation() {
		return (null);
	}

	public IMarker getMarker(long i) {
		return (null);
	}

	public long getModificationStamp() {
		return (0);
	}

	public IContainer getParent() {
		return (null);
	}

	public String getPersistentProperty(QualifiedName qualifiedname)
			throws CoreException {
		return (null);
	}

	public IProject getProject() {
		return (null);
	}

	public IPath getProjectRelativePath() {
		return (null);
	}

	public Object getSessionProperty(QualifiedName qualifiedname)
			throws CoreException {
		return (null);
	}

	public int getType() {
		return (0);
	}

	public IWorkspace getWorkspace() {
		return (null);
	}

	public boolean isAccessible() {
		return (false);
	}

	public boolean isLocal(int i) {
		return (false);
	}

	public boolean isPhantom() {
		return (false);
	}

	public boolean isSynchronized(int i) {
		return (false);
	}

	public void move(IProjectDescription iprojectdescription, boolean i,
			boolean j, IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void move(IPath ipath, boolean i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void move(IProjectDescription iprojectdescription, int i,
			IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public void move(IPath ipath, int i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void refreshLocal(int i, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void setLocal(boolean i, int j, IProgressMonitor iprogressmonitor)
			throws CoreException {
		return;
	}

	public void setPersistentProperty(QualifiedName qualifiedname, String string)
			throws CoreException {
		return;
	}

	public void setReadOnly(boolean i) {
		return;
	}

	public void setSessionProperty(QualifiedName qualifiedname, Object object)
			throws CoreException {
		return;
	}

	public void touch(IProgressMonitor iprogressmonitor) throws CoreException {
		return;
	}

	public boolean isDerived() {
		return (false);
	}

	public void setDerived(boolean i) throws CoreException {
		return;
	}

	public boolean isTeamPrivateMember() {
		return (false);
	}

	public void setTeamPrivateMember(boolean i) throws CoreException {
		return;
	}

	public Object getAdapter(Class class1) {
		return (null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IFile#createLink(org.eclipse.core.runtime.IPath,
	 *      int, org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void createLink(IPath arg0, int arg1, IProgressMonitor arg2)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IFile#getCharset()
	 */
	public String getCharset() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IFile#getCharset(boolean)
	 */
	public String getCharset(boolean arg0) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IFile#getContentDescription()
	 */
	public IContentDescription getContentDescription() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String)
	 */
	public void setCharset(String arg0) throws CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IFile#setCharset(java.lang.String,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void setCharset(String arg0, IProgressMonitor arg1)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResource#accept(org.eclipse.core.resources.IResourceProxyVisitor,
	 *      int)
	 */
	public void accept(IResourceProxyVisitor arg0, int arg1)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResource#getLocalTimeStamp()
	 */
	public long getLocalTimeStamp() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResource#getRawLocation()
	 */
	public IPath getRawLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResource#isLinked()
	 */
	public boolean isLinked() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.resources.IResource#setLocalTimeStamp(long)
	 */
	public long setLocalTimeStamp(long arg0) throws CoreException {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#contains(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean contains(ISchedulingRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.ISchedulingRule#isConflicting(org.eclipse.core.runtime.jobs.ISchedulingRule)
	 */
	public boolean isConflicting(ISchedulingRule rule) {
		// TODO Auto-generated method stub
		return false;
	}

	public ResourceAttributes getResourceAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	public void revertModificationStamp(long value) throws CoreException {
		// TODO Auto-generated method stub

	}

	public void setResourceAttributes(ResourceAttributes attributes)
			throws CoreException {
		// TODO Auto-generated method stub

	}

	public String getCharsetFor(Reader reader) throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

}
