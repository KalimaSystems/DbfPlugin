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

package org.compiere.mfg_scm.eclipse.db.view;

import org.compiere.mfg_scm.eclipse.db.view.TreeView.TreeAttribut;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Class used as a trivial case of a Task Serves as the business object for the
 * TableViewer MfgScm.
 * 
 * A Task has the following properties: name, type, prkey, unq and notnull
 * 
 * @author Laurent
 * 
 * 
 */
public class MfgScmTask {

	private String name = "";

	private String type = "";

	private int size = 0;

	private boolean prkey = false;

	private boolean unq = false;

	private boolean notnull = false;

	private boolean munq = false;

	private String def = "?";

	private boolean idx1 = false;

	private boolean indexu = false;

	private TreeAttribut treeColumn;

	/**
	 * Create a task with an initial type
	 * 
	 * @param string
	 */
	public MfgScmTask(String string) {

		super();
		setType(string);
	}

	/**
	 * @return String task name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return String task type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @return int unq
	 * 
	 */
	public int getSize() {
		return size;
	}

	/**
	 * @return boolean task prkey
	 */
	public boolean isPrKey() {
		return prkey;
	}

	/**
	 * @return boolean unq
	 * 
	 */
	public boolean isUnq() {
		return unq;
	}

	/**
	 * @return true if notnull, false otherwise
	 */
	public boolean isNotNull() {
		return notnull;
	}

	/**
	 * @return true if MUnq, false otherwise
	 */
	public boolean isMUnq() {
		return munq;
	}

	/**
	 * @return String task def
	 */
	public String getDef() {
		return def;
	}

	/**
	 * @return true if Idx1, false otherwise
	 */
	public boolean isIdx1() {
		return idx1;
	}

	/**
	 * @return true if notnull, false otherwise
	 */
	public boolean isIndexU() {
		return indexu;
	}

	/**
	 * Set the 'name' property
	 * 
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * Set the 'type' property
	 * 
	 * @param string
	 */
	public void setType(String string) {
		type = string;
	}

	/**
	 * Set the 'size' property
	 * 
	 * @param i
	 */
	public void setSize(int i) {
		size = i;
	}

	/**
	 * Set the 'prkey' property
	 * 
	 * @param b
	 */
	public void setPrKey(boolean b) {
		prkey = b;
	}

	/**
	 * Set the 'unq' property
	 * 
	 * @param b
	 */
	public void setUnq(boolean b) {
		unq = b;
	}

	/**
	 * Set the 'nunull' property
	 * 
	 * @param b
	 */
	public void setNotNull(boolean b) {
		notnull = b;
	}

	/**
	 * Set the 'munq' property
	 * 
	 * @param b
	 */
	public void setMUnq(boolean b) {
		munq = b;
	}

	/**
	 * Set the 'prkey' property
	 * 
	 * @param string
	 */
	public void setDef(String string) {
		def = string;
	}

	/**
	 * Set the 'idx1' property
	 * 
	 * @param b
	 */
	public void setIdx1(boolean b) {
		idx1 = b;
	}

	/**
	 * Set the 'indexu' property
	 * 
	 * @param b
	 */
	public void setIndexU(boolean b) {
		indexu = b;
	}

	public void setTreeColumn(TreeAttribut treeColumn) {
		this.treeColumn = treeColumn;
	}

	public TreeAttribut getTreeAttribut() {
		return treeColumn;
	}
}
