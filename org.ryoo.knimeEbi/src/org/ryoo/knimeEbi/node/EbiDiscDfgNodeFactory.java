package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscDfgNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscDfgNodeFactory() {
		super("Ebi discover directly-follows-graph", "Discover a directly follows graph.", "directly follows graph", BufferedDataTable.TYPE);
	}
}