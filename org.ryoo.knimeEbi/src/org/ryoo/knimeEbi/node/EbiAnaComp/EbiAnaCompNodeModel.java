package org.ryoo.knimeEbi.node.EbiAnaComp;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.out.XSerializer;
import org.deckfour.xes.out.XesXmlSerializer;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;

import org.processmining.ebi.CallEbi;

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


    private static DataTableSpec createOutputSpec() { // -> in util class
    	return new DataTableSpec(
    			"Ebi Completeness",
    			new String[] {"completeness"},
    			new DataType[] {StringCell.TYPE});
    }
    
    private static String writeLogToTempXesFile(final XLog log) throws IOException { // -> in util class
        final Path tempFile = Files.createTempFile("ebi-input-", ".xes");

        try (OutputStream out = Files.newOutputStream(tempFile)) {
            final XSerializer serializer = new XesXmlSerializer();
            serializer.serialize(log, out);
        }

        return tempFile.toAbsolutePath().toString();
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
    	        final XLogPortObject logPortObject = i.getInPortObject(0);

    	        final DataTableSpec spec = createOutputSpec();
    	        final BufferedDataContainer container =
    	            i.getExecutionContext().createDataContainer(spec);
    	        
    	        String result = null;
    	        String xesPath = null;
    	        
    	        try {
        	        xesPath = writeLogToTempXesFile(logPortObject.getLog());
        	        
        	        result = CallEbi.call_ebi("Ebi analyse completeness", "text", new String[] {xesPath}); // -> replace with CallEbiWrapper from ryoo.knimeintegration
        	        
        	        System.out.println("Path of the temp xes-file:");
        	        System.out.println(xesPath);
    	        } finally {
    	        	if(xesPath != null) {
    	        		Files.deleteIfExists(Path.of(xesPath));
    	        	}
    	        }

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

