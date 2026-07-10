package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConvSdfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvSdfaNodeFactory() {
		super("Ebi convert stochastic-deterministic-finite-automaton", "Convert an object to a stochastic deterministic finite automaton.", "stochastic deterministic finite automaton", BufferedDataTable.TYPE);
	}
}