package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfUemscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfUemscSampleNodeFactory() {
		super("Ebi conformance unit-earth-movers-sample", "Compute unit-earth movers' stochastic conformance, which is 1 - the total variation distance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `uemsc`.", "fraction", BufferedDataTable.TYPE);
	}
}