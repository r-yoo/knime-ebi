package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscUniSptreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscUniSptreeNodeFactory() {
		super("Ebi discover uniform stochastic-process-tree", "Give each leaf a weight of 1 in a process tree.", "stochastic process tree", ProcessTreePortObject.TYPE);
	}
}