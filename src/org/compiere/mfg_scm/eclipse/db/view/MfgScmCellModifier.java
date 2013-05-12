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

import org.compiere.mfg_scm.eclipse.db.dbInterface.DBColumn;
import org.compiere.mfg_scm.eclipse.db.dbInterface.DBTable;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * This class implements an ICellModifier An ICellModifier is called when the
 * user modifes a cell in the tableViewer
 */

public class MfgScmCellModifier implements ICellModifier {

	private TableViewerMfgScm tableViewerMfgScm;

	/**
	 * Constructor
	 * 
	 * @param TableViewerMfgScm
	 *            an instance of a TableViewerMfgScm
	 */
	public MfgScmCellModifier(TableViewerMfgScm tableViewerMfgScm) {
		super();
		this.tableViewerMfgScm = tableViewerMfgScm;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#canModify(java.lang.Object,
	 *      java.lang.String)
	 */
	public boolean canModify(Object element, String property) {
		return true;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#getValue(java.lang.Object,
	 *      java.lang.String)
	 */
	public Object getValue(Object element, String property) {

		// Find the index of the column
		int columnIndex = tableViewerMfgScm.getColumnNames().indexOf(property);

		Object result = null;
		MfgScmTask task = (MfgScmTask) element;

		switch (columnIndex) {
		case 0: // NAME_COLUMN
			result = task.getName();
			break;
		case 1: // TYPE_COLUMN
			String stringValue = task.getType();
			String[] choices = tableViewerMfgScm.getChoices(property);
			int i = choices.length - 1;
			while (!stringValue.toUpperCase().equals(choices[i]) && i > 0)
				--i;
			result = new Integer(i);
			break;
		case 2: // SIZE_COLUMN
			result = task.getSize() + "";
			break;
		case 3: // PRKEY_COLUMN
			result = new Boolean(task.isPrKey());
			break;
		case 4: // UNQ_COLUMN
			result = new Boolean(task.isUnq());
			break;
		case 5: // NOTNULL_COLUMN
			result = new Boolean(task.isNotNull());
			break;
		case 6: // MUNQ_COLUMN
			result = new Boolean(task.isMUnq());
			break;
		case 7: // DEF_COLUMN
			result = task.getDef();
			break;
		case 8: // IDX1_COLUMN
			result = new Boolean(task.isIdx1());
			break;
		case 9: // INDEXU_COLUMN
			result = new Boolean(task.isIndexU());
			break;
		default:
			result = "";
		}
		return result;
	}

	/**
	 * @see org.eclipse.jface.viewers.ICellModifier#modify(java.lang.Object,
	 *      java.lang.String, java.lang.Object)
	 */
	public void modify(Object element, String property, Object value) {

		// Find the index of the column
		int columnIndex = tableViewerMfgScm.getColumnNames().indexOf(property);

		TableItem item = (TableItem) element;
		MfgScmTask task = (MfgScmTask) item.getData();
		String valueString;
		DBTable dbTable = tableViewerMfgScm.getTreeTable().getTable();
		DBColumn column = task.getTreeAttribut().getColumn();

		switch (columnIndex) {
		case 0: // NAME_COLUMN
			valueString = ((String) value).trim();

			task.getTreeAttribut().setName(valueString);
			column.setColumnName(valueString);
			dbTable.changeColumn(task.getName(), column);

			task.setName(valueString);
			break;
		case 1: // TYPE_COLUMN
			valueString = tableViewerMfgScm.getChoices(property)[((Integer) value)
					.intValue()].trim();
			if (!task.getType().equals(valueString)) {

				column.setColumnType(valueString);
				dbTable.changeColumn(task.getName(), column);

				task.setType(valueString);
			}
			break;
		case 2: // SIZE_COLUMN
			valueString = ((String) value).trim();
			if (valueString.length() == 0) {
				// valueString = "0";
				valueString = "" + task.getSize();
			}
			column.setColumnSize(Integer.parseInt(valueString));
			dbTable.changeColumn(task.getName(), column);

			task.setSize(Integer.parseInt(valueString));
			break;
		case 3: // PRKEY_COLUMN
			column.setPrimaryKeyPart(((Boolean) value).booleanValue());
			dbTable.changeColumn(task.getName(), column);

			task.getTreeAttribut().setPrimaryKeyPart(
					((Boolean) value).booleanValue());

			task.setPrKey(((Boolean) value).booleanValue());
			break;
		case 4: // UNQ_COLUMN
			column.setUnq(((Boolean) value).booleanValue());
			dbTable.changeColumn(task.getName(), column);

			task.setUnq(((Boolean) value).booleanValue());
			break;
		case 5: // NOTNULL_COLUMN
			boolean isNullable = ((Boolean) value).booleanValue();
			if (isNullable) {
				column.setNullable(1);
			} else {
				column.setNullable(0);
			}
			dbTable.changeColumn(task.getName(), column);

			task.setNotNull(isNullable);
			break;
		case 6: // MUNQ_COLUMN
			column.setMUnq(((Boolean) value).booleanValue());
			dbTable.changeColumn(task.getName(), column);

			task.setMUnq(((Boolean) value).booleanValue());
			break;
		case 7: // DEF_COLUMN
			valueString = ((String) value).trim();

			column.setDef(valueString);
			dbTable.changeColumn(task.getName(), column);

			task.setDef(valueString);
			break;
		case 8: // IDX1_COLUMN
			column.setIdx1(((Boolean) value).booleanValue());
			dbTable.changeColumn(task.getName(), column);

			task.setIdx1(((Boolean) value).booleanValue());
			break;
		case 9: // INDEXU_COLUMN
			column.setIndexU(((Boolean) value).booleanValue());
			dbTable.changeColumn(task.getName(), column);

			task.setIndexU(((Boolean) value).booleanValue());
			break;
		default:
		}
		tableViewerMfgScm.getTaskList().taskChanged(task);

		tableViewerMfgScm.getTreeTable().getTreeView().getTreeViewer()
				.refresh();
	}
}
