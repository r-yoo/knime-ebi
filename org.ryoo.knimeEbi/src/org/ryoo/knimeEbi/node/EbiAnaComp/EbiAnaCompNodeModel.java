package org.ryoo.knimeEbi.node.EbiAnaComp;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;

import org.processmining.ebi.CallEbi;
import org.processmining.ebi.Pm4KnimeEventLogPort;

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
    
    private static String writeLogToTempXesFile(final Object logPortObject) throws IOException { // -> in util class
        final Path tempFile = Files.createTempFile("ebi-input-", ".xes");

        try (OutputStream out = Files.newOutputStream(tempFile)) {
            serializeLog(logPortObject, out);
        }

        return tempFile.toAbsolutePath().toString();
    }

    private static void serializeLog(final Object logPortObject, final OutputStream out) throws IOException {
        try {
            final Object log = logPortObject.getClass().getMethod("getLog").invoke(logPortObject);
            if (log == null) {
                throw new IOException("Input event log is empty.");
            }

            final ClassLoader pm4knimeClassLoader = log.getClass().getClassLoader();
            final Class<?> xLogClass = Class.forName("org.deckfour.xes.model.XLog", true, pm4knimeClassLoader);
            final Class<?> serializerClass =
                    Class.forName("org.deckfour.xes.out.XesXmlSerializer", true, pm4knimeClassLoader);
            final Object serializer = serializerClass.getConstructor().newInstance();

            serializerClass.getMethod("serialize", xLogClass, OutputStream.class).invoke(serializer, log, out);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException
                | InvocationTargetException ex) {
            throw new IOException("Unable to serialize the PM4KNIME event log to XES.", ex);
        }
    }
    
    public static void configure(final DefaultModel.ConfigureInput i, final DefaultModel.ConfigureOutput o) 
    	throws InvalidSettingsException {
    	
        if (!Pm4KnimeEventLogPort.isEventLogSpec(i.getInPortSpec(0))) {
            throw new InvalidSettingsException("Input is not a valid Event Log!");
        }

        o.setOutSpec(0, createOutputSpec());
    }
    
    public static void execute(final DefaultModel.ExecuteInput i, final DefaultModel.ExecuteOutput o) {
    	    try {
                final Object logPortObject = i.getInPortObject(0);

    	        final DataTableSpec spec = createOutputSpec();
    	        final BufferedDataContainer container =
    	            i.getExecutionContext().createDataContainer(spec);
    	        
    	        String result = null;
    	        String xesPath = null;
    	        
            try {
                xesPath = writeLogToTempXesFile(logPortObject);

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

