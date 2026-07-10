package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiProbTracNodeFactory extends EbiDefaultNodeFactory {
	public EbiProbTracNodeFactory() {
		super("Ebi sample folds", "Compute the probability of a trace in a stochastic model.", "event log", BufferedDataTable.TYPE);
	}
}