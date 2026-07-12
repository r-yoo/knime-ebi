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

public class EbiAnaVarNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnaVarNodeFactory() {
		super("Ebi analyse variety", "Compute the variety of a stochastic language. That is, the average distance between two arbitrary traces in the language.", "fraction", BufferedDataTable.TYPE);
	}
}