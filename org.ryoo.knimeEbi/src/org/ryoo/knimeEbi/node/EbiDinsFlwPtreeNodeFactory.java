package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDinsFlwPtreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsFlwPtreeNodeFactory() {
		super("Ebi discover-non-stochastic flower process-tree", "Discover a process tree that supports any trace with the activities of the log.", "process tree", ProcessTreePortObject.TYPE);
	}
}