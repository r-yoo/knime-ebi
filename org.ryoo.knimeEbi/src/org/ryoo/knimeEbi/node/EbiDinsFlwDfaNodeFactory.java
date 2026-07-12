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

public class EbiDinsFlwDfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsFlwDfaNodeFactory() {
		super("Ebi discover-non-stochastic flower deterministic-finite-automaton", "Discover a DFA that supports any trace with the activities of the log.", "deterministic finite automaton", BufferedDataTable.TYPE);
	}
}