package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfCsscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfCsscSampleNodeFactory() {
		super("Ebi conformance chi-squared-sample", "Compute chi-square stochastic conformance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `cssc`.", "fraction", BufferedDataTable.TYPE);
	}
}