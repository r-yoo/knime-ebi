package org.processmining.ebi;

import java.io.File;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

public class CallEbi {
	
	// This declares that the static `hello` method will be provided
    // a native library.
	/*
	 * How to get the header file:
	 * copy all .jar files from the Ivy cache to the lib folder of the ProM package
	 * javac -h . -cp .ebi.jar:lib/* src/org/processmining/ebi/CallEbi.java
	 */
    static native String call_ebi_internal(String command_name, String output_format, String[] inputs);
    
    
    /**
     * For ProM developers: 
     * 
     * - Download a version of the Ebi lib for your Operating System from http://www.promtools.org/prom6/packages/Rust/.
     * - Unzip the library in the .zip file to the lib/ folder.
     */
    
    static {
        try {
        	Bundle bundle = FrameworkUtil.getBundle(CallEbi.class);
        	URL url = bundle.getEntry("lib/ebi.dll");
        	URL ebiUrl = FileLocator.toFileURL(url);

        	System.load(new File(ebiUrl.toURI()).getAbsolutePath()); // Added lib/ in build.properties
        	System.out.println("Ebi library loaded");
        } catch (Exception e) {
        	e.printStackTrace();
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