package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConfErNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfErNodeFactory() {
		super("Ebi conformance entropic-relevance", "Compute entropic relevance (uniform).", "logarithm", BufferedDataTable.TYPE);
	}
}