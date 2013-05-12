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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class MFGSCMPropertyEditorInput implements IPersistableElement,
		IEditorInput {

	private String fTooltip;

	private String fName;

	public MFGSCMPropertyEditorInput() {
		fTooltip = "Property Tool Tip";
		return;
	}

	public boolean exists() {
		return (true);
	}

	public ImageDescriptor getImageDescriptor() {
		return (null);
	}

	public String getName() {
		return ((fName != null) ? fName : "My Name");
	}

	public IPersistableElement getPersistable() {
		return (this);
	}

	public String getToolTipText() {
		return (fTooltip);
	}

	public Object getAdapter(Class class1) {
		return (new CompiereMFGSCMDbInterfaceProperty());
	}

	public boolean equals(Object object) {
		if (this == object)
			return (true);

		return (false);
	}

	public void setToolTip(String string) {
		fTooltip = string;
		return;
	}

	public String getFactoryId() {
		return ("org.compiere.mfg_scm.eclipse.db.editors.MFGSCMPropertyEditorFactory");
	}

	public void saveState(IMemento imemento) {
		return;
	}

}
