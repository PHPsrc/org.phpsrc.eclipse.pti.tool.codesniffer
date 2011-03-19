/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.ui.actions;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.phpsrc.eclipse.pti.tools.codesniffer.core.jobs.ValidationJob;
import org.phpsrc.eclipse.pti.ui.actions.ResourceAction;

public class ValidateResourcesAction extends ResourceAction {

	
	public void run(IAction arg0) {
		IResource[] resources = getSelectedResources();
		if (resources.length > 0) {
			ValidationJob job = new ValidationJob(resources);
			job.setUser(false);
			job.schedule();
		}
	}
}
