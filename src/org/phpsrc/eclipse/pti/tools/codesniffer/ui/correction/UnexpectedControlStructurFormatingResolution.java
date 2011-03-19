/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.ui.correction;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.ui.Logger;

public class UnexpectedControlStructurFormatingResolution extends AbstractResolution {

	public String getDescription() {
		return "Change control structure formating";
	}

	public Image getImage() {
		return PHPToolCorePlugin.getDefault().getImageRegistry().get(PHPToolCorePlugin.IMG_ACTIVITY);
	}

	public String getLabel() {
		return "Change control structure formating";
	}

	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				// Expected "if (...) {\n"; found "if(...)\n        {\n"
				Pattern p = Pattern.compile("Expected \"([a-z]+.*)\"; found \"(.*)\"");
				Matcher m = p.matcher(msg);
				if (m.matches()) {
					String expected = prepareString(m.group(1));
					String found = prepareString(m.group(2));

					IDocument doc = this.getDocument(marker);
					IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

					int posBraceStart = 0, posBraceEnd = 0, lineStart = 0, bodyStart = 0, braceCount = 0;

					int pos = line.getOffset();
					while (posBraceStart == 0 || posBraceEnd == 0 || bodyStart == 0) {
						char c = doc.getChar(pos);
						if (lineStart == 0 && (c != '\t' && c != ' ')) {
							lineStart = pos;
						} else if (c == '(') {
							if (braceCount == 0)
								posBraceStart = pos;

							braceCount++;
						} else if (c == ')') {
							braceCount--;

							if (braceCount == 0)
								posBraceEnd = pos;
						} else if (braceCount == 0 && c == '{') {
							bodyStart = pos;
						}

						pos++;
					}

					String replace = expected.substring(0, expected.indexOf('('));
					replace += doc.get(posBraceStart, posBraceEnd - posBraceStart);
					replace += expected.substring(expected.lastIndexOf(')'), expected.length());

					doc.replace(lineStart, bodyStart - lineStart + 2, replace);

					marker.delete();
				}
			}

		} catch (CoreException e) {
			Logger.logException(e);
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
	}
}
