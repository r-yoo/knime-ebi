package org.ryoo.knimeEbi.scaffolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.processmining.ebi.CallEbi;

public class NodeFactoryFileGenerator {
	
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
	
	private static String setExecute(final String portType, final String commandName, final String outputType) {
		String executeString = "";
		
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
	
	private static void createEbiNodeFactory(String commandName, String alias, String description, String outputType) {
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
			System.out.println("Error creating EbiNodeFactory.");
			e.printStackTrace();
		}
		
		// TODO: new outputPath to plugin.xml
	}
	
	/*
	 * Extracts only Ebi commands that are available in Java.
	 * Creates list of command names in ebi-commands.txt and ebi-commands.json, which stores all metadata of each command.
	 * Immediately scaffold from ebi-manual.txt
	 */
	public static void createEbiNodes() throws IOException { // generateEbiNodes, so immediately scaffold
		Path inputPath = Path.of( 
			"src",
			"org",
			"ryoo",
			"knimeEbi",
			"scaffolder",
			"ebi-manual.txt"
		);
	 
		Path outputPath = Path.of(
	        "src",
	        "org",
	        "ryoo",
	        "knimeEbi",
	        "scaffolder",
	        "ebi-commands.txt"
	    );
			
		try(BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
			BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)){
			String line;
			
			String prefix = "\\def\\numberofcommands{";
			String suffix = "}";
			
			// Search for \numberofcommands
			while(!(line = reader.readLine()).contains("\\numberofcommands") && (line != null)) {}
			
			int numberOfCommandsLeft = 0;
			if(line != null) {
				String numberOfCommandsString = extractStringFromLine(line, prefix, suffix);
				if(numberOfCommandsString != null) {
					numberOfCommandsLeft = Integer.parseInt(numberOfCommandsString);
				}
			}
			
			// Search for \ebicommands
			while(!(line = reader.readLine()).contains("\\ebicommands") && (line != null)) {}
			System.out.println("\\ebicommands found");
			
			String commandName = "";
			String alias = "";
			String description = "";
			String outputType = "";
			
			// Read each line until \ebifilehandlers
			while(!(line = reader.readLine()).contains("\\ebifilehandlers") && (line != null) && (numberOfCommandsLeft > 0)) {
				
				if(line.contains("\\label{command:")) {
					numberOfCommandsLeft--;
					
					prefix = "\\label{command:";
					suffix = "}";
					commandName = extractStringFromLine(line, prefix, suffix);
				}
				
				if(line.contains("Alias")) {
					description = "";
					alias = getContent(line);
					
					while(!(line = reader.readLine()).contains("\\\\") && (line != null) && !(line.contains("Output"))) {
						description += line.trim();
					}
					// Delete the two \\
					line = line.substring(0, line.length() - 2);
					description += line.trim();
					
					description = cleanLatexDescription(description);
				}
				
				if(line.contains("\\noindent Output:")) {
					outputType = extractOutputType(line);
				}
				
				if(line.contains("This command is not available in Java and ProM") && commandName != null) {
					continue;
				}
				else if(line.contains("This command is available in Java and ProM") && commandName != null) {
					writer.write(commandName);
					writer.newLine();
					writer.write(alias);
					writer.newLine();
					writer.write(description);
					writer.newLine();
					writer.write(outputType);
					writer.newLine();
					writer.newLine();
					createEbiNodeFactory(commandName, alias, description, outputType);
				}
			}
			
			if(numberOfCommandsLeft == 0) {
				System.out.println("All ebi commands found.");
			} else if(numberOfCommandsLeft > 0) {
				System.out.println("Not all ebi commands found.");
			} else {
				System.out.println("Error in counting number of commands.");
			}
			
		} catch(IOException e) {
			System.out.println("Error reading ebi-manual.txt.");
			e.printStackTrace();
		}
		
	}
	
	public static void main(String[] args) {
		// TODO: Create CI/CD Pipeline
		createEbiManual();
		
		try {
		    createEbiNodes();
		} catch (IOException e) {
		    System.out.println("Error creating Ebi nodes.");
		    e.printStackTrace();
		}
	}
}
