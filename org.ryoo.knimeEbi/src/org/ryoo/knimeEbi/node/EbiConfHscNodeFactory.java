package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfHscNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfHscNodeFactory() {
		super("Ebi conformance hellinger", "Compute Hellinger stochastic conformance, which is 1 - the Hellinger distance.", "fraction", BufferedDataTable.TYPE);
	}
}