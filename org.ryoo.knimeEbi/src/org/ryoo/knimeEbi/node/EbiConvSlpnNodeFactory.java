package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiConvSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvSlpnNodeFactory() {
		super("Ebi convert stochastic-labelled-petri-net", "Convert an object to a stochastic labelled Petri net.", "stochastic labelled Petri net", PetriNetPortObject.TYPE);
	}
}