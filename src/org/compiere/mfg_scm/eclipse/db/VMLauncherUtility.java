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

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.debug.ui.JavaUISourceLocator;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Utility class for launching a JVM in Eclipse and registering it to debugger
 * 
 * It might exist better way to implements those operations, or they might
 * already exist in other form JDT
 */

public class VMLauncherUtility {

	static public IVMInstall getVMInstall() {
		IVMInstallType[] vmTypes = JavaRuntime.getVMInstallTypes();
		for (int i = 0; i < vmTypes.length; i++) {
			IVMInstall[] vms = vmTypes[i].getVMInstalls();
			for (int j = 0; j < vms.length; j++) {
				if (vms[j].getId().equals(
						DbfLauncherPlugin.getDefault().getDbfJRE())) {
					return vms[j];
				}
			}
		}
		return JavaRuntime.getDefaultVMInstall();
	}

	static public void runVM(String label, String classToLaunch,
			String[] classpath, String[] bootClasspath, String vmArgs,
			String prgArgs, ISourceLocator sourceLocator, boolean debug,
			boolean showInDebugger, boolean saveConfig) throws CoreException {

		IVMInstall vmInstall = getVMInstall();
		String mode = "";
		if (debug)
			mode = ILaunchManager.DEBUG_MODE;
		else
			mode = ILaunchManager.RUN_MODE;

		IVMRunner vmRunner = vmInstall.getVMRunner(mode);
		// Launch launch = createLaunch(label, classToLaunch, classpath,
		// bootClasspath, vmArgs, prgArgs, sourceLocator, debug, showInDebugger,
		// false);
		ILaunchConfigurationWorkingCopy config = createConfig(label,
				classToLaunch, classpath, bootClasspath, vmArgs, prgArgs,
				sourceLocator, debug, showInDebugger, saveConfig);
		Launch launch = new Launch(config, mode, sourceLocator);

		if (vmRunner != null) {
			VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(
					classToLaunch, classpath);
			ExecutionArguments executionArguments = new ExecutionArguments(
					vmArgs, prgArgs);
			vmConfig.setVMArguments(executionArguments.getVMArgumentsArray());
			vmConfig.setProgramArguments(executionArguments
					.getProgramArgumentsArray());

			if (bootClasspath.length == 0) {
				vmConfig.setBootClassPath(null); // use default bootclasspath
			} else {
				vmConfig.setBootClassPath(bootClasspath);
			}

			vmRunner.run(vmConfig, launch, null);
		}

		// Show in debugger
		if (showInDebugger) {
			DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
		}

	}

	static public void log(String label, String classToLaunch,
			String[] classpath, String[] bootClasspath, String vmArgs,
			String prgArgs, ISourceLocator sourceLocator, boolean debug,
			boolean showInDebugger) {
		StringBuffer trace = new StringBuffer(
				"\n-------- Launch Dbf JVM Connect --------");
		trace.append("\n-> Label : " + label);
		trace.append("\n-> ClassToLaunch : " + classToLaunch);
		trace.append("\n-> Classpath : ");
		for (int i = 0; i < classpath.length; i++) {
			trace.append(" | " + classpath[i] + " | ");
		}
		trace.append("\n-> BootClasspath : ");
		for (int i = 0; i < bootClasspath.length; i++) {
			trace.append(" | " + bootClasspath[i] + " | ");
		}
		trace.append("\n-> Vmargs : " + vmArgs);
		trace.append("\n-> PrgArgs : " + prgArgs);
		trace.append("\n-> Debug : " + debug);
		trace.append("\n-------- Launch Dbf JVM End--------");
		DbfLauncherPlugin.log(trace.toString());
	}

	static public ILaunchConfigurationWorkingCopy createConfig(String label,
			String classToLaunch, String[] classpath, String[] bootClasspath,
			String vmArgs, String prgArgs, ISourceLocator sourceLocator,
			boolean debug, boolean showInDebugger, boolean saveConfig)
			throws CoreException {
		IVMInstall vmInstall = getVMInstall();

		ILaunchConfigurationType launchType = DebugPlugin.getDefault()
				.getLaunchManager().getLaunchConfigurationType(
						"org.eclipse.jdt.launching.localJavaApplication");
		ILaunchConfigurationWorkingCopy config = launchType.newInstance(null,
				label);
		config.setAttribute(IDebugUIConstants.ATTR_PRIVATE, !saveConfig);
		String targetPerspective = DbfLauncherPlugin.getDefault()
				.getTargetPerspective();
		config.setAttribute(IDebugUIConstants.ATTR_TARGET_DEBUG_PERSPECTIVE,
				targetPerspective);
		config.setAttribute(IDebugUIConstants.ATTR_TARGET_RUN_PERSPECTIVE,
				targetPerspective);

		config.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID,
				JavaUISourceLocator.ID_PROMPTING_JAVA_SOURCE_LOCATOR);
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				classToLaunch);

		ArrayList classpathMementos = new ArrayList();
		for (int i = 0; i < classpath.length; i++) {
			IRuntimeClasspathEntry cpEntry = JavaRuntime
					.newArchiveRuntimeClasspathEntry(new Path(classpath[i]));
			cpEntry.setClasspathProperty(IRuntimeClasspathEntry.USER_CLASSES);
			classpathMementos.add(cpEntry.getMemento());
		}
		if (bootClasspath.length == 0) {
			LibraryLocation[] librairies = vmInstall.getVMInstallType()
					.getDefaultLibraryLocations(vmInstall.getInstallLocation());
			for (int i = 0; i < librairies.length; i++) {
				IRuntimeClasspathEntry cpEntry = JavaRuntime
						.newArchiveRuntimeClasspathEntry(librairies[i]
								.getSystemLibraryPath());
				cpEntry
						.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
				classpathMementos.add(cpEntry.getMemento());
			}
		} else {
			for (int i = 0; i < bootClasspath.length; i++) {
				IRuntimeClasspathEntry cpEntry = JavaRuntime
						.newArchiveRuntimeClasspathEntry(new Path(
								bootClasspath[i]));
				cpEntry
						.setClasspathProperty(IRuntimeClasspathEntry.BOOTSTRAP_CLASSES);
				classpathMementos.add(cpEntry.getMemento());
			}
		}

		config
				.setAttribute(
						IJavaLaunchConfigurationConstants.ATTR_DEFAULT_CLASSPATH,
						false);
		config.setAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH,
				classpathMementos);
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS,
				prgArgs);
		config.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS, vmArgs);

		if (saveConfig) {
			config.doSave();
		}

		return config;
	}

}
