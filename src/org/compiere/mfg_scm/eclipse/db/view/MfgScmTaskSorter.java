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

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

/**
 * Sorter for the TableViewerMfgScm that displays items of type
 * <code>MfgScmTask</code>. The sorter supports four sort criteria:
 * <p>
 * <code>NAME</code>: Task name (String)
 * <p>
 * <code>TYPE</code>: Task type (String)
 * </p>
 * <p>
 * <code>SIZE</code>: Task size (String)
 * </p>
 * <p>
 * <code>PRKEY</code>: Task PrKey (String)
 * </p>
 * <p>
 * <code>UNQ</code>: Task Unq (int).
 * </p>
 * <p>
 * <code>NOTNULL</code>: Task NotNull (boolean).
 * </p>
 * <p>
 * <code>MUNQ_COLUMN</code>: Task MUnq (boolean).
 * </p>
 * <p>
 * <code>DEF</code>: Task Def (String)
 * </p>
 * <p>
 * <code>IDX1_COLUMN</code>: Task Idex1 (boolean).
 * </p>
 * <p>
 * <code>INDEXU_COLUMN</code>: Task IndexU (boolean).
 * </p>
 */
public class MfgScmTaskSorter extends ViewerSorter {

	/**
	 * Constructor argument values that indicate to sort items by type, prkey or
	 * percent complete.
	 */
	public final static int NAME = 0;

	public final static int TYPE = 1;

	public final static int SIZE = 2;

	public final static int PRKEY = 3;

	public final static int UNQ = 4;

	public final static int NOTNULL = 5;

	public final static int MUNQ_COLUMN = 6;

	public final static int DEF = 7;

	public final static int IDX1_COLUMN = 8;

	public final static int INDEXU_COLUMN = 9;

	// Criteria that the instance uses
	private int criteria;

	/**
	 * Creates a resource sorter that will use the given sort criteria.
	 * 
	 * @param criteria
	 *            the sort criterion to use: one of <code>NAME</code> or
	 *            <code>TYPE</code>
	 */
	public MfgScmTaskSorter(int criteria) {
		super();
		this.criteria = criteria;
	}

	/*
	 * (non-Javadoc) Method declared on ViewerSorter.
	 */
	public int compare(Viewer viewer, Object o1, Object o2) {

		MfgScmTask task1 = (MfgScmTask) o1;
		MfgScmTask task2 = (MfgScmTask) o2;

		switch (criteria) {
		case NAME:
			return compareNames(task1, task2);
		case TYPE:
			return compareTypes(task1, task2);
		case SIZE:
			return compareSizes(task1, task2);
		case PRKEY:
			return comparePrKeys(task1, task2);
		case UNQ:
			return compareUnq(task1, task2);
		case NOTNULL:
			return compareNotNull(task1, task2);
		case MUNQ_COLUMN:
			return compareMUnq(task1, task2);
		case DEF:
			return compareDef(task1, task2);
		case IDX1_COLUMN:
			return compareIdx1(task1, task2);
		case INDEXU_COLUMN:
			return compareIndexU(task1, task2);
		default:
			return 0;
		}
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the Unq field.
	 * 
	 * @param task1
	 * @param task2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareUnq(MfgScmTask task1, MfgScmTask task2) {
		if (task1.isUnq() && task2.isUnq())
			return 1;
		else
			return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the notNull field.
	 * 
	 * @param task1
	 * @param task2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareNotNull(MfgScmTask task1, MfgScmTask task2) {
		if (task1.isNotNull() && task2.isNotNull())
			return 1;
		else
			return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the munq field.
	 * 
	 * @param task1
	 * @param task2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareMUnq(MfgScmTask task1, MfgScmTask task2) {
		if (task1.isMUnq() && task2.isMUnq())
			return 1;
		else
			return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the idx1 field.
	 * 
	 * @param task1
	 * @param task2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareIdx1(MfgScmTask task1, MfgScmTask task2) {
		if (task1.isIdx1() && task2.isIdx1())
			return 1;
		else
			return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the indexu field.
	 * 
	 * @param task1
	 * @param task2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareIndexU(MfgScmTask task1, MfgScmTask task2) {
		if (task1.isIndexU() && task2.isIndexU())
			return 1;
		else
			return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the name.
	 * 
	 * @param task1
	 *            the first task element to be ordered
	 * @param resource2
	 *            the second task element to be ordered
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	protected int compareNames(MfgScmTask task1, MfgScmTask task2) {
		return collator.compare(task1.getType(), task2.getType());
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the Size field.
	 * 
	 * @param task1
	 * @param task2
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	private int compareSizes(MfgScmTask task1, MfgScmTask task2) {
		int result = task1.getSize() - task2.getSize();
		result = result < 0 ? -1 : (result > 0) ? 1 : 0;
		return result;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on the type.
	 * 
	 * @param task1
	 *            the first task element to be ordered
	 * @param resource2
	 *            the second task element to be ordered
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	protected int compareTypes(MfgScmTask task1, MfgScmTask task2) {
		return collator.compare(task1.getType(), task2.getType());
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on their prkey.
	 * 
	 * @param resource1
	 *            the first resource element to be ordered
	 * @param resource2
	 *            the second resource element to be ordered
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	protected int comparePrKeys(MfgScmTask task1, MfgScmTask task2) {
		if (task1.isPrKey() && task2.isPrKey())
			return 1;
		else
			return 0;
	}

	/**
	 * Returns a number reflecting the collation order of the given tasks based
	 * on their def.
	 * 
	 * @param resource1
	 *            the first resource element to be ordered
	 * @param resource2
	 *            the second resource element to be ordered
	 * @return a negative number if the first element is less than the second
	 *         element; the value <code>0</code> if the first element is equal
	 *         to the second element; and a positive number if the first element
	 *         is greater than the second element
	 */
	protected int compareDef(MfgScmTask task1, MfgScmTask task2) {
		return collator.compare(task1.getDef(), task2.getDef());
	}

	/**
	 * Returns the sort criteria of this this sorter.
	 * 
	 * @return the sort criterion
	 */
	public int getCriteria() {
		return criteria;
	}
}
