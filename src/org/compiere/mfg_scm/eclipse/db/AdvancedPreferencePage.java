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

import java.beans.IntrospectionException;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.ddlutils.Platform;
import org.apache.ddlutils.PlatformFactory;
import org.apache.ddlutils.io.DatabaseIO;
import org.apache.ddlutils.io.JdbcModelReader;
import org.apache.ddlutils.model.Database;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBConnectAction;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DbInterfaceProperties;
import org.compiere.mfg_scm.eclipse.db.editors.ProjectListEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.xml.sax.SAXException;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class AdvancedPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage, DbfPluginResources {

	private FileFieldEditor creationFile;

	private ProjectListEditor projectListEditor;

	private Button buttonCreation;

	private Button buttonCreatFromFile;

	private Button buttonCreatFromXML;

	private Connection conn;

	private DBConnectAction dbconnectaction;

	public AdvancedPreferencePage() {
		super();
		dbconnectaction = null;
		setPreferenceStore(DbfLauncherPlugin.getDefault().getPreferenceStore());
	}

	/*
	 * @see PreferencePage#createContents(Composite)
	 */
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		composite.setLayout(new GridLayout(1, false));

		Group homeGroup = new Group(composite, SWT.NONE);

		Label titleLabel = new Label(homeGroup, SWT.NONE);
		titleLabel.setText(PREF_PAGE_CREATIONTITLE_LABEL);
		titleLabel.setEnabled(true);
		Label spaceTitle = new Label(homeGroup, SWT.NONE);
		spaceTitle.setText("");
		spaceTitle.setEnabled(true);
		Label space1Title = new Label(homeGroup, SWT.NONE);
		space1Title.setText("");
		space1Title.setEnabled(true);
		// base = new DirectoryFieldEditor(DbfLauncherPlugin.DBF_PREF_BASE_KEY,
		// PREF_PAGE_BASE_LABEL, homeGroup);

		creationFile = new FileFieldEditor(
				DbfLauncherPlugin.DBF_PREF_CREAT_KEY, PREF_PAGE_CREAT_LABEL,
				homeGroup);
		buttonCreation = new Button(homeGroup, SWT.NONE);
		buttonCreation.setText("create Tables from Bundle");
		// buttonCreation.addKeyListener(new MyTablesCreateKeyListener());
		buttonCreation.addMouseListener(new MyTablesCreateMousListener());
		buttonCreatFromFile = new Button(homeGroup, SWT.NONE);
		buttonCreatFromFile.setText("Execute script from file");
		// buttonCreatFromFile.addKeyListener(new MyTablesCreateKeyListener());
		buttonCreatFromFile
				.addMouseListener(new MyTablesCreateFromFileMousListener());
		buttonCreatFromXML = new Button(homeGroup, SWT.NONE);
		buttonCreatFromXML.setText("create Tables from torque XML");
		// buttonCreatFromXML.addKeyListener(new
		// MyTablesCreateFromXMLKeyListener());
		buttonCreatFromXML
				.addMouseListener(new MyTablesCreateFromXMLMousListener());

		initLayoutAndData(homeGroup, 3);

		Group projectListGroup = new Group(composite, SWT.NONE);
		String[] excludedProjectsNature = { DbfLauncherPlugin.NATURE_ID };
		projectListEditor = new ProjectListEditor(excludedProjectsNature);
		projectListEditor.setLabel(PREF_PAGE_PROJECTINCP_LABEL);
		Control projectList = projectListEditor.getControl(projectListGroup);
		GridData gd2 = new GridData();
		gd2.horizontalAlignment = GridData.FILL;
		projectList.setLayoutData(gd2);
		initLayoutAndData(projectListGroup, 1);

		// this.initField(base);
		this.initField(creationFile);

		new Label(composite, SWT.NULL); // blank

		return composite;
	}

	/*
	 * @see IWorkbenchPreferencePage#init(IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}

	public boolean performOk() {
		// base.store();
		creationFile.store();
		DbfLauncherPlugin.getDefault().setProjectsInCP(
				projectListEditor.getCheckedElements());
		DbfLauncherPlugin.getDefault().savePluginPreferences();
		return true;
	}

	private void initField(FieldEditor field) {
		field.setPreferenceStore(getPreferenceStore());
		field.setPreferencePage(this);
		field.load();
	}

	private void initLayoutAndData(Group aGroup, int spanH, int spanV,
			int numColumns) {
		GridLayout gl = new GridLayout(numColumns, false);
		aGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = spanH;
		gd.verticalSpan = spanV;
		aGroup.setLayoutData(gd);
	}

	private void initLayoutAndData(Group aGroup, int numColumns) {
		GridLayout gl = new GridLayout(numColumns, false);
		aGroup.setLayout(gl);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		aGroup.setLayoutData(gd);
	}

	class MyTablesCreateKeyListener implements KeyListener {

		MyTablesCreateKeyListener() {
			return;
		}

		public void keyReleased(KeyEvent keyevent) {
			return;
		}

		public void keyPressed(KeyEvent keyevent) {
			/*
			 * if (removeJar()) { bDirty = true; handlePropertyChange(1); }
			 */
			return;
		}

	}

	class MyTablesCreateMousListener implements MouseListener {

		MyTablesCreateMousListener() {
			return;
		}

		public void mouseUp(MouseEvent mouseevent) {
			/*
			 * if (removeJar()) { bDirty = true; handlePropertyChange(1); }
			 */
			return;
		}

		public void mouseDown(MouseEvent mouseevent) {
			// File statementFile = new
			// File("/home/Absynt/Compiere/mfg_scm/sbin/statement.sql");
			// String statementResource = "sbin/statement.sql";
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			String platform = dbInterfaceproperties.getPlatform();

			String statementResource = "";
			if (platform.equals("Db2"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Hsqldb"))
				statementResource = "src/createHSqlDb.sql";
			else if (platform.equals("Informix"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("MsAccess"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("MsSQLServer"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("MySQL"))
				statementResource = "src/createMySql.sql";
			else if (platform.equals("Oracle"))
				statementResource = "src/createOracle.sql";
			else if (platform.equals("PostgreSQL"))
				statementResource = "src/createPostgres.sql";
			else if (platform.equals("Sybase"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("SybaseASE"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("SybaseASA"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Sapdb"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Firebird"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Axion"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("NonstopSql"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Oracle9i"))
				statementResource = "src/createOracle.sql";
			else if (platform.equals("MaxDB"))
				statementResource = "src/createStdSql.sql";
			else
				statementResource = "src/createStdSql.sql";

			// TODO Add a message box to ask to confirm
			// TODO Control if tables already exists and warn if exists
			// statementResource = "src/statement.sql";
			String m_stmd = null;
			try {
				// m_stmd = FileUtil.readTextFile(statementFile);
				m_stmd = JarUtil.readTextResource(statementResource);
			} catch (IOException e) {
				DbfLauncherPlugin.log(e);
			}
			try {
				connectToDB();
				Statement statement = conn.createStatement();
				boolean res = statement.execute(m_stmd);
			} catch (Exception exception) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dbconnectaction != null) {
					dbconnectaction.actionReConnect();
				}
				DbfLauncherPlugin.log("MOUSE DOWN exception :"
						+ exception.getMessage() + "\n stmd:" + m_stmd);
				DbfLauncherPlugin.log(exception);
			}
			return;
		}

		public void mouseDoubleClick(MouseEvent mouseevent) {
			return;
		}

	}

	class MyTablesCreateFromFileMousListener implements MouseListener {

		MyTablesCreateFromFileMousListener() {
			return;
		}

		public void mouseUp(MouseEvent mouseevent) {
			/*
			 * if (removeJar()) { bDirty = true; handlePropertyChange(1); }
			 */
			return;
		}

		public void mouseDown(MouseEvent mouseevent) {
			String statementFile = creationFile.getStringValue();

			if (statementFile.length() == 0) {
				return;
			}

			String m_stmd = null;
			try {
				m_stmd = FileUtil.readTextFile(new File(statementFile));
			} catch (IOException e) {
				DbfLauncherPlugin.log(e);
			}
			try {
				System.out.println("MOUSE DOWN " + m_stmd);
				connectToDB();
				Statement statement = conn.createStatement();
				boolean res = statement.execute(m_stmd);
				System.out.println("MOUSE DOWN finished " + res);
			} catch (Exception exception) {
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dbconnectaction != null) {
					System.out.println("MOUSE DOWN actionReConnect");
					dbconnectaction.actionReConnect();
				}
				DbfLauncherPlugin.log("MOUSE DOWN exception :"
						+ exception.getMessage() + "\n stmd:" + m_stmd);
				DbfLauncherPlugin.log(exception);
			}
			return;
		}

		public void mouseDoubleClick(MouseEvent mouseevent) {
			return;
		}

	}

	class MyTablesCreateFromXMLMousListener implements MouseListener {

		MyTablesCreateFromXMLMousListener() {
			return;
		}

		public void mouseUp(MouseEvent mouseevent) {
			/*
			 * if (removeJar()) { bDirty = true; handlePropertyChange(1); }
			 */
			return;
		}

		public void mouseDown(MouseEvent mouseevent) {
			// File statementFile = new
			// File("/home/Absynt/Compiere/mfg_scm/sbin/statement.sql");
			// String statementResource = "sbin/statement.sql";
			DbInterfaceProperties dbInterfaceproperties = getDbInterfaceProperties();
			String platform = dbInterfaceproperties.getPlatform();
			String statementResource = "";
			if (platform.equals("Db2"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Hsqldb"))
				statementResource = "src/createHSqlDb.sql";
			else if (platform.equals("Informix"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("MsAccess"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("MsSQLServer"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("MySQL"))
				statementResource = "src/createMySql.sql";
			else if (platform.equals("Oracle"))
				statementResource = "src/createOracle.sql";
			else if (platform.equals("PostgreSQL"))
				statementResource = "src/createPostgres.sql";
			else if (platform.equals("Sybase"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("SybaseASE"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("SybaseASA"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Sapdb"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Firebird"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Axion"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("NonstopSql"))
				statementResource = "src/createStdSql.sql";
			else if (platform.equals("Oracle9i"))
				statementResource = "src/createOracle.sql";
			else if (platform.equals("MaxDB"))
				statementResource = "src/createStdSql.sql";
			else
				statementResource = "src/createStdSql.sql";

			String modelDir = dbInterfaceproperties.getXmlModelRepositoryPath();
			statementResource = modelDir + System.getProperty("file.separator")
					+ "model.xml";

			// TODO Add a message box to ask to confirm

			String m_stmd = null;
			try {
				connectToDB();

				Database targetModel = new DatabaseIO().read(statementResource);

				Platform platform1 = PlatformFactory
						.createNewPlatformInstance(dbInterfaceproperties
								.getPlatform());

				boolean alterDb = true;
				if (alterDb) {
					platform1.alterTables(conn, targetModel, true); // Use
					// existing
					// connection...
				} else {
					platform1.createTables(conn, targetModel, true, false); // Use
					// existing
					// connection...
				}
				// void alterTables(Connection connection, Database desiredDb,
				// boolean continueOnError)
				// void alterTables(Connection connection, Database desiredDb,
				// boolean doDrops, boolean modifyColumns, boolean
				// continueOnError)
				// void alterTables(Database desiredDb, boolean continueOnError)
				// void alterTables(Database desiredDb, boolean doDrops, boolean
				// modifyColumns, boolean continueOnError)
			} catch (Exception exception) {
				exception.printStackTrace();
				try {
					conn.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (dbconnectaction != null) {
					dbconnectaction.actionReConnect();
				}
				DbfLauncherPlugin.log("MOUSE DOWN exception :"
						+ exception.getMessage() + "\n stmd:" + m_stmd);
				DbfLauncherPlugin.log(exception);
			}
			return;
		}

		public void mouseDoubleClick(MouseEvent mouseevent) {
			return;
		}

	}

	private boolean connectToDB() {
		Boolean i = Boolean.FALSE;
		System.out.println("connectToDB dbconnectaction");
		DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
				.getFromFile();
		;
		dbconnectaction = DbfLauncherPlugin.getDefault().getDBConnectAction();
		if (dbconnectaction == null) {
			System.out.println("connectToDB dbconnectaction == null");
			dbconnectaction = new DBConnectAction(DbInterfaceProperties
					.getPropertie(dbInterfaceproperties));
		}
		conn = dbconnectaction.actionConnect();
		i = Boolean.TRUE;
		return (i.booleanValue());
	}

	public DbInterfaceProperties getDbInterfaceProperties() {
		DbInterfaceProperties dbInterfaceproperties = DbInterfaceProperties
				.getFromFile();
		return (dbInterfaceproperties);
	}

	public Database readDatabase() throws ClassNotFoundException, SQLException {
		try {
			System.out.println("readDatabase");
			connectToDB();
			System.out.println("readDatabase");
			JdbcModelReader reader = new JdbcModelReader(conn);
			return reader.getDatabase();
		} catch (Exception exception) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dbconnectaction != null) {
				System.out.println("readDatabase actionReConnect");
				dbconnectaction.actionReConnect();
			}
			DbfLauncherPlugin.log("readDatabase exception :"
					+ exception.getMessage());
			DbfLauncherPlugin.log(exception);
			return (null);
		}
	}

	public Database readDatabaseFromTree() throws ClassNotFoundException,
			SQLException {
		try {
			System.out.println("readDatabase");
			connectToDB();
			System.out.println("readDatabase");
			JdbcModelReader reader = new JdbcModelReader(conn);
			return reader.getDatabase();
		} catch (Exception exception) {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (dbconnectaction != null) {
				System.out.println("readDatabase actionReConnect");
				dbconnectaction.actionReConnect();
			}
			DbfLauncherPlugin.log("readDatabase exception :"
					+ exception.getMessage());
			DbfLauncherPlugin.log(exception);
			return (null);
		}
	}

	public void writeDatabaseToXML(Database db, String fileName)
			throws IOException, SAXException, IntrospectionException {
		new DatabaseIO().write(db, fileName);
		return;
	}

	public Database readDatabaseFromXML(String fileName) throws IOException,
			SAXException, IntrospectionException {
		return (new DatabaseIO().read(fileName));
	}

}
