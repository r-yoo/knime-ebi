package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDinsFlwDfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsFlwDfaNodeFactory() {
		super("Ebi discover-non-stochastic flower deterministic-finite-automaton", "Discover a DFA that supports any trace with the activities of the log.", "deterministic finite automaton", BufferedDataTable.TYPE);
	}
}