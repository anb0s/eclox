/*******************************************************************************
 * Copyright (C) 2015, Andre Bossert
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Andre Bossert - Initial API and implementation
 *
 ******************************************************************************/

package eclox.core.ui;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public class CustomDoxygenDialog extends Dialog {

	private String targetDirectory;
	private FieldEditorPreferencePage page;
	private DirectoryFieldEditor targetDirectoryEditor;

	public CustomDoxygenDialog(Shell parentShell, String targetDirectory) {
		super(parentShell);
		this.targetDirectory = targetDirectory;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select a location containing doxygen.");
		newShell.setSize(500, 170);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		page = new FieldEditorPreferencePage(FieldEditorPreferencePage.GRID) {
			@Override
			protected void createFieldEditors() {
				targetDirectoryEditor = new DirectoryFieldEditor("",
						"Custom Doxygen directory:", getFieldEditorParent());
				targetDirectoryEditor.setStringValue(targetDirectory == null ? "" : targetDirectory);
				addField(targetDirectoryEditor);
			}

			@Override
			public void createControl(Composite parentComposite) {
				noDefaultAndApplyButton();
				super.createControl(parentComposite);
			}

			@Override
			protected void updateApplyButton() {
				updateButtons(isValid());
				super.updateApplyButton();
			}

			// @Override
			// protected void createButtonsForButtonBar(Composite parent) {
			// super.createButtonsForButtonBar(parent);
			// updateButtons(page.isValid());
			// }
			private void updateButtons(boolean isValid) {
				Button okButton = getButton(IDialogConstants.OK_ID);
				if (okButton != null) {
					okButton.setEnabled(isValid);
				}
			}
		};
		page.createControl(composite);
		Control pageControl = page.getControl();
		pageControl.setLayoutData(new GridData(GridData.FILL_BOTH));
		return pageControl;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (buttonId == IDialogConstants.OK_ID) {
			targetDirectory = targetDirectoryEditor.getStringValue();
		}
		super.buttonPressed(buttonId);
	}

	public String getTargetDirectory() {
		return targetDirectory;
	}
}