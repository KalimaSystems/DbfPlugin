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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IStatus;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * container for managing a number of OjbClassPathEntry objects
 * 
 */
public class DbfClassPathEntries {
	public static final String TAG_NAME = "dbfClassPathEntries";

	private static final String ENTRY_TAG_NAME = "ojbClassPathEntry";

	private List entries;

	public DbfClassPathEntries() {
		entries = new ArrayList();
	}

	public DbfClassPathEntries(List values) {
		entries = values;
	}

	/** returns the number of webclasspath-entries */
	public int size() {
		return entries.size();
	}

	/** return the OjbClassPathEntry value at the index provided */
	public String getOjbClassPathEntry(int index) {
		if (index >= entries.size())
			return null;
		String entry = (String) entries.get(index);
		return entry;
	}

	/** add a OjbClassPathEntry value */
	public void addOjbClassPathEntry(String value) {
		if (entries.contains(value))
			return;
		entries.add(value);
	}

	public List getList() {
		return entries;
	}

	/**
	 * transfer the state of this object to an XML string
	 */
	public String xmlMarshal() {
		return xmlMarshal(0);
	}

	public String xmlMarshal(int spacesToIntend) {
		String spaces = "";
		for (int i = 0; i < spacesToIntend; i++) {
			spaces = spaces + " ";
		}
		String xml = spaces + startTag() + "\n";

		for (Iterator it = entries.iterator(); it.hasNext();) {
			String entry = (String) it.next();
			xml += spaces + spaces + startEntryTag() + entry + endEntryTag()
					+ "\n";
		}

		xml += spaces + endTag() + "\n";
		return xml;
	}

	/**
	 * instantiate a DbfClassPathEntries object and intialize it with the xml
	 * data provided
	 * 
	 * @return the object if unmarshaling had no errors. returns null if the
	 *         marshaling was unsuccessfully.
	 */
	public static DbfClassPathEntries xmlUnmarshal(String xmlString) {
		if (xmlString == null || xmlString.trim().length() == 0) {
			return null;
		}
		int start = xmlString.indexOf(startTag());
		int end = xmlString.indexOf(endTag());
		if (start < 0 || end <= start)
			return null;

		String value = xmlString.substring(start + startTag().length(), end);

		value = value.trim();

		DbfClassPathEntries webEntries = new DbfClassPathEntries();
		while (value != null && value.length() > 0) {
			start = value.indexOf(startEntryTag());
			end = value.indexOf(endEntryTag());
			if (start >= 0 || end > start) {
				String entryValue = value.substring(start
						+ startEntryTag().length(), end);
				if (entryValue.trim().length() > 0) {
					webEntries.addOjbClassPathEntry(entryValue);
				}
				value = value.substring(end + endEntryTag().length());
			} else {
				value = null;
			}
		}

		return webEntries;
	}

	private static String startTag() {
		return "<" + TAG_NAME + ">";
	}

	private static String endTag() {
		return "</" + TAG_NAME + ">";
	}

	private static String startEntryTag() {
		return "<" + ENTRY_TAG_NAME + ">";
	}

	private static String endEntryTag() {
		return "</" + ENTRY_TAG_NAME + ">";
	}

	/**
	 * main method yust for some simple tests - should be in a Junit Testclass
	 * but I don't want to add the junit reference to this project
	 */
	public static void main(String[] arguments) {
		String xml = "";
		DbfClassPathEntries entries = xmlUnmarshal(xml);

		if (entries != null) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"invalid xml must result in null object !");
			System.exit(1);
		}

		xml = "<dbfClassPathEntries></dbfClassPathEntries>";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries valid xml must result in an object !");
			System.exit(1);
		}
		if (entries.size() != 0) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected size 0 but was "
							+ entries.size());
			System.exit(1);
		}
		xml = "<root><dbfClassPathEntries>\n</dbfClassPathEntries>\n</root>";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries valid xml must result in an object !");
			System.exit(1);
		}
		if (entries.size() != 0) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected size 0 but was "
							+ entries.size());
			System.exit(1);
		}

		xml = "<dbfClassPathEntries><ojbClassPathEntry>abc</ojbClassPathEntry></dbfClassPathEntries>";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries valid xml must result in an object !");
			System.exit(1);
		}
		if (entries.size() != 1) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected size 1 but was "
							+ entries.size());
			System.exit(1);
		}
		if (!entries.getOjbClassPathEntry(0).equals("abc")) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected 'abc' but was '"
							+ entries.getOjbClassPathEntry(0) + "'");
			System.exit(1);
		}

		xml = "<dbfClassPathEntries>\n<ojbClassPathEntry>abc</ojbClassPathEntry>\n<ojbClassPathEntry>def</ojbClassPathEntry>\n<ojbClassPathEntry>123</ojbClassPathEntry>\nxxxxx</dbfClassPathEntries>\n";
		entries = xmlUnmarshal(xml);
		if (entries == null) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries valid xml must result in an object !");
			System.exit(1);
		}
		if (entries.size() != 3) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected size 1 but was "
							+ entries.size());
			System.exit(1);
		}
		if (!entries.getOjbClassPathEntry(0).equals("abc")) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected 'abc' but was '"
							+ entries.getOjbClassPathEntry(0) + "'");
			System.exit(1);
		}
		if (!entries.getOjbClassPathEntry(1).equals("def")) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected 'def' but was '"
							+ entries.getOjbClassPathEntry(1) + "'");
			System.exit(1);
		}
		if (!entries.getOjbClassPathEntry(2).equals("123")) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected '123' but was '"
							+ entries.getOjbClassPathEntry(2) + "'");
			System.exit(1);
		}

		xml = "<dbfClassPathEntries>\n<ojbClassPathEntry>abc</ojbClassPathEntry>\n<ojbClassPathEntry>def</ojbClassPathEntry>\n<ojbClassPathEntry>123</ojbClassPathEntry>\n</dbfClassPathEntries>\n";
		String gen = entries.xmlMarshal();
		if (gen.equals(xml) == false) {
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries generated xml is incorrect:\n!" + gen
							+ "!");
			DbfLauncherPlugin.log(IStatus.ERROR,
					"DbfClassPathEntries expected xml is :\n!" + xml + "!");
			System.exit(1);
		}

		DbfLauncherPlugin.log(IStatus.INFO, DbfLauncherPlugin
				.getResourceString("All okay !"));
	}
}
