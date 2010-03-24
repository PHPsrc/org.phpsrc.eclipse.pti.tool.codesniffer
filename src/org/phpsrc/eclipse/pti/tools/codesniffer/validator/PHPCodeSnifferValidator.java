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
package org.phpsrc.eclipse.pti.tools.codesniffer.validator;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.wst.validation.AbstractValidator;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.phpsrc.eclipse.pti.core.PHPToolkitUtil;
import org.phpsrc.eclipse.pti.tools.codesniffer.ICodeSnifferConstants;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.PHPCodeSniffer;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferences;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.preferences.PHPCodeSnifferPreferencesFactory;
import org.phpsrc.eclipse.pti.ui.Logger;

public class PHPCodeSnifferValidator extends AbstractValidator {

	public ValidationResult validate(IResource resource, int kind, ValidationState state, IProgressMonitor monitor) {
		// process only PHP files
		if (resource.getType() != IResource.FILE) {
			return null;
		}

		PHPCodeSnifferPreferences prefs = PHPCodeSnifferPreferencesFactory.factory(resource);
		String[] fileExtensions = prefs.getFileExtensions();
		if ((fileExtensions == null || fileExtensions.length == 0)) {
			if (!(PHPToolkitUtil.isPhpFile((IFile) resource)))
				return null;
		} else {
			String fileName = resource.getName();
			boolean allowed = false;
			for (String ext : fileExtensions) {
				if (fileName.endsWith("." + ext)) {
					allowed = true;
					break;
				}
			}

			if (!allowed)
				return null;
		}

		ValidationResult result = new ValidationResult();
		validateFile(result, (IFile) resource, kind);
		return result;
	}

	protected void validateFile(ValidationResult result, IFile file, int kind) {
		// remove the markers currently existing for this resource
		// in case of project/folder, the markers are deleted recursively
		try {
			file.deleteMarkers(ICodeSnifferConstants.VALIDATOR_CODESNIFFER_MARKER, false, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
		}

		PHPCodeSniffer cs = PHPCodeSniffer.getInstance();
		IProblem[] problems;
		try {
			problems = cs.parse(file);
			for (IProblem problem : problems) {
				IMarker marker = file.createMarker(ICodeSnifferConstants.VALIDATOR_CODESNIFFER_MARKER);
				marker.setAttribute(IMarker.PROBLEM, true);
				marker.setAttribute(IMarker.LINE_NUMBER, problem.getSourceLineNumber());

				if (problem.isWarning()) {
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
					result.incrementWarning(1);
				} else {
					marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
					result.incrementError(1);
				}
				marker.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
				marker.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
				marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
			}
		} catch (CoreException e) {
			Logger.logException(e);
		} catch (IOException e) {
			Logger.logException(e);
		}
	}
}