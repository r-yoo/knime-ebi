package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscRndSptreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscRndSptreeNodeFactory() {
		super("Ebi discover random stochastic-process-tree", "Give each leaf a random weight between 0 (exclusive) and 1 (inclusive).", "stochastic process tree", BufferedDataTable.TYPE);
	}
}