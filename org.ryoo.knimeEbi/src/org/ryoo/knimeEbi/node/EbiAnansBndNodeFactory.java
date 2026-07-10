package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAnansBndNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnansBndNodeFactory() {
		super("Ebi analyse-non-stochastic bounded", "Compute whether the model has a bounded state space.For Petri nets, a coverability graph is computed.For other types of models, `true' is returned.", "bool", BufferedDataTable.TYPE);
	}
}