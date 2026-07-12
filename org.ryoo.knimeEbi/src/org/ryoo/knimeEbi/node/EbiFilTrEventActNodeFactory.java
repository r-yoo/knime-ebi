package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiFilTrEventActNodeFactory extends EbiDefaultNodeFactory {
	public EbiFilTrEventActNodeFactory() {
		super("Ebi filter traces event activity", "Remove traces that have event(s) as specified.", "XES event log", XLogPortObject.TYPE);
	}
}