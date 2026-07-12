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

public class EbiConfEmscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfEmscSampleNodeFactory() {
		super("Ebi conformance earth-movers-sample", "Compute Earth mover's stochastic conformance, which is 1 - the Wasserstein distance, where one or both of the inputs needs to be sampled. If both inputs are logs or finite stochastic languages, then use `emsc`.", "fraction", BufferedDataTable.TYPE);
	}
}