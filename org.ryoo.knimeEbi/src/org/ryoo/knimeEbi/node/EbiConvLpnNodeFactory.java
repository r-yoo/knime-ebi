package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConvLpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvLpnNodeFactory() {
		super("Ebi convert log", "Convert an object to a labelled Petri net.", "event log", BufferedDataTable.TYPE);
	}
}