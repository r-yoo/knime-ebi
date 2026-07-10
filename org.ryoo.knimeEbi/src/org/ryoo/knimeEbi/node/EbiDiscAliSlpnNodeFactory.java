package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDiscAliSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscAliSlpnNodeFactory() {
		super("Ebi discover alignments stochastic-labelled-Petri-nets", "Give each transition a weight that matches the aligned occurrences of its label. The model must be livelock-free.", "stochastic labelled Petri net", BufferedDataTable.TYPE);
	}
}