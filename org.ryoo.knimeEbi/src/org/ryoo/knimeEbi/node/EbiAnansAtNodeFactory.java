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

public class EbiAnansAtNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnansAtNodeFactory() {
		super("Ebi analyse-non-stochastic any-traces", "Compute whether the model has any traces.Reasons for a model not to have any traces could be if the initial state is part of a livelock, or if there is no initial state.'true' means that the model has traces, 'false' means that the model has no traces.The computation may not terminate if the model is unbounded.", "bool", BufferedDataTable.TYPE);
	}
}