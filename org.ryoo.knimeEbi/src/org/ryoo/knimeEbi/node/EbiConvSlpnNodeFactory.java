package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiConvSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvSlpnNodeFactory() {
		super("Ebi convert stochastic-labelled-petri-net", "Convert an object to a stochastic labelled Petri net.", "stochastic labelled Petri net", BufferedDataTable.TYPE);
	}
}