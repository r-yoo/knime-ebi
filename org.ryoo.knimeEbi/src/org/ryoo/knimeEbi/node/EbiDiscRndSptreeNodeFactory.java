package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscRndSptreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscRndSptreeNodeFactory() {
		super("Ebi discover random stochastic-process-tree", "Give each leaf a random weight between 0 (exclusive) and 1 (inclusive).", "stochastic process tree", ProcessTreePortObject.TYPE);
	}
}