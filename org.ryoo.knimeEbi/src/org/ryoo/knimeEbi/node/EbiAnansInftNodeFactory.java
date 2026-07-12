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

public class EbiAnansInftNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnansInftNodeFactory() {
		super("Ebi analyse-non-stochastic infinitely-many-traces", "Compute whether the model has infinitely many traces. The computation may not terminate if the model is unbounded.", "bool", BufferedDataTable.TYPE);
	}
}