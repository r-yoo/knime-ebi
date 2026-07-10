package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConvSnfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvSnfaNodeFactory() {
		super("Ebi convert stochastic-nondeterministic-finite-automaton", "Convert an object to a stochastic nondeterministic finite automaton.", "stochastic non-deterministic finite automaton", BufferedDataTable.TYPE);
	}
}