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

public class EbiConfJsscNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfJsscNodeFactory() {
		super("Ebi conformance jensen-shannon", "Compute Jensen-Shannon stochastic conformance, which is 1 - the Jensen-Shannon distance.", "rootlog", BufferedDataTable.TYPE);
	}
}