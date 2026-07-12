package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.BpmnPortObject;
import org.pm4knime.portobject.BpmnPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiConvSnfaNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvSnfaNodeFactory() {
		super("Ebi convert stochastic-nondeterministic-finite-automaton", "Convert an object to a stochastic nondeterministic finite automaton.", "stochastic non-deterministic finite automaton", BpmnPortObject.TYPE);
	}
}