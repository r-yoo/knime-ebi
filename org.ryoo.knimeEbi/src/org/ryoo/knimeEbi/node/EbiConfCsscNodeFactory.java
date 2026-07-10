package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfCsscNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfCsscNodeFactory() {
		super("Ebi conformance chi-squared", "Compute chi-square stochastic conformance.", "fraction", BufferedDataTable.TYPE);
	}
}