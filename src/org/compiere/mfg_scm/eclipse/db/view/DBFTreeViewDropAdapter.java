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

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ViewerDropAdapter;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBFTreeViewDropAdapter extends ViewerDropAdapter {
	private TransferData currentTransfer;

	private TreeView dbfTreeView;

	private static boolean debug = true;

	public DBFTreeViewDropAdapter(TreeView dbfTreeView) {
		super(dbfTreeView.getTreeViewer());
		this.dbfTreeView = dbfTreeView;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#performDrop(java.lang.Object)
	 */
	public boolean performDrop(Object data) {
		DbfLauncherPlugin.log(IStatus.INFO, "DBFTreeViewDropAdapter DROP!!!!");
		if (data instanceof String) {
			dbfTreeView.doPaste(getCurrentTarget(), (String) data);
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBFTreeViewDropAdapter DROP true");
			return true;
		}
		return false;
	}

	/**
	 * @see org.eclipse.jface.viewers.ViewerDropAdapter#validateDrop(java.lang.Object,
	 *      int, org.eclipse.swt.dnd.TransferData)
	 */
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDropAdapter VALIDATEDROP");
		currentTransfer = transferType;
		if (currentTransfer != null
				&& TextTransfer.getInstance().isSupportedType(currentTransfer)) {
			return validateTarget();
		}
		return false;
	}

	private boolean validateTarget() {
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDropAdapter VALIDATETARGET");
		// FIXME implement
		Object target = getCurrentTarget();
		return true;
	}

}
