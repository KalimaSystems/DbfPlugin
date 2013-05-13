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
import java.sql.Statement;
import java.util.Vector;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfProject;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBConnectAction;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DbInterfaceProperties;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class TableView extends ViewPart {

	public static final String NAME = "org.compiere.mfg_scm.eclipse.db.view.TableView";

	private TableViewer viewer = null;

	private Action reloadAction;

	private Action sqlQueryAction;

	private String m_stmd;

	private Vector shortList;

	protected final Image fRefreshIcon;

	protected final Image fSQLIcon;

	private DBConnectAction dbconnectaction = null;

	private DbInterfaceProperties dbInterfaceproperties;

	private java.sql.Connection conn;

	final String VIEWER_TABLE_LABEL_ERROR = DbfLauncherPlugin
			.getResourceString("viewer.table.label.error");

	public TableView() {
		super();
		m_stmd = "";
		conn = null;
		shortList = new Vector();
		fRefreshIcon = TreeView.createImage("icons/reloadDB.gif", getClass());
		fSQLIcon = TreeView.createImage("icons/SQL.gif", getClass());
		return;
	}

	public void setInput(String string) {
		m_stmd = "select * from " + string;
		shortList.add(m_stmd);
		viewer.setInput(getResultSet());
		return;
	}

	public void createPartControl(Composite composite) {
		makeActions();
		Table table = new Table(composite, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL | SWT.MULTI | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		System.out.println("MAKETABLE VIEWER");
		viewer = new TableViewer(table);
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(ResourcesPlugin.getWorkspace());
		hookContextMenu();
		contributeToActionBars();
		return;
	}

	public void setDbInterfaceProperties(
			DbInterfaceProperties dbInterfaceProperties) {
	}

	public void makeTableCol(String[] columns) {
		Table table = viewer.getTable();
		TableColumn tablecolumn;
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getColumn(i).setText("");
			table.getColumn(i).setWidth(0);
		}
		if (table.getColumnCount() < columns.length) {
			for (int i = table.getColumnCount(); i <= columns.length; i++) {
				tablecolumn = new TableColumn(table, SWT.LEFT, 0);
				tablecolumn.setText("");
				tablecolumn.setWidth(0);
			}
		}
		for (int i = 0; i < columns.length; i++) {
			tablecolumn = table.getColumn(i);
			tablecolumn.setText(columns[i]);
			tablecolumn.setWidth(columns[i].length() * 15);
		}
		return;
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
		if (viewer == null)
			System.out.println("HOOK CONTROL VIEWER NULL");
		viewer.getControl();
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
		return;
	}

	private void fillContextMenu(IMenuManager imenumanager) {
		imenumanager.add(reloadAction);
		return;
	}

	private void fillLocalToolBar(IToolBarManager itoolbarmanager) {
		itoolbarmanager.add(sqlQueryAction);
		itoolbarmanager.add(reloadAction);
		return;
	}

	private boolean connectToDB() {
		final String VIEWER_EXCEPTION_DB_CONNECT = DbfLauncherPlugin
				.getResourceString("viewer.exception.db.connect");
		Boolean i = Boolean.FALSE;
		if (conn == null) {
			try {
				dbInterfaceproperties = DbInterfaceProperties.getFromFile();
				// OJB
				// System.out.println("MfgScmTableElement connectToDB");
				dbconnectaction = DbfLauncherPlugin.getDefault()
						.getDBConnectAction();
				if (dbconnectaction == null) {
					// System.out.println("MfgScmTableElement new
					// DBConnectAction");
					dbconnectaction = new DBConnectAction(DbInterfaceProperties
							.getPropertie(dbInterfaceproperties));
				}
				conn = dbconnectaction.actionConnect();
				i = Boolean.TRUE;
			} catch (Exception exception) {
				DbfLauncherPlugin.log(IStatus.ERROR, "TableView "
						+ VIEWER_EXCEPTION_DB_CONNECT);
				DbfLauncherPlugin.log(exception);
			}
		} else {
			i = Boolean.TRUE;
		}
		return (i.booleanValue());
	}

	public ResultSet getResultSet() {
		try {
			connectToDB();
			Statement statement = conn.createStatement();
			ResultSet resultset = statement.executeQuery(m_stmd);
			return (resultset);
		} catch (Exception exception) {
			showMessage(VIEWER_TABLE_LABEL_ERROR + exception.getMessage()
					+ "\n stmd:" + m_stmd);
			return (null);
		}
	}

	private void makeActions() {
		makeReloadAction();
		makeGetSQLQueryAction();
		return;
	}

	private void makeReloadAction() {
		final String VIEWER_TABLE_LABEL_RELOAD = DbfLauncherPlugin
				.getResourceString("viewer.table.label.reload");
		final String VIEWER_TABLE_TIP_RELOAD = DbfLauncherPlugin
				.getResourceString("viewer.table.tip.reload");
		reloadAction = new ReloadAction();
		reloadAction.setText(VIEWER_TABLE_LABEL_RELOAD);
		reloadAction.setToolTipText(VIEWER_TABLE_TIP_RELOAD);
		reloadAction.setImageDescriptor(ImageDescriptor
				.createFromURL(getClass().getResource("icons/reloadDB.gif")));
		return;
	}

	private void makeGetSQLQueryAction() {
		final String VIEWER_TABLE_LABEL_QUERY = DbfLauncherPlugin
				.getResourceString("viewer.table.label.query");
		final String VIEWER_TABLE_TIP_QUERY = DbfLauncherPlugin
				.getResourceString("viewer.table.tip.query");
		sqlQueryAction = new TableViewAction();
		sqlQueryAction.setText(VIEWER_TABLE_LABEL_QUERY);
		sqlQueryAction.setToolTipText(VIEWER_TABLE_TIP_QUERY);
		sqlQueryAction.setImageDescriptor(ImageDescriptor
				.createFromURL(getClass().getResource("icons/SQL.gif")));
		return;
	}

	private void showMessage(String string) {
		final String VIEWER_TABLE_LABEL_MESSAGE = DbfLauncherPlugin
				.getResourceString("viewer.table.label.message");
		MessageDialog.openInformation(viewer.getControl().getShell(),
				VIEWER_TABLE_LABEL_MESSAGE, string);
		return;
	}

	private String inputSQLDialog() {
		int i;
		SQLDialog sqldialog = null;
		int j;
		sqldialog = new SQLDialog(viewer.getControl().getShell());

		sqldialog.setSQL(m_stmd);
		sqldialog.setShortList(shortList);
		sqldialog.open();
		if (sqldialog.getReturnCode() == 0) {
			String tmpStr = sqldialog.getSQL();
			i = 1;
			for (j = 0; j < shortList.size(); j++) {
				if (((String) shortList.get(j)).equals(tmpStr))
					i = 0;
			}
			if (i != 0) {
				shortList.add(tmpStr);
				while (shortList.size() > 10)
					shortList.remove(0);
			}
			return (tmpStr);
		}
		return (null);
	}

	public void setFocus() {
		viewer.getControl().setFocus();
		return;
	}

	private boolean getOK(String string) {
		final String VIEWER_TABLE_LABEL_CONFIRM = DbfLauncherPlugin
				.getResourceString("viewer.table.label.confirm");
		return (MessageDialog.openConfirm(viewer.getControl().getShell(),
				VIEWER_TABLE_LABEL_CONFIRM, string));
	}

	protected IJavaProject getJavaProject() throws CoreException {
		IProject project = (IProject) (this.getAdapter(IProject.class));
		return (IJavaProject) (project.getNature(JavaCore.NATURE_ID));
	}

	protected DbfProject getDbfProject() throws CoreException {
		return DbfProject.create(getJavaProject());
	}

	final class TableViewAction extends Action {

		TableViewAction() {
			super();
			return;
		}

		public void run() {
			String tmpStr = inputSQLDialog();
			if (tmpStr != null) {
				m_stmd = tmpStr;
				viewer.setInput(getResultSet());
			}
			return;
		}

	}

	final class ReloadAction extends Action {

		ReloadAction() {
			super();
			return;
		}

		public void run() {
			viewer.setInput(getResultSet());
			return;
		}

	}

	class NameSorter extends ViewerSorter {

		NameSorter() {
			super();
			return;
		}

	}

	class ViewContentProvider implements IStructuredContentProvider {

		ViewContentProvider() {
			return;
		}

		public void inputChanged(Viewer viewer, Object object, Object object3) {
			return;
		}

		public void dispose() {
			return;
		}

		public Object[] getElements(Object elements) {
			final String VIEWER_TABLE_LABEL_EMPTY = DbfLauncherPlugin
					.getResourceString("viewer.table.label.empty");
			final String VIEWER_TABLE_PARAM_MAXROWS = DbfLauncherPlugin
					.getResourceString("viewer.table.param.maxrows");
			final String VIEWER_TABLE_LABEL_MAXROWS = DbfLauncherPlugin
					.getResourceString("viewer.table.label.maxrows");
			long maxRows = (new Long(VIEWER_TABLE_PARAM_MAXROWS)).longValue();
			String[] strRows = new String[1];
			strRows[0] = VIEWER_TABLE_LABEL_EMPTY;
			if (elements instanceof ResultSet) {
				try {
					ResultSet resultset = (ResultSet) elements;
					int colNumber = resultset.getMetaData().getColumnCount();
					String[] strColumns = new String[colNumber];
					Vector vectorRows;
					for (int colNum = 1; colNum <= colNumber; colNum++) {
						strColumns[colNum - 1] = resultset.getMetaData()
								.getColumnName(colNum);
					}
					makeTableCol(strColumns);
					vectorRows = new Vector();
					for (long rowNum = 0; resultset.next(); rowNum++) {
						if (rowNum == maxRows
								&& !getOK(VIEWER_TABLE_LABEL_MAXROWS)) {
							resultset.close();
							resultset = null;
							return (vectorRows.toArray());
						}
						strColumns = new String[colNumber];
						for (int colNum = 0; colNum < colNumber; colNum++) {
							strColumns[colNum] = resultset
									.getString(colNum + 1);
							if (strColumns[colNum] == null)
								strColumns[colNum] = "";
						}
						vectorRows.add(strColumns);
					}
					resultset.close();
					resultset = null;
					return (vectorRows.toArray());
				} catch (Exception exception) {
					showMessage(VIEWER_TABLE_LABEL_ERROR
							+ exception.getMessage());
					DbfLauncherPlugin.log(exception);
				}
			}
			return (strRows);
		}

	}

	class ViewLabelProvider extends LabelProvider implements
			ITableLabelProvider {

		ViewLabelProvider() {
			super();
			return;
		}

		public String getColumnText(Object object, int i) {
			if (object instanceof String[]) {
				String[] stringArr = (String[]) object;
				if (i < stringArr.length)
					return (stringArr[i]);

				return ("");
			}
			return ((String) object);
		}

		public Image getColumnImage(Object object, int i) {
			return (null);
		}

		public Image getImage(Object object) {
			return (PlatformUI.getWorkbench().getSharedImages()
					.getImage("IMG_OBJ_ELEMENTS"));
		}

	}
}
