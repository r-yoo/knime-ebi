package org.ryoo.knimeEbi.scaffolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.pm4knime.util.*;

import org.processmining.ebi.CallEbi;

public class NodeFactoryFileGenerator {
	
	// TODO: Delete if not needed anymore
	public static void createEbiManual() {
		String manual = CallEbi.call_ebi("Ebi itself manual", "text", new String[0]);
		
		Path outputPath = Path.of(
			"src",
			"org",
			"ryoo",
			"knimeEbi",
			"scaffolder",
			"ebi-manual.txt" // \ebicommands to \ebifilehandlers
		);
		
		try {
			Files.writeString(outputPath, manual, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Error writing manual to txt-file.");
			e.printStackTrace();
		}
	}
	
	// TODO: Delete if not needed anymore
	public static void printEbiPluginsClass() {
		String ebiCommands = CallEbi.call_ebi("Ebi itself java", "text", new String[0]);
		
		Path outputPath = Path.of(
			"src",
			"org",
			"ryoo",
			"knimeEbi",
			"scaffolder",
			"ebi-itself-java-output.txt"
		);
			
		try {
			Files.writeString(outputPath, ebiCommands, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("Error writing output of \"Ebi itself java\" to txt-file.");
			e.printStackTrace();
		}
	}
	
	private static String extractStringFromLine(String line, String prefix, String suffix) {
		if (line.startsWith(prefix) && line.endsWith(suffix)) {
		    String subString = line.substring(
		        prefix.length(),
		        line.length() - suffix.length()
		    );

		    return subString;
		}
		else {
			System.out.println("Provided line does not start with provided prefix and suffix!");
			return null;
		}
	}
	
	/*
	 * Extract Content within {}
	 */
	private static String getContent(final String line) {
		String[] open = line.split("\\{");
		return open[1].split("\\}")[0];
	}
	
	private static String extractOutputType(final String line) {
	    String prefix = "\\noindent Output:";

	    if (!line.startsWith(prefix)) {
	    	System.out.println("Provided line does not start with \\noindent Output:");
	        return null;
	    }

	    int start = prefix.length();
	    int end = line.indexOf(",", start);

	    if (end < 0) {
	    	System.out.println("There exists no comma in this line!");
	        return null;
	    }

	    return line.substring(start, end).trim();
	}
	
	private static String cleanLatexDescription(final String description) {
	    return description
	        .replaceAll("~?\\\\cite\\{[^}]*\\}", "")
	        .replaceAll("~", " ")
	        .replaceAll("\\s+", " ")
	        .trim();
	}
	
	private static String toClassNamePrefix(final String alias) {
	    String[] words = alias.trim().split("[^A-Za-z0-9]+");

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
	
	private static String getPm4KnimePortType(final String ebiOutput) {
		// TODO: Include all supported files of Ebi, and we need to add specific output parameters in calling Ebi for the stochastic outputs
	    return switch (ebiOutput) {
	        case "event log", "XES event log", "compressed event log" ->
	            "XLogPortObject";

	        case "business process model and notation", "stochastic deterministic finite automaton", "stochastic non-deterministic finite automaton" -> 
	            "BpmnPortObject";

	        case "directly follows graph" ->
	            "DfgMsdPortObject";
	           
	        case "directly follows model" ->
	        	"DFMPortObject";
	            
	        case "labelled Petri net", "LoLa Petri net", "Petri net markup language", "stochastic labelled Petri net" ->
	            "PetriNetPortObject";

	        case "process tree", "process tree markup language", "stochastic process tree" ->
	            "ProcessTreePortObject";

	        default ->
	            "BufferedDataTable"; // or skip/TODO
	    };
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
	
	// TODO: change signature and add cases for TwoInput and ZeroInput
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
	            + "            category-path=\"/\"" + newline
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
	
	// TODO: Modify so that it takes an EbiCommandMetadata object as parameter
	private static void generateEbiNodeFactory(String commandName, String alias, String description, String outputType) {
		// TODO: scaffold class that extends EbiDefaultNodeFactory and add factories to plugin.xml
		// For Ebi convert log possibly create dynamic input ports and fixed ouput port XLog
		String classNamePrefix = toClassNamePrefix(alias);
		String factoryClassName = classNamePrefix + "NodeFactory";
		
		Path outputPath = Path.of(
	        "src",
	        "org",
	        "ryoo",
	        "knimeEbi",
	        "node",
	        factoryClassName + ".java"
	    );
		
		System.out.println(outputPath);

		String portType = "";
		if(EbiTableOutputType.isTableCompatible(outputType)) {
			portType = "BufferedDataTable";
		}
		else {
			// TODO: change, so that the output actually matches to the portTypes of pm4knime. Check portType and add import of the portObject.
			portType = getPm4KnimePortType(outputType);
		}
		
		try(BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)){
			writer.write("package org.ryoo.knimeEbi.node;");
			writer.newLine();
			writer.newLine();
			String portTypeImport = setPortTypeImport(portType);
			writer.write(portTypeImport);
			writer.newLine();
			writer.newLine();
			writer.write("public class " + factoryClassName + " extends EbiDefaultNodeFactory {");
			writer.newLine();
				writer.write("	public " + factoryClassName + "() {");
				writer.newLine();
				// TODO: 
					writer.write("		super(\"" + commandName + "\", \"" + description + "\", \"" + outputType + "\", " + portType + ".TYPE);");
					writer.newLine();
				writer.write("	}");
				writer.newLine();
				writer.newLine();
				String configure = setConfigure(portType, commandName);
				writer.write(configure);
				writer.newLine();
				writer.newLine();
				String execute = setExecute(portType, commandName, outputType);
				writer.write(execute);
				writer.newLine();
			writer.write("}");
		} catch (IOException e) {
			System.out.println("Error creating " + factoryClassName + ".");
			e.printStackTrace();
		}
		
		try {
			String fullyQualifiedFactoryClassName = "org.ryoo.knimeEbi.node." + factoryClassName;
			
			addNodeToPlugin(fullyQualifiedFactoryClassName);
		} catch (IOException e) {
			System.out.println("Error adding " + factoryClassName + " to plugin.xml.");
			e.printStackTrace();
		}
	}
	
	/*
	 * Extracts only Ebi commands that are available in Java.
	 * Creates list of command names in ebi-commands.txt, which stores all metadata of each command.
	 * Immediately scaffold from ebi-manual.txt
	 */
	
	// TODO: original text (iterate through split elements) -> metadata parameter call EbiCommandMetadata constructor
	// Look up regex Java, regex online editor
	public static void generateEbiNodes() throws IOException { // generateEbiNodes, so immediately scaffold -> change to only parsing from 
		String ebiItselfJavaOutput = CallEbi.call_ebi("Ebi itself java", "text", new String[0]);
		
		String [] ebiCommandsMetadata = ebiItselfJavaOutput.split("// ==");
		
		// Iterate from 2nd element
		for (int i = 1; i < ebiCommandsMetadata.length; i++) {
			EbiCommandMetadata ebiCommandBlock = new EbiCommandMetadata(ebiCommandsMetadata[i].trim());
			
			// generateEbiNodeFactories(...); <- Modify function
		}
	}
	
	public static void main(String[] args) {
		// TODO: Create CI/CD Pipeline
//		createEbiManual();
//		printEbiPluginsClass();
//		
//		try {
//		    generateEbiNodes();
//		} catch (IOException e) {
//		    System.out.println("Error generating Ebi nodes.");
//		    e.printStackTrace();
//		}
		
		Path eventLogPath = Path.of( 
			"src",
			"org",
			"ryoo",
			"knimeEbi",
			"scaffolder",
			"event-log.xes"
		);
		
		try {
			String xesContent = Files.readString(eventLogPath, StandardCharsets.UTF_8);
			CallEbi.call_ebi("Ebi analyse completeness", ".frac", new String[] {xesContent});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
