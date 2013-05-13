/*
 * ====================================================================
 *  * Copyright 2001-2013 Andre Charles Legendre <andre.legendre@kalimasystems.org>
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

package org.compiere.mfg_scm.eclipse.db.dbInterface;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Properties;

import org.compiere.mfg_scm.eclipse.db.DbfLauncherPlugin;
import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class GenerateJavaClasses {

	public GenerateJavaClasses() {
		return;
	}

	public static void actionGenerateJavaFiles(DBMeta dbmeta,
			DbInterfaceProperties dbInterfaceproperties) {
		Properties properties = DbInterfaceProperties
				.getPropertie(dbInterfaceproperties);
		String packageDirName = dbInterfaceproperties.getJavaRepositoryPath();
		File packageDir = new File(packageDirName);
		if (!packageDir.isDirectory())
			return;

		// Generate Header
		String includeDirName = properties.getProperty("includeRepositoryPath",
				"");
		File includeDir = new File(includeDirName);

		if (!includeDir.exists())
			includeDir.mkdirs();
		String strHeader = "";
		File f = new File(includeDir, "header.txt");
		if (f.exists()) {
			// strHeader += " //
			// ---------------------------------------------------------------------------";
			// strHeader += System.getProperty("line.separator");
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String inputLine;
			try {
				while ((inputLine = in.readLine()) != null) {
					strHeader += inputLine;
					strHeader += System.getProperty("line.separator");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		String strFooter = "";
		f = new File(includeDir, "footer.txt");
		if (f.exists()) {
			strFooter += "	// ---------------------------------------------------------------------------";
			strFooter += System.getProperty("line.separator");
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(
						new FileInputStream(f)));
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String inputLine;
			try {
				while ((inputLine = in.readLine()) != null) {
					strFooter += inputLine;
					strFooter += System.getProperty("line.separator");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if (packageDir.canWrite()) {
				dbmeta.generateJava(packageDir, properties, strHeader,
						strFooter);
			}
		} catch (Exception exception) {
			DbfLauncherPlugin.log(exception);
		}
		return;
	}

	public static int actionGenerateJavaFiles(DBTable dbtable,
			DbInterfaceProperties dbInterfaceproperties) {
		Properties properties = DbInterfaceProperties
				.getPropertie(dbInterfaceproperties);
		String packageDirName = dbInterfaceproperties.getJavaRepositoryPath();
		File packageDir = new File(packageDirName);
		if (!packageDir.isDirectory())
			return (-1);

		String strHeader = "";
		String strFooter = "";
		try {
			if (packageDir.canWrite())
				dbtable.generateJava(packageDir, properties, strHeader,
						strFooter);
		} catch (Exception exception) {
			DbfLauncherPlugin.log(exception);
		}
		return (0);
	}

	public static void actionGenerateDisplayFiles(DBMeta dbmeta,
			DbInterfaceProperties dbInterfaceproperties) {
		Properties properties = DbInterfaceProperties
				.getPropertie(dbInterfaceproperties);
		String packageDirName = dbInterfaceproperties
				.getDisplayRepositoryPath();
		File packageDir = new File(packageDirName);
		if (!packageDir.isDirectory())
			return;

		String strHeader = "";
		String strFooter = "";
		try {
			if (packageDir.canWrite()) {
				dbmeta.generateDisplay(packageDir, properties, strHeader,
						strFooter);
			}
		} catch (Exception exception) {
			DbfLauncherPlugin.log(exception);
		}
		return;
	}

	public static int actionGenerateDisplayFiles(DBTable dbtable,
			DbInterfaceProperties dbInterfaceproperties) {
		Properties properties = DbInterfaceProperties
				.getPropertie(dbInterfaceproperties);
		String packageDirName = dbInterfaceproperties
				.getDisplayRepositoryPath();
		File packageDir = new File(packageDirName);
		if (!packageDir.isDirectory())
			return (-1);

		String strHeader = "";
		String strFooter = "";
		try {
			if (packageDir.canWrite())
				dbtable.generateDisplay(packageDir, properties, strHeader,
						strFooter);
		} catch (Exception exception) {
			DbfLauncherPlugin.log(exception);
		}
		return (0);
	}

	public static void actionGenerateXmlModel(DBMeta dbmeta,
			DbInterfaceProperties dbInterfaceproperties) {
		if (!DbInterfaceProperties.getUpdateXml())
			return;
		String modelDir = dbInterfaceproperties.getXmlModelRepositoryPath();
		String modelFileName = modelDir + System.getProperty("file.separator")
				+ "model.xml";
		System.out.println("actionGenerateXmlModel modelFileName "
				+ modelFileName);
		File modelFile = new File(modelFileName);
		try {
			if (!modelFile.exists())
				modelFile.createNewFile();
			if (modelFile.canWrite()) {
				PrintWriter writerModelRepository = new PrintWriter(
						new FileOutputStream(modelFile));
				String dbAlias = dbInterfaceproperties.getDbalias();
				// Postgres [// localhost:5432/mfg_scm]
				// MySQL [//localhost:3306/ciapl]
				int lastIndexSlash = dbAlias.lastIndexOf('/');
				String dbName = dbAlias;
				if (lastIndexSlash >= 0) {
					dbName = dbAlias.substring(lastIndexSlash + 1);
				}

				writerModelRepository.println("<?xml version=\"1.0\"?>");
				writerModelRepository.println("<!DOCTYPE database SYSTEM \""
						+ DbInterfaceProperties.getDtdUrl() + "\">");
				writerModelRepository.println("<database name='" + dbName
						+ "' defaultJavaNamingMethod=\"nochange\">");
				dbmeta.generateXmlModel(writerModelRepository);
				writerModelRepository.println("</database>");
				writerModelRepository.close();
			}
		} catch (FileNotFoundException filenotfoundexception) {
			DbfLauncherPlugin.log(IStatus.ERROR, "Fail to generate model"
					+ modelFileName);
			DbfLauncherPlugin.log(filenotfoundexception);
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR, "Fail to generate model"
					+ modelFileName);
			DbfLauncherPlugin.log(ioexception);
		}

		return;
	}

	public static int actionGenerateXmlModel(DBTable dbtable,
			DbInterfaceProperties dbInterfaceproperties) {
		String modelDir = dbInterfaceproperties.getXmlModelRepositoryPath();
		String modelFileName = modelDir + System.getProperty("file.separator")
				+ dbtable.getTableName() + "model.xml";
		File modelFile = new File(modelFileName);
		try {
			if (!modelFile.exists())
				modelFile.createNewFile();
			if (modelFile.canWrite()) {
				PrintWriter writerUserRepository = new PrintWriter(
						new FileOutputStream(modelFile));
				dbtable.generateXmlModel(writerUserRepository);
				writerUserRepository.close();
			}
		} catch (FileNotFoundException filenotfoundexception) {
			DbfLauncherPlugin.log(IStatus.ERROR, "Fail to generate model"
					+ modelFileName);
			DbfLauncherPlugin.log(filenotfoundexception);
		} catch (IOException ioexception) {
			DbfLauncherPlugin.log(IStatus.ERROR, "Fail to generate model"
					+ modelFileName);
			DbfLauncherPlugin.log(ioexception);
		}

		return (0);
	}
}
