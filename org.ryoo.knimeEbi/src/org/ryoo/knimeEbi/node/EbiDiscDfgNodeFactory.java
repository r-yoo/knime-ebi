package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.portobject.DfgMsdPortObject;
import org.pm4knime.portobject.DfgMsdPortObjectSpec;

import org.processmining.ebi.CallEbi;
import org.ryoo.knimeEbi.defaultNode.EbiDefaultNodeFactory;
import org.ryoo.knimeEbi.util.*;

public class EbiDiscDfgNodeFactory extends EbiDefaultNodeFactory {
	public EbiDiscDfgNodeFactory() {
		super("Ebi discover directly-follows-graph", "More information:", "", "directly follows graph", DfgMsdPortObject.TYPE);
	}

public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
    	
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        output.setOutSpec(0, new DfgMsdPortObjectSpec());
    }


}