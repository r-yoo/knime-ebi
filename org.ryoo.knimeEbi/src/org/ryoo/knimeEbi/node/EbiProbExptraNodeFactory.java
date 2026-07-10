package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiProbExptraNodeFactory extends EbiDefaultNodeFactory {
	public EbiProbExptraNodeFactory() {
		super("Ebi probability log", "Compute the most likely explanation of a trace given the stochastic model.", "fraction", BufferedDataTable.TYPE);
	}
}