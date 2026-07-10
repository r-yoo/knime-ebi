package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfHscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfHscSampleNodeFactory() {
		super("Ebi conformance hellinger-sample", "Compute Hellinger stochastic conformance, which is 1 - the Hellinger distance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `hsc`.", "fraction", BufferedDataTable.TYPE);
	}
}