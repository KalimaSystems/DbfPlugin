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

import java.io.File;
import java.io.FilenameFilter;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class Dbf11Bootstrap extends DbfBootstrap {

	static private String DEBUG_VIEW_LABEL = "Dbf 3.3";

	/*
	 * @see DbfBootstrap#getClasspath()
	 */
	public String[] getClasspath() {
		String[] classpath = new String[1];

		String toolsJarLocation = VMLauncherUtility.getVMInstall()
				.getInstallLocation()
				+ File.separator + "lib" + File.separator + "tools.jar";
		classpath[0] = toolsJarLocation;

		File libDir = new File(getDbfDir() + File.separator + "lib");
		classpath = this.addJarsOfDirectory(classpath, libDir);

		File containerDir = new File(getDbfDir() + File.separator + "lib"
				+ File.separator + "container");
		classpath = this.addJarsOfDirectory(classpath, containerDir);

		File commonDir = new File(getDbfDir() + File.separator + "lib"
				+ File.separator + "common");
		classpath = this.addJarsOfDirectory(classpath, commonDir);

		File appsDir = new File(getDbfDir() + File.separator + "lib"
				+ File.separator + "apps");
		classpath = this.addJarsOfDirectory(classpath, appsDir);

		return classpath;
	}

	/*
	 * @see DbfBootstrap#getMainClass()
	 */
	public String getMainClass() {
		return "org.apache.dbf.startup.Main";
	}

	/*
	 * @see DbfBootstrap#getConnectCommand()
	 */
	public String getConnectCommand() {
		return "connect";
	}

	/*
	 * @see DbfBootstrap#getDisconnectCommand()
	 */
	public String getDisconnectCommand() {
		return "disconnect";
	}

	public String[] getPrgArgs(String command) {
		String[] prgArgs = null;
		if (command.equals(getConnectCommand())) {
			prgArgs = new String[3];
			prgArgs[0] = command;
			prgArgs[1] = "-config";
			prgArgs[2] = DbfLauncherPlugin.getDefault().getConfigFile();
		} else {
			prgArgs = new String[1];
			prgArgs[0] = command;
		}
		return prgArgs;
	}

	/*
	 * @see DbfBootstrap#getVmArgs()
	 */
	public String[] getVmArgs() {
		String[] vmArgs = new String[1];
		vmArgs[0] = "-Ddbf.home=" + getDbfDir();

		return vmArgs;
	}

	/*
	 * Add all jar files of directory dir to previous array
	 */
	protected String[] addJarsOfDirectory(String[] previous, File dir) {
		if ((dir != null) && (dir.isDirectory())) {
			// Filter for .jar files
			FilenameFilter filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					return filename.endsWith(".jar");
				}
			};

			String[] jars = null;

			File[] files = dir.listFiles(filter);
			jars = new String[files.length];
			for (int i = 0; i < files.length; i++)
				jars[i] = files[i].getAbsolutePath();

			return StringUtil.concat(previous, jars);
		} else {
			return previous;
		}
	}

	/**
	 * @see DbfBootstrap#getLabel()
	 */
	public String getLabel() {
		return DEBUG_VIEW_LABEL;
	}

	public String getContextWorkDir(String workFolder) {
		StringBuffer workDir = new StringBuffer("workDir=");
		workDir.append('"');
		workDir.append(workFolder);
		workDir.append('"');
		return workDir.toString();
	}

}
