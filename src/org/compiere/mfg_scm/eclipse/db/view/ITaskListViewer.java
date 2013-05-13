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

package org.compiere.mfg_scm.eclipse.db.view;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public interface ITaskListViewer {

	/**
	 * Update the view to reflect the fact that a task was added to the task
	 * list
	 * 
	 * @param task
	 */
	public void addTask(MfgScmTask task);

	/**
	 * Update the view to reflect the fact that a task was removed from the task
	 * list
	 * 
	 * @param task
	 */
	public void removeTask(MfgScmTask task);

	/**
	 * Update the view to reflect the fact that one of the tasks was modified
	 * 
	 * @param task
	 */
	public void updateTask(MfgScmTask task);
}
