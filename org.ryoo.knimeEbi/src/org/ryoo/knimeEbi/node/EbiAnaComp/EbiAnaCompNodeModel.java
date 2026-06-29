package org.ryoo.knimeEbi.node.EbiAnaComp;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.processmining.ebi.CallEbi;
import org.processmining.ebi.Pm4KnimeEventLogPort;
// import org.deckfour.xes.model.XLog;

import org.ryoo.knimeEbi.util.*;

/**
 * <code>NodeModel</code> for the "EbiAnaComp" node.
 *
 * @author 
 */
public class EbiAnaCompNodeModel {
    
    /**
     * Constructor for the node model.
     */
    public EbiAnaCompNodeModel(final Class<?> modelSettingsClass) {}
    
    public static void configure(final DefaultModel.ConfigureInput i, final DefaultModel.ConfigureOutput o) 
    	throws InvalidSettingsException {
    	
        if (!Pm4KnimeEventLogPort.isEventLogSpec(i.getInPortSpec(0))) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        o.setOutSpec(0, TableUtil.createOutputSpec("Ebi Completeness", "completeness", StringCell.TYPE));
    }
    
    public static void execute(final DefaultModel.ExecuteInput i, final DefaultModel.ExecuteOutput o) {
    	    try {
                final Object logPortObject = i.getInPortObject(0);

    	        final DataTableSpec spec = TableUtil.createOutputSpec("Ebi Completeness", "completeness", StringCell.TYPE);
    	        final BufferedDataContainer container =
    	            i.getExecutionContext().createDataContainer(spec);
    	        
                final String xesContent = XesUtil.writeLogToXesString(logPortObject);

                final String result = CallEbi.call_ebi(
                		"Ebi analyse completeness",
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

