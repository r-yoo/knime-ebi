package org.ryoo.knimeEbi.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

public class XesUtil {
	
	public static String writeLogToXesString(final Object logPortObject) throws IOException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
	
		serializeLog(logPortObject, out);
	
	    return out.toString(StandardCharsets.UTF_8);
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
}
