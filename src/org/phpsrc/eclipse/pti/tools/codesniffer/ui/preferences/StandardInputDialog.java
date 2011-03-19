/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.ui.preferences;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.php.internal.ui.util.StatusInfo;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IStringButtonAdapter;
import org.eclipse.php.internal.ui.wizards.fields.LayoutUtil;
import org.eclipse.php.internal.ui.wizards.fields.StringButtonDialogField;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.Standard;

/**
 * Dialog to enter a na new task tag
 */
public class StandardInputDialog extends StatusDialog {

	private class CompilerStandardInputAdapter implements IDialogFieldListener {
		public void dialogFieldChanged(DialogField field) {
			doValidation();
		}
	}

	public static final boolean WINDOWS = java.io.File.separatorChar == '\\'; //$NON-NLS-1$

	private final StringDialogField fNameDialogField;
	private final StringButtonDialogField fPathDialogField;

	private final List<String> fExistingNames;

	public StandardInputDialog(Shell parent, Standard standard, List<Standard> existingEntries) {
		super(parent);

		fExistingNames = new ArrayList<String>(existingEntries.size());
		for (int i = 0; i < existingEntries.size(); i++) {
			Standard curr = existingEntries.get(i);
			if (!curr.equals(standard)) {
				fExistingNames.add(curr.name);
			}
		}

		if (standard == null) {
			setTitle("New Custom Standard");
		} else {
			setTitle("Edit Custom Standard");
		}

		fPathDialogField = new StringButtonDialogField(new IStringButtonAdapter() {
			public void changeControlPressed(DialogField field) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setFilterPath(fPathDialogField.getText());
				dialog.setText("Select the standard path");
				dialog.setMessage("Please select the path which represent the CodeSniffer standard");
				String newPath = dialog.open();
				if (newPath != null) {
					fPathDialogField.setText(newPath);
					doValidation();
				}
			}
		});
		fPathDialogField.setLabelText("Path:");
		fPathDialogField.setButtonLabel("Browse...");
		fPathDialogField.setText((standard != null) ? standard.path : ""); //$NON-NLS-1$

		fNameDialogField = new StringDialogField();
		fNameDialogField.setLabelText("Name:");
		CompilerStandardInputAdapter adapter = new CompilerStandardInputAdapter();
		fNameDialogField.setDialogFieldListener(adapter);
		fNameDialogField.setText((standard != null) ? standard.name : ""); //$NON-NLS-1$
	}

	public Standard getResult() {
		Standard standard = new Standard();
		standard.name = fNameDialogField.getText().trim();
		standard.custom = true;
		standard.path = fPathDialogField.getText().trim();
		return standard;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		Composite inner = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		inner.setLayout(layout);

		fNameDialogField.doFillIntoGrid(inner, 3);

		LayoutUtil.setHorizontalGrabbing(fNameDialogField.getTextControl(null));
		LayoutUtil.setWidthHint(fNameDialogField.getTextControl(null), convertWidthInCharsToPixels(60));

		fNameDialogField.postSetFocusOnDialogField(parent.getDisplay());

		fPathDialogField.doFillIntoGrid(inner, 3);

		applyDialogFont(composite);
		return composite;
	}

	private void doValidation() {
		StatusInfo status = new StatusInfo();

		String newName = fNameDialogField.getText();
		String newPath = fPathDialogField.getText();

		if (newPath.length() == 0) {
			status.setError("Enter standard path.");
		}

		if (newName.length() == 0 && newPath.length() > 0) {
			int lastIndex = WINDOWS ? newPath.lastIndexOf("\\") : newPath.lastIndexOf("/");
			if (lastIndex > 0) {
				newName = newPath.substring(lastIndex + 1).replace(' ', '_');
				fNameDialogField.setText(newName);
			}
		}

		if (newName.length() == 0) {
			status.setError("Enter standard name.");
		} else {
			if (!Pattern.matches("^[a-zA-Z0-9_]+$", newName)) {
				status.setError("Name can only contain letters, numbers and underscores");
			} else if (fExistingNames.contains(newName)) {
				status.setError("An entry with the same name already exists");
			}
		}

		if (newPath != null && newPath.length() > 0) {
			IPath path = Path.fromOSString(newPath);
			if (path != null) {
				File codingStandardPHP = new File(newPath + File.separatorChar + path.lastSegment()
						+ "CodingStandard.php");
				File codingStandardXML = new File(newPath + File.separatorChar + "ruleset.xml");
				if (!codingStandardPHP.exists() && !codingStandardXML.exists()) {
					status.setError("Missing standard file " + path.lastSegment() + "CodingStandard.php or ruleset.xml");
				}
			}
		}

		updateStatus(status);
	}

	/*
	 * @see org.eclipse.jface.window.Window#configureShell(Shell)
	 */

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// TODO - Add the Help contex id
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IPHPHelpContextIds.TODO_TASK_INPUT_DIALOG);
	}
}
