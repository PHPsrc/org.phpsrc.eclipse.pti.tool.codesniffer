/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer;

public interface ICodeSnifferConstants {
	public static final String PLUGIN_ID = PHPCodeSnifferPlugin.PLUGIN_ID;

	public static final String PREFERENCE_PAGE_ID = PLUGIN_ID + ".preferences.CodeSnifferPreferencePage"; //$NON-NLS-1$
	public static final String PROJECT_PAGE_ID = PLUGIN_ID + ".properties.CodeSnifferPreferencePage"; //$NON-NLS-1$

	public static final String VALIDATOR_CODESNIFFER_MARKER = "org.phpsrc.eclipse.pti.tools.codesniffer.validator.phpToolCodeSnifferMarker"; //$NON-NLS-1$

}
