package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscUniSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscUniSlpnNodeFactory() {
		super("Ebi discover uniform stochastic-labelled-Petri-net", "Give each transition a weight of 1.", "stochastic labelled Petri net", PetriNetPortObject.TYPE);
	}
}