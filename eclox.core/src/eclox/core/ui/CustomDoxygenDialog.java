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

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
						"Custom Doxygen directory:", getFieldEditorParent()) {
		            /** The own control is the variableButton */
		            private static final int NUMBER_OF_OWN_CONTROLS = 1;

		            @Override
		            protected boolean doCheckState() {
		                String dirName = getTextControl().getText();
		                dirName = dirName.trim();
		                if (dirName.length() == 0 && isEmptyStringAllowed())
		                    return true;
		                IStringVariableManager manager = VariablesPlugin.getDefault()
		                        .getStringVariableManager();
		                String substitutedDirName;
		                try {
		                    substitutedDirName = manager
		                            .performStringSubstitution(dirName);
		                } catch (CoreException e) {
		                    // It's apparently invalid
		                    return false;
		                }
		                File dir = new File(substitutedDirName);
		                // require the file to exist
		                return dir.exists() && dir.isDirectory();
		            }

		            @Override
		            public int getNumberOfControls() {
		                return super.getNumberOfControls() + NUMBER_OF_OWN_CONTROLS;
		            }

		            @Override
		            protected void doFillIntoGrid(Composite parent, int numColumns) {
		                super.doFillIntoGrid(parent,
		                        numColumns - NUMBER_OF_OWN_CONTROLS);
		            }

		            @Override
		            protected void adjustForNumColumns(int numColumns) {
		                super.adjustForNumColumns(numColumns - NUMBER_OF_OWN_CONTROLS);
		            }

		            @Override
		            protected void createControl(Composite parent) {
		                // setting validate strategy using the setter method is too late
		                super.setValidateStrategy(
		                        StringFieldEditor.VALIDATE_ON_KEY_STROKE);
		                super.createControl(parent);
		                if (hasDebugUiBundle()) {
		                    addVariablesButton(parent);
		                }
		            }

		            private void addVariablesButton(Composite parent) {
		                Button variableButton = new Button(parent, SWT.PUSH);
		                variableButton.setText("&Variable...");
		                variableButton.addSelectionListener(new SelectionAdapter() {
		                    @Override
		                    public void widgetSelected(SelectionEvent e) {
		                        org.eclipse.debug.ui.StringVariableSelectionDialog dialog = new org.eclipse.debug.ui.StringVariableSelectionDialog(
		                                getShell());
		                        int returnCode = dialog.open();
		                        if (returnCode == Window.OK)
		                            setStringValue(dialog.getVariableExpression());
		                    }
		                });
		            }
				};
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

    private static final boolean hasDebugUiBundle() {
        try {
            return Class
                    .forName("org.eclipse.debug.ui.StringVariableSelectionDialog") != null; //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}