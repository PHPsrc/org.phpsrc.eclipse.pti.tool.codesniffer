/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences;

import org.phpsrc.eclipse.pti.library.pear.core.preferences.AbstractPEARPHPToolPreferences;

public class PHPCodeSnifferPreferences extends AbstractPEARPHPToolPreferences {
	protected Standard[] standards;
	protected int tabWidth;
	protected String ignorePattern;
	protected String[] ignoreSniffs;
	protected String[] fileExtensions;

	public PHPCodeSnifferPreferences(String phpExecutable, boolean printOutput, String pearLibraryName,
			Standard[] standards, int tabWidth, String[] fileExtensions, String ignorePattern, String[] ignoreSniffs) {
		super(phpExecutable, printOutput, pearLibraryName);
		this.standards = standards;
		this.tabWidth = tabWidth;
		this.fileExtensions = fileExtensions;
		if (ignorePattern != null && ignorePattern.length() > 0)
			this.ignorePattern = ignorePattern;
		else
			this.ignorePattern = null;
		this.ignoreSniffs = ignoreSniffs;
	}

	public Standard[] getStandards() {
		return standards;
	}

	public int getTabWidth() {
		return tabWidth;
	}

	public String[] getFileExtensions() {
		return fileExtensions;
	}

	public String getIgnorePattern() {
		return ignorePattern;
	}

	public String[] getIgnoreSniffs() {
		return ignoreSniffs;
	}
}