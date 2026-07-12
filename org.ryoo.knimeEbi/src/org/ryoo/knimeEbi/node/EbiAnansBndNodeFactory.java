package org.ryoo.knimeEbi.node;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiAnansBndNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnansBndNodeFactory() {
		super("Ebi analyse-non-stochastic bounded", "Compute whether the model has a bounded state space.For Petri nets, a coverability graph is computed.For other types of models, `true' is returned.", "bool", BufferedDataTable.TYPE);
	}
}