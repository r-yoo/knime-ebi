package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDinsPfxtPtreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsPfxtPtreeNodeFactory() {
		super("Ebi filter traces empty", "Discover a process tree that is a prefix tree of the log.", "XES event log", XLogPortObject.TYPE);
	}
}