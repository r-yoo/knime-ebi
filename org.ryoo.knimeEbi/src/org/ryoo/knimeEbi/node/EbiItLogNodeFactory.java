package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiItLogNodeFactory extends EbiDefaultNodeFactory {
	public EbiItLogNodeFactory() {
		super("Ebi itself logo", "Print the logo of Ebi.", "text", BufferedDataTable.TYPE);
	}
}