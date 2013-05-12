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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 * @author <a href="mailto:adl@compiere-mfgscm.org">Andre Charles Legendre </a>
 */

public class MfgScmScrolledForm extends ScrolledForm {

	private Section section = null;

	private Composite composite1 = null;

	private Button button = null;

	private MfgScmLabel mainLabel2 = null;

	private MfgScmCheckBox myCheckBox = null;

	private Combo combo = null;

	private MfgScmCombo myCombo = null;

	private MfgScmText myText1 = null;

	private Label label1 = null;

	private Label label2 = null;

	private Label label3 = null;

	private Label label4 = null;

	private Label label5 = null;

	private Label label6 = null;

	private Label label9 = null;

	private MfgScmRadioButton myRadioButton1 = null;

	private Label label = null;

	public MfgScmScrolledForm(Composite parent) {
		super(parent);
		initialize();
		// TODO Auto-generated constructor stub
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
		this.setText("My form title");
		this.setLayout(new GridLayout());

		createSection();

	}

	/**
	 * This method initializes section
	 * 
	 */
	private void createSection() {
		section = new Section(this.getBody(), SWT.BORDER);
		section.setText("My Section title");
		createComposite1();
		section.setClient(composite1);
		section
				.setBounds(new org.eclipse.swt.graphics.Rectangle(0, 0, 510,
						200));
	}

	/**
	 * This method initializes composite1
	 * 
	 */
	private void createComposite1() {
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 9;
		composite1 = new Composite(section, SWT.NONE);
		composite1.setLayout(gridLayout);
		button = new Button(composite1, SWT.NONE);
		createCombo();
		createMfgScmCombo();
		createMfgScmLabel();
		createMfgScmCheckBox();
		createMfgScmText1();
		label1 = new Label(composite1, SWT.NONE);
		label2 = new Label(composite1, SWT.NONE);
		label3 = new Label(composite1, SWT.NONE);
		label4 = new Label(composite1, SWT.NONE);
		label = new Label(composite1, SWT.NONE);
		label5 = new Label(composite1, SWT.NONE);
		label6 = new Label(composite1, SWT.NONE);
		createMfgScmRadioButton1();
		label9 = new Label(composite1, SWT.NONE);
	}

	/**
	 * This method initializes mainLabel2
	 * 
	 */
	private void createMfgScmLabel() {
		mainLabel2 = new MfgScmLabel(composite1, SWT.NONE);
		mainLabel2.setText("Turlututu");
	}

	/**
	 * This method initializes myCheckBox
	 * 
	 */
	private void createMfgScmCheckBox() {
		myCheckBox = new MfgScmCheckBox(composite1, SWT.NONE);
	}

	/**
	 * This method initializes combo
	 * 
	 */
	private void createCombo() {
		GridData gridData = new org.eclipse.swt.layout.GridData();
		gridData.horizontalSpan = 5;
		gridData.widthHint = 70;
		combo = new Combo(composite1, SWT.NONE);
		combo.setItems(new String[] { "250", "500", "750" });
		combo.setText(combo.getItem(0));
		combo.pack();

	}

	/**
	 * This method initializes myCombo
	 * 
	 */
	private void createMfgScmCombo() {
		myCombo = new MfgScmCombo(composite1, SWT.NONE);
		myCombo.setItems(new String[] { "250", "500", "750" });
		myCombo.setText(myCombo.getItem(0));
		myCombo.pack();
	}

	/**
	 * This method initializes myText1
	 * 
	 */
	private void createMfgScmText1() {
		myText1 = new MfgScmText(composite1, SWT.NONE);
		myText1.setText("Mon Texte ici");
		myText1.setTextSize(new org.eclipse.swt.graphics.Point(100, 25));
		myText1.setFont(new Font(Display.getDefault(), "Sans", 10, SWT.ITALIC));
	}

	/**
	 * This method initializes myRadioButton1
	 * 
	 */
	private void createMfgScmRadioButton1() {
		myRadioButton1 = new MfgScmRadioButton(composite1, SWT.NONE);
		myRadioButton1.setText("Radio");
		myRadioButton1.setAlignment(SWT.LEFT);
	}
}
