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

public class InvalidEndOfLineCharacterResolution extends AbstractResolution {

	public String getDescription() {
		return "Change end of line character.";
	}

	public Image getImage() {
		return PHPToolCorePlugin.getDefault().getImageRegistry().get(PHPToolCorePlugin.IMG_ACTIVITY);
	}

	public String getLabel() {
		return "Change end of line character";
	}

	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				Pattern p = Pattern.compile("End of line character is invalid; expected \"(.*)\" but found \"(.*)\"");
				Matcher m = p.matcher(msg);
				if (m.matches()) {
					String expected = prepareString(m.group(1));
					String found = prepareString(m.group(2));

					IDocument doc = this.getDocument(marker);
					IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

					if (found.equals(doc.getLineDelimiter(lineNumber.intValue() - 1))) {
						doc.replace(line.getOffset() + line.getLength(), found.length(), expected);

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
