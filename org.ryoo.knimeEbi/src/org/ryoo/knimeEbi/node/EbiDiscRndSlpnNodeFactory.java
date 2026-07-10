package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscRndSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscRndSlpnNodeFactory() {
		super("Ebi discover random stochastic-labelled-Petri-net", "Give each transition a random weight between 0 (exclusive) and 1 (inclusive).", "stochastic labelled Petri net", BufferedDataTable.TYPE);
	}
}