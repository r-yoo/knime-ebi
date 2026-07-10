package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfEmscNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfEmscNodeFactory() {
		super("Ebi conformance earth-movers", "Compute Earth mover's stochastic conformance, which is 1 - the Wasserstein distance.", "fraction", BufferedDataTable.TYPE);
	}
}