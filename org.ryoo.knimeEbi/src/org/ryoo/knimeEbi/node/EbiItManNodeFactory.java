package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiItManNodeFactory extends EbiDefaultNodeFactory {
	public EbiItManNodeFactory() {
		super("Ebi itself manual", "Print the automatically generated parts of the manual of Ebi in Latex format.", "text", BufferedDataTable.TYPE);
	}
}