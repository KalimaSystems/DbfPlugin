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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Utility class for JDT It might exist better way to implements those
 * operations, or they might already exist in JDT
 */

public class JDTUtil {

	/**
	 * Adds a nature to a project (From BuildPathsBlock class)
	 */

	public static void addNatureToProject(IProject project, String natureId)
			throws CoreException {
		IProject proj = project.getProject(); // Needed if project is a
		// IJavaProject
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();

		int natureIndex = -1;
		for (int i = 0; i < prevNatures.length; i++) {
			if (prevNatures[i].equals(natureId)) {
				natureIndex = i;
				i = prevNatures.length;
			}
		}

		// Add nature only if it is not already there
		if (natureIndex == -1) {
			String[] newNatures = new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length] = natureId;
			description.setNatureIds(newNatures);
			proj.setDescription(description, null);
		}
	}

	/**
	 * Remove a Nature to a Project
	 */

	public static void removeNatureToProject(IProject project, String natureId)
			throws CoreException {
		IProject proj = project.getProject(); // Needed if project is a
		// IJavaProject
		IProjectDescription description = proj.getDescription();
		String[] prevNatures = description.getNatureIds();

		int natureIndex = -1;
		for (int i = 0; i < prevNatures.length; i++) {
			if (prevNatures[i].equals(natureId)) {
				natureIndex = i;
				i = prevNatures.length;
			}
		}

		// Remove nature only if it exists...
		if (natureIndex != -1) {
			String[] newNatures = new String[prevNatures.length - 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, natureIndex);
			System.arraycopy(prevNatures, natureIndex + 1, newNatures,
					natureIndex, prevNatures.length - (natureIndex + 1));
			description.setNatureIds(newNatures);
			proj.setDescription(description, null);
		}
	}

}
