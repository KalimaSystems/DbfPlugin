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

package org.compiere.mfg_scm.eclipse.db.view;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Label provider for the TableViewerMfgScm
 * 
 * @see org.eclipse.jface.viewers.LabelProvider
 */
public class MfgScmLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	// Names of images used to represent checkboxes
	public static final String CHECKED_IMAGE = "checked";

	public static final String UNCHECKED_IMAGE = "unchecked";

	// For the checkbox images
	private static ImageRegistry imageRegistry = new ImageRegistry();

	/**
	 * Note: An image registry owns all of the image objects registered with it,
	 * and automatically disposes of them the SWT Display is disposed.
	 */
	static {
		String iconPath = "/icons/";
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(
				TableViewerMfgScm.class, iconPath + CHECKED_IMAGE + ".gif"));
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(
				TableViewerMfgScm.class, iconPath + UNCHECKED_IMAGE + ".gif"));
	}

	/**
	 * Returns the image with the given key, or <code>null</code> if not
	 * found.
	 */
	private Image getImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
		/*
		 * if (imageRegistry.get(key) == null) System.out.println("GETIMAGE NULL
		 * KEY !!!!!!!!!!!!! " + key);
		 */
		return imageRegistry.get(key);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object,
	 *      int)
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		MfgScmTask task = (MfgScmTask) element;
		switch (columnIndex) {
		case 0: // NAME_COLUMN
			result = task.getName();
			break;
		case 1:
			result = task.getType();
			break;
		case 2:
			result = task.getSize() + "";
			break;
		case 3:
			result = task.isPrKey() + "";
			break;
		case 4:
			result = task.isUnq() + "";
			break;
		case 5:
			result = task.isNotNull() + "";
			break;
		case 6:
			result = task.isMUnq() + "";
			break;
		case 7:
			result = task.getDef();
			break;
		case 8:
			result = task.isIdx1() + "";
			break;
		case 9:
			result = task.isIndexU() + "";
			break;
		default:
			break;
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object,
	 *      int)
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		/*
		 * return (columnIndex == 4) ? // NOTNULL_COLUMN? getImage(((MfgScmTask)
		 * element).isNotNull()) : null;
		 */
		/*
		 * System.out.println("getColumnImage columnIndex !!!!!!!!!!!!! " +
		 * columnIndex);
		 */
		switch (columnIndex) {
		case 0: // NAME_COLUMN
			return null;
		case 1:
			return null;
		case 2:
			return null;
		case 3:
			return (getImage(((MfgScmTask) element).isPrKey()));
		case 4:
			return (getImage(((MfgScmTask) element).isUnq()));
		case 5:
			return (getImage(((MfgScmTask) element).isNotNull()));
		case 6:
			return (getImage(((MfgScmTask) element).isMUnq()));
		case 7:
			return null;
		case 8:
			return (getImage(((MfgScmTask) element).isIdx1()));
		case 9:
			return (getImage(((MfgScmTask) element).isIndexU()));
		default:
			break;
		}
		System.out.println("getColumnImage columnIndex NULL !!!!!!!!!!!!! "
				+ columnIndex);
		return null;
	}

}
