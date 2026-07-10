package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAssoAttsNodeFactory extends EbiDefaultNodeFactory {
	public EbiAssoAttsNodeFactory() {
		super("Ebi association all-trace-attributes", "Compute the association between the process and trace attributes.", "text", BufferedDataTable.TYPE);
	}
}