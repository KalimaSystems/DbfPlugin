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

package org.compiere.mfg_scm.eclipse.db.editors;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class DbfDirectoryFieldEditor extends DirectoryFieldEditor {

	boolean enabledField = true;

	public DbfDirectoryFieldEditor(String name, String labelText,
			Composite parent) {
		super(name, labelText, parent);
	}

	protected boolean doCheckState() {
		if (enabledField) {
			return super.doCheckState();
		}
		return true;
	}

	public void setEnabled(boolean enabled, Composite parent) {
		this.enabledField = enabled;
		super.setEnabled(enabled, parent);
	}

	public void valueChanged() {
		super.valueChanged();
	}

}
