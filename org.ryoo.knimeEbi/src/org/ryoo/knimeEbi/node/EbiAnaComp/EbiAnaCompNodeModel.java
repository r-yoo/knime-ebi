package org.ryoo.knimeEbi.node.EbiAnaComp;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

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


    private static DataTableSpec createOutputSpec() {
    	return new DataTableSpec(
    			"Ebi Completeness",
    			new String[] {"completeness"},
    			new DataType[] {StringCell.TYPE});
    }
    
    public static void configure(final DefaultModel.ConfigureInput i, final DefaultModel.ConfigureOutput o) 
    	throws InvalidSettingsException {
    	
    	if (!(i.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        o.setOutSpec(0, createOutputSpec());
    }
    
    public static void execute(final DefaultModel.ExecuteInput i, final DefaultModel.ExecuteOutput o) {
    	    try {
    	        final XLogPortObject log = i.getInPortObject(0);

    	        final DataTableSpec spec = createOutputSpec();
    	        final BufferedDataContainer container =
    	            i.getExecutionContext().createDataContainer(spec);

    	        String result = "3/4"; // replace with Ebi call result

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

