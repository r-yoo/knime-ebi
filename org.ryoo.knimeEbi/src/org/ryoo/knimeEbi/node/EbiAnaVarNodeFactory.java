package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAnaVarNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnaVarNodeFactory() {
		super("Ebi analyse variety", "Compute the variety of a stochastic language. That is, the average distance between two arbitrary traces in the language.", "fraction", BufferedDataTable.TYPE);
	}
}