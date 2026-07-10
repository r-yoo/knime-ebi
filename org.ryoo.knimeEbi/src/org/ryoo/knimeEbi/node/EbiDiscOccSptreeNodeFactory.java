package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscOccSptreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscOccSptreeNodeFactory() {
		super("Ebi discover occurrence stochastic-process-tree", "Give each leaf a weight that matches the occurrences of its label; silent leaves get a weight of 1.", "stochastic process tree", BufferedDataTable.TYPE);
	}
}