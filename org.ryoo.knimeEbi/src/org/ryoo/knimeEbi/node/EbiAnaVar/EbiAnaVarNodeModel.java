package org.ryoo.knimeEbi.node.EbiAnaVar;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

import org.ryoo.knimeEbi.util.*;

public class EbiAnaVarNodeModel {
	
	public EbiAnaVarNodeModel(final Class<?> modelSettingsClass) {}
	
	public static void configure(final DefaultModel.ConfigureInput i, final DefaultModel.ConfigureOutput o) 
	    	throws InvalidSettingsException {
	    	
	        if (!(i.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
	            throw new InvalidSettingsException("Input is not a valid Event Log!");
	        }

	        o.setOutSpec(0, TableUtil.createOutputSpec("Ebi Variety", "variety", StringCell.TYPE));
	    }
	    
    public static void execute(final DefaultModel.ExecuteInput i, final DefaultModel.ExecuteOutput o) {
    	    try {
                final Object logPortObject = i.getInPortObject(0);

    	        final DataTableSpec spec = TableUtil.createOutputSpec("Ebi Variety", "variety", StringCell.TYPE);
    	        final BufferedDataContainer container =
    	            i.getExecutionContext().createDataContainer(spec);
    	        
                final String xesContent = XesUtil.writeLogToXesString(logPortObject);

                final String result = CallEbi.call_ebi(
                		"Ebi analyse variety",
                		".frac",
                		new String[] {xesContent}); // -> replace with CallEbiWrapper from ryoo.knimeintegration

    	        container.addRowToTable(new DefaultRow(
    	            "Row0",
    	            new StringCell(result)));

    	        container.close();
    	        o.setOutData(0, container.getTable());
    	    } catch (Exception ex) {
    	        throw new RuntimeException(ex);
    	    }
    	}
}
