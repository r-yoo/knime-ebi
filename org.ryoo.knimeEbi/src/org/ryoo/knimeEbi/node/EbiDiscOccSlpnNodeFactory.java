package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscOccSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscOccSlpnNodeFactory() {
		super("Ebi discover occurrence stochastic-labelled-Petri-net", "Give each transition a weight that matches the occurrences of its label; silent transitions get a weight of 1.", "stochastic labelled Petri net", PetriNetPortObject.TYPE);
	}
}