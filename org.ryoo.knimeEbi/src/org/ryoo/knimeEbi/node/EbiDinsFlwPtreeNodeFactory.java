package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDinsFlwPtreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsFlwPtreeNodeFactory() {
		super("Ebi discover-non-stochastic flower process-tree", "Discover a process tree that supports any trace with the activities of the log.", "process tree", BufferedDataTable.TYPE);
	}
}