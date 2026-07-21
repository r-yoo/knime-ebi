package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;

import org.processmining.ebi.CallEbi;
import org.ryoo.knimeEbi.defaultNode.EbiDefaultNodeFactory;
import org.ryoo.knimeEbi.util.*;

public class EbiDiscOccSlpnNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscOccSlpnNodeFactory() {
		super("Ebi discover occurrence stochastic-labelled-Petri-net", "More information:", "stochastic labelled Petri net", PetriNetPortObject.TYPE);
	}

public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
    	
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        output.setOutSpec(0, new PetriNetPortObjectSpec());
    }


}