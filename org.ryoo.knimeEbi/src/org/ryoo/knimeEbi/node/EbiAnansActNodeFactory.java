package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAnansActNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnansActNodeFactory() {
		super("Ebi analyse-non-stochastic activities", "Shows the activities that are declared in the object.", "text", BufferedDataTable.TYPE);
	}
}