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

package org.compiere.mfg_scm.eclipse.db.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.MultiPageEditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionConstants;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class MultiPageEditorContributor extends
		MultiPageEditorActionBarContributor {

	private IEditorPart activeEditorPart;

	private Action sampleAction;

	public MultiPageEditorContributor() {
		super();
		createActions();
		return;
	}

	protected IAction getAction(ITextEditor itexteditor, String string) {
		return ((itexteditor == null) ? null : itexteditor.getAction(string));
	}

	public void setActivePage(IEditorPart ieditorpart) {
		IActionBars iactionbars;
		if (activeEditorPart == ieditorpart)
			return;

		activeEditorPart = ieditorpart;
		iactionbars = getActionBars();
		if (iactionbars != null) {
			ITextEditor editor = (ieditorpart instanceof ITextEditor) ? (ITextEditor) ieditorpart
					: null;

			iactionbars.setGlobalActionHandler(
					IWorkbenchActionConstants.DELETE, getAction(editor,
							ITextEditorActionConstants.DELETE));
			iactionbars.setGlobalActionHandler(IWorkbenchActionConstants.UNDO,
					getAction(editor, ITextEditorActionConstants.UNDO));
			iactionbars.setGlobalActionHandler(IWorkbenchActionConstants.REDO,
					getAction(editor, ITextEditorActionConstants.REDO));
			iactionbars.setGlobalActionHandler(IWorkbenchActionConstants.CUT,
					getAction(editor, ITextEditorActionConstants.CUT));
			iactionbars.setGlobalActionHandler(IWorkbenchActionConstants.COPY,
					getAction(editor, ITextEditorActionConstants.COPY));
			iactionbars.setGlobalActionHandler(IWorkbenchActionConstants.PASTE,
					getAction(editor, ITextEditorActionConstants.PASTE));
			iactionbars.setGlobalActionHandler(
					IWorkbenchActionConstants.SELECT_ALL, getAction(editor,
							ITextEditorActionConstants.SELECT_ALL));
			iactionbars.setGlobalActionHandler(IWorkbenchActionConstants.FIND,
					getAction(editor, ITextEditorActionConstants.FIND));
			iactionbars.setGlobalActionHandler(
					IWorkbenchActionConstants.BOOKMARK, getAction(editor,
							ITextEditorActionConstants.BOOKMARK));
			iactionbars.updateActionBars();
		}
		return;
	}

	private void createActions() {
		sampleAction = new Action() {
			public void run() {
				MessageDialog.openInformation(null, "TestPlugin Plug-in",
						"Sample Action Executed");
			}
		};
		sampleAction.setText("Sample Action");
		sampleAction.setToolTipText("Sample Action tool tip");
		sampleAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor("IMG_OBJS_TASK_TSK"));
		return;
	}

	public void contributeToMenu(IMenuManager imenumanager) {
		IContributionManager icontributionmanager = new MenuManager(
				"Editor &Menu");
		imenumanager.prependToGroup("additions",
				(IContributionItem) icontributionmanager);
		icontributionmanager.add(sampleAction);
		return;
	}

	public void contributeToToolBar(IToolBarManager itoolbarmanager) {
		itoolbarmanager.add(new Separator());
		itoolbarmanager.add(sampleAction);
		return;
	}
}
