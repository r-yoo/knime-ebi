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

public class EbiConfMaNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfMaNodeFactory() {
		super("Ebi conformance markovian", "Compute the conformance between two stochastic languages using a stochastic Markovian abstraction, which represents languages based on the expected frequency of subtraces to handle partially matching traces.", "fraction", BufferedDataTable.TYPE);
	}
}