package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.BpmnPortObject;
import org.pm4knime.portobject.BpmnPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiConvSdfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvSdfaNodeFactory() {
		super("Ebi convert stochastic-deterministic-finite-automaton", "Convert an object to a stochastic deterministic finite automaton.", "stochastic deterministic finite automaton", BpmnPortObject.TYPE);
	}
}