/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer;

import java.io.File;
import java.io.FileFilter;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.osgi.framework.BundleContext;
import org.phpsrc.eclipse.pti.core.AbstractPHPToolPlugin;
import org.phpsrc.eclipse.pti.library.pear.PHPLibraryPEARPlugin;
import org.phpsrc.eclipse.pti.library.pear.core.preferences.PEARPreferences;
import org.phpsrc.eclipse.pti.library.pear.core.preferences.PEARPreferencesFactory;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferences;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferencesFactory;

/**
 * The activator class controls the plug-in life cycle
 */
public class PHPCodeSnifferPlugin extends AbstractPHPToolPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.phpsrc.eclipse.pti.tools.codesniffer";
	private static final String DEFAULT_PEAR_LIB_PATH = "/php/library/PEAR/PHP/CodeSniffer/Standards";

	// The shared instance
	private static PHPCodeSnifferPlugin plugin;

	/**
	 * The constructor
	 */
	public PHPCodeSnifferPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */

	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */

	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static PHPCodeSnifferPlugin getDefault() {
		return plugin;
	}

	public String[] getCodeSnifferStandards(String lib) {
		String libPath = null;
		if (lib != null && !"".equals(lib) && !lib.startsWith("<")) {
			PEARPreferences prefs = PEARPreferencesFactory.factoryByName(lib);
			if (prefs != null)
				libPath = prefs.getLibraryPath();
		}

		File standardPath = null;
		if (libPath != null) {
			standardPath = new File(libPath + "/PHP/CodeSniffer/Standards");
			if (!standardPath.exists())
				standardPath = new File(libPath + "/PEAR/PHP/CodeSniffer/Standards");
		} else {
			standardPath = new File(PHPLibraryPEARPlugin.getDefault()
					.resolvePluginResource(DEFAULT_PEAR_LIB_PATH).toPortableString());
		}

		if (standardPath == null || !standardPath.exists())
			return new String[0];

		File[] dirs = standardPath.listFiles(new FileFilter() {
			public boolean accept(File f) {
				return f.isDirectory() && !f.getName().startsWith(".")
						&& !f.getName().toLowerCase().equals("generic");
			}
		});

		if (dirs == null)
			return new String[0];

		String[] standards = new String[dirs.length];
		for (int i = 0; i < dirs.length; i++) {
			standards[i] = dirs[i].getName();
		}

		return standards;
	}

	public IPath[] getPluginIncludePaths(IProject project) {
		PHPCodeSnifferPreferences prefs = PHPCodeSnifferPreferencesFactory.factory(project);
		IPath[] pearPaths = PHPLibraryPEARPlugin.getDefault().getPluginIncludePaths(
				prefs.getPearLibraryName());

		IPath[] includePaths = new IPath[pearPaths.length + 1];
		includePaths[0] = resolvePluginResource("/php/tools");
		System.arraycopy(pearPaths, 0, includePaths, 1, pearPaths.length);

		return includePaths;
	}
}
