package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiConvertLogNodeFactory extends EbiDefaultNodeFactory {
	public EbiConvertLogNodeFactory() {
		super("Ebi convert log", "\noindent Output: event log, which can be written as a compressed event log (.xes.gz -- \cref{filehandler:compressed event log}), a deterministic finite automaton (.dfa -- \cref{filehandler:deterministic finite automaton}), an extensible event stream (.xes -- \cref{filehandler:extensible event stream}), a finite language (.lang -- \cref{filehandler:finite language}), a finite stochastic language (.slang -- \cref{filehandler:finite stochastic language}), a stochastic deterministic finite automaton (.sdfa -- \cref{filehandler:stochastic deterministic finite automaton}), a comma-separated values (.csv -- \cref{filehandler:comma-separated values}), a stochastic non-deterministic finite automaton (.snfa -- \cref{filehandler:stochastic non-deterministic finite automaton}) or a Python event log (.pel -- \cref{filehandler:Python event log}).", "event log", XLogPortObject.TYPE);
	}

public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
    	
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        output.setOutSpec(0, new XLogPortObjectSpec());
    }


}