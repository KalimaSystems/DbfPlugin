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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Utility class for Files operation
 */

public class FileUtil {

	public static String readTextFile(File f) throws IOException {

		StringBuffer buf = new StringBuffer();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				new FileInputStream(f)));
		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			buf.append(inputLine);
			buf.append('\n');
		}

		in.close();
		return buf.toString();

	}

	public static void toTextFile(File f, String content) throws IOException {
		FileWriter out = new FileWriter(f);
		out.write(content);
		out.close();
	}

	public static void copy(String inputFilename, String outputFilename)
			throws IOException {
		FileUtil.copy(new File(inputFilename), new File(outputFilename));
	}

	/**
	 * Copie un fichier vers un autre fichier ou un r�pertoire vers un autre
	 * r�pertoire
	 */
	public static void copy(File input, File output) throws IOException {
		if (input.isDirectory() && output.isDirectory()) {
			FileUtil.copyDir(input, output);
		} else {
			FileUtil.copyFile(input, output);
		}
	}

	/**
	 * Copie un fichier vers un autre
	 */
	public static void copyFile(File inputFile, File outputFile)
			throws IOException {
		BufferedInputStream fr = new BufferedInputStream(new FileInputStream(
				inputFile));
		BufferedOutputStream fw = new BufferedOutputStream(
				new FileOutputStream(outputFile));
		byte[] buf = new byte[8192];
		int n;
		while ((n = fr.read(buf)) >= 0)
			fw.write(buf, 0, n);
		fr.close();
		fw.close();
	}

	/**
	 * Copie un r�pertoire dans un autre
	 */
	public static void copyDir(File inputDir, File outputDir)
			throws IOException {
		File[] files = inputDir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File destFile = new File(outputDir.getAbsolutePath()
					+ File.separator + files[i].getName());
			if (!destFile.exists()) {
				if (files[i].isDirectory()) {
					destFile.mkdir();
				}
			}
			FileUtil.copy(files[i], destFile);
		}
	}

	/**
	 * return true if the directory contains files with the extension
	 */

	public static boolean dirContainsFiles(File dir, String extension,
			boolean recursive) {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isFile() && files[i].getName().endsWith(extension))
				return true;
			if (recursive && files[i].isDirectory())
				return FileUtil
						.dirContainsFiles(files[i], extension, recursive);
		}

		return false;
	}

	/**
	 * 
	 */
	public static String readPropertyInXMLFile(File file, String property)
			throws IOException {
		String content = FileUtil.readTextFile(file);
		int startTagIdx = content.indexOf("<" + property + ">");
		int endTagIdx = content.indexOf("</" + property + ">");
		if (startTagIdx == -1)
			throw new IOException("Property " + property
					+ " not found in file " + file);

		return content
				.substring(startTagIdx + property.length() + 2, endTagIdx);
	}

	/**
	 * Recursive delete of a directory. <br>
	 * The directory itself will be deleted
	 */
	public static void removeDir(File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			if (files[i].isDirectory()) {
				FileUtil.removeDir(files[i]);
			} else {
				files[i].delete();
			}
		}
		dir.delete();
	}

}
