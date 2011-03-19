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

public class AddingMissingCommentResolution extends AbstractResolution {

	public String getDescription() {
		return "A expected comment. An existing coment will be replaced.";
	}

	public Image getImage() {
		return PHPToolCorePlugin.getDefault().getImageRegistry().get(PHPToolCorePlugin.IMG_ACTIVITY);
	}

	public String getLabel() {
		return "Add expected comment";
	}

	public void run(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);
			Integer lineNumber = (Integer) marker.getAttribute(IMarker.LINE_NUMBER);

			if (lineNumber != null && lineNumber.intValue() > 0) {
				// Expected //end class
				Pattern p = Pattern.compile("Expected //(.*)");
				Matcher m = p.matcher(msg);
				if (m.matches()) {
					String expected = prepareString(m.group(1));

					IDocument doc = this.getDocument(marker);
					IRegion line = doc.getLineInformation(lineNumber.intValue() - 1);

					// replace existing comments
					int startExistingComment = line.getLength();
					for (int i = 0; i < line.getLength(); i++) {
						if (doc.getChar(line.getOffset() + i) == '/' && doc.getChar(line.getOffset() + i + 1) == '/') {
							startExistingComment = i;
							break;
						}
					}

					doc.replace(line.getOffset() + startExistingComment, line.getLength() - startExistingComment, "//"
							+ expected);
				}
			}

		} catch (CoreException e) {
			Logger.logException(e);
		} catch (BadLocationException e) {
			Logger.logException(e);
		}
	}
}
