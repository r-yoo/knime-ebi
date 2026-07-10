package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfJsscNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfJsscNodeFactory() {
		super("Ebi conformance jensen-shannon", "Compute Jensen-Shannon stochastic conformance, which is 1 - the Jensen-Shannon distance.", "rootlog", BufferedDataTable.TYPE);
	}
}