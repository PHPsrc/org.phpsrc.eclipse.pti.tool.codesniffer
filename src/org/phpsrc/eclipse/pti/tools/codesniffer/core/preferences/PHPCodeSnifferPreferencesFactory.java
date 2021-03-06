/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.phpsrc.eclipse.pti.tools.codesniffer.PHPCodeSnifferPlugin;
import org.phpsrc.eclipse.pti.tools.codesniffer.ui.preferences.PHPCodeSnifferPreferenceNames;

public class PHPCodeSnifferPreferencesFactory {
	public static PHPCodeSnifferPreferences factory(IFile file) {
		return factory(file.getProject());
	}

	public static PHPCodeSnifferPreferences factory(IResource resource) {
		return factory(resource.getProject());
	}

	public static PHPCodeSnifferPreferences factory(IProject project) {
		Preferences prefs = PHPCodeSnifferPlugin.getDefault().getPluginPreferences();

		String phpExe = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE);

		// Check first the standard path. Is it not empty we have a custom
		// standard, so we must use the path instead of the name.
		String defaultStandard = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH);
		String activeStandards = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_ACTIVE_STANDARDS);
		String pearLibraryName = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_PEAR_LIBRARY);
		String customStandardNames = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_NAMES);
		String customStandardPaths = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_PATHS);

		int tabWidth = prefs.getInt(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH);
		String fileExtensions = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_FILE_EXTENSIONS);
		boolean printOutput = prefs.getBoolean(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT);
		String ignorePattern = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_IGNORE_PATTERN);
		String ignoreSniffs = prefs.getString(PHPCodeSnifferPreferenceNames.PREF_IGNORE_SNIFFS);

		IScopeContext[] preferenceScopes = createPreferenceScopes(project);
		if (preferenceScopes[0] instanceof ProjectScope) {
			IEclipsePreferences node = preferenceScopes[0].getNode(PHPCodeSnifferPlugin.PLUGIN_ID);
			if (node != null) {
				phpExe = node.get(PHPCodeSnifferPreferenceNames.PREF_PHP_EXECUTABLE, phpExe);

				defaultStandard = node.get(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_STANDARD_PATH, defaultStandard);
				activeStandards = node.get(PHPCodeSnifferPreferenceNames.PREF_ACTIVE_STANDARDS, activeStandards);
				pearLibraryName = node.get(PHPCodeSnifferPreferenceNames.PREF_PEAR_LIBRARY, pearLibraryName);
				customStandardNames = node.get(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_NAMES,
						customStandardNames);
				customStandardPaths = node.get(PHPCodeSnifferPreferenceNames.PREF_CUSTOM_STANDARD_PATHS,
						customStandardPaths);

				tabWidth = node.getInt(PHPCodeSnifferPreferenceNames.PREF_DEFAULT_TAB_WITH, tabWidth);
				fileExtensions = node.get(PHPCodeSnifferPreferenceNames.PREF_FILE_EXTENSIONS, fileExtensions);
				printOutput = node.getBoolean(PHPCodeSnifferPreferenceNames.PREF_DEBUG_PRINT_OUTPUT, printOutput);
				ignorePattern = node.get(PHPCodeSnifferPreferenceNames.PREF_IGNORE_PATTERN, ignorePattern);
				ignoreSniffs = node.get(PHPCodeSnifferPreferenceNames.PREF_IGNORE_SNIFFS, ignoreSniffs);
			}
		}

		String[] fileExtensionsList = fileExtensions == null || fileExtensions.length() == 0 ? new String[0]
				: fileExtensions.split(" *, *");

		String[] activeList = new String[0];
		if (activeStandards != null) {
			activeList = activeStandards.split(";");
		} else if (defaultStandard != null) {
			activeList = new String[] { defaultStandard };
		}

		String[] customNameList = new String[0];
		String[] customPathList = new String[0];
		if (customStandardNames != null && customStandardPaths != null) {
			customNameList = customStandardNames.split(";");
			customPathList = customStandardPaths.split(";");
		}

		ArrayList<Standard> standards = new ArrayList<Standard>(activeList.length);

		for (String s : activeList) {
			Standard standard = new Standard();
			standard.name = s;

			for (int i = 0; i < customNameList.length; i++) {
				if (customNameList[i].equals(s)) {
					standard.path = customPathList[i];
					standard.custom = true;
					break;
				}
			}

			standards.add(standard);
		}

		return new PHPCodeSnifferPreferences(phpExe, printOutput, pearLibraryName, standards.toArray(new Standard[0]),
				tabWidth, fileExtensionsList, ignorePattern, ignoreSniffs == null || ignoreSniffs.length() == 0 ? null
						: ignoreSniffs.split(" *, *"));
	}

	protected static IScopeContext[] createPreferenceScopes(IProject project) {
		if (project != null) {
			return new IScopeContext[] { new ProjectScope(project), new InstanceScope(), new DefaultScope() };
		}
		return new IScopeContext[] { new InstanceScope(), new DefaultScope() };
	}
}
