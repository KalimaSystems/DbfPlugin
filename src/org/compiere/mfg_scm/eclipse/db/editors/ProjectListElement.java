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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class ProjectListElement {

	IProject project;

	public ProjectListElement(IProject project) {
		super();
		this.project = project;
	}

	public String toString() {
		return getID(project);
	}

	public String getID() {
		return getID(project);
	}

	static protected String getID(IProject project) {
		return project.getName();
	}

	static public List stringsToProjectsList(List projectIdList) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject[] projects = root.getProjects();

		List selectedProjects = new ArrayList();
		for (int i = 0; i < projects.length; i++) {
			if (projectIdList.contains(getID(projects[i]))) {
				selectedProjects.add(new ProjectListElement(projects[i]));
			}
		}

		return selectedProjects;
	}

	/*
	 * @see Object#equals(Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof ProjectListElement)
			return this.getID().equals(((ProjectListElement) obj).getID());

		return false;
	}

	/*
	 * @see Object#hashCode()
	 */
	public int hashCode() {
		return this.getID().hashCode();
	}

	/**
	 * Gets the project.
	 * 
	 * @return Returns a IProject
	 */
	public IProject getProject() {
		return project;
	}

}
