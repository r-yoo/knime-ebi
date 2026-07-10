package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiInfoNodeFactory extends EbiDefaultNodeFactory {
	public EbiInfoNodeFactory() {
		super("Ebi information", "Show information about a file.", "text", BufferedDataTable.TYPE);
	}
}