package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiFilTrEventActNodeFactory extends EbiDefaultNodeFactory {
	public EbiFilTrEventActNodeFactory() {
		super("Ebi filter traces event activity", "Remove traces that have event(s) as specified.", "XES event log", BufferedDataTable.TYPE);
	}
}