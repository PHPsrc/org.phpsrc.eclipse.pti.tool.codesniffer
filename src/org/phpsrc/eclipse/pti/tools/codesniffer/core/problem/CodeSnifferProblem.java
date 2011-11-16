/*******************************************************************************
 * Copyright (c) 2009, 2010, 2011 Sven Kiera
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.phpsrc.eclipse.pti.tools.codesniffer.core.problem;

import org.eclipse.dltk.compiler.problem.IProblemIdentifier;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.phpsrc.eclipse.pti.core.compiler.problem.DefaultProblem;

public class CodeSnifferProblem extends DefaultProblem {

	protected String source;

	public CodeSnifferProblem(String originatingFileName, String message,
			IProblemIdentifier id, String[] stringArguments,
			ProblemSeverity severity, int startPosition, int endPosition,
			int line, int column, String source) {
		super(originatingFileName, message, id, stringArguments, severity,
				startPosition, endPosition, line, column);
		this.source = source;
	}

	public String getSource() {
		return source;
	}

	public boolean equals(Object o) {
		if (o == null || !(o instanceof CodeSnifferProblem))
			return false;

		CodeSnifferProblem p = (CodeSnifferProblem) o;

		return p.getOriginatingFileName().equals(this.getOriginatingFileName())
				&& p.getSourceLineNumber() == this.getSourceLineNumber()
				&& p.getColumn() == this.getColumn()
				&& p.getMessage().equals(this.getMessage());
	}
}
