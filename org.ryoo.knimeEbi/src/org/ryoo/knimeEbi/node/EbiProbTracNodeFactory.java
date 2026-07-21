package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;
import org.ryoo.knimeEbi.defaultNode.EbiDefaultNodeFactory;
import org.ryoo.knimeEbi.util.*;

public class EbiProbTracNodeFactory extends EbiDefaultNodeFactory {
	public EbiProbTracNodeFactory() {
		super("Ebi sample folds", "Compute the probability of a trace in a stochastic model.", "event log", XLogPortObject.TYPE);
	}

public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
    	
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        output.setOutSpec(0, new XLogPortObjectSpec());
    }


}