/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
