package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAssoAttNodeFactory extends EbiDefaultNodeFactory {
	public EbiAssoAttNodeFactory() {
		super("Ebi association trace-attribute", "Compute the association between the process and a trace attribute.", "root", BufferedDataTable.TYPE);
	}
}