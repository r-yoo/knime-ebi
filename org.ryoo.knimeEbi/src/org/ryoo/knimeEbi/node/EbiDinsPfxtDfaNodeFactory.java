package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDinsPfxtDfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsPfxtDfaNodeFactory() {
		super("Ebi discover-non-stochastic prefix-tree deterministic-finite-automaton", "Discover a DFA that is a prefix tree of the log.", "deterministic finite automaton", BufferedDataTable.TYPE);
	}
}