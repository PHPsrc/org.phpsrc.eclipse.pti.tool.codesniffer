/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.ui.correction;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.php.internal.ui.editor.PHPStructuredEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

public abstract class AbstractResolution implements IMarkerResolution2 {
	protected IDocument getDocument(IMarker marker) {
		IFile file = (IFile) marker.getResource();

		IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IEditorPart part = page.getActiveEditor();

		// Get the editor's document.
		if (!(part instanceof PHPStructuredEditor)) {
			return null;
		}

		PHPStructuredEditor editor = (PHPStructuredEditor) part;
		return editor.getDocument();
	}

	protected String prepareString(String str) {
		return str.replace("\\n", "\n").replace("\\r", "\r");
	}
}
