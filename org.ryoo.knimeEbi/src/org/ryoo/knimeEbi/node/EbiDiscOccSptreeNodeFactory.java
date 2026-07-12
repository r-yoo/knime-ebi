package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscOccSptreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscOccSptreeNodeFactory() {
		super("Ebi discover occurrence stochastic-process-tree", "Give each leaf a weight that matches the occurrences of its label; silent leaves get a weight of 1.", "stochastic process tree", ProcessTreePortObject.TYPE);
	}
}