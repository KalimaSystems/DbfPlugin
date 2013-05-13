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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class JarUtil {
	private byte[] buffer = new byte[4096];

	private Map includes = new LinkedHashMap();

	static final int BUFFER = 2048;

	/**
	 * Add all files in the specified directory to the archive.
	 * 
	 * @param excludesExtensions
	 *            Sets the list of extensions to exclude
	 * @param excludesFiles
	 *            Sets the list of Files to exclude
	 * @param baseDir
	 *            the directory to add
	 */
	protected void addDirectory(String excludesExtensions,
			String excludesFiles, File baseDir) throws IOException {
		List excludesExtensionsList = null;
		List excludesFilesList = null;
		if (!baseDir.exists()) {
			return;
		}

		if (excludesExtensions != null) {
			excludesExtensionsList = Arrays.asList(excludesExtensions
					.split(","));
		}

		if (excludesFiles != null) {
			excludesFilesList = Arrays.asList(excludesFiles.split(","));
		}
		File[] files = baseDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File tmpFile = files[i];
			String tmpName = tmpFile.getName();
			int index = tmpName.lastIndexOf('.');
			String extension = tmpName.substring(0, index + 1);
			if (tmpFile.isFile()
					&& (excludesExtensionsList == null || !excludesExtensionsList
							.contains(extension))
					&& (excludesFilesList == null || !excludesFilesList
							.contains(tmpName))) {
				includes.put(tmpName, tmpFile);
			} else if (tmpFile.isDirectory()) {
				addDirectory(excludesExtensions, excludesFiles, tmpFile);
			}
		}
	}

	/**
	 * Add the specified file to the archive.
	 * 
	 * @param file
	 *            the file to add
	 */
	public void addFile(File file) throws IOException {
		if (!file.exists()) {
			return;
		}

		String tmpName = file.getName();
		includes.put(tmpName, file);
	}

	/**
	 * Create a manifest for the jar file
	 * 
	 * @return a default manifest; the Manifest-Version and Created-By
	 *         attributes are initialized
	 */
	protected Manifest createManifest() {
		Manifest mf = new Manifest();
		Attributes attrs = mf.getMainAttributes();
		attrs.putValue(Attributes.Name.MANIFEST_VERSION.toString(), "1.0");
		attrs.putValue("Created-By", "(Compiere MFG + SCM)");
		return mf;
	}

	/**
	 * Create the jar file specified and include the listed files.
	 * 
	 * @param jarFile
	 *            the jar file to create
	 * @throws IOException
	 *             if there is a problem writing the archive or reading the
	 *             sources
	 */
	public void createJar(File jarFile) throws IOException {
		JarOutputStream jos = null;
		File parentJarFile = jarFile.getParentFile();
		if (!parentJarFile.exists()) {
			parentJarFile.mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(jarFile);
		Manifest mf = createManifest();
		try {
			jos = new JarOutputStream(fos, mf);
		} catch (IOException e) {
			try {
				fos.close();
				jarFile.delete();
			} catch (IOException e1) {
				// ignore
			}
			throw e;
		}
		try {
			addEntries(jos);
		} finally {
			jos.close();
		}
	}

	boolean extractJar(String jarName, String outputDir) {
		File dirToExtractTo = new File(outputDir);
		byte data[] = new byte[BUFFER];

		try {
			URL resourceURL = DbfLauncherPlugin.class.getClassLoader()
					.getResource(jarName);

			// Run through the entries
			File newExtractedFile = new File(dirToExtractTo, jarName);
			File parentFile = newExtractedFile.getParentFile();
			while (!parentFile.exists()) {
				parentFile.mkdirs();
				parentFile = parentFile.getParentFile();
			}

			BufferedInputStream bis = new BufferedInputStream(resourceURL
					.openStream(), BUFFER);

			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(newExtractedFile), BUFFER);

			int count;

			while ((count = bis.read(data, 0, BUFFER)) != -1) {
				bos.write(data, 0, count);
			}

			bos.close();
		} catch (IOException e) {
			DbfLauncherPlugin.log(e);
		}

		return true;
	}

	public static String readTextResource(String resourceName)
			throws IOException {

		StringBuffer buf = new StringBuffer();
		String inputLine;

		try {
			URL resourceURL = DbfLauncherPlugin.class.getClassLoader()
					.getResource(resourceName);

			BufferedReader in = new BufferedReader(new InputStreamReader(
					resourceURL.openStream()));

			while ((inputLine = in.readLine()) != null) {
				buf.append(inputLine);
				buf.append('\n');
			}
			in.close();
		} catch (IOException e) {
			DbfLauncherPlugin.log(e);
		}

		return buf.toString();
	}

	/**
	 * Add all entries in the supplied Map to the jar
	 * 
	 * @param jos
	 *            a JarOutputStream that can be used to write to the jar
	 * @throws IOException
	 *             if there is a problem writing the archive or reading the
	 *             sources
	 */
	protected void addEntries(JarOutputStream jos) throws IOException {
		for (Iterator i = includes.entrySet().iterator(); i.hasNext();) {
			Map.Entry entry = (Map.Entry) i.next();
			String name = (String) entry.getKey();
			File file = (File) entry.getValue();
			addEntry(jos, name, file);
		}
	}

	/**
	 * Add a single entry to the jar
	 * 
	 * @param jos
	 *            a JarOutputStream that can be used to write to the jar
	 * @param name
	 *            the entry name to use; must be '/' delimited
	 * @param source
	 *            the file to add
	 * @throws IOException
	 *             if there is a problem writing the archive or reading the
	 *             sources
	 */
	protected void addEntry(JarOutputStream jos, String name, File source)
			throws IOException {
		FileInputStream fis = new FileInputStream(source);
		try {
			jos.putNextEntry(new JarEntry(name));
			int count;
			while ((count = fis.read(buffer)) > 0) {
				jos.write(buffer, 0, count);
			}
			jos.closeEntry();
		} finally {
			fis.close();
		}
	}
}
