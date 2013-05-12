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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Vector;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.compiere.mfg_scm.eclipse.db.DbfProjectPropertyPage;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DbInterfaceProperties;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfProjectPropertyEditor {

	private Vector dbValues;

	private Vector genValues;

	private Vector displayValues;

	private Vector modelValues;

	private Vector mapValues;

	private boolean bDirty;

	private List listClasspath;

	private StyledText previewText;

	protected Button addJarZipButton;

	private String lastPath;

	private DbfProjectPropertyPage page;

	public boolean isDirty() {
		return (bDirty);
	}

	public DbfProjectPropertyEditor(DbfProjectPropertyPage page) {
		this.page = page;
		dbValues = new Vector();
		genValues = new Vector();
		displayValues = new Vector();
		modelValues = new Vector();
		mapValues = new Vector();
		bDirty = false;
		listClasspath = null;
		return;
	}

	private Vector getDBElements() {
		final String MULTIPAGE_EDITOR_LABEL_DEFAULT = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.default");
		final String MULTIPAGE_EDITOR_TIP_DEFAULT = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.default");
		final String MULTIPAGE_EDITOR_LABEL_PLATFORM = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.platform");
		final String MULTIPAGE_EDITOR_TIP_PLATFORM = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.platform");
		final String MULTIPAGE_EDITOR_LABEL_JCDALIAS = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.jcdalias");
		final String MULTIPAGE_EDITOR_TIP_JCDALIAS = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.jcdalias");
		final String MULTIPAGE_EDITOR_LABEL_JDBCLEVEL = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.jdbclevel");
		final String MULTIPAGE_EDITOR_TIP_JDBCLEVEL = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.dbclevel");
		final String MULTIPAGE_EDITOR_LABEL_DRIVER = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.driver");
		final String MULTIPAGE_EDITOR_TIP_DRIVER = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.driver");
		final String MULTIPAGE_EDITOR_LABEL_PROTOCOL = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.protocol");
		final String MULTIPAGE_EDITOR_TIP_PROTOCOL = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.protocol");
		final String MULTIPAGE_EDITOR_LABEL_SUBPROTOCOL = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.subprotocol");
		final String MULTIPAGE_EDITOR_TIP_SUBPROTOCOL = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.subprotocol");
		final String MULTIPAGE_EDITOR_LABEL_DBALIAS = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.dbalias");
		final String MULTIPAGE_EDITOR_TIP_DBALIAS = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.dbalias");
		final String MULTIPAGE_EDITOR_LABEL_USER = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.username");
		final String MULTIPAGE_EDITOR_TIP_USER = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.username");
		final String MULTIPAGE_EDITOR_LABEL_PASSWD = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.password");
		final String MULTIPAGE_EDITOR_TIP_PASSWD = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.password");
		final String MULTIPAGE_EDITOR_LABEL_CATALOG = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.catalog");
		final String MULTIPAGE_EDITOR_TIP_CATALOG = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.catalog");
		final String MULTIPAGE_EDITOR_LABEL_SCHEMA = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.schema");
		final String MULTIPAGE_EDITOR_TIP_SCHEMA = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.schema");
		final String MULTIPAGE_EDITOR_LABEL_BATCH = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.batchmode");
		final String MULTIPAGE_EDITOR_TIP_BATCH = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.batchmode");
		final String MULTIPAGE_EDITOR_LABEL_EAGER = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.eager");
		final String MULTIPAGE_EDITOR_TIP_EAGER = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.eager");
		final String MULTIPAGE_EDITOR_LABEL_AUTO = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.auto");
		final String MULTIPAGE_EDITOR_TIP_AUTO = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.auto");
		final String MULTIPAGE_EDITOR_LABEL_IGNORE = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.ignore");
		final String MULTIPAGE_EDITOR_TIP_IGNORE = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.ignore");

		Vector vector = new Vector();
		DbInterfaceProperties dbInterfaceproperties = page
				.getDbInterfaceProperties();
		MyElement myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DEFAULT);
		myelement.add("Hsqldb");
		myelement.add("PostgreSQL");
		myelement.add("Db2");
		myelement.add("MsSQLServer");
		myelement.add("Oracle");
		myelement.add("MySQL");
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DEFAULT);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_PLATFORM);
		myelement.add("Db2");
		myelement.add("Hsqldb");
		myelement.add("Informix");
		myelement.add("MsAccess");
		myelement.add("MsSQLServer");
		myelement.add("MySQL");
		myelement.add("Oracle");
		myelement.add("PostgreSQL");
		myelement.add("Sybase");
		myelement.add("SybaseASE");
		myelement.add("SybaseASA");
		myelement.add("Sapdb");
		myelement.add("Firebird");
		myelement.add("Axion");
		myelement.add("NonstopSql");
		myelement.add("Oracle9i");
		myelement.add("MaxDB");
		myelement.setValue(dbInterfaceproperties.getPlatform());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_PLATFORM);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_JCDALIAS);
		myelement.setValue(dbInterfaceproperties.getJcdAlias());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_JCDALIAS);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_JDBCLEVEL);
		myelement.setValue(dbInterfaceproperties.getJdbcLevel());
		myelement.add("1.0");
		myelement.add("2.0");
		myelement.add("3.0");
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_JDBCLEVEL);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DRIVER);
		myelement.setValue(dbInterfaceproperties.getDriver());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DRIVER);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_PROTOCOL);
		myelement.setValue(dbInterfaceproperties.getProtocol());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_PROTOCOL);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_SUBPROTOCOL);
		myelement.setValue(dbInterfaceproperties.getSubprotocol());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_SUBPROTOCOL);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DBALIAS);
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DBALIAS);
		myelement.setValue(dbInterfaceproperties.getDbalias());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_USER);
		myelement.setValue(dbInterfaceproperties.getUsername());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_USER);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_PASSWD);
		myelement.setValue(dbInterfaceproperties.getPassword());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_PASSWD);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_CATALOG);
		myelement.setValue(dbInterfaceproperties.getCatalog());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_CATALOG);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_SCHEMA);
		myelement.setValue(dbInterfaceproperties.getSchema());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_SCHEMA);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_BATCH);
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_BATCH);
		myelement.setValue("false");
		if (dbInterfaceproperties.isBatchMode())
			myelement.setValue("true");

		myelement.add("false");
		myelement.add("true");
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_EAGER);
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_EAGER);
		myelement.setValue("false");
		if (dbInterfaceproperties.isEagerRelease())
			myelement.setValue("true");

		myelement.add("false");
		myelement.add("true");
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_AUTO);
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_AUTO);
		myelement.setValue(dbInterfaceproperties.getAutoCommit());
		myelement.add("0");
		myelement.add("1");
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_IGNORE);
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_IGNORE);
		myelement.setValue("false");
		if (dbInterfaceproperties.isAcExceptions())
			myelement.setValue("true");

		myelement.add("false");
		myelement.add("true");
		vector.add(myelement);
		return (vector);
	}

	private Vector getGenElements() {
		final String MULTIPAGE_EDITOR_LABEL_AUTOINCREMENT = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.autoincrement");
		final String MULTIPAGE_EDITOR_TIP_AUTOINCREMENT = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.autoincrement");
		final String MULTIPAGE_EDITOR_LABEL_USERREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.userRepository");
		final String MULTIPAGE_EDITOR_TIP_USERREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.userRepository");
		final String MULTIPAGE_EDITOR_LABEL_JAVAREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.javaRepository");
		final String MULTIPAGE_EDITOR_TIP_JAVAREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.javaRepository");
		final String MULTIPAGE_EDITOR_LABEL_INCLUDEREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.includeRepository");
		final String MULTIPAGE_EDITOR_TIP_INCLUDEREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.includeRepository");
		final String MULTIPAGE_EDITOR_LABEL_PACKAGE = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.package");
		final String MULTIPAGE_EDITOR_TIP_PACKAGE = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.package");
		final String MULTIPAGE_EDITOR_LABEL_IMPORT = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.import");
		final String MULTIPAGE_EDITOR_TIP_IMPORT = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.import");
		final String MULTIPAGE_EDITOR_LABEL_IMPLEMENT = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.implements");
		final String MULTIPAGE_EDITOR_TIP_IMPLEMENT = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.implements");
		final String MULTIPAGE_EDITOR_LABEL_EXTENDS = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.extends");
		final String MULTIPAGE_EDITOR_TIP_EXTENDS = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.extends");
		final String MULTIPAGE_EDITOR_LABEL_COPYFUNCTION = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.copyfuction");
		final String MULTIPAGE_EDITOR_TIP_COPYFUNCTION = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.copyfuction");
		final String MULTIPAGE_EDITOR_LABEL_USESCHEMA = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.useSchema");
		final String MULTIPAGE_EDITOR_TIP_USESCHEMA = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.useSchema");
		Vector vector = new Vector();
		DbInterfaceProperties dbInterfaceproperties = page
				.getDbInterfaceProperties();
		MyElement myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_AUTOINCREMENT);
		myelement.add(dbInterfaceproperties.getAutoincrement());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_AUTOINCREMENT);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_USERREPOSITORY);
		myelement.setValue(dbInterfaceproperties.getUserRepositoryPath());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_USERREPOSITORY);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_JAVAREPOSITORY);
		myelement.setValue(dbInterfaceproperties.getJavaRepositoryPath());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_JAVAREPOSITORY);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_INCLUDEREPOSITORY);
		myelement.setValue(dbInterfaceproperties.getIncludeRepositoryPath());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_INCLUDEREPOSITORY);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_PACKAGE);
		myelement.setValue(dbInterfaceproperties.getSPackage());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_PACKAGE);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_IMPORT);
		myelement.setValue(dbInterfaceproperties.getSImport());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_IMPORT);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_IMPLEMENT);
		myelement.setValue(dbInterfaceproperties.getSImplements());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_IMPLEMENT);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_EXTENDS);
		myelement.setValue(dbInterfaceproperties.getSExtends());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_EXTENDS);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_COPYFUNCTION);
		myelement.setValue("false");
		if (dbInterfaceproperties.isCopyfuction())
			myelement.setValue("true");
		myelement.add("false");
		myelement.add("true");
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_COPYFUNCTION);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(true);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_USESCHEMA);
		myelement.setValue("true");
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_USESCHEMA);
		if (!dbInterfaceproperties.isCopyfuction())
			myelement.setValue("false");
		myelement.add("false");
		myelement.add("true");
		myelement.setToolTip(MULTIPAGE_EDITOR_LABEL_USESCHEMA);
		vector.add(myelement);
		return (vector);
	}

	private Vector getDisplayElements() {
		final String MULTIPAGE_EDITOR_LABEL_DISPLAYREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.displayRepository");
		final String MULTIPAGE_EDITOR_TIP_DISPLAYREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.displayRepository");
		final String MULTIPAGE_EDITOR_LABEL_DISPLAYPACKAGE = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.displaypackage");
		final String MULTIPAGE_EDITOR_TIP_DISPLAYPACKAGE = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.displaypackage");
		final String MULTIPAGE_EDITOR_LABEL_DISPLAYIMPORT = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.displayimport");
		final String MULTIPAGE_EDITOR_TIP_DISPLAYIMPORT = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.displayimport");
		final String MULTIPAGE_EDITOR_LABEL_DISPLAYIMPLEMENT = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.displayimplements");
		final String MULTIPAGE_EDITOR_TIP_DISPLAYIMPLEMENT = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.displayimplements");
		final String MULTIPAGE_EDITOR_LABEL_DISPLAYEXTENDS = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.displayextends");
		final String MULTIPAGE_EDITOR_TIP_DISPLAYEXTENDS = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.displayextends");
		Vector vector = new Vector();
		DbInterfaceProperties dbInterfaceproperties = page
				.getDbInterfaceProperties();
		MyElement myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DISPLAYREPOSITORY);
		myelement.setValue(dbInterfaceproperties.getDisplayRepositoryPath());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DISPLAYREPOSITORY);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DISPLAYPACKAGE);
		myelement.setValue(dbInterfaceproperties.getDPackage());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DISPLAYPACKAGE);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DISPLAYIMPORT);
		myelement.setValue(dbInterfaceproperties.getDImport());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DISPLAYIMPORT);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DISPLAYIMPLEMENT);
		myelement.setValue(dbInterfaceproperties.getDImplements());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DISPLAYIMPLEMENT);
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DISPLAYEXTENDS);
		myelement.setValue(dbInterfaceproperties.getDExtends());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DISPLAYEXTENDS);
		vector.add(myelement);
		return (vector);
	}

	private Vector getModelElements() {
		final String MULTIPAGE_EDITOR_LABEL_DISPLAYREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.label.modelRepository");
		final String MULTIPAGE_EDITOR_TIP_DISPLAYREPOSITORY = DbfLauncherPlugin
				.getResourceString("multipage.editor.tip.modelRepository");
		Vector vector = new Vector();
		DbInterfaceProperties dbInterfaceproperties = page
				.getDbInterfaceProperties();
		MyElement myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel(MULTIPAGE_EDITOR_LABEL_DISPLAYREPOSITORY);
		myelement.setValue(dbInterfaceproperties.getDisplayRepositoryPath());
		myelement.setToolTip(MULTIPAGE_EDITOR_TIP_DISPLAYREPOSITORY);
		vector.add(myelement);
		return (vector);
	}

	private Vector getMapElements() {
		Vector vector = new Vector();
		DbInterfaceProperties dbInterfaceproperties = page
				.getDbInterfaceProperties();
		MyElement myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.ARRAY");
		myelement.setValue(dbInterfaceproperties.getJavaTypesARRAY());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.BIGINT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesBIGINT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.BINARY");
		myelement.setValue(dbInterfaceproperties.getJavaTypesBINARY());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.BIT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesBIT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.BLOB");
		myelement.setValue(dbInterfaceproperties.getJavaTypesBLOB());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.CHAR");
		myelement.setValue(dbInterfaceproperties.getJavaTypesCHAR());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.CLOB");
		myelement.setValue(dbInterfaceproperties.getJavaTypesCLOB());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.DATE");
		myelement.setValue(dbInterfaceproperties.getJavaTypesDATE());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.DECIMAL");
		myelement.setValue(dbInterfaceproperties.getJavaTypesDECIMAL());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.DISTINCT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesDISTINCT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.DOUBLE");
		myelement.setValue(dbInterfaceproperties.getJavaTypesDOUBLE());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.FLOAT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesFLOAT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.INTEGER");
		myelement.setValue(dbInterfaceproperties.getJavaTypesINTEGER());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.JAVA_OBJECT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesJAVA_OBJECT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.LONGVARBINARY");
		myelement.setValue(dbInterfaceproperties.getJavaTypesLONGVARBINARY());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.LONGVARCHAR");
		myelement.setValue(dbInterfaceproperties.getJavaTypesLONGVARCHAR());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.NULL");
		myelement.setValue(dbInterfaceproperties.getJavaTypesNULL());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.NUMERIC");
		myelement.setValue(dbInterfaceproperties.getJavaTypesNUMERIC());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.OTHER");
		myelement.setValue(dbInterfaceproperties.getJavaTypesOTHER());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.REAL");
		myelement.setValue(dbInterfaceproperties.getJavaTypesREAL());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.REF");
		myelement.setValue(dbInterfaceproperties.getJavaTypesREF());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.SMALLINT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesSMALLINT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.STRUCT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesSTRUCT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.TIME");
		myelement.setValue(dbInterfaceproperties.getJavaTypesTIME());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.TIMESTAMP");
		myelement.setValue(dbInterfaceproperties.getJavaTypesTIMESTAMP());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.TINYINT");
		myelement.setValue(dbInterfaceproperties.getJavaTypesTINYINT());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.VARBINARY");
		myelement.setValue(dbInterfaceproperties.getJavaTypesVARBINARY());
		vector.add(myelement);
		myelement = new MyElement();
		myelement.setIsCombo(false);
		myelement.setLabel("JavaTypes.VARCHAR");
		myelement.setValue(dbInterfaceproperties.getJavaTypesVARCHAR());
		vector.add(myelement);
		return (vector);
	}

	public Control createDBSettings(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_DBSETTING = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.dbsetting");
		ScrolledComposite scrolledcomposite = new ScrolledComposite(parent,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Composite composite = new Composite(scrolledcomposite, SWT.NONE);
		scrolledcomposite.setContent(composite);
		FormLayout formlayout = new FormLayout();
		composite.setLayout(formlayout);
		Vector vector = getDBElements();
		short k = 200;
		short l = 400;
		dbValues.removeAllElements();
		MyKeyListener mykeylistener = new MyKeyListener();
		MyDefaultSelectionListener mydefaultselectionlistener = new MyDefaultSelectionListener();
		Label labelDb = null;
		Button button = null;
		FormData testCon;
		for (int m = 0; m < vector.size(); m++) {
			MyElement myelement = (MyElement) vector.get(m);
			FormData formdata = new FormData();
			FormData dbSetting = new FormData();
			Label label = new Label(composite, SWT.CENTER | SWT.WRAP);
			label.setText(myelement.getLabel());
			Combo combo = null;
			Text text = null;
			if (myelement.isCombo()) {
				combo = new Combo(composite, SWT.READ_ONLY);
				combo.addSelectionListener(mydefaultselectionlistener);
				for (int n = 0; n < myelement.getValues().size(); n++) {
					combo.add((String) myelement.getValues().get(n));
					if (myelement.getValue().equals(
							(String) myelement.getValues().get(n)))
						combo.select(n);

					if (m == 0)
						combo.addSelectionListener(new MyDBSelectionListener());
				}
				dbValues.add(combo);
			} else if (!myelement.isCheckbox) {
				text = new Text(composite, SWT.BORDER);
				text.setText(myelement.getValue());
				text.setToolTipText(myelement.getToolTip());
				text.addKeyListener(mykeylistener);
				dbValues.add(text);
			}
			if (labelDb == null) {
				formdata.top = new FormAttachment(5, 5);
				dbSetting.top = new FormAttachment(5, 5);
			} else {
				formdata.top = new FormAttachment(labelDb, 5);
				dbSetting.top = new FormAttachment(labelDb, 5);
			}
			formdata.left = new FormAttachment(0, k);
			formdata.right = new FormAttachment(0, l);
			dbSetting.left = new FormAttachment(0, 3);
			if (combo != null)
				combo.setLayoutData(formdata);
			else if (text != null)
				text.setLayoutData(formdata);
			label.setLayoutData(dbSetting);
			labelDb = label;
		}
		button = new Button(composite, SWT.BORDER);
		testCon = new FormData();
		button.setText("Test Connection");
		if (labelDb != null)
			testCon.top = new FormAttachment(labelDb, 10);
		else
			testCon.top = new FormAttachment(0, 10);
		testCon.left = new FormAttachment(0, k);
		button.setLayoutData(testCon);
		button.addKeyListener(new MyBtnKeyListener());
		button.addMouseListener(new MyBtnMousListener());
		composite.setSize(composite.computeSize(-1, -1));
		scrolledcomposite.pack();
		return scrolledcomposite;
	}

	public Control createDbInterfaceSettings(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_DBINTERFACE = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.dbinterface");
		Composite composite = new Composite(parent, SWT.BORDER);
		FormLayout formlayout = new FormLayout();
		formlayout.marginWidth = 3;
		formlayout.marginHeight = 3;
		composite.setLayout(formlayout);
		Vector vector = getGenElements();
		short k = 150;
		short l = 400;
		Label labelInterface = null;
		genValues.removeAllElements();
		MyKeyListener mykeylistener = new MyKeyListener();
		MyDefaultSelectionListener mydefaultselectionlistener = new MyDefaultSelectionListener();
		for (int m = 0; m < vector.size(); m++) {
			MyElement myelement = (MyElement) vector.get(m);
			FormData formdata = new FormData();
			FormData interfaceSetting = new FormData();
			Label label = new Label(composite, SWT.CENTER | SWT.WRAP);
			label.setText(myelement.getLabel());
			Text text = null;
			Combo combo = null;
			if (myelement.isCombo()) {
				combo = new Combo(composite, SWT.READ_ONLY);
				((Combo) combo)
						.addSelectionListener(mydefaultselectionlistener);
				for (int n = 0; n < myelement.getValues().size(); n++) {
					((Combo) combo).add((String) myelement.getValues().get(n));
					if (myelement.getValue().equals(
							(String) myelement.getValues().get(n)))
						((Combo) combo).select(n);
				}
				genValues.add(combo);
			} else if (!myelement.isCheckbox) {
				text = new Text(composite, SWT.BORDER);
				if (myelement.getValue() != null)
					text.setText(myelement.getValue());
				if (myelement.getToolTip() != null)
					text.setToolTipText(myelement.getToolTip());
				text.addKeyListener(mykeylistener);
				genValues.add(text);
			}
			if (labelInterface == null) {
				formdata.top = new FormAttachment(5, 5);
				interfaceSetting.top = new FormAttachment(5, 5);
			} else {
				formdata.top = new FormAttachment(labelInterface, 5);
				interfaceSetting.top = new FormAttachment(labelInterface, 5);
			}
			formdata.left = new FormAttachment(0, k);
			formdata.right = new FormAttachment(0, l);
			interfaceSetting.left = new FormAttachment(0, 3);
			if (combo != null)
				combo.setLayoutData(formdata);
			else if (text != null)
				text.setLayoutData(formdata);
			label.setLayoutData(interfaceSetting);
			labelInterface = label;
		}
		return composite;
	}

	public Control createDisplaySettings(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_DISPLAY = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.display");
		Composite composite = new Composite(parent, SWT.BORDER);
		FormLayout formlayout = new FormLayout();
		formlayout.marginWidth = 3;
		formlayout.marginHeight = 3;
		composite.setLayout(formlayout);
		Vector vector = getDisplayElements();
		short k = 150;
		short l = 400;
		Label labelInterface = null;
		displayValues.removeAllElements();
		MyKeyListener mykeylistener = new MyKeyListener();
		MyDefaultSelectionListener mydefaultselectionlistener = new MyDefaultSelectionListener();
		for (int m = 0; m < vector.size(); m++) {
			MyElement myelement = (MyElement) vector.get(m);
			FormData formdata = new FormData();
			FormData interfaceSetting = new FormData();
			Label label = new Label(composite, SWT.CENTER | SWT.WRAP);
			label.setText(myelement.getLabel());
			Text text = null;
			Combo combo = null;
			if (myelement.isCombo()) {
				combo = new Combo(composite, SWT.READ_ONLY);
				((Combo) combo)
						.addSelectionListener(mydefaultselectionlistener);
				for (int n = 0; n < myelement.getValues().size(); n++) {
					((Combo) combo).add((String) myelement.getValues().get(n));
					if (myelement.getValue().equals(
							(String) myelement.getValues().get(n)))
						((Combo) combo).select(n);
				}
				displayValues.add(combo);
			} else if (!myelement.isCheckbox) {
				text = new Text(composite, SWT.BORDER);
				if (myelement.getValue() != null)
					text.setText(myelement.getValue());
				if (myelement.getToolTip() != null)
					text.setToolTipText(myelement.getToolTip());
				text.addKeyListener(mykeylistener);
				displayValues.add(text);
			}
			if (labelInterface == null) {
				formdata.top = new FormAttachment(5, 5);
				interfaceSetting.top = new FormAttachment(5, 5);
			} else {
				formdata.top = new FormAttachment(labelInterface, 5);
				interfaceSetting.top = new FormAttachment(labelInterface, 5);
			}
			formdata.left = new FormAttachment(0, k);
			formdata.right = new FormAttachment(0, l);
			interfaceSetting.left = new FormAttachment(0, 3);
			if (combo != null)
				combo.setLayoutData(formdata);
			else if (text != null)
				text.setLayoutData(formdata);
			label.setLayoutData(interfaceSetting);
			labelInterface = label;
		}
		return composite;
	}

	public Control createModelSettings(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_MODEL = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.model");
		Composite composite = new Composite(parent, SWT.BORDER);
		FormLayout formlayout = new FormLayout();
		formlayout.marginWidth = 3;
		formlayout.marginHeight = 3;
		composite.setLayout(formlayout);
		Vector vector = getModelElements();
		short k = 150;
		short l = 400;
		Label labelInterface = null;
		modelValues.removeAllElements();
		MyKeyListener mykeylistener = new MyKeyListener();
		MyDefaultSelectionListener mydefaultselectionlistener = new MyDefaultSelectionListener();
		for (int m = 0; m < vector.size(); m++) {
			MyElement myelement = (MyElement) vector.get(m);
			FormData formdata = new FormData();
			FormData interfaceSetting = new FormData();
			Label label = new Label(composite, SWT.CENTER | SWT.WRAP);
			label.setText(myelement.getLabel());
			Text text = null;
			Combo combo = null;
			if (myelement.isCombo()) {
				combo = new Combo(composite, SWT.READ_ONLY);
				((Combo) combo)
						.addSelectionListener(mydefaultselectionlistener);
				for (int n = 0; n < myelement.getValues().size(); n++) {
					((Combo) combo).add((String) myelement.getValues().get(n));
					if (myelement.getValue().equals(
							(String) myelement.getValues().get(n)))
						((Combo) combo).select(n);
				}
				modelValues.add(combo);
			} else if (!myelement.isCheckbox) {
				text = new Text(composite, SWT.BORDER);
				if (myelement.getValue() != null)
					text.setText(myelement.getValue());
				if (myelement.getToolTip() != null)
					text.setToolTipText(myelement.getToolTip());
				text.addKeyListener(mykeylistener);
				modelValues.add(text);
			}
			if (labelInterface == null) {
				formdata.top = new FormAttachment(5, 5);
				interfaceSetting.top = new FormAttachment(5, 5);
			} else {
				formdata.top = new FormAttachment(labelInterface, 5);
				interfaceSetting.top = new FormAttachment(labelInterface, 5);
			}
			formdata.left = new FormAttachment(0, k);
			formdata.right = new FormAttachment(0, l);
			interfaceSetting.left = new FormAttachment(0, 3);
			if (combo != null)
				combo.setLayoutData(formdata);
			else if (text != null)
				text.setLayoutData(formdata);
			label.setLayoutData(interfaceSetting);
			labelInterface = label;
		}
		return composite;
	}

	public Control createMappingSettings(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_MAPPING = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.mapping");
		ScrolledComposite scrolledcomposite = new ScrolledComposite(parent,
				SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		Composite composite = new Composite(scrolledcomposite, SWT.NONE);
		scrolledcomposite.setContent(composite);
		FormLayout formlayout = new FormLayout();
		formlayout.marginWidth = 3;
		formlayout.marginHeight = 3;
		composite.setLayout(formlayout);
		Vector vector = getMapElements();
		short k = 200;
		short l = 350;
		mapValues.removeAllElements();
		MyKeyListener mykeylistener = new MyKeyListener();
		MyDefaultSelectionListener mydefaultselectionlistener = new MyDefaultSelectionListener();
		FormData mappingSetting;
		Label label;
		int n;
		Text text = null;
		MyElement myelement;
		Label labelMapping = null;
		FormData formdata;
		for (int m = 0; m < vector.size(); m++) {
			myelement = (MyElement) vector.get(m);
			formdata = new FormData();
			mappingSetting = new FormData();
			label = new Label(composite, SWT.CENTER | SWT.WRAP);
			label.setText(myelement.getLabel());
			if (myelement.isCombo()) {
				Combo combo = new Combo(composite, SWT.READ_ONLY);
				((Combo) combo)
						.addSelectionListener(mydefaultselectionlistener);
				n = 0;
				for (; n < myelement.getValues().size();) {
					((Combo) combo).add((String) myelement.getValues().get(n));
					n++;
				}
				mapValues.add(combo);
			} else {
				text = new Text(composite, SWT.NONE);
				text.setText(myelement.getValue());
				text.setToolTipText(myelement.getToolTip());
				text.setLayoutData(formdata);
				text.addKeyListener(mykeylistener);
				mapValues.add(text);
			}
			if (labelMapping == null) {
				formdata.top = new FormAttachment(0, 3);
				mappingSetting.top = new FormAttachment(0, 3);
			} else {
				formdata.top = new FormAttachment(labelMapping, 3);
				mappingSetting.top = new FormAttachment(labelMapping, 3);
			}
			formdata.left = new FormAttachment(0, k);
			formdata.right = new FormAttachment(0, l);
			mappingSetting.left = new FormAttachment(0, 3);
			label.setLayoutData(mappingSetting);
			labelMapping = label;
		}
		composite.setSize(composite.computeSize(-1, -1));
		scrolledcomposite.pack();
		return scrolledcomposite;
	}

	public Control createDriverSettings(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_DRIVER = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.driver");
		Composite composite = new Composite(parent, SWT.BORDER);
		FormLayout formlayout = new FormLayout();
		DbInterfaceProperties dbInterfaceproperties = page
				.getDbInterfaceProperties();
		formlayout.marginWidth = 3;
		formlayout.marginHeight = 3;
		composite.setLayout(formlayout);
		byte k = 100;
		listClasspath = new List(composite, SWT.BORDER);
		FormData formdata = new FormData();
		formdata.top = new FormAttachment(0, 3);
		formdata.bottom = new FormAttachment(0, 200);
		formdata.left = new FormAttachment(0, 3);
		formdata.right = new FormAttachment(0, 400);
		listClasspath.setLayoutData(formdata);
		Button buttonDriver;
		FormData rmDriver;
		FormData addDriver;
		for (int m = 0; m < dbInterfaceproperties.getJdbcDrivers().size(); m++) {
			listClasspath.add((String) dbInterfaceproperties.getJdbcDrivers()
					.get(m));
		}
		addJarZipButton = new Button(composite, SWT.NONE);
		addDriver = new FormData();
		addJarZipButton.setText("Add Driver");
		addDriver.top = new FormAttachment(0, 200);
		addDriver.left = new FormAttachment(0, k);
		addJarZipButton.setLayoutData(addDriver);
		addJarZipButton.addKeyListener(new MyDrvAddKeyListener());
		addJarZipButton.addMouseListener(new MyDrvAddMousListener());
		buttonDriver = new Button(composite, SWT.NONE);
		rmDriver = new FormData();
		buttonDriver.setText("Remove Driver");
		rmDriver.top = new FormAttachment(0, 200);
		rmDriver.left = new FormAttachment(0, k + 100);
		buttonDriver.setLayoutData(rmDriver);
		buttonDriver.addKeyListener(new MyDrvRemoveKeyListener());
		buttonDriver.addMouseListener(new MyDrvRemoveMousListener());
		// int n = addPage(composite);
		// setPageText(n, MULTIPAGE_EDITOR_PAGE_DRIVER);
		return composite;
	}

	public Control createPreview(Composite parent) {
		final String MULTIPAGE_EDITOR_PAGE_PREVIEW = DbfLauncherPlugin
				.getResourceString("multipage.editor.page.preview");
		Composite composite = new Composite(parent, SWT.NONE);
		FillLayout filllayout = new FillLayout();
		composite.setLayout(filllayout);
		previewText = new StyledText(composite, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);
		previewText.setEditable(false);
		return composite;
	}

	public void doSave(IProgressMonitor iprogressmonitor) {
		String propertieName = page.getInterfacePropertiesFile();
		saveFile(propertieName);
		bDirty = false;
		handlePropertyChange(1);
		// TODO implement
		// iprogressmonitor.done();
		return;
	}

	/**
	 * @param i
	 */
	private void handlePropertyChange(int i) {
		// TODO Auto-generated method stub

	}

	public void saveFile(String fileName) {
		final String MULTIPAGE_EDITOR_SAVE_ERROR = DbfLauncherPlugin
				.getResourceString("multipage.editor.save.error");
		File file = new File(fileName);
		try {
			PrintWriter printwriter;
			if (!file.exists())
				file.createNewFile();

			if (file.canWrite()) {
				printwriter = new PrintWriter(new FileOutputStream(file));
				printwriter.println(getEntries().toString());
				printwriter.close();
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			page
					.showError(MULTIPAGE_EDITOR_SAVE_ERROR
							+ exception.getMessage());
		}
		return;
	}

	public void doSaveAs() {
		// FIXME activate
		/*
		 * FileDialog filedialog = new FileDialog(getControl(0).getShell(),
		 * 8192); filedialog.open(); String fileName =
		 * filedialog.getFilterPath() + System.getProperty("file.separator") +
		 * filedialog.getFileName(); saveFile(fileName);
		 */
		bDirty = false;
		handlePropertyChange(1);
		return;
	}

	public boolean isSaveAsAllowed() {
		return (true);
	}

	private void testDBSettings(Properties properties) {
		final String MULTIPAGE_EDITOR_PROPERTY_ERROR = DbfLauncherPlugin
				.getResourceString("multipage.editor.property.error");
		final String MULTIPAGE_EDITOR_DRIVER_ERROR = DbfLauncherPlugin
				.getResourceString("multipage.editor.driver.error");
		final String MULTIPAGE_EDITOR_META_ERROR = DbfLauncherPlugin
				.getResourceString("multipage.editor.meta.error");
		final String MULTIPAGE_EDITOR_TABLE_ERROR = DbfLauncherPlugin
				.getResourceString("multipage.editor.table.error");
		final String MULTIPAGE_EDITOR_TABLEINFO_ERROR = DbfLauncherPlugin
				.getResourceString("multipage.editor.tableinfo.error");
		final String MULTIPAGE_EDITOR_TEST_OK = DbfLauncherPlugin
				.getResourceString("multipage.editor.test.ok");
		final String MULTIPAGE_EDITOR_OPENDB_ERROR = DbfLauncherPlugin
				.getResourceString("viewer.error.property.url");
		java.sql.DatabaseMetaData dbMeta = null;
		ResultSet resultset;
		java.sql.Connection connection;
		String strCatalog = null;
		String strUsername;
		String strPassword;
		String strJDBCDriver;
		String strJDBCURL;
		String strSchema = null;
		if (properties == null || properties.isEmpty()) {
			page.showError(MULTIPAGE_EDITOR_PROPERTY_ERROR);
			return;
		}
		strJDBCDriver = properties.getProperty("driver", "");
		if (strJDBCDriver == null || strJDBCDriver.length() < 1) {
			page.showError(MULTIPAGE_EDITOR_DRIVER_ERROR);
			return;
		}
		strUsername = properties.getProperty("username", "");
		strPassword = properties.getProperty("password", "");
		strJDBCURL = "";
		strJDBCURL += properties.getProperty("protocol", "") + ":";
		strJDBCURL += properties.getProperty("subprotocol", "") + ":";
		strJDBCURL += properties.getProperty("dbalias", "");
		try {
			Class.forName(strJDBCDriver); // "com.informix.jdbc.IfxDriver"
			connection = java.sql.DriverManager.getConnection(strJDBCURL,
					strUsername, strPassword); // "jdbc:informix-sqli://moon:1526/bci_test:INFORMIXSERVER=ol_bci",
			// "informix", "informix"
			if (connection == null) {
				page.showError(MULTIPAGE_EDITOR_OPENDB_ERROR + "\ndriver: "
						+ strJDBCDriver + "\nurl: " + strJDBCURL + "\nuser: "
						+ strUsername + "\npassword: " + strPassword);
				return;
			}
		} catch (Exception exception) {
			page.showError(MULTIPAGE_EDITOR_OPENDB_ERROR + "\nurl: "
					+ strJDBCURL + "\nuser: " + strUsername + "\npassword: "
					+ strPassword + "\nerror: " + exception.getMessage());
			return;
		}
		try {
			dbMeta = connection.getMetaData();
		} catch (Exception exception) {
			try {
				connection.close();
			} catch (Exception closeEx) {
				connection = null;
				page.showError(MULTIPAGE_EDITOR_META_ERROR
						+ exception.getMessage());
				exception = null;
				closeEx = null;
				if (properties.getProperty("catalog").equals("null"))
					exception = null;
				else
					strCatalog = properties.getProperty("catalog");
				if (properties.getProperty("schema").equals("null"))
					closeEx = null;
				else
					strSchema = properties.getProperty("schema");
				return;
			}
		}

		try {
			resultset = dbMeta.getTables(strCatalog, strSchema, "%", null);
			if (!resultset.next()) {
				connection.close();
				connection = null;
				page.showError(MULTIPAGE_EDITOR_TABLE_ERROR + strCatalog + ","
						+ strSchema + ",%,null)");
				return;
			}
		} catch (SQLException sqlexception) {
			page.showError(MULTIPAGE_EDITOR_TABLEINFO_ERROR
					+ sqlexception.getMessage());
			try {
				connection.close();
			} catch (SQLException closeEx) {
				connection = null;
				return;
			}
		}
		page.showMessage(MULTIPAGE_EDITOR_TEST_OK + strJDBCDriver);
		return;
	}

	private DbInterfaceProperties getEntries() {
		DbInterfaceProperties dbInterfaceproperties = new DbInterfaceProperties();
		dbInterfaceproperties
				.setPlatform(((Combo) (dbValues.get(1))).getText());
		dbInterfaceproperties.setJcdAlias(((Text) (dbValues.get(2))).getText());
		dbInterfaceproperties.setJdbcLevel(((Combo) (dbValues.get(3)))
				.getText());
		dbInterfaceproperties.setDriver(((Text) (dbValues.get(4))).getText());
		dbInterfaceproperties.setProtocol(((Text) (dbValues.get(5))).getText());
		dbInterfaceproperties.setSubprotocol(((Text) (dbValues.get(6)))
				.getText());
		dbInterfaceproperties.setDbalias(((Text) (dbValues.get(7))).getText());
		dbInterfaceproperties.setUsername(((Text) (dbValues.get(8))).getText());
		dbInterfaceproperties.setPassword(((Text) (dbValues.get(9))).getText());
		dbInterfaceproperties.setCatalog(((Text) (dbValues.get(10))).getText());
		dbInterfaceproperties.setSchema(((Text) (dbValues.get(11))).getText());
		dbInterfaceproperties.setBatchMode(((Combo) (dbValues.get(12)))
				.getText().equals("true"));
		dbInterfaceproperties.setEagerRelease(((Combo) (dbValues.get(13)))
				.getText().equals("true"));
		dbInterfaceproperties.setAutoCommit(((Text) (dbValues.get(14)))
				.getText());
		dbInterfaceproperties.setAcExceptions(((Combo) (dbValues.get(15)))
				.getText().equals("true"));
		dbInterfaceproperties.setAutoincrement(((Text) (genValues.get(0)))
				.getText());
		dbInterfaceproperties.setUserRepositoryPath(((Text) (genValues.get(1)))
				.getText());
		dbInterfaceproperties.setJavaRepositoryPath(((Text) (genValues.get(2)))
				.getText());
		dbInterfaceproperties.setIncludeRepositoryPath(((Text) (genValues
				.get(3))).getText());
		dbInterfaceproperties
				.setSPackage(((Text) (genValues.get(4))).getText());
		dbInterfaceproperties.setSImport(((Text) (genValues.get(5))).getText());
		dbInterfaceproperties.setSImplements(((Text) (genValues.get(6)))
				.getText());
		dbInterfaceproperties
				.setSExtends(((Text) (genValues.get(7))).getText());
		dbInterfaceproperties.setCopyfuction(((Combo) (genValues.get(8)))
				.getText().equals("true"));
		dbInterfaceproperties
				.setUseSchemaForSelect(((Combo) (genValues.get(9))).getText()
						.equals("true"));
		dbInterfaceproperties.setDisplayRepositoryPath(((Text) (displayValues
				.get(0))).getText());
		dbInterfaceproperties.setDPackage(((Text) (displayValues.get(1)))
				.getText());
		dbInterfaceproperties.setDImport(((Text) (displayValues.get(2)))
				.getText());
		dbInterfaceproperties.setDImplements(((Text) (displayValues.get(3)))
				.getText());
		dbInterfaceproperties.setDExtends(((Text) (displayValues.get(4)))
				.getText());
		dbInterfaceproperties.setXmlModelRepositoryPath(((Text) (modelValues
				.get(0))).getText());
		dbInterfaceproperties.setJavaTypesARRAY(((Text) (mapValues.get(0)))
				.getText());
		dbInterfaceproperties.setJavaTypesBIGINT(((Text) (mapValues.get(1)))
				.getText());
		dbInterfaceproperties.setJavaTypesBINARY(((Text) (mapValues.get(2)))
				.getText());
		dbInterfaceproperties.setJavaTypesBIT(((Text) (mapValues.get(3)))
				.getText());
		dbInterfaceproperties.setJavaTypesBLOB(((Text) (mapValues.get(4)))
				.getText());
		dbInterfaceproperties.setJavaTypesCHAR(((Text) (mapValues.get(5)))
				.getText());
		dbInterfaceproperties.setJavaTypesCLOB(((Text) (mapValues.get(6)))
				.getText());
		dbInterfaceproperties.setJavaTypesDATE(((Text) (mapValues.get(7)))
				.getText());
		dbInterfaceproperties.setJavaTypesDECIMAL(((Text) (mapValues.get(8)))
				.getText());
		dbInterfaceproperties.setJavaTypesDISTINCT(((Text) (mapValues.get(9)))
				.getText());
		dbInterfaceproperties.setJavaTypesDOUBLE(((Text) (mapValues.get(10)))
				.getText());
		dbInterfaceproperties.setJavaTypesFLOAT(((Text) (mapValues.get(11)))
				.getText());
		dbInterfaceproperties.setJavaTypesINTEGER(((Text) (mapValues.get(12)))
				.getText());
		dbInterfaceproperties.setJavaTypesJAVA_OBJECT(((Text) (mapValues
				.get(13))).getText());
		dbInterfaceproperties.setJavaTypesLONGVARBINARY(((Text) (mapValues
				.get(14))).getText());
		dbInterfaceproperties.setJavaTypesLONGVARCHAR(((Text) (mapValues
				.get(15))).getText());
		dbInterfaceproperties.setJavaTypesNULL(((Text) (mapValues.get(16)))
				.getText());
		dbInterfaceproperties.setJavaTypesNUMERIC(((Text) (mapValues.get(17)))
				.getText());
		dbInterfaceproperties.setJavaTypesOTHER(((Text) (mapValues.get(18)))
				.getText());
		dbInterfaceproperties.setJavaTypesREAL(((Text) (mapValues.get(19)))
				.getText());
		dbInterfaceproperties.setJavaTypesREF(((Text) (mapValues.get(20)))
				.getText());
		dbInterfaceproperties.setJavaTypesSMALLINT(((Text) (mapValues.get(21)))
				.getText());
		dbInterfaceproperties.setJavaTypesSTRUCT(((Text) (mapValues.get(22)))
				.getText());
		dbInterfaceproperties.setJavaTypesTIME(((Text) (mapValues.get(23)))
				.getText());
		dbInterfaceproperties
				.setJavaTypesTIMESTAMP(((Text) (mapValues.get(24))).getText());
		dbInterfaceproperties.setJavaTypesTINYINT(((Text) (mapValues.get(25)))
				.getText());
		dbInterfaceproperties
				.setJavaTypesVARBINARY(((Text) (mapValues.get(26))).getText());
		dbInterfaceproperties.setJavaTypesVARCHAR(((Text) (mapValues.get(27)))
				.getText());
		int i = 0;
		for (; i < listClasspath.getItemCount();) {
			dbInterfaceproperties.addJdbcDriver(listClasspath.getItem(i));
			i++;
		}
		return (dbInterfaceproperties);
	}

	public void printProperties() {
		previewText.setText(getEntries().toString());
		return;
	}

	private void addJar() {
		FileDialog dialog = new FileDialog(addJarZipButton.getShell(),
				SWT.MULTI);
		// TODO manage extension filter
		// dialog.setFilterExtensions(new String[1]);
		if (lastPath != null) {
			if (new File(lastPath).exists())
				dialog.setFilterPath(lastPath);
		}
		String fileName = dialog.open();
		if (fileName != null) {
			lastPath = dialog.getFilterPath();
			File file = new File(fileName);
			try {
				listClasspath.add(file.toURL().toString());
			} catch (MalformedURLException malformedurlexception) {
			}
		}
		return;
	}

	private boolean removeJar() {
		boolean i = false;
		if (listClasspath.getItemCount() >= 0
				&& listClasspath.getSelectionIndex() >= 0) {
			listClasspath.remove(listClasspath.getSelectionIndex());
			i = true;
		}
		return (i);
	}

	class ClassLoader extends URLClassLoader {

		public ClassLoader(URL[] urlArr, ClassLoader classloader) {
			super(urlArr, classloader);
			return;
		}

		public void addURL(URL url) {
			URL[] urlArr = getURLs();
			int i = 0;
			for (; i < urlArr.length;) {
				if (urlArr[i].equals(url))
					return;

				i++;
			}
			addURL(url);
			return;
		}

	}

	class MyBtnKeyListener implements KeyListener {

		MyBtnKeyListener() {
			return;
		}

		public void keyReleased(KeyEvent keyevent) {
			return;
		}

		public void keyPressed(KeyEvent keyevent) {
			testDBSettings(DbInterfaceProperties.getPropertie(getEntries()));
			return;
		}

	}

	class MyBtnMousListener implements MouseListener {

		MyBtnMousListener() {
			return;
		}

		public void mouseUp(MouseEvent mouseevent) {
			testDBSettings(DbInterfaceProperties.getPropertie(getEntries()));
			return;
		}

		public void mouseDown(MouseEvent mouseevent) {
			return;
		}

		public void mouseDoubleClick(MouseEvent mouseevent) {
			return;
		}

	}

	class MyDBSelectionListener implements SelectionListener {

		MyDBSelectionListener() {
			return;
		}

		public void widgetDefaultSelected(SelectionEvent selectionevent) {
			return;
		}

		public void widgetSelected(SelectionEvent selectionevent) {
			Combo combo = ((Combo) (dbValues.get(0)));

			DbInterfaceProperties dbInterfaceproperties = (DbInterfaceProperties) DbInterfaceProperties
					.getDefaults().get(combo.getText());
			((Combo) (dbValues.get(1))).setText(combo.getText());
			((Text) (dbValues.get(2))).setText(dbInterfaceproperties
					.getJcdAlias());
			((Combo) (dbValues.get(3))).select(1);
			if (dbInterfaceproperties.getJdbcLevel().equals("1.0"))
				((Combo) (dbValues.get(3))).select(0);
			if (dbInterfaceproperties.getJdbcLevel().equals("3.0"))
				((Combo) (dbValues.get(3))).select(2);

			((Text) (dbValues.get(4))).setText(dbInterfaceproperties
					.getDriver());
			((Text) (dbValues.get(5))).setText(dbInterfaceproperties
					.getProtocol());
			((Text) (dbValues.get(6))).setText(dbInterfaceproperties
					.getSubprotocol());
			((Text) (dbValues.get(7))).setText(dbInterfaceproperties
					.getDbalias());
			((Text) (dbValues.get(8))).setText(dbInterfaceproperties
					.getUsername());
			((Text) (dbValues.get(9))).setText(dbInterfaceproperties
					.getPassword());
			((Text) (dbValues.get(10))).setText(dbInterfaceproperties
					.getCatalog());
			((Text) (dbValues.get(11))).setText(dbInterfaceproperties
					.getSchema());
			return;
		}

	}

	class MyDefaultSelectionListener implements SelectionListener {

		MyDefaultSelectionListener() {
			return;
		}

		public void widgetDefaultSelected(SelectionEvent selectionevent) {
			return;
		}

		public void widgetSelected(SelectionEvent selectionevent) {
			bDirty = true;
			handlePropertyChange(1);
			return;
		}

	}

	class MyDrvAddKeyListener implements KeyListener {

		MyDrvAddKeyListener() {
			return;
		}

		public void keyReleased(KeyEvent keyevent) {
			return;
		}

		public void keyPressed(KeyEvent keyevent) {
			addJar();
			bDirty = true;
			handlePropertyChange(1);
			return;
		}

	}

	class MyDrvAddMousListener implements MouseListener {

		MyDrvAddMousListener() {
			return;
		}

		public void mouseUp(MouseEvent mouseevent) {
			addJar();
			bDirty = true;
			handlePropertyChange(1);
			return;
		}

		public void mouseDown(MouseEvent mouseevent) {
			return;
		}

		public void mouseDoubleClick(MouseEvent mouseevent) {
			return;
		}

	}

	class MyDrvRemoveKeyListener implements KeyListener {

		MyDrvRemoveKeyListener() {
			return;
		}

		public void keyReleased(KeyEvent keyevent) {
			return;
		}

		public void keyPressed(KeyEvent keyevent) {
			if (removeJar()) {
				bDirty = true;
				handlePropertyChange(1);
			}
			return;
		}

	}

	class MyDrvRemoveMousListener implements MouseListener {

		MyDrvRemoveMousListener() {
			return;
		}

		public void mouseUp(MouseEvent mouseevent) {
			if (removeJar()) {
				bDirty = true;
				handlePropertyChange(1);
			}
			return;
		}

		public void mouseDown(MouseEvent mouseevent) {
			return;
		}

		public void mouseDoubleClick(MouseEvent mouseevent) {
			return;
		}

	}

	class MyElement {

		private String Value;

		private Vector Values;

		private String Label;

		private String ToolTip;

		private boolean isCombo;

		private boolean isCheckbox;

		MyElement() {
			Value = "";
			Values = new Vector();
			Label = "";
			ToolTip = "";
			isCombo = false;
			isCheckbox = false;
			return;
		}

		public boolean isCombo() {
			return (isCombo);
		}

		public String getLabel() {
			return (Label);
		}

		public String getValue() {
			return (Value);
		}

		public Vector getValues() {
			return (Values);
		}

		public void setIsCombo(boolean i) {
			isCombo = i;
			return;
		}

		public void setLabel(String string) {
			Label = string;
			return;
		}

		public void setValue(String string) {
			Value = string;
			return;
		}

		public void setValues(Vector vector) {
			Values = vector;
			return;
		}

		public void add(Object object) {
			Values.add(object);
			return;
		}

		public String getToolTip() {
			return (ToolTip);
		}

		public void setToolTip(String string) {
			ToolTip = string;
			return;
		}

		public boolean isCheckbox() {
			return (isCheckbox);
		}

		public void setIsCheckbox(boolean i) {
			isCheckbox = i;
			return;
		}

	}

	class MyKeyListener implements KeyListener {

		MyKeyListener() {
			return;
		}

		public void keyReleased(KeyEvent keyevent) {
			return;
		}

		public void keyPressed(KeyEvent keyevent) {
			bDirty = true;
			handlePropertyChange(1);
			return;
		}

	}

}
