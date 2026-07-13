package org.ryoo.knimeEbi.node;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiConfUemscSampleNodeFactory extends EbiDefaultNodeFactory {
	public EbiConfUemscSampleNodeFactory() {
		super("Ebi conformance unit-earth-movers-sample", "Compute unit-earth movers' stochastic conformance, which is 1 - the total variation distance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `uemsc`.", "fraction", BufferedDataTable.TYPE);
	}

public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
    	
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi conformance unit-earth-movers-sample", "Ebi conformance unit-earth-movers-sample", StringCell.TYPE));
    }

public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
	    try {
            final Object logPortObject = input.getInPortObject(0);

	        final DataTableSpec spec = TableUtil.createOutputSpec("Ebi Completeness", "completeness", StringCell.TYPE);
	        final BufferedDataContainer container =
	            input.getExecutionContext().createDataContainer(spec);
	        
            final String xesContent = XESUtil.writeLogToXesString(logPortObject);

            final String result = CallEbi.call_ebi(
            		"Ebi conformance unit-earth-movers-sample",
            		"fraction",
            		new String[] {xesContent});

	        container.addRowToTable(new DefaultRow(
	            "Row0",
	            new StringCell(result)));

	        container.close();
	        output.setOutData(0, container.getTable());
	    } catch (Exception ex) {
	        throw new RuntimeException(ex);
	    }
	}
}