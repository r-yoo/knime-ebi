package org.processmining.ebi;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class CallEbi {
	
    static native String call_ebi_internal(String command_name, String output_format, String[] inputs);
    
    
    static {
        try {
        	Bundle bundle = FrameworkUtil.getBundle(CallEbi.class);
        	
        	if(bundle != null) {
        		URL url = bundle.getEntry("lib/ebi.dll");
            	URL ebiUrl = FileLocator.toFileURL(url);

            	System.load(new File(ebiUrl.toURI()).getAbsolutePath()); // Added lib/ in build.properties
        	}
        	else {
        		System.load(Path.of("lib", "ebi.dll").toAbsolutePath().toString());
        	}
        	
        	System.out.println("Ebi library loaded");
        } catch (Exception e) {
        	throw new ExceptionInInitializerError(e);
        }
    }
    
    public static String call_ebi(String command_name, String output_format, String[] inputs) {
    	for (String input: inputs) {
    		if (input == null) {
    			throw new RuntimeException("Ebi does not support null parameters.");
    		}
    	}
    	
    	String result = call_ebi_internal(command_name, output_format, inputs);
    	System.out.println("== KNIME received");
    	System.out.println(result);
    	return result;
    }
    
}