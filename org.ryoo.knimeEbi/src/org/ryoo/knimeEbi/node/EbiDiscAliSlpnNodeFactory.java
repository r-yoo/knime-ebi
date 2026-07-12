package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiDiscAliSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscAliSlpnNodeFactory() {
		super("Ebi discover alignments stochastic-labelled-Petri-nets", "Give each transition a weight that matches the aligned occurrences of its label. The model must be livelock-free.", "stochastic labelled Petri net", PetriNetPortObject.TYPE);
	}
}