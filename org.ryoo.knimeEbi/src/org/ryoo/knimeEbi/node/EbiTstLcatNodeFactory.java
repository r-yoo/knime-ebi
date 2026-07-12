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

public class EbiTstLcatNodeFactory extends EbiDefaultNodeFactory {
	public EbiTstLcatNodeFactory() {
		super("Ebi test log-categorical-attribute", "Test the hypothesis that the sub-logs defined by the categorical attribute are derived from identical processes.", "text", BufferedDataTable.TYPE);
	}
}