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

package org.compiere.mfg_scm.eclipse.db.FormsUtils;

import java.rmi.RemoteException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class MfgScmText extends Composite implements DBFfield {

	private Text text = null;

	private String name;

	private int id;

	private int type;

	private boolean PrKey;

	private boolean Unq;

	private boolean NotN;

	private boolean MUnq;

	private String Def;

	private boolean Idx1;

	private boolean INDEX_U;

	private String comment;

	private int refTable;

	private int refField;

	private boolean onDelete;

	private boolean onUpdate;

	public MfgScmText(Composite parent, int style) {
		super(parent, style);
		initialize();
	}

	private void initialize() {
		text = new Text(this, SWT.BORDER);
		text.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0, 100, 25));
		// setSize(new Point(300, 200));
	}

	public String getName() throws RemoteException {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getId() throws RemoteException {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getType() throws RemoteException {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean getPrKey() throws RemoteException {
		return PrKey;
	}

	public void setPrKey(boolean prKey) {
		this.PrKey = prKey;
	}

	public boolean getUnq() throws RemoteException {
		return Unq;
	}

	public void setUnq(boolean unq) {
		this.Unq = unq;
	}

	public boolean getNotN() throws RemoteException {
		return NotN;
	}

	public void setNotN(boolean notN) {
		this.NotN = notN;
	}

	public boolean getMUnq() throws RemoteException {
		return MUnq;
	}

	public void setMUnq(boolean mUnq) {
		this.MUnq = mUnq;
	}

	public String getDef() throws RemoteException {
		return Def;
	}

	public void setDef(String def) {
		this.Def = def;
	}

	public boolean getIdx1() throws RemoteException {
		return Idx1;
	}

	public void setIdx1(boolean idx1) {
		this.Idx1 = idx1;
	}

	public boolean getIndex_U() throws RemoteException {
		return INDEX_U;
	}

	public void setIndex_U(boolean INDEX_U) {
		this.INDEX_U = INDEX_U;
	}

	public String getComment() throws RemoteException {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getRefTable() throws RemoteException {
		return refTable;
	}

	public void setRefTable(int refTable) {
		this.refTable = refTable;
	}

	public int getRefField() throws RemoteException {
		return refField;
	}

	public void setRefField(int refField) {
		this.refField = refField;
	}

	public boolean OnDelete() throws RemoteException {
		return onDelete;
	}

	public void setOnDelete(boolean onDelete) {
		this.onDelete = onDelete;
	}

	public boolean OnUpdate() throws RemoteException {
		return onUpdate;
	}

	public void setOnUpdate(boolean onUpdate) {
		this.onUpdate = onUpdate;
	}

	// --------------------------------

	public String getText() {
		return text.getText();
	}

	public void setText(String string) {
		text.setText(string);
	}

	public Point getTextSize() {
		return text.getSize();
	}

	public void setTextSize(Point point) {
		text.setSize(point);
	}

	public void computeTextSize(int wHint, int hHint, boolean changed) {
		text.computeSize(wHint, hHint, changed);
	}

	public int getCharCount() {
		return text.getCharCount();
	}

	public Font getFont() {
		return text.getFont();
	}

	public void setFont(Font font) {
		text.setFont(font);
	}
}
