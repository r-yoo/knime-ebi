package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscOccSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscOccSlpnNodeFactory() {
		super("Ebi discover occurrence stochastic-labelled-Petri-net", "Give each transition a weight that matches the occurrences of its label; silent transitions get a weight of 1.", "stochastic labelled Petri net", BufferedDataTable.TYPE);
	}
}