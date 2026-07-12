package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscRndSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscRndSlpnNodeFactory() {
		super("Ebi discover random stochastic-labelled-Petri-net", "Give each transition a random weight between 0 (exclusive) and 1 (inclusive).", "stochastic labelled Petri net", PetriNetPortObject.TYPE);
	}
}