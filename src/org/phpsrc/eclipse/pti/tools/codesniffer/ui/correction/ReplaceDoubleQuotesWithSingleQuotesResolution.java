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
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.graphics.Image;
import org.phpsrc.eclipse.pti.core.PHPToolCorePlugin;
import org.phpsrc.eclipse.pti.ui.Logger;

public class ReplaceDoubleQuotesWithSingleQuotesResolution extends AbstractResolution {

	public String getDescription() {
		return "Replace double quotes with single quotes.";
	}

	public Image getImage() {
		return PHPToolCorePlugin.getDefault().getImageRegistry().get(PHPToolCorePlugin.IMG_ACTIVITY);
	}

	public String getLabel() {
		return "Replace double quotes with single quotes";
	}

	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				// String "..." does not require double quotes; use single
				// quotes instead
				Pattern p = Pattern.compile(
						"String \"(.*)\" does not require double quotes; use single quotes instead", Pattern.MULTILINE
								| Pattern.DOTALL);
				Matcher m = p.matcher(msg);
				if (m.matches()) {
					String found = prepareString(m.group(1));

					IDocument doc = this.getDocument(marker);
					IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

					FindReplaceDocumentAdapter findReplace = new FindReplaceDocumentAdapter(doc);

					// codesniffer converts tabs to spaces so check for any
					// whitespace
					String search = "\\Q" + found.replaceAll("[\\s]+", "\\\\E[\\\\s]+\\\\Q") + "\\E";
					IRegion region = findReplace
							.find(line.getOffset(), "\"(" + search + ")\"", true, true, false, true);
					if (region != null) {
						findReplace.replace("'$1'", true);
						marker.delete();
					}
				}
			}
		} catch (CoreException e) {
			Logger.logException(e);
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
	}

}
