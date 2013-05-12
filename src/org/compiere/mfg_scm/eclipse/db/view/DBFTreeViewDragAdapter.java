/*
 * ====================================================================
 * Copyright 2001-2005 Compiere MFG + SCM.
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
import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeObject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.dnd.*;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DBFTreeViewDragAdapter extends DragSourceAdapter {
	ISelectionProvider selectionProvider;

	Object dragData;

	static TreeView dbfTreeView;

	/**
	 * NavigatorDragAction constructor comment.
	 */
	public DBFTreeViewDragAdapter(ISelectionProvider provider,
			TreeView dbfTreeView) {
		DbfLauncherPlugin.log(IStatus.INFO, "DBFTreeViewDragAdapter !!!!");
		selectionProvider = provider;
		DBFTreeViewDragAdapter.dbfTreeView = dbfTreeView;
	}

	/**
	 * Returns the data to be transferred in a drag and drop operation.
	 */
	public void dragSetData(DragSourceEvent event) {
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDragAdapter dragSetData!!!!!");
		if (event.doit == false)
			return;
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDragAdapter dragSetData true");
		if (TextTransfer.getInstance().isSupportedType(event.dataType)) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBFTreeViewDragAdapter dragSetData data");
			Object obj = ((IStructuredSelection) selectionProvider
					.getSelection()).getFirstElement();
			event.data = ((TreeObject) obj).getName();
			// dragData must contain data of type handle in transfers declared
			// in DBFTreeView
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBFTreeViewDragAdapter event.data " + event.data);
			TreeObject parent = ((TreeObject) obj).getParent();

			DbfLauncherPlugin.log(IStatus.INFO,
					"DBFTreeViewDragAdapter parent not TreeZone");
			dragData = event.data;
			dbfTreeView.doCut((TreeObject) obj);
			return;
		}
	}

	/**
	 * All selection must be named model objects.
	 */
	public void dragStart(DragSourceEvent event) {
		DbfLauncherPlugin.log(IStatus.INFO, "DBFTreeViewDragAdapter START!!!!");
		DragSource dragSource = (DragSource) event.widget;
		Control control = dragSource.getControl();
		if (control != control.getDisplay().getFocusControl()) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBFTreeViewDragAdapter dragStart STARTFALSE");
			event.doit = false;
			return;
		}
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDragAdapter dragStart true");
		// FIXME forced for tests
		event.doit = true;
		// event.doit = canDrag();
	}

	public void dragFinished(DragSourceEvent event) {
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDragAdapter drag FINISH" + event.detail + "  "
						+ event.doit);
		// dragDataDelete de DragSource met moveData a true necessaire pour que
		// DragEnd mette doit a true. Quand est elle lancee
		// ?????????????????????
		if (event.doit == false || dragData == null)
			return;
		// FIXME voir a avoir undo en cas de pb sur drop
		DbfLauncherPlugin.log(IStatus.INFO,
				"DBFTreeViewDragAdapter dragFinished event.detail "
						+ event.detail);
		if (event.detail == DND.DROP_MOVE) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"DBFTreeViewDragAdapter dragFinished DND.DROP_MOVE");
		}
		dragData = null;
	}

	private boolean canDrag() {
		return canCopy((IStructuredSelection) selectionProvider.getSelection());
	}

	static boolean canCopy(IStructuredSelection selection) {
		return TreeView.canCopy(selection);
	}

	/*
	 * private ISchemaObject[] getSelectedModelObjects() { return
	 * createObjectRepresentation( (IStructuredSelection)
	 * selectionProvider.getSelection()); }
	 * 
	 * static ISchemaObject[] createObjectRepresentation(IStructuredSelection
	 * selection) { ArrayList objects = new ArrayList(); for (Iterator iter =
	 * selection.iterator(); iter.hasNext();) { Object obj = iter.next(); if
	 * (obj instanceof ISchemaObject) objects.add(obj); else return new
	 * ISchemaObject[0]; } return (ISchemaObject[]) objects.toArray( new
	 * ISchemaObject[objects.size()]); }
	 */
}
