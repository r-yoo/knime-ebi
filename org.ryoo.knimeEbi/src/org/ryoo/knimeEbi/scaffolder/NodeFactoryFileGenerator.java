package org.ryoo.knimeEbi.scaffolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pm4knime.util.*;

import org.processmining.ebi.CallEbi;

public class NodeFactoryFileGenerator {
	private static final Pattern PLUGIN_DECLARATION_PATTERN = Pattern.compile("(?m)^\\s*@Plugin\\s*\\(");
	
	/*
	 * Extracts only Ebi commands that are available in Java.
	 * Immediately scaffold from output of Ebi itself java
	 * original text (iterate through split elements) -> metadata parameter call EbiCommandMetadata constructor
	 */
	public static void generateEbiNodeFactories() {
		System.out.println("Starting generating Ebi Nodes...");
		
		String ebiItselfJavaOutput = CallEbi.call_ebi("Ebi itself java", ".txt", new String[0]);
		
		String [] ebiCommandsMetadataBlocks = ebiItselfJavaOutput.split("// == command");

		// Iterate from 2nd element
		for (int i = 1; i < ebiCommandsMetadataBlocks.length; i++) {
			String metadataBlock = ebiCommandsMetadataBlocks[i].trim();
			
			if (!containsPluginDeclaration(metadataBlock)) {
		        String commandName = metadataBlock.split("==", 2)[0].trim();
		        System.out.println("Skipping command without plugin declaration: " + commandName);
		        System.out.println("");
		        continue;
		    }
			
			// For checking metadata extraction
			EbiCommandMetadata metadata = new EbiCommandMetadata(metadataBlock);
			System.out.println(metadata.toString());
			System.out.println("");
			
			String factoryClassName = toClassNamePrefix(metadata.commandName) + "NodeFactory";
			
			generateEbiNodeFactory(metadata, factoryClassName);
			
			// Adding Node Factory to plugin.xml
			try {
				String fullyQualifiedFactoryClassName = "org.ryoo.knimeEbi.node." + factoryClassName;
				
				addNodeToPlugin(fullyQualifiedFactoryClassName);
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
	
	private static void generateEbiNodeFactory(EbiCommandMetadata metadata, String factoryClassName) {
		// TODO: Implement logic
		// Check zero input, one input or two input
		if(metadata.inputs == null || metadata.inputs.size() == 0) {
			System.out.println(metadata.commandName + " is a itself type command...");
			System.out.println("Node Factory will not be created...");
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		
		/*
		 * TODO: Build String here
		 * */
		
		String ebiNodeFactoryString = builder.toString();
		
		try {
			Files.writeString(
					Path.of("src", "org", "ryoo", "knimeEbi", "node", factoryClassName + ".java"),
				    ebiNodeFactoryString,
				    StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			System.out.println("Error writing " + factoryClassName + ".java");
			e.printStackTrace();
		}
	}
	
	private static String setPortTypeImport(final String portType) {
		String portTypeImport = "";
		
		if(portType == "BufferedDataTable") {
			portTypeImport = "import org.knime.core.data.DataTableSpec;\r\n"
							+ "import org.knime.core.data.def.StringCell;\r\n"
							+ "import org.knime.core.data.def.DefaultRow;\r\n"
							+ "import org.knime.core.node.BufferedDataContainer;\r\n"
							+ "import org.knime.core.node.BufferedDataTable;\r\n"
							+ "import org.knime.core.node.InvalidSettingsException;\r\n"
							+ "import org.knime.node.DefaultModel;\r\n"
							+ "\r\n"
							+ "import org.pm4knime.portobject.XLogPortObjectSpec;\r\n"
							+ "\r\n"
							+ "import org.processmining.ebi.CallEbi;\r\n"
							+ "\r\n"
							+ "import org.ryoo.knimeEbi.util.*;";
		}
		else {
			portTypeImport = "import org.knime.core.node.InvalidSettingsException;\r\n"
							+ "import org.knime.node.DefaultModel;\r\n"
							+ "\r\n"
							+ "import org.pm4knime.portobject.XLogPortObjectSpec;\r\n" // TODO: Add case for portType = XLog
							+ "import org.pm4knime.portobject." + portType + ";\r\n"
							+ "import org.pm4knime.portobject." + portType + "Spec;\r\n"
							+ "\r\n"
							+ "import org.processmining.ebi.CallEbi;\r\n"
							+ "\r\n"
							+ "import org.ryoo.knimeEbi.util.*;";
		}
		
		return portTypeImport;
	}
	
	// TODO: change signature and cases for two inputs and zero inputs
	private static String setConfigure(final String portType, final String commandName) {
		String configureString = "";
		
		if(portType == "BufferedDataTable") {
			configureString = "public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) \r\n"
						+ "    	throws InvalidSettingsException {\r\n"
						+ "    	\r\n"
						+ "        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {\r\n"
						+ "            throw new InvalidSettingsException(\"Input is not a valid Event Log!\");\r\n"
						+ "        }\r\n"
						+ "\r\n"
						+ "        output.setOutSpec(0, TableUtil.createOutputSpec(\"" + commandName + "\", \"" + commandName + "\", StringCell.TYPE));\r\n"
						+ "    }";
		}
		else {
			configureString = "public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) \r\n"
						+ "    	throws InvalidSettingsException {\r\n"
						+ "    	\r\n"
						+ "        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {\r\n"
						+ "            throw new InvalidSettingsException(\"Input is not a valid Event Log!\");\r\n"
						+ "        }\r\n"
						+ "\r\n"
						+ "        output.setOutSpec(0, new " + portType + "Spec());\r\n"
						+ "    }";
		}
		
		return configureString;
	}
	
	// TODO: change signature and add cases for TwoInput and ZeroInput -> += String
	private static String setExecute(final String portType, final String commandName, final String outputType) {
		String executeString = "";
		// TODO: How many inputs from metadata and what kind of mandatory parameters -> Settings or Dialog?
		if(portType == "BufferedDataTable") {
			executeString = "public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {\r\n"
						+ "	    try {\r\n"
						+ "            final Object logPortObject = input.getInPortObject(0);\r\n"
						+ "\r\n"
						+ "	        final DataTableSpec spec = TableUtil.createOutputSpec(\"Ebi Completeness\", \"completeness\", StringCell.TYPE);\r\n"
						+ "	        final BufferedDataContainer container =\r\n"
						+ "	            input.getExecutionContext().createDataContainer(spec);\r\n"
						+ "	        \r\n"
						+ "            final String xesContent = XESUtil.writeLogToXesString(logPortObject);\r\n"
						+ "\r\n"
						+ "            final String result = CallEbi.call_ebi(\r\n"
						+ "            		\"" + commandName + "\",\r\n"
						+ "            		\"" + outputType + "\",\r\n"
						+ "            		new String[] {xesContent});\r\n"
						+ "\r\n"
						+ "	        container.addRowToTable(new DefaultRow(\r\n"
						+ "	            \"Row0\",\r\n"
						+ "	            new StringCell(result)));\r\n"
						+ "\r\n"
						+ "	        container.close();\r\n"
						+ "	        output.setOutData(0, container.getTable());\r\n"
						+ "	    } catch (Exception ex) {\r\n"
						+ "	        throw new RuntimeException(ex);\r\n"
						+ "	    }\r\n"
						+ "	}";
		}
		else {
			executeString = "";
		}
		
		return executeString;
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
	
	private static void addNodeToPlugin(final String factoryClassName) throws IOException {

	    Path pluginXml = Path.of("plugin.xml");

	    if (!Files.exists(pluginXml)) {
	        throw new IOException(
	            "Cannot find plugin.xml at: "
	                + pluginXml.toAbsolutePath()
	        );
	    }

	    String xml = Files.readString(
	        pluginXml,
	        StandardCharsets.UTF_8
	    );

	    // Do not add the same factory more than once.
	    Pattern existingFactoryPattern = Pattern.compile(
	        "factory-class\\s*=\\s*[\"']"
	            + Pattern.quote(factoryClassName)
	            + "[\"']"
	    );

	    if (existingFactoryPattern.matcher(xml).find()) {
	        System.out.println(
	            factoryClassName + " is already registered in plugin.xml."
	        );
	        return;
	    }

	    String updatedXml = findOrCreateNodeExtension(
	        xml,
	        factoryClassName
	    );

	    Files.writeString(
	        pluginXml,
	        updatedXml,
	        StandardCharsets.UTF_8
	    );
	}

	private static String findOrCreateNodeExtension(final String xml, final String factoryClassName) {

	    String newline = xml.contains("\r\n") ? "\r\n" : "\n";

	    Pattern extensionPattern = Pattern.compile(
	        "<extension\\b"
	            + "(?=[^>]*\\bpoint\\s*=\\s*[\"']"
	            + "org\\.knime\\.workbench\\.repository\\.nodes"
	            + "[\"'])"
	            + "[^>]*>",
	        Pattern.DOTALL
	    );

	    Matcher matcher = extensionPattern.matcher(xml);

	    String nodeXml =
	        "      <node" + newline
	            + "            category-path=\"/\"" + newline // TODO: Specify category
	            + "            factory-class=\""
	            + factoryClassName
	            + "\"/>"
	            + newline;

	    if (matcher.find()) {
	        int extensionEnd = xml.indexOf(
	            "</extension>",
	            matcher.end()
	        );

	        if (extensionEnd < 0) {
	            throw new IllegalStateException(
	                "The KNIME node extension has no closing </extension> tag."
	            );
	        }

	        // Add another node to the existing extension.
	        return xml.substring(0, extensionEnd)
	            + nodeXml
	            + xml.substring(extensionEnd);
	    }

	    int pluginEnd = xml.lastIndexOf("</plugin>");

	    if (pluginEnd < 0) {
	        throw new IllegalStateException(
	            "plugin.xml has no closing </plugin> tag."
	        );
	    }

	    // No node extension exists, so create one.
	    String extensionXml =
	        "   <extension" + newline
	            + "         point=\"org.knime.workbench.repository.nodes\">"
	            + newline
	            + nodeXml
	            + "   </extension>"
	            + newline
	            + newline;

	    return xml.substring(0, pluginEnd)
	        + extensionXml
	        + xml.substring(pluginEnd);
	}
}
