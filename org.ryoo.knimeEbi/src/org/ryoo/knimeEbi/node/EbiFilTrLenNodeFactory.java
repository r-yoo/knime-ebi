package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiFilTrLenNodeFactory extends EbiDefaultNodeFactory {
	public EbiFilTrLenNodeFactory() {
		super("Ebi filter traces length", "Remove traces that have a given length.", "XES event log", XLogPortObject.TYPE);
	}
}