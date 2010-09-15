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
