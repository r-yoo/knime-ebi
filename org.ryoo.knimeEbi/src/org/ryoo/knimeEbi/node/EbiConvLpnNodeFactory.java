package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiConvLpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvLpnNodeFactory() {
		super("Ebi convert log", "Convert an object to a labelled Petri net.", "event log", XLogPortObject.TYPE);
	}
}