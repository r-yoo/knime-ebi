package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfUemscNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfUemscNodeFactory() {
		super("Ebi conformance unit-earth-movers", "Compute unit-earth movers' stochastic conformance, which is 1 - the total variation distance.", "fraction", BufferedDataTable.TYPE);
	}
}