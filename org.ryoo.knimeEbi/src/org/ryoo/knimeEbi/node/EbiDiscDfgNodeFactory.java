package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.DfgMsdPortObject;
import org.pm4knime.portobject.DfgMsdPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscDfgNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscDfgNodeFactory() {
		super("Ebi discover directly-follows-graph", "Discover a directly follows graph.", "directly follows graph", DfgMsdPortObject.TYPE);
	}
}