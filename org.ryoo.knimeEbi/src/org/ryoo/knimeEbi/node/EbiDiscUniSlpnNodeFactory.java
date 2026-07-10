package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscUniSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscUniSlpnNodeFactory() {
		super("Ebi discover uniform stochastic-labelled-Petri-net", "Give each transition a weight of 1.", "stochastic labelled Petri net", BufferedDataTable.TYPE);
	}
}