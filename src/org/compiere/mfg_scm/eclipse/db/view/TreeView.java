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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfPluginResources;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBCatalog;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBColumn;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBConnectAction;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBMeta;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBSchema;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBTable;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DbInterfaceProperties;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DriverManager;
import org.compiere.mfg_scm.eclipse.db.dbInterface.GenerateJavaClasses;
import org.compiere.mfg_scm.eclipse.db.dbInterface.GenerateXmlFiles;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class TreeView extends ViewPart implements DbfPluginResources {

	public static final String NAME = "org.compiere.mfg_scm.eclipse.db.view.TreeView";

	private TreeViewer viewer;

	private DrillDownAdapter drillDownAdapter;

	private Action reloadAction;

	private Action generateJavaAction;

	private Action generateDisplayAction;

	private Action generateXMLAction;

	private Action generateModelAction;

	private Action doubleClickAction;

	private Action testConAction;

	private TreeParent invisibleRoot;

	private ShowJobActionHandler showJobAction;

	private CutActionHandler cutAction;

	private CopyActionHandler copyAction;

	private PasteActionHandler pasteAction;

	private DeleteActionHandler deleteAction;

	private SelectActionHandler selectAction;

	private FindActionHandler findAction;

	private UndoActionHandler undoAction;

	private RedoActionHandler redoAction;

	private addServiceHandler addServiceAction;

	private editServiceHandler editServiceAction;

	private adminServiceHandler adminServiceAction;

	private Clipboard clipboard;

	private Transfer[] transfers = null;

	private TreeObject[] cutObjects = null;

	private IWorkbenchPage page;

	private DBMeta dbMeta;

	private java.sql.Connection conn;

	private String m_schema;

	private ArrayBlockingQueue history;

	private int historyCapacity;

	protected final Image fFolderIcon;

	protected final Image fAttributIcon;

	protected final Image fAttributKeyIcon;

	protected final Image fTableIcon;

	private DbInterfaceProperties dbInterfaceproperties;

	public TreeView() {
		super();
		dbMeta = null;
		conn = null;
		m_schema = "";
		fFolderIcon = createImage("icons/Folder.gif", getClass());
		fAttributIcon = createImage("icons/Attribut.gif", getClass());
		fAttributKeyIcon = createImage("icons/AttributKey.gif", getClass());
		fTableIcon = createImage("icons/Table.gif", getClass());
		historyCapacity = 12;
		history = new ArrayBlockingQueue(historyCapacity);
		dbInterfaceproperties = null;
		return;
	}

	public void createPartControl(Composite composite) {
		viewer = new TreeViewer(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setSorter(new NameSorter());
		viewer.setInput(ResourcesPlugin.getWorkspace());
		page = getSite().getPage();
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		hookF2Action();
		contributeToActionBars();
		initDragAndDrop();
		return;
	}

	private void hookF2Action() {

		// TreeViewer.setCellEditors() and TreeViewer.setCellModifier()
		// Create the editor and set its attributes
		final TreeEditor editor = new TreeEditor(viewer.getTree());
		editor.horizontalAlignment = SWT.LEFT;
		editor.grabHorizontal = true;

		// Add a key listener to the tree that listens for F2.
		// If F2 is pressed, we do the editing
		final Tree tree = viewer.getTree();
		tree.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				// Make sure one and only one item is selected when F2 is
				// pressed
				DbfLauncherPlugin.log(IStatus.INFO,
						"MFGSCMTreeView event.keyCode " + event.keyCode);
				if (event.keyCode == SWT.F2 && tree.getSelectionCount() == 1) {
					// Determine the item to edit
					final TreeItem item = tree.getSelection()[0];
					final IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();

					// Create a text field to do the editing
					final Text text = new Text(tree, SWT.NONE);
					text.setText(item.getText());
					text.selectAll();
					text.setFocus();

					// If the text field loses focus, set its text into the tree
					// and end the editing session
					text.addFocusListener(new FocusAdapter() {
						public void focusLost(FocusEvent event) {
							item.setText(text.getText());
							doRenameJob(selection, text.getText());
							text.dispose();
						}
					});

					// If they hit Enter, set the text into the tree and end the
					// editing
					// session. If they hit Escape, ignore the text and end the
					// editing
					// session
					text.addKeyListener(new KeyAdapter() {
						public void keyPressed(KeyEvent event) {
							switch (event.keyCode) {
							case SWT.CR:
								// Enter hit--set the text into the tree and
								// drop through
								item.setText(text.getText());
								doRenameJob(selection, text.getText());
								text.dispose();
							case SWT.ESC:
								// End editing session
								text.dispose();
								break;
							}
						}
					});

					// Set the text field into the editor
					editor.setEditor(text, item);
				} else if (event.keyCode == SWT.F1
						&& tree.getSelectionCount() == 1) {
					DbfLauncherPlugin.log(IStatus.INFO,
							"MFGSCMTreeView event.keyCode == SWT.F1");
					final IStructuredSelection selection = (IStructuredSelection) viewer
							.getSelection();
					Object object = ((IStructuredSelection) selection)
							.getFirstElement();
					try {
						DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
						TableStructView structview = (TableStructView) page
								.showView("org.compiere.mfg_scm.eclipse.db.view.TableStructView");
						structview
								.setDbInterfaceProperties(dbInterfaceproperties);
						if (object instanceof TreeTable) {
							TreeTable treetable = (TreeTable) object;
							String strTableName = treetable.getTable()
									.getTableName();
							if (dbInterfaceproperties.getSchema()
									.equals("null")
									&& dbInterfaceproperties
											.isUseSchemaForSelect()) {
								m_schema = dbInterfaceproperties.getSchema();
								if (m_schema.length() > 0)
									strTableName = m_schema
											+ "."
											+ treetable.getTable()
													.getTableName();

							}
							structview.setInput(treetable);
							structview.setDbMeta(dbMeta);
						}
					} catch (PartInitException partinitexception) {
						DbfLauncherPlugin.log(partinitexception);
						DbfLauncherPlugin.log(IStatus.INFO,
								"MFGSCMTreeView partinitexception "
										+ partinitexception.getMessage());
					}
				}

			}
		});

	}

	private void initDragAndDrop() {
		DbfLauncherPlugin.log(IStatus.INFO, "DBFTreeView initDragAndDrop");
		int ops = DND.DROP_COPY | DND.DROP_MOVE;
		transfers = new Transfer[] { TextTransfer.getInstance() };
		viewer.addDragSupport(ops, transfers, new DBFTreeViewDragAdapter(
				(ISelectionProvider) viewer, this));
		viewer.addDropSupport(ops | DND.DROP_DEFAULT, transfers,
				new DBFTreeViewDropAdapter(this));
	}

	static boolean canShowJob(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "MFGSCMTreeView canShowJob");
		Object treeobject = ((IStructuredSelection) selection)
				.getFirstElement();
		if (treeobject instanceof TreeTable) {
			return true;
		} else if (treeobject instanceof TreeAttribut) {
			return true;
		} else if (treeobject instanceof TreeRoot) {
			return false;
		} else
			return false;
	}

	static boolean canCut(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "MFGSCMTreeView canCut");
		Object firstObject = ((IStructuredSelection) selection)
				.getFirstElement();
		if (firstObject instanceof TreeTable) {
			return true;
		} else if (firstObject instanceof TreeAttribut) {
			return true;
		} else if (firstObject instanceof TreeRoot) {
			return false;
		} else
			return false;
	}

	static boolean canCopy(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView canCopy");
		if (selection.isEmpty())
			return false;
		Class objClass = null;
		Object firstObject = ((IStructuredSelection) selection)
				.getFirstElement();
		if (firstObject instanceof TreeTable) {
			objClass = TreeTable.class;
		} else if (firstObject instanceof TreeAttribut) {
			objClass = TreeAttribut.class;
		} else if (firstObject instanceof TreeRoot) {
			objClass = TreeRoot.class;
		}

		// To control that they are all members of the ame class
		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			Object obj = iter.next();
			if (!(objClass.isInstance(obj)))
				return false;
		}
		return true;
	}

	protected boolean canPaste(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView canPaste");
		boolean validTarget = false;
		Object target = ((IStructuredSelection) selection).getFirstElement();
		if (target instanceof TreeObject) {
			validTarget = true;
		} else
			validTarget = false;
		if (validTarget) {
			TextTransfer textTransfer = TextTransfer.getInstance();
			// We get a string containing liste of objects to Paste
			String tokenString = (String) clipboard.getContents(textTransfer);
			if (tokenString != null) {
				StringTokenizer tok = new StringTokenizer(tokenString,
						", \t\n\r\f");
				String[] objects = new String[tok.countTokens()];
				int i = 0;
				while (tok.hasMoreTokens()) {
					objects[i++] = tok.nextToken();
				}
				if (objects.length > 0)
					return canPaste(target, objects);
			}
		}
		return false;
	}

	protected boolean canPaste(Object target, Object[] objects) {
		DbfLauncherPlugin.log(IStatus.INFO,
				"TreeView canPaste objects in target "
						+ ((TreeObject) target).getName());
		// If cutObject null nothing to paste
		if (cutObjects == null)
			return false;
		// We don't use clipBoard but cutObjects
		// We controle that target can be a legal parent of them
		for (int i = 0; i < cutObjects.length; i++) {
			// FIXME
			// Attention a gerer nom longs
			Object cutObject = cutObjects[i];
			String obj = ((TreeObject) cutObject).getName();
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView canPaste objects obj String " + obj);
			if (cutObject == null)
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView canPaste objects cutObject null");
			else
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView canPaste objects cutObject " + obj);
			if (target instanceof TreeRoot) {
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView canPaste target TreeRoot " + obj);
				if (!(cutObject instanceof TreeTable))
					return false;
			} else if (target instanceof TreeTable) {
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView canPaste target TreeTable " + obj);
				if (!(cutObject instanceof TreeAttribut))
					return false;
			} else if (target instanceof TreeAttribut) {
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView cannotPaste target TreeAttribut " + obj);
				return false;
			}
		}
		DbfLauncherPlugin.log(IStatus.INFO,
				"TreeView canPaste objects cutObject true ");
		return true;
	}

	public void dispose() {
		if (clipboard != null) {
			clipboard.dispose();
			clipboard = null;
		}
		super.dispose();
	}

	private void hookContextMenu() {
		MenuManager menumanager = new MenuManager("#PopupMenu");
		menumanager.setRemoveAllWhenShown(true);
		menumanager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager imenumanager) {
				fillContextMenu(imenumanager);
				return;
			}
		});
		Menu menu = menumanager.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menumanager, viewer);
		return;
	}

	private void contributeToActionBars() {
		IActionBars iactionbars = getViewSite().getActionBars();
		fillLocalPullDown(iactionbars.getMenuManager());
		fillLocalToolBar(iactionbars.getToolBarManager());
		return;
	}

	private void fillLocalPullDown(IMenuManager imenumanager) {
		imenumanager.add(reloadAction);
		imenumanager.add(generateJavaAction);
		imenumanager.add(generateDisplayAction);
		imenumanager.add(generateXMLAction);
		imenumanager.add(generateModelAction);
		imenumanager.add(new Separator());
		imenumanager.add(testConAction);
		return;
	}

	private void fillContextMenu(IMenuManager imenumanager) {
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		DbfLauncherPlugin.log(IStatus.INFO, "MFGSCMTreeView fillContextMenu 1");
		if (selection.size() == 1) {
			Object sobj = selection.getFirstElement();
			boolean addSeparator = false;
			if (sobj instanceof TreeTable) {
			} else if (sobj instanceof TreeAttribut) {
			} else if (sobj instanceof TreeRoot) {
			} else
				showMessage(VIEWER_MESSAGE_SELECT_SERVICE);
		}
		if (selection.size() > 0) {
			/*
			 * if (canShowJob(selection)) {
			 * showJobAction.setSelection(selection);
			 * imenumanager.add(showJobAction); imenumanager.add(new
			 * Separator()); }
			 */
			Object sobj = selection.getFirstElement();
			DbfLauncherPlugin.log(IStatus.INFO,
					"MFGSCMTreeView fillContextMenu 2");
			if (canCopy(selection)) {
				if (!(sobj instanceof TreeRoot)) {
					copyAction.setSelection(selection);
					imenumanager.add(copyAction);
					imenumanager.add(new Separator());
				}
			}
			DbfLauncherPlugin.log(IStatus.INFO,
					"MFGSCMTreeView fillContextMenu 3");
			if (canPaste(selection)) {
				pasteAction.setSelection(selection);
				imenumanager.add(pasteAction);
				imenumanager.add(new Separator());
			}
			DbfLauncherPlugin.log(IStatus.INFO,
					"MFGSCMTreeView fillContextMenu 4");
			if (canCut(selection)) {
				cutAction.setSelection(selection);
				imenumanager.add(cutAction);
				imenumanager.add(new Separator());
			}
			// ???? if (!(sobj instanceof TreeAttributKey)) {
			addServiceAction.setSelection(selection);
			imenumanager.add(addServiceAction);
			imenumanager.add(new Separator());
			// }
			if (!(sobj instanceof TreeRoot)) {
				editServiceAction.setSelection(selection);
				imenumanager.add(editServiceAction);
				imenumanager.add(new Separator());
			}
			adminServiceAction.setSelection(selection);
			imenumanager.add(adminServiceAction);
			imenumanager.add(new Separator());
		}
		DbfLauncherPlugin.log(IStatus.INFO, "MFGSCMTreeView fillContextMenu 5");
		imenumanager.add(reloadAction);
		imenumanager.add(generateXMLAction);
		imenumanager.add(new Separator());
		imenumanager.add(testConAction);
		imenumanager.add(new Separator());
		DbfLauncherPlugin.log(IStatus.INFO,
				"MFGSCMTreeView fillContextMenu addNavigationActions");
		drillDownAdapter.addNavigationActions(imenumanager);
		imenumanager.add(new Separator("Additions"));
		return;
	}

	private void fillLocalToolBar(IToolBarManager itoolbarmanager) {
		itoolbarmanager.add(reloadAction);
		itoolbarmanager.add(generateJavaAction);
		itoolbarmanager.add(generateDisplayAction);
		itoolbarmanager.add(generateXMLAction);
		itoolbarmanager.add(generateModelAction);
		itoolbarmanager.add(new Separator());
		itoolbarmanager.add(testConAction);
		itoolbarmanager.add(new Separator());
		drillDownAdapter.addNavigationActions(itoolbarmanager);
		return;
	}

	private void makeActions() {
		clipboard = new Clipboard(viewer.getControl().getDisplay());
		makeReloadAction();
		makeGenerateJavaAction();
		makeGenerateDisplayAction();
		makeGenerateXMLAction();
		makeGenerateModelAction();
		makeAddServiceAction();
		makeEditServiceAction();
		makeAdminServiceAction();
		makeTestConAction();
		makeDoubleClickAction();
		makeShowJobAction();
		makeCutAction();
		makeCopyAction();
		makePasteAction();
		makeDeleteAction();
		makeSelectAction();
		makeFindAction();
		makeUndoAction();
		makeRedoAction();
		return;
	}

	private void makeDoubleClickAction() {
		doubleClickAction = new doubleClick();
		return;
	}

	private void makeShowJobAction() {
		showJobAction = new ShowJobActionHandler(clipboard);
		showJobAction.setText(VIEWER_ACTION_SHOWJOB_SERVICE);
		showJobAction.setToolTipText(VIEWER_ACTION_SHOWJOB_SERVICE);
		// TODO Create an icon for add Service
		showJobAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeCutAction() {
		cutAction = new CutActionHandler(clipboard);
		cutAction.setText(VIEWER_ACTION_CUT_SERVICE);
		cutAction.setToolTipText(VIEWER_ACTION_CUT_SERVICE);
		// TODO Create an icon for cut Service
		cutAction.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
				.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeCopyAction() {
		copyAction = new CopyActionHandler(clipboard);
		copyAction.setText(VIEWER_ACTION_COPY_SERVICE);
		copyAction.setToolTipText(VIEWER_ACTION_COPY_SERVICE);
		// TODO Create an icon for copy Service
		copyAction.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
				.getResource("icons/generateXML.gif")));
		return;
	}

	private void makePasteAction() {
		pasteAction = new PasteActionHandler(clipboard);
		pasteAction.setText(VIEWER_ACTION_PASTE_SERVICE);
		pasteAction.setToolTipText(VIEWER_ACTION_PASTE_SERVICE);
		// TODO Create an icon for add Service
		pasteAction.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
				.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeDeleteAction() {
		deleteAction = new DeleteActionHandler(clipboard);
		deleteAction.setText(VIEWER_ACTION_DEL_SERVICE);
		deleteAction.setToolTipText(VIEWER_ACTION_DEL_SERVICE);
		// TODO Create an icon for add Service
		deleteAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeSelectAction() {
		selectAction = new SelectActionHandler(clipboard);
		selectAction.setText(VIEWER_ACTION_SEL_SERVICE);
		selectAction.setToolTipText(VIEWER_ACTION_SEL_SERVICE);
		// TODO Create an icon for add Service
		selectAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeFindAction() {
		findAction = new FindActionHandler(clipboard);
		findAction.setText(VIEWER_ACTION_FIND_SERVICE);
		findAction.setToolTipText(VIEWER_ACTION_FIND_SERVICE);
		// TODO Create an icon for add Service
		findAction.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
				.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeUndoAction() {
		undoAction = new UndoActionHandler(clipboard);
		undoAction.setText(VIEWER_ACTION_UNDO_ACTION);
		undoAction.setToolTipText(VIEWER_ACTION_UNDO_ACTION);
		// TODO Create an icon for add Service
		undoAction.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
				.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeRedoAction() {
		redoAction = new RedoActionHandler(clipboard);
		redoAction.setText(VIEWER_ACTION_REDO_ACTION);
		redoAction.setToolTipText(VIEWER_ACTION_REDO_ACTION);
		// TODO Create an icon for add Service
		redoAction.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
				.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeAddServiceAction() {
		addServiceAction = new addServiceHandler();
		addServiceAction.setText(VIEWER_ACTION_ADD_SERVICE);
		addServiceAction.setToolTipText(VIEWER_ACTION_ADD_SERVICE);
		// TODO Create an icon for add Service
		addServiceAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeEditServiceAction() {
		editServiceAction = new editServiceHandler();
		editServiceAction.setText(VIEWER_ACTION_EDIT_SERVICE);
		editServiceAction.setToolTipText(VIEWER_ACTION_EDIT_SERVICE);
		// TODO Create an icon for edit Service
		editServiceAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeAdminServiceAction() {
		adminServiceAction = new adminServiceHandler();
		adminServiceAction.setText(VIEWER_ACTION_ADMIN_SERVICE);
		adminServiceAction.setToolTipText(VIEWER_ACTION_ADMIN_SERVICE);
		// TODO Create an icon for admin Service
		adminServiceAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeTestConAction() {
		testConAction = new testCon();
		testConAction.setText(VIEWER_ACTION_TEST_CON);
		testConAction.setToolTipText(VIEWER_ACTION_TEST_CON);
		testConAction.setImageDescriptor(ImageDescriptor
				.createFromURL(getClass().getResource("icons/testDB.gif")));
		return;
	}

	private void makeGenerateXMLAction() {
		generateXMLAction = new generateXML();
		generateXMLAction.setText(VIEWER_ACTION_GENERATE_XML);
		generateXMLAction.setToolTipText(VIEWER_ACTION_GENERATE_XML);
		generateXMLAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateXML.gif")));
		return;
	}

	private void makeGenerateModelAction() {
		generateModelAction = new generateXmlModel();
		generateModelAction.setText(VIEWER_ACTION_GENERATE_MODEL);
		generateModelAction.setToolTipText(VIEWER_ACTION_GENERATE_MODEL);
		generateModelAction.setImageDescriptor(ImageDescriptor
				.createFromURL(getClass()
						.getResource("icons/generateModel.gif")));
		return;
	}

	private void makeGenerateJavaAction() {
		generateJavaAction = new generateJava();
		generateJavaAction.setText(VIEWER_ACTION_GENERATE_JAVA);
		generateJavaAction.setToolTipText(VIEWER_ACTION_GENERATE_JAVA);
		generateJavaAction
				.setImageDescriptor(ImageDescriptor.createFromURL(getClass()
						.getResource("icons/generateJava.gif")));
		return;
	}

	private void makeGenerateDisplayAction() {
		generateDisplayAction = new generateDisplay();
		generateDisplayAction.setText(VIEWER_ACTION_GENERATE_DISPLAY);
		generateDisplayAction.setToolTipText(VIEWER_ACTION_GENERATE_DISPLAY);
		generateDisplayAction.setImageDescriptor(ImageDescriptor
				.createFromURL(getClass().getResource(
						"icons/generateDisplay.gif")));
		return;
	}

	private void makeReloadAction() {
		reloadAction = new reload();
		reloadAction.setText(VIEWER_ACTION_RELOAD_TREE);
		reloadAction.setToolTipText(VIEWER_ACTION_RELOAD_TREE);
		reloadAction.setImageDescriptor(ImageDescriptor
				.createFromURL(getClass().getResource("icons/reloadDB.gif")));
		return;
	}

	private void hookDoubleClickAction() {
		viewer.addDoubleClickListener(new doubleClickListener());
		return;
	}

	private void showMessage(String string) {
		MessageDialog.openInformation(viewer.getControl().getShell(),
				VIEWER_MESSAGE_OPEN_INFO, string);
		return;
	}

	private void showError(String string) {
		MessageDialog.openError(viewer.getControl().getShell(),
				VIEWER_MESSAGE_OPEN_ERROR, string);
		return;
	}

	private boolean getOK(String string) {
		return (MessageDialog.openConfirm(viewer.getControl().getShell(),
				VIEWER_MESSAGE_OPEN_CONFIRM, string));
	}

	public void setFocus() {
		viewer.getControl().setFocus();
		return;
	}

	private boolean connectToDB() {
		Boolean i = Boolean.FALSE;
		DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
		// OJB
		DBConnectAction dbconnectaction = DbfLauncherPlugin.getDefault()
				.getDBConnectAction();
		if (dbconnectaction == null) {
			dbconnectaction = new DBConnectAction(DbInterfaceProperties
					.getPropertie(dbInterfaceproperties));
		}
		conn = dbconnectaction.actionConnect();
		if (dbMeta == null && conn != null) {
			dbMeta = dbconnectaction.getDbMetaData(dbInterfaceproperties);
			m_schema = dbMeta.getSchema();
			if (dbMeta != null)
				i = Boolean.TRUE;
		}
		return (i.booleanValue());
	}

	protected static Image createImage(String strImg, Class className) {
		try {
			return (new Image(Display.getCurrent(), className
					.getResourceAsStream(strImg)));
		} catch (Exception exception) {
			DbfLauncherPlugin.log(IStatus.ERROR, "TreeView "
					+ VIEWER_EXCEPTION_LOAD_IMAGE);
			DbfLauncherPlugin.log(exception);
		}
		return (new Image(Display.getCurrent(), 1, 1));
	}

	private void handleDelete(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView handleDelete");
		for (Iterator iter = selection.iterator(); iter.hasNext();) {
			handleDelete(iter.next());
		}
	}

	void handleDelete(Object object) {
		// FIXME implement undeploy of corresponding operationalString
		// first test instanceof idem for doubleclick
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView handleDelete treeObject");
		if (object == null) {
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView handleDelete object == null ");
			return;
		}
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView handleDelete " + object);
		if (object instanceof TreeAttribut) {
			TreeAttribut treeAttribute = (TreeAttribut) object;
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView handleDelete treeAttribut OK");
			String attributeName = treeAttribute.getName();
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView handleDelete attributeName " + attributeName);
			boolean enabled = true;
			if (enabled
					&& getOK(VIEWER_MESSAGE_CONFIRM_DELETE + attributeName
							+ " ? ")) {
				delService(treeAttribute);
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView handleDelete refresh serviceName "
								+ attributeName);
				viewer.refresh();
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView handleDelete refreshed serviceName "
								+ attributeName);
			}
		} else if (object instanceof TreeObject) {
			TreeObject treeObject = (TreeObject) object;
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView handleDelete treeObject OK");
			String serviceName = treeObject.getName();
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView handleDelete serviceName " + serviceName);
			boolean enabled = true;
			if (treeObject instanceof TreeRoot)
				enabled = false;
			if (enabled
					&& getOK(VIEWER_MESSAGE_CONFIRM_DELETE + serviceName
							+ " ? ")) {
				delService(treeObject);
				// invisibleRoot = null;
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView handleDelete refresh serviceName "
								+ serviceName);
				viewer.refresh();
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView handleDelete refreshed serviceName "
								+ serviceName);
			}
		} else {
			DbfLauncherPlugin
					.log(IStatus.INFO,
							"TreeView handleDelete object not TreeObject or TreeAttribut");
			return;
		}

	}

	/**
	 * 
	 */
	private void delService(TreeObject treeObject) {
		// FIXME Implement
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView delService : "
				+ treeObject.getName());
		/*
		 * MfgScmTableElement mfgScmTableElement = new MfgScmTableElement();
		 * mfgScmTableElement.setServiceManager(serviceManager);
		 * mfgScmTableElement.setTreeObject(treeObject);
		 * mfgScmTableElement.deleteService();
		 * DbfLauncherPlugin.log(IStatus.INFO, "TreeView delService finish ");
		 */
		// TODO add delete of all others elements of service
		// for example sons etc...
		// Probably that sons are taken of automacicaly for anythings but
		// capacities
		// Need to be controled probably have to delete capacity in WorkCenter
		// Value if it is a capacity
		return;
	}

	public Clipboard getClipboard() {
		return clipboard;
	}

	public void disableAllTable() {
		HashMap treemap = dbMeta.getCatalogs();
		DBCatalog dbcatalog = null;
		DBSchema dbschema = null;
		DBTable dbtable;
		if (treemap == null)
			return;
		Iterator catalogIt = treemap.values().iterator();
		while (catalogIt.hasNext()) {
			dbcatalog = (DBCatalog) catalogIt.next();
			for (int i = 0; i < dbcatalog.getChildCount(); i++) {
				dbschema = (DBSchema) dbcatalog.getChildAt(i);
				for (int j = 0; j < dbschema.getChildCount(); j++) {
					dbtable = (DBTable) dbschema.getChildAt(j);
					dbtable.setEnabled(false);
				}
			}
		}
		return;
	}

	public void enableAllTable() {
		HashMap treemap = dbMeta.getCatalogs();
		DBCatalog dbcatalog = null;
		DBSchema dbschema = null;
		DBTable dbtable;
		if (treemap == null)
			return;
		dbMeta.setEnabled(true);
		Iterator catalogIt = treemap.values().iterator();
		while (catalogIt.hasNext()) {
			dbcatalog = (DBCatalog) catalogIt.next();
			dbcatalog.setEnabled(true);
			for (int i = 0; i < dbcatalog.getChildCount(); i++) {
				dbschema = (DBSchema) dbcatalog.getChildAt(i);
				dbschema.setEnabled(true);
				for (int j = 0; j < dbschema.getChildCount(); j++) {
					dbtable = (DBTable) dbschema.getChildAt(j);
					dbtable.setEnabled(true);
				}
			}
		}
		return;
	}

	final class doubleClick extends Action {

		doubleClick() {
			super();
			return;
		}

		public void run() {
			ISelection iselection = viewer.getSelection();
			Object object = ((IStructuredSelection) iselection)
					.getFirstElement();
			try {
				DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
				TableView dataview = (TableView) page
						.showView("org.compiere.mfg_scm.eclipse.db.view.TableView");
				dataview.setDbInterfaceProperties(dbInterfaceproperties);
				if (object instanceof TreeTable) {
					TreeTable treetable = (TreeTable) object;
					String strTableName = treetable.getTable().getTableName();
					if (dbInterfaceproperties.getSchema().equals("null")
							&& dbInterfaceproperties.isUseSchemaForSelect()) {
						m_schema = dbInterfaceproperties.getSchema();
						if (m_schema.length() > 0)
							strTableName = m_schema + "."
									+ treetable.getTable().getTableName();

					}
					dataview.setInput(strTableName);
				}
			} catch (PartInitException partinitexception) {
				DbfLauncherPlugin.log(partinitexception);
				return;
			}
			return;
		}

	}

	final class testCon extends Action {

		testCon() {
			super();
			return;
		}

		public void run() {
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			java.sql.Connection connection;
			String strSchema = "";
			ResultSet rs;
			java.sql.DatabaseMetaData dbMeta = null;
			String strCatalog = "";
			if (dbInterfaceproperties == null
					|| dbInterfaceproperties.getDriver().length() == 0) {
				showError(VIEWER_ERROR_PROPERTY_FILE);
				return;
			}
			String strJDBCDriver = dbInterfaceproperties.getDriver();
			if (strJDBCDriver == null || strJDBCDriver.length() < 1) {
				showError(VIEWER_ERROR_PROPERTY_DRIVER);
				return;
			}
			String strUsername = dbInterfaceproperties.getUsername();
			String strPassword = dbInterfaceproperties.getPassword();
			String strJDBCURL = dbInterfaceproperties.getProtocol() + ":";
			strJDBCURL += dbInterfaceproperties.getSubprotocol() + ":";
			strJDBCURL += dbInterfaceproperties.getDbalias();
			try {
				connection = DriverManager.reloadInstance().getConnection(
						strJDBCDriver, strJDBCURL, strUsername, strPassword);
			} catch (Exception exception) {
				showError(VIEWER_ERROR_PROPERTY_URL + " \nurl: " + strJDBCURL
						+ "\nuser: " + strUsername + "\npassword: "
						+ strPassword + "\nerror: " + exception.getMessage());
				return;
			}
			try {
				dbMeta = connection.getMetaData();
				// FIXME close base and put error message if null
				if (!dbInterfaceproperties.getCatalog().equals("null"))
					strCatalog = dbInterfaceproperties.getCatalog();

				if (!dbInterfaceproperties.getSchema().equals("null"))
					strSchema = dbInterfaceproperties.getSchema();
			} catch (Exception exception) {
				try {
					connection.close();
					connection = null;
				} catch (SQLException sqlexception) {
					showError(VIEWER_ERROR_LOAD_META + " \nerror: "
							+ exception.getMessage());
					return;
				}
			}

			try {
				rs = dbMeta.getTables(strCatalog, strSchema, "%", null);
				if (!rs.next()) {
					connection.close();
					connection = null;
					showError(VIEWER_ERROR_LOAD_NOTABLES + strCatalog + ","
							+ strSchema);
				}
				return;
			} catch (SQLException sqlexception) {
				showError(VIEWER_ERROR_LOAD_TABLES + "\nerror: "
						+ sqlexception.getMessage());
				try {
					connection.close();
					connection = null;
				} catch (SQLException sqlex) {
					return;
				}
			}
			showMessage(VIEWER_MESSAGE_TEST_OK + strJDBCDriver);
			return;
		}

	}

	final class addServiceHandler extends Action {
		private IStructuredSelection selection;

		addServiceHandler() {
			super();
			return;
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(true);
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doAddJob(selection);
		}

	}

	final class editServiceHandler extends Action {
		private IStructuredSelection selection;

		editServiceHandler() {
			super();
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(true);
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doEditJob(selection);
		}

	}

	final class adminServiceHandler extends Action {
		private IStructuredSelection selection;

		/**
		 * Constructor for adminServiceHandler.
		 */
		protected adminServiceHandler() {
			super();
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(true);
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doAdminJob(selection);
		}

	}

	final class generateXML extends Action {

		generateXML() {
			super();
			return;
		}

		public void run() {
			disableAllTable();
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			int enabled = 0;
			ISelection iselection = viewer.getSelection();
			Iterator selectIt = ((IStructuredSelection) iselection).iterator();
			while (selectIt.hasNext()) {
				TreeObject treeobject = (TreeObject) selectIt.next();
				if (treeobject instanceof TreeTable) {
					enabled = 1;
					treeobject.getTable().setEnabled(true);
					continue;
				} else if (!(treeobject instanceof TreeRoot))
					showMessage(VIEWER_MESSAGE_SELECT_TABLE);

			}
			if (enabled != 0)
				// Generate Xml file ?
				GenerateXmlFiles.actionPerformed(dbMeta, DbInterfaceProperties
						.getPropertie(dbInterfaceproperties));

			else if (getOK(VIEWER_MESSAGE_CONFIRM_XML)) {
				enableAllTable();
				// Generate Xml file ?
				GenerateXmlFiles.actionPerformed(dbMeta, DbInterfaceProperties
						.getPropertie(dbInterfaceproperties));
				showMessage(VIEWER_MESSAGE_OK_XML);
			}
			enableAllTable();
			return;
		}

	}

	final class generateJava extends Action {

		generateJava() {
			super();
			return;
		}

		public void run() {
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			int enabled = 0;
			ISelection iselection = viewer.getSelection();
			Iterator it = ((IStructuredSelection) iselection).iterator();
			while (it.hasNext()) {
				TreeObject treeobject = (TreeObject) it.next();
				if (treeobject instanceof TreeTable) {
					enabled = 1;
					int number = GenerateJavaClasses.actionGenerateJavaFiles(
							treeobject.getTable(), dbInterfaceproperties);
					if (number >= 0) {
						// System.out.println("Generate " +
						// treeobject.toString());
						continue;
					}
					DbfLauncherPlugin.log(IStatus.ERROR, "TreeView "
							+ VIEWER_MESSAGE_ERROR_TABLE
							+ treeobject.toString());
					continue;
				} else if (!(treeobject instanceof TreeRoot))
					showMessage(VIEWER_MESSAGE_ERROR_TABLE);

			}
			// FIXME Verifier si Ok ???
			if (enabled == 0 && getOK(VIEWER_MESSAGE_CONFIRM_JAVA)) {
				enableAllTable();
				GenerateJavaClasses.actionGenerateJavaFiles(dbMeta,
						dbInterfaceproperties);
				showMessage(VIEWER_MESSAGE_OK_TABLE);
			}

			return;
		}

	}

	final class generateDisplay extends Action {

		generateDisplay() {
			super();
			return;
		}

		public void run() {
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			int enabled = 0;
			ISelection iselection = viewer.getSelection();
			Iterator it = ((IStructuredSelection) iselection).iterator();
			while (it.hasNext()) {
				TreeObject treeobject = (TreeObject) it.next();
				if (treeobject instanceof TreeTable) {
					enabled = 1;
					int number = GenerateJavaClasses
							.actionGenerateDisplayFiles(treeobject.getTable(),
									dbInterfaceproperties);
					if (number >= 0) {
						// System.out.println("Generate " +
						// treeobject.toString());
						continue;
					}
					DbfLauncherPlugin.log(IStatus.ERROR, "TreeView "
							+ VIEWER_MESSAGE_ERROR_DISPLAY
							+ treeobject.toString());
					continue;
				} else if (!(treeobject instanceof TreeRoot))
					showMessage(VIEWER_MESSAGE_ERROR_DISPLAY);

			}
			// FIXME Verifier si Ok ???
			if (enabled == 0 && getOK(VIEWER_MESSAGE_CONFIRM_DISPLAY)) {
				enableAllTable();
				GenerateJavaClasses.actionGenerateDisplayFiles(dbMeta,
						dbInterfaceproperties);
				showMessage(VIEWER_MESSAGE_OK_DISPLAY);
			}

			return;
		}

	}

	final class generateXmlModel extends Action {

		generateXmlModel() {
			super();
			return;
		}

		public void run() {
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			int enabled = 0;
			ISelection iselection = viewer.getSelection();
			Iterator it = ((IStructuredSelection) iselection).iterator();
			while (it.hasNext()) {
				TreeObject treeobject = (TreeObject) it.next();
				if (treeobject instanceof TreeTable) {
					enabled = 1;
					int number = GenerateJavaClasses.actionGenerateXmlModel(
							treeobject.getTable(), dbInterfaceproperties);
					if (number >= 0) {
						// System.out.println("Generate " +
						// treeobject.toString());
						continue;
					}
					DbfLauncherPlugin.log(IStatus.ERROR, "TreeView "
							+ VIEWER_MESSAGE_ERROR_MODEL
							+ treeobject.toString());
					continue;
				} else if (!(treeobject instanceof TreeRoot))
					showMessage(VIEWER_MESSAGE_ERROR_MODEL);

			}
			// FIXME Verifier si Ok ???
			if (enabled == 0 && getOK(VIEWER_MESSAGE_CONFIRM_MODEL)) {
				enableAllTable();
				GenerateJavaClasses.actionGenerateXmlModel(dbMeta,
						dbInterfaceproperties);
				showMessage(VIEWER_MESSAGE_OK_MODEL);
			}

			return;
		}

	}

	final class reload extends Action {

		reload() {
			super();
			return;
		}

		public void run() {
			DriverManager.reloadInstance();
			// TODO actionReconnect and reload meta ?
			ViewContentProvider viewcontentprovider = (ViewContentProvider) viewer
					.getContentProvider();
			invisibleRoot = null;
			viewer.refresh();
			return;
		}

	}

	final class doubleClickListener implements IDoubleClickListener {

		doubleClickListener() {
			return;
		}

		public void doubleClick(DoubleClickEvent doubleclickevent) {
			doubleClickAction.run();
			return;
		}

	}

	class NameSorter extends ViewerSorter {

		NameSorter() {
			super();
			return;
		}

	}

	public DbInterfaceProperties getDbInterfaceProperties() {
		if (this.dbInterfaceproperties == null) {
			DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
					.getFromFile();
			this.dbInterfaceproperties = dbInterfaceproperties;
		}
		return (dbInterfaceproperties);
	}

	private class ShowJobActionHandler extends Action {
		private IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for ShowJobActionHandler.
		 */
		protected ShowJobActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for ShowJobActionHandler.
		 * 
		 * @param text
		 */
		protected ShowJobActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canShowJob(selection));
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doShowJob(selection);
		}
	}

	private class CutActionHandler extends Action {
		private IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for CutActionHandler.
		 */
		protected CutActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for CutActionHandler.
		 * 
		 * @param text
		 */
		protected CutActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canCut(selection));
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doCut(selection);
		}
	}

	private class CopyActionHandler extends Action {
		IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for CopyActionHandler.
		 */
		protected CopyActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for CopyActionHandler.
		 * 
		 * @param text
		 */
		protected CopyActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canCopy(selection));
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doCopy(selection);
		}
	}

	private class PasteActionHandler extends Action {
		IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for PasteActionHandler.
		 */
		protected PasteActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for PasteActionHandler.
		 * 
		 * @param text
		 */
		protected PasteActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canPaste(selection));
		}

		public void run() {
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView PasteActionHandler selection");
			if (selection.isEmpty())
				return;
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView PasteActionHandler doPaste");
			doPaste(selection);
		}
	}

	private class DeleteActionHandler extends Action {
		IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for DeleteActionHandler.
		 */
		protected DeleteActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for DeleteActionHandler.
		 * 
		 * @param text
		 */
		protected DeleteActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canDelete(selection));
		}

		private boolean canDelete(IStructuredSelection selection) {
			if (selection.isEmpty())
				return false;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (!(obj instanceof TreeObject))
					return false;
			}
			return true;
		}

		public void run() {
			if (selection.isEmpty())
				return;
			doDelete(selection);
		}

		private void doDelete(IStructuredSelection selection) {
			handleDelete(selection);
		}
	}

	// TODO see wht to suppress. By now same than Copy
	private class SelectActionHandler extends Action {
		IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for SelectActionHandler.
		 */
		protected SelectActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for SelectActionHandler.
		 * 
		 * @param text
		 */
		protected SelectActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canSelect(selection));
		}

		private boolean canSelect(IStructuredSelection selection) {
			if (selection.isEmpty())
				return false;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (!(obj instanceof TreeObject))
					return false;
			}
			return true;
		}

		public void run() {
			if (selection.isEmpty())
				return;
			ArrayList services = new ArrayList();
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof TreeObject)
					services.add(obj);
			}
			doSelect(services);
		}

		private void doSelect(ArrayList services) {
			// Get the service names and a string representation
			int len = services.size();
			String[] serviceNames = new String[len];
			StringBuffer buf = new StringBuffer();
			for (int i = 0, length = len; i < length; i++) {
				TreeObject service = (TreeObject) services.get(i);
				serviceNames[i] = service.getName();
				if (i > 0)
					buf.append("\n"); //$NON-NLS-1$
				buf.append(service.getName());
			}

			// set the clipboard contents
			clipboard.setContents(
					new Object[] { serviceNames, buf.toString() },
					new Transfer[] { TextTransfer.getInstance() });
		}
	}

	private class FindActionHandler extends Action {
		IStructuredSelection selection;

		private Clipboard clipboard;

		/**
		 * Constructor for FindActionHandler.
		 */
		protected FindActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for FindActionHandler.
		 * 
		 * @param text
		 */
		protected FindActionHandler(String text) {
			super(text);
		}

		public void setSelection(IStructuredSelection selection) {
			this.selection = selection;
			setEnabled(canFind(selection));
		}

		private boolean canFind(IStructuredSelection selection) {
			if (selection.isEmpty())
				return false;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (!(obj instanceof TreeObject))
					return false;
			}
			return true;
		}

		public void run() {
			if (selection.isEmpty())
				return;
			ArrayList services = new ArrayList();
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				Object obj = iter.next();
				if (obj instanceof TreeObject)
					services.add(obj);
			}
			doFind(services);
		}

		private void doFind(ArrayList services) {
			// Get the service names and a string representation
			int len = services.size();
			String[] serviceNames = new String[len];
			StringBuffer buf = new StringBuffer();
			for (int i = 0, length = len; i < length; i++) {
				TreeObject service = (TreeObject) services.get(i);
				serviceNames[i] = service.getName();
				// TODO implement find..........
				if (i > 0)
					buf.append("\n"); //$NON-NLS-1$
				buf.append(service.getName());
			}

		}
	}

	private class UndoActionHandler extends Action {
		// FIXME implement
		private Clipboard clipboard;

		/**
		 * Constructor for UndoActionHandler.
		 */
		protected UndoActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for UndoActionHandler.
		 * 
		 * @param text
		 */
		protected UndoActionHandler(String text) {
			super(text);
		}

		private boolean canUndo(IStructuredSelection selection) {
			return true;
		}

		public void run() {
		}
	}

	private class RedoActionHandler extends Action {
		// FIXME implement
		private Clipboard clipboard;

		/**
		 * Constructor for RedoActionHandler.
		 */
		protected RedoActionHandler(Clipboard clipboard) {
			setEnabled(false);
			this.clipboard = clipboard;
		}

		/**
		 * Constructor for RedoActionHandler.
		 * 
		 * @param text
		 */
		protected RedoActionHandler(String text) {
			super(text);
		}

		private boolean canRedo(IStructuredSelection selection) {
			return true;
		}

		public void run() {
		}
	}

	class TreeAttribut extends TreeObject {

		private ArrayList children;

		private DBColumn column;

		private boolean isPrimaryKeyPart = false;

		public TreeAttribut(String string) {
			super(string);
			children = new ArrayList();
			return;
		}

		public void clear() {
			children.clear();
			return;
		}

		public void setColumn(DBColumn dbcolumn) {
			column = dbcolumn;
			return;
		}

		public DBColumn getColumn() {
			return (column);
		}

		public void setPrimaryKeyPart(boolean b) {
			this.isPrimaryKeyPart = b;
		}

		public boolean isPrimaryKeyPart() {
			return (this.isPrimaryKeyPart);
		}

	}

	class TreeObject implements IAdaptable {

		private String name;

		private TreeParent parent;

		private DBTable table;

		public TreeObject(String string) {
			parent = null;
			table = null;
			name = string;
			return;
		}

		public String getName() {
			return (name);
		}

		public void setName(String newName) {
			this.name = newName;
		}

		public void setParent(TreeParent treeparent) {
			parent = treeparent;
			return;
		}

		public TreeParent getParent() {
			return (parent);
		}

		public String toString() {
			return (getName());
		}

		public Object getAdapter(Class class1) {
			return (null);
		}

		public void clear() {
			return;
		}

		public void setTable(DBTable dbtable) {
			table = dbtable;
			return;
		}

		public DBTable getTable() {
			return (table);
		}

		public TreeView getTreeView() {
			return TreeView.this;
		}
	}

	class TreeParent extends TreeObject {

		private ArrayList children;

		public TreeParent(String string) {
			super(string);
			children = new ArrayList();
			return;
		}

		public void addChild(TreeObject treeobject) {
			children.add(treeobject);
			treeobject.setParent(this);
			return;
		}

		public void removeChild(TreeObject treeobject) {
			children.remove(treeobject);
			treeobject.setParent(null);
			return;
		}

		public TreeObject[] getChildren() {
			return ((TreeObject[]) children.toArray(new TreeObject[children
					.size()]));
		}

		public boolean hasChildren() {
			if (children.size() > 0)
				return (true);

			return (false);
		}

		public void clear() {
			children.clear();
			return;
		}

	}

	class TreeRoot extends TreeParent {

		private ArrayList children;

		public TreeRoot(String string) {
			super(string);
			children = new ArrayList();
			return;
		}

		public void clear() {
			children.clear();
			return;
		}

	}

	class TreeTable extends TreeParent {

		private ArrayList children;

		private String m_schema;

		public TreeTable(String string) {
			super(string);
			m_schema = "";
			children = new ArrayList();
			return;
		}

		public void setSchema(String string) {
			m_schema = string;
			return;
		}

		public String getSchema() {
			return (m_schema);
		}

		public void clear() {
			children.clear();
			return;
		}

	}

	class ViewContentProvider implements IStructuredContentProvider,
			ITreeContentProvider {

		ViewContentProvider() {
			return;
		}

		public void inputChanged(Viewer viewer, Object object, Object object3) {
			return;
		}

		public void dispose() {
			return;
		}

		public Object[] getElements(Object object) {
			if (object.equals(ResourcesPlugin.getWorkspace())) {
				if (invisibleRoot == null)
					initialize();

				return (getChildren(invisibleRoot));
			}
			return (getChildren(object));
		}

		public Object getParent(Object object) {
			if (object instanceof TreeObject)
				return (((TreeObject) object).getParent());

			return (null);
		}

		public Object[] getChildren(Object object) {
			if (object instanceof TreeParent)
				return (((TreeParent) object).getChildren());

			return (new Object[0]);
		}

		public boolean hasChildren(Object object) {
			if (object instanceof TreeParent)
				return (((TreeParent) object).hasChildren());

			return (false);
		}

		public void initialize() {
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			String strDbalias = dbInterfaceproperties.getDbalias();
			DBCatalog dbcatalog = null;
			DBTable dbtable;
			TreeRoot treeroot;
			HashMap treemap;
			DBSchema dbschema = null;
			TreeTable treetable;
			TreeAttribut treecolumn;
			String strColumn = "";
			DBColumn dbcolumn;
			Iterator columnIt;
			if (strDbalias != null && strDbalias.length() > 0)
				treeroot = new TreeRoot(strDbalias);

			else
				treeroot = new TreeRoot("Root");

			invisibleRoot = new TreeParent("");
			invisibleRoot.addChild(treeroot);
			try {
				if (!connectToDB())
					return;

				treemap = dbMeta.getCatalogs();
				if (treemap == null)
					return;
				Iterator catalogIt = treemap.values().iterator();
				while (catalogIt.hasNext()) {
					dbcatalog = (DBCatalog) catalogIt.next();
					for (int i = 0; i < dbcatalog.getChildCount(); i++) {
						dbschema = (DBSchema) dbcatalog.getChildAt(i);
						for (int j = 0; j < dbschema.getChildCount(); j++) {
							dbtable = (DBTable) dbschema.getChildAt(j);
							// FIXME control
							treetable = new TreeTable(dbtable.getSClassName());
							treetable.setTable(dbtable);
							treetable.setSchema(m_schema);
							columnIt = dbtable.getColums().values().iterator();
							while (columnIt.hasNext()) {
								dbcolumn = (DBColumn) columnIt.next();
								strColumn = dbcolumn.getJavaFieldName();
								treecolumn = new TreeAttribut(strColumn);
								treecolumn.setColumn(dbcolumn);
								treecolumn.setPrimaryKeyPart(dbcolumn
										.isPrimaryKeyPart());
								treetable.addChild(treecolumn);
							}
							treeroot.addChild(treetable);
						}
						invisibleRoot = new TreeParent("");
						invisibleRoot.addChild(treeroot);
					}
				}
			} catch (Exception exception) {
				DbfLauncherPlugin.log(IStatus.INFO, "EXCEPTION "
						+ exception.getMessage());
				return;
			}
			return;
		}
	}

	class ViewLabelProvider extends LabelProvider {

		ViewLabelProvider() {
			super();
			return;
		}

		public String getText(Object object) {
			return (object.toString());
		}

		public Image getImage(Object object) {
			if (object instanceof TreeRoot)
				return (fFolderIcon);

			if (object instanceof TreeTable)
				return (fTableIcon);

			if (object instanceof TreeAttribut) {
				if (((TreeAttribut) object).isPrimaryKeyPart()) {
					return (fAttributKeyIcon);
				} else {
					return (fAttributIcon);
				}
			}

			return (fAttributIcon);
		}

	}

	protected void doShowJob() {
		if (transfers == null)
			return;
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		doShowJob(selection);
	}

	private void doShowJob(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doShowJob selection");
		// Get the service names and a string representation
		/*
		 * RioClientOpString rioClientOpString = null; MfgServiceRunner runner =
		 * null; for (Iterator iter = selection.iterator(); iter.hasNext();) {
		 * TreeObject service = (TreeObject) (iter.next()); rioClientOpString =
		 * (RioClientOpString) service.getClientOpString(); runner =
		 * rioClientOpString.getMfgServiceRunner(); runner.doShowJob(); }
		 */
	}

	private void doAddJob(IStructuredSelection selection) {
		Object treeobject = ((IStructuredSelection) selection)
				.getFirstElement();
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doAddJob selection");
		doAddJob(treeobject);
	}

	public Object doAddJob(Object treeobject) {
		Object result = null;
		if (treeobject instanceof TreeRoot) {
			// DBTable dbtable = new DBTable(dbMeta, dbMeta.getSchema(),
			// "no_Name");
			DBTable dbtable = null;
			try {
				dbtable = dbMeta.addTable("no_Name");
				TreeTable treetable = new TreeTable("no_Name");
				treetable.setTable(dbtable);
				treetable.setSchema(m_schema);
				DbfLauncherPlugin.log(IStatus.INFO,
						"TreeView doAddJob addChild");
				((TreeRoot) treeobject).addChild(treetable);
				result = treetable;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (treeobject instanceof TreeTable) {
			TreeTable treeTable = (TreeTable) treeobject;
			DBColumn dbColumn = dbMeta
					.addColumn(treeTable.getTable().getTableName(), "no_Name",
							12, "VARCHAR", 200, 0, "no_Def");
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView doAddJob addChild Field");
			TreeAttribut treeAtribute = new TreeAttribut("no_Name");
			treeAtribute.setColumn(dbColumn);
			result = treeAtribute;
			treeTable.addChild(treeAtribute);
		} else
			showMessage(VIEWER_MESSAGE_SELECT_SERVICE);
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doAddJob refresh");
		viewer.refresh();

		return result;
	}

	private void doRenameJob(IStructuredSelection selection, String newName) {
		Object treeobject = ((IStructuredSelection) selection)
				.getFirstElement();
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doRenameJob selection "
				+ newName);
		doRenameJob(treeobject, newName);
	}

	private void doRenameJob(Object treeobject, String newName) {
		if (treeobject instanceof TreeTable) {
			TreeTable treeTable = (TreeTable) treeobject;
			DbfLauncherPlugin.log(IStatus.INFO, "TreeView doRenameJob "
					+ newName);
			DBTable dbTable = treeTable.getTable();
			dbTable.setTableName(newName);
			dbMeta.changeTable(treeTable.getName(), dbTable);
			treeTable.setName(newName);
		} else if (treeobject instanceof TreeAttribut) {
			TreeAttribut treeColumn = (TreeAttribut) treeobject;
			TreeTable treeTable = (TreeTable) treeColumn.getParent();
			DBTable dbTable = treeTable.getTable();
			DBColumn dbColumn = treeColumn.getColumn();
			String columnName = dbColumn.getColumnName();
			/*
			 * String colStr = treeColumn.getName();
			 * DbfLauncherPlugin.log(IStatus.INFO, "TreeView doRenameJob colStr " +
			 * colStr); int typeIndex = colStr.indexOf(" ["); String columnName =
			 * null; if (typeIndex != -1) columnName = colStr.substring(0,
			 * colStr.indexOf(" [")); else columnName = colStr;
			 */
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView doRenameJob columnName " + columnName);
			// DBColumn dbColumn = dbTable.getColumn(columnName);
			// FIXME
			DbfLauncherPlugin.log(IStatus.INFO, "TreeView doRenameJob "
					+ newName);
			// Probablement faire ca au niveau de la saisie pas ici
			dbColumn.setJavaFieldName(newName);
			/*
			 * dbColumn.setColumnName(newName);
			 * dbMeta.changeColumn(treeTable.getName(), colStr, dbColumn);
			 */
			treeColumn.setName(newName);
		} else if (treeobject instanceof TreeRoot) {
		} else
			showMessage(VIEWER_MESSAGE_SELECT_SERVICE);
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doRenameJob refresh");
		viewer.refresh();
	}

	private void doEditJob(IStructuredSelection selection) {
		Object treeobject = ((IStructuredSelection) selection)
				.getFirstElement();
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doEditJob selection");
		if (treeobject instanceof TreeTable) {
			/*
			 * FIXME just do it // Create the wizard DbfModuleEditionWizard
			 * wizard = new DbfModuleEditionWizard();
			 * wizard.setServiceManager(serviceManager);
			 * wizard.init(DbfLauncherPlugin.getDefault().getWorkbench(),
			 * selection); // Create the wizard dialog WizardDialog dialog = new
			 * WizardDialog(DbfLauncherPlugin.getShell(), wizard); // Open the
			 * wizard dialog dialog.open();
			 */
		} else if (treeobject instanceof TreeAttribut) {
		} else if (treeobject instanceof TreeRoot) {
		} else
			showMessage(VIEWER_MESSAGE_SELECT_SERVICE);
	}

	private void doAdminJob(IStructuredSelection selection) {
		Object treeobject = ((IStructuredSelection) selection)
				.getFirstElement();
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doAdminJob selection");
		if (treeobject instanceof TreeTable) {
		} else if (treeobject instanceof TreeAttribut) {
		} else if (treeobject instanceof TreeRoot) {
		} else
			showMessage(VIEWER_MESSAGE_SELECT_SERVICE);
	}

	protected void doCopy() {
		if (transfers == null)
			return;
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		doCopy(selection);
	}

	private void doCopy(IStructuredSelection selection) {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCopy selection");
		// Get the service names and a string representation
		String tokenString = null;
		cutObjects = new TreeObject[selection.size()];
		synchronized (history) {
			int i = 0;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				TreeObject service = (TreeObject) (iter.next());
				cutObjects[i++] = service;
				if (tokenString == null)
					tokenString = service.getName();
				else
					tokenString = tokenString.concat("	" + service.getName());
				if (!history.offer(service)) {
					history.poll();
					// Should never arrive
					if (!history.offer(service))
						DbfLauncherPlugin.log(IStatus.ERROR,
								"TreeView history Full " + tokenString);
				}
			}
		}
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCopy tokenString "
				+ tokenString);
		// set the clipboard contents
		clipboard.setContents(new Object[] { tokenString }, transfers);
	}

	void doCopy(TreeObject service) {
		// Get the service names and a string representation
		String tokenString = null;
		cutObjects = new TreeObject[1];
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCopy service "
				+ service.getName());
		cutObjects[0] = service;
		synchronized (history) {
			tokenString = service.getName();
			if (!history.offer(service)) {
				history.poll();
				// Should never arrive
				if (!history.offer(service))
					DbfLauncherPlugin.log(IStatus.ERROR,
							"TreeView history Full " + tokenString);
			}
		}
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCopy tokenString "
				+ tokenString);
		// set the clipboard contents
		// FIXME commented for tests
		// clipboard.setContents(new Object[] { tokenString }, transfers);
	}

	protected void doCut() {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut");
		if (transfers == null)
			return;
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		doCut(selection);
	}

	private void doCut(IStructuredSelection selection) {
		// Get the service names and a string representation
		String tokenString = null;
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut selection");
		cutObjects = new TreeObject[selection.size()];
		synchronized (history) {
			int i = 0;
			for (Iterator iter = selection.iterator(); iter.hasNext();) {
				TreeObject service = (TreeObject) (iter.next());
				cutObjects[i++] = service;
				if (tokenString == null)
					tokenString = service.getName();
				else
					tokenString = tokenString.concat("	" + service.getName());
				if (!history.offer(service)) {
					history.poll();
					// Should never arrive
					if (!history.offer(service))
						DbfLauncherPlugin.log(IStatus.ERROR,
								"TreeView history Full " + tokenString);
				}
			}
		}
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut tokenString "
				+ tokenString);
		// set the clipboard contents
		clipboard.setContents(new Object[] { tokenString }, transfers);
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut handleDelete "
				+ tokenString);
		handleDelete(selection);
	}

	void doCut(TreeObject service) {
		// Get the service names and a string representation
		String tokenString = null;
		cutObjects = new TreeObject[1];
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut service "
				+ service.getName());
		cutObjects[0] = service;
		synchronized (history) {
			tokenString = service.getName();
			if (!history.offer(service)) {
				history.poll();
				// Should never arrive
				if (!history.offer(service))
					DbfLauncherPlugin.log(IStatus.ERROR,
							"TreeView history Full " + tokenString);
			}
		}
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut tokenString "
				+ tokenString);
		// set the clipboard contents
		// FIXME commented for tests
		// clipboard.setContents(new Object[] { tokenString }, transfers);
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doCut handleDelete "
				+ tokenString);
		handleDelete(service);
	}

	/*
	 * void doCut(Object dragData) { // Get the service names and a string
	 * representation String tokenString = null; System.out.println("TreeView
	 * doCut dragData " + dragData); cutObjects = new TreeObject[1]; TreeObject
	 * service = (TreeObject) (serviceManager.getRioTreeList()) .get(dragData);
	 * System.out.println("TreeView doCut service " + service.getName());
	 * cutObjects[0] = service; synchronized (history) { tokenString =
	 * service.getName(); if (!history.offer(service)) { history.poll(); //
	 * Should never arrive if (!history.offer(service))
	 * System.err.println("TreeView history Full " + tokenString); } }
	 * System.out.println("TreeView doCut tokenString " + tokenString); // set
	 * the clipboard contents // FIXME commented for tests //
	 * clipboard.setContents(new Object[] { tokenString }, transfers);
	 * System.out.println("TreeView doCut handleDelete " + tokenString);
	 * handleDelete(service); }
	 */

	protected void doPaste() {
		if (transfers == null)
			return;
		IStructuredSelection selection = (IStructuredSelection) viewer
				.getSelection();
		doPaste(selection);
	}

	public void doPaste(IStructuredSelection selection) {
		TreeObject target = (TreeObject) selection.getFirstElement();
		TextTransfer textTransfer = TextTransfer.getInstance();
		/*
		 * String tokenString = (String) clipboard.getContents(textTransfer); if
		 * (tokenString != null) { doPaste(target, tokenString); }
		 */
		// On regarde le plus vieux
		TreeObject treeobject = (TreeObject) (history.peek());
		DbfLauncherPlugin.log(IStatus.INFO,
				"TreeView PasteActionHandler doPaste");
		if (treeobject instanceof TreeTable) {
			DbfLauncherPlugin.log(IStatus.INFO, "TreeView doPaste TreeTable");
			TreeTable treeparent = (TreeTable) treeobject;
			if (target instanceof TreeRoot) {
				DbfLauncherPlugin
						.log(IStatus.INFO,
								"TreeView doPaste TreeTable target instanceof TreeRoot");
				// Si Ok on recupere l'enregistrement
				// On l'enregistre sequence ????
				// On affiche
				history.poll();
			}
		} else if (treeobject instanceof TreeAttribut) {
			DbfLauncherPlugin
					.log(IStatus.INFO, "TreeView doPaste TreeAttribut");
			TreeAttribut treeparent = (TreeAttribut) treeobject;
			if (target instanceof TreeTable) {
				DbfLauncherPlugin
						.log(IStatus.INFO,
								"TreeView doPaste TreeTable target instanceof TreeTable");
				history.poll();
				doPaste(target, treeobject);
			}
		} else if (treeobject instanceof TreeRoot) {
			DbfLauncherPlugin.log(IStatus.INFO, "TreeView dataview.setInput");

		} else
			showMessage(VIEWER_MESSAGE_SELECT_SERVICE);
	}

	public void doPaste(Object target, String tokenString) {
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doPaste " + tokenString
				+ " on " + target);
		// We get a string containing liste of objects to Paste
		/*
		 * StringTokenizer tok = new StringTokenizer(tokenString, ", \t\n\r\f");
		 * while (tok.hasMoreTokens()) { Object tmpObject = tok.nextToken();
		 * //FIXME implement probablement en faisint une recherche dans les tree
		 * pour trouver les TreeObjects correspondants //doPaste(target,
		 * tmpObject); }
		 */
		if (cutObjects == null)
			return;
		// We don't use clipBoard but cutObjects
		// We controle that target can be a legal parent of them
		for (int i = 0; i < cutObjects.length; i++) {
			// FIXME
			// Attention a gerer nom longs
			Object cutObject = cutObjects[i];
			String obj = ((TreeObject) cutObject).getName();
			DbfLauncherPlugin.log(IStatus.INFO,
					"TreeView doPaste objects obj String " + obj + " target "
							+ ((TreeObject) target).getName());
			doPaste((TreeObject) target, (TreeObject) cutObject);
		}
		DbfLauncherPlugin.log(IStatus.INFO,
				"TreeView doPaste objects cutObject finish ");
	}

	private void doPaste(TreeObject target, TreeObject treeObject) {
		// FIXME implement
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doPaste1 : "
				+ treeObject.getName());
		/*
		 * MfgScmTableElement mfgScmTableElement = new MfgScmTableElement();
		 * DbfLauncherPlugin.log(IStatus.INFO, "TreeView doPaste1
		 * setServiceManager: " + treeObject.getName());
		 * mfgScmTableElement.setServiceManager(serviceManager);
		 * mfgScmTableElement.setTreeObject(treeObject);
		 * mfgScmTableElement.registerService(target);
		 */
		DbfLauncherPlugin.log(IStatus.INFO, "TreeView doPaste finish ");
	}

	public Viewer getTreeViewer() {
		return viewer;
	}
}
