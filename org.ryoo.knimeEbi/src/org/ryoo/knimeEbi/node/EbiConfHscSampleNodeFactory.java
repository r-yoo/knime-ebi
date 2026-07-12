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

public class EbiConfHscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfHscSampleNodeFactory() {
		super("Ebi conformance hellinger-sample", "Compute Hellinger stochastic conformance, which is 1 - the Hellinger distance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `hsc`.", "fraction", BufferedDataTable.TYPE);
	}
}