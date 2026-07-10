package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAnaCompNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnaCompNodeFactory() {
		super("Ebi analyse completeness", "Estimate the completeness of an event log using species discovery.", "fraction", BufferedDataTable.TYPE);
	}
}