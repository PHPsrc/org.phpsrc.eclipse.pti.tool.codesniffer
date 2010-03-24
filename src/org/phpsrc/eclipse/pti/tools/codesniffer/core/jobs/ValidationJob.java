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

package org.phpsrc.eclipse.pti.tools.codesniffer.core.jobs;

import java.util.ArrayList;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.wst.validation.ValidationState;
import org.phpsrc.eclipse.pti.tools.codesniffer.validator.PHPCodeSnifferValidator;
import org.phpsrc.eclipse.pti.ui.Logger;

public class ValidationJob extends Job {

	protected IResource[] resources;

	public ValidationJob(IResource[] resources) {
		super("PHP CodeSniffer");
		this.resources = resources;
	}

	
	protected IStatus run(IProgressMonitor monitor) {
		ArrayList<IFile> files = new ArrayList<IFile>(10);

		for (IResource resource : resources) {
			getFilesFromResouce(resource, files);
		}

		int count = files.size();

		monitor.beginTask("Validating ...", count);

		int completed = 0;
		for (IFile file : files.toArray(new IFile[0])) {
			monitor.setTaskName("Validating " + file.getProjectRelativePath().toString() + " (" + (completed + 1) + "/"
					+ count + ")");

			validateFile(file, monitor);
			monitor.worked(++completed);

			if (monitor.isCanceled())
				return Status.CANCEL_STATUS;
		}

		return Status.OK_STATUS;
	}

	protected void getFilesFromResouce(IResource resource, ArrayList<IFile> files) {
		if (resource instanceof IContainer) {
			getFilesFromContainer((IContainer) resource, files);
		} else if (resource instanceof IFile) {
			files.add((IFile) resource);
		}
	}

	protected void getFilesFromContainer(IContainer folder, ArrayList<IFile> files) {
		try {
			IResource[] members = folder.members();
			for (IResource member : members) {
				getFilesFromResouce(member, files);
			}
		} catch (CoreException e) {
			Logger.logException(e);
		}
	}

	protected void validateFile(final IFile file, IProgressMonitor monitor) {
		PHPCodeSnifferValidator validator = new PHPCodeSnifferValidator();
		validator.validate(file, IResourceDelta.NO_CHANGE, new ValidationState(), monitor);
	}
}
