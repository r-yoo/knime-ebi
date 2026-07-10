package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiFilTrLenNodeFactory extends EbiDefaultNodeFactory {
	public EbiFilTrLenNodeFactory() {
		super("Ebi filter traces length", "Remove traces that have a given length.", "XES event log", BufferedDataTable.TYPE);
	}
}