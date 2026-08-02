package org.ryoo.knimeEbi.scaffolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

import org.processmining.ebi.CallEbi;

public class EbiNodeGenerator {
	private static final Pattern PLUGIN_DECLARATION_PATTERN = Pattern.compile("(?m)^\\s*@Plugin\\s*\\(");
	
	/*
	 * Extracts only Ebi commands that are available in Java.
	 * Immediately scaffold from output of Ebi itself java
	 * original text (iterate through split elements) -> metadata parameter call EbiCommandMetadata constructor
	 */
	public static void generateEbiNodes() {
		System.out.println("Starting generating Ebi Nodes...");
		
		String ebiItselfJavaOutput = CallEbi.call_ebi("Ebi itself java", ".txt", new String[0]);
		
		String [] ebiCommandsMetadataBlocks = ebiItselfJavaOutput.split("// == command");

		// Iterate from 2nd element
		for (int i = 1; i < ebiCommandsMetadataBlocks.length; i++) {
			String metadataBlock = ebiCommandsMetadataBlocks[i].trim();
			
			if (!containsPluginDeclaration(metadataBlock)) {
		        String commandName = metadataBlock.split("==", 2)[0].trim();
		        System.out.println("Skipping command without plugin declaration: " + commandName);
		        continue;
		    }
			
			EbiCommandMetadata metadata = new EbiCommandMetadata(metadataBlock);
			
			// For checking metadata extraction
			// System.out.println(metadata.toString());
			
			String factoryClassName = toClassNamePrefix(metadata.commandName) + "NodeFactory";
			String settingsClassName = toClassNamePrefix(metadata.commandName) + "NodeSettings";
			
			if(metadata.inputs == null || metadata.inputs.size() == 0) {
				System.out.println(metadata.commandName + " is a itself type command...");
				System.out.println("Node Factory will not be created...");
				continue;
			}
			
			generateEbiNodeFactory(metadata, factoryClassName, settingsClassName);
			
			if(!metadata.hasNoPrimitiveInputs()) {
				generateEbiNodeSettings(metadata, settingsClassName);
			}
			
			// Adding Node Factory to plugin.xml
			try {
				String fullyQualifiedFactoryClassName = "org.ryoo.knimeEbi.node." + factoryClassName;
				
				PluginXmlNodeRegistrar.register(fullyQualifiedFactoryClassName);
			} catch (IOException e) {
				System.out.println("Error adding " + factoryClassName + " to plugin.xml");
				e.printStackTrace();
			}
		}
		
		System.out.println("Generated all Ebi Nodes.");
	}
	
	private static boolean containsPluginDeclaration(final String metadataBlock) {
	    return metadataBlock != null && PLUGIN_DECLARATION_PATTERN.matcher(metadataBlock).find();
	}
	
	private static void generateEbiNodeFactory(final EbiCommandMetadata metadata, final String factoryClassName, final String settingsClassName) {
		String ebiNodeFactorySource = EbiNodeFactorySourceGenerator.generate(metadata, factoryClassName, settingsClassName);
		
		try {
			Files.writeString(
					Path.of("src", "org", "ryoo", "knimeEbi", "node", factoryClassName + ".java"),
				    ebiNodeFactorySource,
				    StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			System.out.println("Error writing " + factoryClassName + ".java");
			e.printStackTrace();
		}
	}
	
	private static void generateEbiNodeSettings(final EbiCommandMetadata metadata, final String settingsClassName) {
		if(metadata.inputs == null || metadata.inputs.size() == 0) {
			System.out.println(metadata.commandName + " is a itself type command...");
			System.out.println("Node Settings will not be created...");
			return;
		}

		String ebiNodeSettingsSource = EbiNodeSettingsSourceGenerator.generate(metadata, settingsClassName);

		try {
			Files.writeString(
					Path.of("src", "org", "ryoo", "knimeEbi", "node", settingsClassName + ".java"),
				    ebiNodeSettingsSource,
				    StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			System.out.println("Error writing " + settingsClassName + ".java");
			e.printStackTrace();
		}
	}
	
	private static String toClassNamePrefix(final String commandName) {
	    String[] words = commandName.trim().split("[^A-Za-z0-9]+");

	    StringBuilder result = new StringBuilder();

	    for (String word : words) {
	        if (word.isBlank()) {
	            continue;
	        }

	        String lower = word.toLowerCase();
	        result.append(Character.toUpperCase(lower.charAt(0)));
	        result.append(lower.substring(1));
	    }

	    return result.toString();
	}
}
