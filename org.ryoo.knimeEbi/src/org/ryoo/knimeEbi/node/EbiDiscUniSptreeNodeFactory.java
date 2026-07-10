package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscUniSptreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscUniSptreeNodeFactory() {
		super("Ebi discover uniform stochastic-process-tree", "Give each leaf a weight of 1 in a process tree.", "stochastic process tree", BufferedDataTable.TYPE);
	}
}