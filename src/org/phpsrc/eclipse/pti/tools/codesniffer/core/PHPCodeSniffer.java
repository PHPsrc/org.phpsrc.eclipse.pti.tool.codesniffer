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
package org.phpsrc.eclipse.pti.tools.codesniffer.core;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.xerces.parsers.DOMParser;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.ProblemSeverities;
import org.phpsrc.eclipse.pti.core.launching.OperatingSystem;
import org.phpsrc.eclipse.pti.core.launching.PHPToolLauncher;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileEntry;
import org.phpsrc.eclipse.pti.core.php.inifile.INIFileUtil;
import org.phpsrc.eclipse.pti.core.php.source.ISourceFile;
import org.phpsrc.eclipse.pti.core.php.source.PHPSourceFile;
import org.phpsrc.eclipse.pti.core.tools.AbstractPHPTool;
import org.phpsrc.eclipse.pti.tools.codesniffer.PHPCodeSnifferPlugin;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferences;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferencesFactory;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.Standard;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.problem.CodeSnifferProblem;
import org.phpsrc.eclipse.pti.ui.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class PHPCodeSniffer extends AbstractPHPTool {

	public final static QualifiedName QUALIFIED_NAME = new QualifiedName(PHPCodeSnifferPlugin.PLUGIN_ID,
			"phpCodeSnifferTool");
	private static PHPCodeSniffer instance;

	protected PHPCodeSniffer() {
	}

	public static PHPCodeSniffer getInstance() {
		if (instance == null)
			instance = new PHPCodeSniffer();

		return instance;
	}

	public IProblem[] parse(IFile file) throws CoreException, IOException {
		PHPCodeSnifferPreferences prefs = PHPCodeSnifferPreferencesFactory.factory(file);
		if (canParse(prefs.getIgnorePattern(), prefs.getFileExtensions(), file)) {

			ArrayList<IProblem> list = new ArrayList<IProblem>(10);
			for (Standard standard : prefs.getStandards()) {
				IProblem[] problems = parseOutput(new PHPSourceFile(file), launchFile(file, prefs, standard), prefs);
				for (IProblem problem : problems)
					if (!list.contains(problem))
						list.add(problem);
			}

			return list.toArray(new IProblem[0]);
		} else {
			return new IProblem[0];
		}
	}

	protected boolean canParse(String ignorePattern, String[] fileExtensions, IFile file) {
		boolean can = true;

		if (fileExtensions != null && fileExtensions.length > 0) {
			can = false;

			String fileName = file.getName();
			for (String extension : fileExtensions) {
				if (fileName.endsWith("." + extension)) {
					can = true;
					break;
				}

			}
		}

		if (can && ignorePattern != null && ignorePattern.length() > 0) {
			String filePath = file.getFullPath().toPortableString();

			String[] patterns = ignorePattern.split(",");
			for (String pattern : patterns) {
				pattern = pattern.trim();
				if (pattern.length() > 0) {
					pattern = pattern.replace("\\", "/").replace(".", "\\.").replace("*", ".*").replace("?", ".?");
					Pattern p = Pattern.compile(pattern);
					if (p.matcher(filePath).matches()) {
						can = false;
						break;
					}
				}
			}
		}

		return can;
	}

	protected String launchFile(IFile file, PHPCodeSnifferPreferences prefs, Standard standard) {

		String output = null;
		try {
			PHPToolLauncher launcher = getPHPToolLauncher(file.getProject(), prefs, standard);
			output = launcher.launch(file);
		} catch (Exception e) {
			Logger.logException(e);
		}

		if (output == null)
			return "";
		else
			return output;
	}

	protected IProblem[] parseOutput(ISourceFile file, String output, PHPCodeSnifferPreferences prefs) {
		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		try {
			if (output.length() > 0) {

				int tabWidth = 0;
				if (prefs != null)
					tabWidth = prefs.getTabWidth();

				int xmlStart = output.indexOf("<?xml");
				if (xmlStart >= 0) {
					output = output.substring(xmlStart).trim();

					DOMParser parser = new DOMParser();
					parser.parse(new InputSource(new StringReader(output)));

					Document doc = parser.getDocument();
					problems.addAll(createProblemMarker(file, doc.getElementsByTagName("error"),
							ProblemSeverities.Error, tabWidth, prefs.getIgnoreSniffs()));
					problems.addAll(createProblemMarker(file, doc.getElementsByTagName("warning"),
							ProblemSeverities.Warning, tabWidth, prefs.getIgnoreSniffs()));
				}
			}
		} catch (Exception e) {
			Logger.logException(e);
		}

		return problems.toArray(new IProblem[0]);
	}

	protected ArrayList<IProblem> createProblemMarker(ISourceFile file, NodeList list, int type, int tabWidth,
			String[] ignoreSniffs) {
		if (tabWidth <= 0)
			tabWidth = 2;

		ArrayList<IProblem> problems = new ArrayList<IProblem>();

		int length = list.getLength();
		for (int i = 0; i < length; i++) {
			Node item = list.item(i);

			NamedNodeMap attr = item.getAttributes();

			String source = attr.getNamedItem("source").getTextContent();
			if (ignoreSniffs != null && ignoreSniffs.length > 0) {
				boolean ignoreSniff = false;
				for (int s = 0; s < ignoreSniffs.length; s++) {
					if (ignoreSniffs[s].equals(source)) {
						ignoreSniff = true;
						break;
					}
				}
				if (ignoreSniff)
					continue;
			}

			int lineNr = Integer.parseInt(attr.getNamedItem("line").getTextContent());
			int column = Integer.parseInt(attr.getNamedItem("column").getTextContent());
			int lineStart = file.lineStart(lineNr);

			// calculate real column with tabs
			if (column > 1)
				lineStart += (column - 1 - (file.lineStartTabCount(lineNr) * (tabWidth - 1)));

			problems.add(new CodeSnifferProblem(file.getFile().getFullPath().toOSString(), item.getTextContent(),
					IProblem.Syntax, new String[0], type, lineStart, file.lineEnd(lineNr), lineNr, column, source));
		}

		return problems;
	}

	protected PHPToolLauncher getPHPToolLauncher(IProject project, PHPCodeSnifferPreferences prefs, Standard standard) {
		PHPToolLauncher launcher;
		try {
			launcher = (PHPToolLauncher) project.getSessionProperty(QUALIFIED_NAME);
			if (launcher != null) {
				launcher.setCommandLineArgs(getCommandLineArgs(standard, prefs.getTabWidth()));
				return launcher;
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}

		launcher = new PHPToolLauncher(QUALIFIED_NAME, getPHPExecutable(prefs.getPhpExecutable()), getScriptFile(),
				getCommandLineArgs(standard, prefs.getTabWidth()), getPHPINIEntries(prefs, project, standard));

		launcher.setPrintOuput(prefs.isPrintOutput());

		try {
			project.setSessionProperty(QUALIFIED_NAME, launcher);
		} catch (CoreException e) {
			Logger.logException(e);
		}

		return launcher;
	}

	private INIFileEntry[] getPHPINIEntries(PHPCodeSnifferPreferences prefs, IProject project, Standard standard) {

		IPath[] includePaths = PHPCodeSnifferPlugin.getDefault().getPluginIncludePaths(project);

		if (standard.custom) {
			IPath[] tmpIncludePaths = new IPath[includePaths.length + 2];
			System.arraycopy(includePaths, 0, tmpIncludePaths, 2, includePaths.length);
			tmpIncludePaths[0] = new Path(standard.path);
			tmpIncludePaths[1] = new Path(standard.path).removeLastSegments(1);
			includePaths = tmpIncludePaths;
		}

		INIFileEntry[] entries;
		if (includePaths.length > 0) {
			entries = new INIFileEntry[] { INIFileUtil.createIncludePathEntry(includePaths) };
		} else {
			entries = new INIFileEntry[0];
		}

		return entries;
	}

	public static IPath getScriptFile() {
		return PHPCodeSnifferPlugin.getDefault().resolvePluginResource("/php/tools/phpcs.php");
	}

	private String getCommandLineArgs(Standard standard, int tabWidth) {

		String args = "--report=xml --standard="
				+ (standard.custom ? OperatingSystem.escapeShellFileArg(standard.path) : OperatingSystem
						.escapeShellArg(standard.name));

		if (tabWidth > 0)
			args += " --tab-width=" + tabWidth;

		return args + " " + PHPToolLauncher.COMMANDLINE_PLACEHOLDER_FILE;
	}
}
