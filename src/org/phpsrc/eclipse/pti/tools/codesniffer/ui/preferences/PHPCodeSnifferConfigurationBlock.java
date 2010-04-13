/*******************************************************************************
 * Copyright (c) 2010, Sven Kiera
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * - Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * - Neither the name of the Organisation nor the names of its contributors may
 *   be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package org.phpsrc.eclipse.pti.tools.codesniffer.ui.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.window.Window;
import org.eclipse.php.internal.ui.preferences.IStatusChangeListener;
import org.eclipse.php.internal.ui.preferences.util.Key;
import org.eclipse.php.internal.ui.util.PixelConverter;
import org.eclipse.php.internal.ui.wizards.fields.CheckedListDialogField;
import org.eclipse.php.internal.ui.wizards.fields.DialogField;
import org.eclipse.php.internal.ui.wizards.fields.IDialogFieldListener;
import org.eclipse.php.internal.ui.wizards.fields.IListAdapter;
import org.eclipse.php.internal.ui.wizards.fields.ListDialogField;
import org.eclipse.php.internal.ui.wizards.fields.StringDialogField;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.phpsrc.eclipse.pti.library.pear.ui.preferences.AbstractPEARPHPToolConfigurationBlock;
import org.phpsrc.eclipse.pti.tools.codesniffer.PHPCodeSnifferPlugin;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.PHPCodeSniffer;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.Standard;
import org.phpsrc.eclipse.pti.ui.widgets.listener.NumberOnlyVerifyListener;

public class PHPCodeSnifferConfigurationBlock extends AbstractPEARPHPToolConfigurationBlock {

	private static final Key PREF_PHP_EXECUTABLE = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE);
	private static final Key PREF_PEAR_LIBRARY = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_PEAR_LIBRARY);
	private static final Key PREF_DEBUG_PRINT_OUTPUT = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
	private static final Key PREF_CUSTOM_STANDARD_NAMES = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_NAMES);
	private static final Key PREF_CUSTOM_STANDARD_PATHS = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_PATHS);
	private static final Key PREF_DEFAULT_STANDARD_NAME = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_NAME);
	private static final Key PREF_DEFAULT_STANDARD_PATH = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH);
	private static final Key PREF_DEFAULT_TAB_WITH = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH);
	private static final Key PREF_ACTIVE_STANDARDS = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_ACTIVE_STANDARDS);
	private static final Key PREF_FILE_EXTENSIONS = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_FILE_EXTENSIONS);
	private static final Key PREF_IGNORE_PATTERN = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_IGNORE_PATTERN);
	private static final Key PREF_IGNORE_SNIFFS = getCodeSnifferKey(PHPCodeSnifferPreferenceNames.PREF_IGNORE_SNIFFS);

	private static final int IDX_ADD = 0;
	private static final int IDX_EDIT = 1;
	private static final int IDX_REMOVE = 2;

	private final CheckedListDialogField fStandardsList;
	private final StringDialogField fTabWidth;
	private final StringDialogField fFileExtension;
	private final StringDialogField fIgnorePattern;
	private final StringDialogField fIgnoreSniffs;

	private class CodeSnifferLabelProvider extends LabelProvider implements ITableLabelProvider, IFontProvider {

		public CodeSnifferLabelProvider() {
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getImage(java.lang.Object)
		 */

		public Image getImage(Object element) {
			return null; // JavaPluginImages.get(JavaPluginImages.IMG_OBJS_REFACTORING_INFO);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
		 */

		public String getText(Object element) {
			return getColumnText(element, 0);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java
		 * .lang.Object, int)
		 */
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.
		 * lang.Object, int)
		 */
		public String getColumnText(Object element, int columnIndex) {
			Standard standard = (Standard) element;
			if (columnIndex == 0) {
				return standard.name;
			} else if (columnIndex == 1) {
				return standard.custom ? "yes" : "no";
			} else if (columnIndex == 2) {
				return standard.path;
			} else {
				return "";
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.viewers.IFontProvider#getFont(java.lang.Object)
		 */
		public Font getFont(Object element) {
			if (false) {
				return JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
			}
			return null;
		}
	}

	private class StandardAdapter implements IListAdapter<Object>, IDialogFieldListener {

		private boolean canEdit(List<Object> selectedElements) {
			return selectedElements.size() == 1 && ((Standard) selectedElements.get(0)).custom;
		}

		private boolean canRemove(List<Object> selectedElements) {
			int count = selectedElements.size();

			if (count == 0)
				return false;

			for (int i = 0; i < count; i++) {
				if (!((Standard) selectedElements.get(i)).custom)
					return false;
			}

			return true;
		}

		public void customButtonPressed(ListDialogField<Object> field, int index) {
			doStandardButtonPressed(index);
		}

		public void selectionChanged(ListDialogField<Object> field) {
			List<Object> selectedElements = field.getSelectedElements();
			field.enableButton(IDX_EDIT, canEdit(selectedElements));
			field.enableButton(IDX_REMOVE, canRemove(selectedElements));
		}

		public void doubleClicked(ListDialogField<Object> field) {
			if (canEdit(field.getSelectedElements())) {
				doStandardButtonPressed(IDX_EDIT);
			}
		}

		public void dialogFieldChanged(DialogField field) {
			updateModel(field);
		}

	}

	public PHPCodeSnifferConfigurationBlock(IStatusChangeListener context, IProject project,
			IWorkbenchPreferenceContainer container) {
		super(context, project, getKeys(), container);

		StandardAdapter adapter = new StandardAdapter();

		String[] buttons = new String[] { "New...", "Edit...", "Remove" };

		fStandardsList = new CheckedListDialogField(adapter, buttons, new CodeSnifferLabelProvider());
		fStandardsList.setDialogFieldListener(adapter);
		fStandardsList.setRemoveButtonIndex(IDX_REMOVE);

		String[] columnsHeaders = new String[] { "Name", "Custom", "Path" };

		ColumnLayoutData[] data = new ColumnLayoutData[] { new ColumnWeightData(2), new ColumnWeightData(1),
				new ColumnWeightData(4) };

		fStandardsList.setTableColumns(new ListDialogField.ColumnsDescription(data, columnsHeaders, true));
		fStandardsList.setViewerSorter(new ViewerSorter());

		if (fStandardsList.getSize() > 0) {
			fStandardsList.selectFirstElement();
		} else {
			fStandardsList.enableButton(IDX_EDIT, false);
		}

		fTabWidth = new StringDialogField();
		fTabWidth.setLabelText("Tab width:");

		unpackTabWidth();

		fFileExtension = new StringDialogField();
		fFileExtension.setLabelText("File Extensions:");

		unpackFileExtensions();

		fIgnorePattern = new StringDialogField();
		fIgnorePattern.setLabelText("Patterns:");

		unpackIgnorePattern();

		fIgnoreSniffs = new StringDialogField();
		fIgnoreSniffs.setLabelText("Sniffs:");

		unpackIgnoreSniffs();
	}

	private static Key[] getKeys() {
		return new Key[] { PREF_PHP_EXECUTABLE, PREF_PEAR_LIBRARY, PREF_DEBUG_PRINT_OUTPUT, PREF_CUSTOM_STANDARD_NAMES,
				PREF_CUSTOM_STANDARD_PATHS, PREF_ACTIVE_STANDARDS, PREF_DEFAULT_TAB_WITH, PREF_FILE_EXTENSIONS,
				PREF_IGNORE_PATTERN, PREF_IGNORE_SNIFFS };
	}

	protected Composite createToolContents(Composite parent) {
		Composite standardsComposite = createStandardsTabContent(parent);
		validateSettings(null, null, null);

		return standardsComposite;
	}

	private Composite createStandardsTabContent(Composite folder) {

		PixelConverter conv = new PixelConverter(folder);

		GridLayout markersLayout = new GridLayout();
		markersLayout.marginHeight = 5;
		markersLayout.marginWidth = 0;
		markersLayout.numColumns = 3;

		Group markersGroup = new Group(folder, SWT.NULL);
		markersGroup.setText("CodeSniffer Standards");
		markersGroup.setLayout(markersLayout);
		markersGroup.setFont(folder.getFont());

		GridData listData = new GridData(GridData.FILL_BOTH);
		listData.widthHint = conv.convertWidthInCharsToPixels(50);
		Control listControl = fStandardsList.getListControl(markersGroup);
		listControl.setLayoutData(listData);

		Control buttonsControl = fStandardsList.getButtonBox(markersGroup);
		buttonsControl.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_BEGINNING));

		GridLayout tabWidthLayout = new GridLayout();
		tabWidthLayout.marginHeight = 5;
		tabWidthLayout.marginWidth = 0;
		tabWidthLayout.numColumns = 3;
		tabWidthLayout.marginLeft = 4;
		tabWidthLayout.marginRight = 4;

		Group tabWidthGroup = new Group(folder, SWT.NULL);
		tabWidthGroup.setText("Standard Tab Width");
		tabWidthGroup.setLayout(tabWidthLayout);

		GridData tabWidthData = new GridData(GridData.FILL_HORIZONTAL);
		tabWidthGroup.setLayoutData(tabWidthData);

		fTabWidth.doFillIntoGrid(tabWidthGroup, 3);
		fTabWidth.getTextControl(null).addListener(SWT.Verify, new NumberOnlyVerifyListener());

		createDialogFieldWithInfoLink(
				folder,
				fFileExtension,
				"File Extensions",
				"Extensions are sperarated by a comma. If empty, all files associated with the <a>PHP Content Type</a> will be checked.",
				"org.eclipse.ui.preferencePages.ContentTypes");

		createDialogFieldsWithInfoText(folder, new DialogField[] { fIgnorePattern }, "Ignore Directories and Files",
				new String[] { "Patterns are separated by a comma (* = any string, ?= any character)" });

		createDialogFieldsWithInfoText(folder, new DialogField[] { fIgnoreSniffs }, "Ignore Sniffs",
				new String[] { "Sniffs are separated by a comma" });

		unpackStandards(pearLibraryCombo.getText());
		pearLibraryCombo.addSelectionListener(new SelectionListener() {
			public void widgetSelected(SelectionEvent e) {
				unpackStandards(((Combo) e.widget).getText());
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		return markersGroup;
	}

	protected void validateSettings(Key changedKey, String oldValue, String newValue) {
		// TODO Auto-generated method stub
	}

	private void doStandardButtonPressed(int index) {
		Standard edited = null;
		if (index != IDX_ADD) {
			edited = (Standard) fStandardsList.getSelectedElements().get(0);
		}
		if (index == IDX_ADD || index == IDX_EDIT) {
			StandardInputDialog dialog = new StandardInputDialog(getShell(), edited, fStandardsList.getElements());
			if (dialog.open() == Window.OK) {
				if (edited != null) {
					fStandardsList.replaceElement(edited, dialog.getResult());
				} else {
					fStandardsList.addElement(dialog.getResult());
				}
			}
		}
	}

	protected final void updateModel(DialogField field) {
		if (field == fStandardsList) {
			StringBuffer customStandards = new StringBuffer();
			StringBuffer customPaths = new StringBuffer();
			StringBuffer checkedStandards = new StringBuffer();

			List<Standard> list = fStandardsList.getElements();
			for (int i = 0; i < list.size(); i++) {
				Standard elem = list.get(i);
				if (elem.custom) {
					if (customStandards.length() > 0) {
						customStandards.append(';');
						customPaths.append(';');
					}

					customStandards.append(elem.name);
					customPaths.append(elem.path);
				}
			}

			list = fStandardsList.getCheckedElements();
			for (int i = 0; i < list.size(); i++) {
				Standard elem = list.get(i);
				if (checkedStandards.length() > 0) {
					checkedStandards.append(';');
				}
				checkedStandards.append(elem.name);
			}

			setValue(PREF_CUSTOM_STANDARD_NAMES, customStandards.toString());
			setValue(PREF_CUSTOM_STANDARD_PATHS, customPaths.toString());
			setValue(PREF_ACTIVE_STANDARDS, checkedStandards.toString());

			// delete old entries
			setValue(PREF_DEFAULT_STANDARD_NAME, null);
			setValue(PREF_DEFAULT_STANDARD_PATH, null);

			validateSettings(PREF_CUSTOM_STANDARD_NAMES, null, null);
		}
	}

	protected boolean processChanges(IWorkbenchPreferenceContainer container) {
		clearProjectLauncherCache(PHPCodeSniffer.QUALIFIED_NAME);

		int tabWidth = 0;
		try {
			tabWidth = Integer.parseInt(fTabWidth.getText());
		} catch (Exception e) {
		}
		setValue(PREF_DEFAULT_TAB_WITH, "" + tabWidth);
		setValue(PREF_FILE_EXTENSIONS, "" + fFileExtension.getText());
		setValue(PREF_IGNORE_PATTERN, fIgnorePattern.getText());
		setValue(PREF_IGNORE_SNIFFS, fIgnoreSniffs.getText());

		return super.processChanges(container);
	}

	public void useProjectSpecificSettings(boolean enable) {
		super.useProjectSpecificSettings(enable);
		fStandardsList.setEnabled(enable);
	}

	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		String title = "CodeSniffer Settings Changed";
		String message;
		if (fProject == null) {
			message = "The settings have changed. A full rebuild is required for changes to take effect. Execute the full build now?";
		} else {
			message = "The settings have changed. A rebuild of the project is required for changes to take effect. Build the project now?";
		}
		return new String[] { title, message };
	}

	protected final static Key getCodeSnifferKey(String key) {
		return getKey(PHPCodeSnifferPlugin.PLUGIN_ID, key);
	}

	protected Key getPHPExecutableKey() {
		return PREF_PHP_EXECUTABLE;
	}

	protected Key getDebugPrintOutputKey() {
		return PREF_DEBUG_PRINT_OUTPUT;
	}

	protected Key getPEARLibraryKey() {
		return PREF_PEAR_LIBRARY;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.jdt.internal.ui.preferences.OptionsConfigurationBlock#
	 * updateControls()
	 */

	protected void updateControls() {
		unpackStandards(pearLibraryCombo.getText());
		unpackTabWidth();
		unpackFileExtensions();
		unpackIgnorePattern();
		unpackIgnoreSniffs();
	}

	private void unpackTabWidth() {
		String tabWidth = getValue(PREF_DEFAULT_TAB_WITH);
		if (tabWidth != null)
			fTabWidth.setText(tabWidth);
	}

	private void unpackFileExtensions() {
		String fileExtensions = getValue(PREF_FILE_EXTENSIONS);
		if (fileExtensions != null)
			fFileExtension.setText(fileExtensions);
	}

	private void unpackIgnorePattern() {
		String ignorePattern = getValue(PREF_IGNORE_PATTERN);
		if (ignorePattern != null)
			fIgnorePattern.setText(ignorePattern);
	}

	private void unpackIgnoreSniffs() {
		String ignoreSniffs = getValue(PREF_IGNORE_SNIFFS);
		if (ignoreSniffs != null)
			fIgnoreSniffs.setText(ignoreSniffs);
	}

	private void unpackStandards(String libName) {
		String activeStandards = getValue(PREF_ACTIVE_STANDARDS);
		ArrayList<String> activeList = new ArrayList<String>();

		if (activeStandards != null) {
			for (String active : activeStandards.split(";"))
				activeList.add(active);
		} else {
			String defaultName = getValue(PREF_DEFAULT_STANDARD_NAME);
			if (defaultName != null)
				activeList.add(defaultName);
		}

		String customStandardPrefs = getValue(PREF_CUSTOM_STANDARD_NAMES);

		String[] standards = PHPCodeSnifferPlugin.getDefault().getCodeSnifferStandards(libName);
		String[] customStandards = {};
		String[] customPaths = {};

		if (customStandardPrefs != null) {
			customStandards = getTokens(customStandardPrefs, ";"); //$NON-NLS-1$

			String customPathPrefs = getValue(PREF_CUSTOM_STANDARD_PATHS);
			customPaths = getTokens(customPathPrefs, ";"); //$NON-NLS-1$
		}

		ArrayList<Standard> elements = new ArrayList<Standard>(standards.length + customStandards.length);
		ArrayList<Standard> checkedElements = new ArrayList<Standard>(activeList.size());

		// CodeSniffer own standards
		for (int i = 0; i < standards.length; i++) {
			Standard standard = new Standard();
			standard.name = standards[i].trim();
			standard.custom = false;
			standard.path = "";

			elements.add(standard);
			if (activeList.contains(standard.name))
				checkedElements.add(standard);
		}

		// Custom standards
		for (int i = 0; i < customStandards.length; i++) {
			Standard standard = new Standard();
			standard.name = customStandards[i].trim();
			standard.custom = true;
			standard.path = customPaths[i].trim();

			elements.add(standard);
			if (activeList.contains(standard.name))
				checkedElements.add(standard);
		}

		fStandardsList.setElements(elements);
		fStandardsList.setCheckedElements(checkedElements);
	}
}