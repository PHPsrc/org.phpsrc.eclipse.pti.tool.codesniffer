/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.ui.correction;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.phpsrc.eclipse.pti.ui.Logger;

public class CorrectionMarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			String msg = (String) marker.getAttribute(IMarker.MESSAGE);

			IMarkerResolution2 resolution = findResolution(msg);
			if (resolution != null)
				return new IMarkerResolution[] { resolution };

		} catch (CoreException e) {
			Logger.logException(e);
		}

		return null;
	}

	public boolean hasResolutions(IMarker marker) {
		String msg;
		try {
			msg = (String) marker.getAttribute(IMarker.MESSAGE);
			return findResolution(msg) != null;
		} catch (CoreException e) {
			Logger.logException(e);
		}

		return false;
	}

	protected IMarkerResolution2 findResolution(String msg) {
		if (msg.startsWith("End of line character is invalid;")) {
			return new InvalidEndOfLineCharacterResolution();
			// } else if (msg.startsWith("Expected \"") && msg.endsWith("\"")) {
			// return new UnexpectedControlStructurFormatingResolution();
		} else if (msg.startsWith("String \"")
				&& msg.endsWith("does not require double quotes; use single quotes instead")) {
			return new ReplaceDoubleQuotesWithSingleQuotesResolution();
		} else if (msg.equals("Whitespace found at end of line")) {
			return new RemoveWhitespaceAtEndOfLineResolution();
		} else if (msg.startsWith("Expected //")) {
			return new AddingMissingCommentResolution();
		}

		// Expected //end doSomething()

		return null;
	}
}
