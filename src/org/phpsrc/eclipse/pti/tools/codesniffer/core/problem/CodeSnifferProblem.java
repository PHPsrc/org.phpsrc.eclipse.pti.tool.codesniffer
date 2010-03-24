package org.phpsrc.eclipse.pti.tools.codesniffer.core.problem;

import org.eclipse.dltk.compiler.problem.DefaultProblem;

public class CodeSnifferProblem extends DefaultProblem {

	protected String source;

	public CodeSnifferProblem(String originatingFileName, String message, int id, String[] stringArguments,
			int severity, int startPosition, int endPosition, int line, int column, String source) {
		super(originatingFileName, message, id, stringArguments, severity, startPosition, endPosition, line, column);
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
				&& p.getSourceLineNumber() == this.getSourceLineNumber() && p.getColumn() == this.getColumn()
				&& p.getMessage().equals(this.getMessage());
	}
}
