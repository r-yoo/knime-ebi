package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfJsscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfJsscSampleNodeFactory() {
		super("Ebi conformance jensen-shannon-sample", "Compute Jensen-Shannon stochastic conformance, which is 1 - the Jensen-Shannon distance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `jssc`.", "rootlog", BufferedDataTable.TYPE);
	}
}