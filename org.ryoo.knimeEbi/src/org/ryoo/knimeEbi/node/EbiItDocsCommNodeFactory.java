package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiItDocsCommNodeFactory extends EbiDefaultNodeFactory {
	public EbiItDocsCommNodeFactory() {
		super("Ebi itself documentation commands", "Print the HTML documentation of Ebi's commands.", "text", BufferedDataTable.TYPE);
	}
}