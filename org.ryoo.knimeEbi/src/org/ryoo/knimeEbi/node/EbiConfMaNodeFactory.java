package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfMaNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfMaNodeFactory() {
		super("Ebi conformance markovian", "Compute the conformance between two stochastic languages using a stochastic Markovian abstraction, which represents languages based on the expected frequency of subtraces to handle partially matching traces.", "fraction", BufferedDataTable.TYPE);
	}
}