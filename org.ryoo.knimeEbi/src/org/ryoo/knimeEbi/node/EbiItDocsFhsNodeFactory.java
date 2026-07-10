package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiItDocsFhsNodeFactory extends EbiDefaultNodeFactory {
	public EbiItDocsFhsNodeFactory() {
		super("Ebi itself java", "Print the HTML documentation of Ebi's file handlers.", "text", BufferedDataTable.TYPE);
	}
}