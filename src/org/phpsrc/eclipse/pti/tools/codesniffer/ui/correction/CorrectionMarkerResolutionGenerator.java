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
