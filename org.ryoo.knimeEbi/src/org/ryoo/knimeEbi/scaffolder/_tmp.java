package org.ryoo.knimeEbi.scaffolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

// Loeschen nicht vergessen!!!

public class _tmp {
	// ebi-itself-java
	/*
	public static void generateEbiNodes() throws IOException { // generateEbiNodes, so immediately scaffold -> change to only parsing from 
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
			String shortDescription = "";
			String outputType = "";
			
			// Read each line until \ebifilehandlers
			while(!(line = reader.readLine()).contains("\\ebifilehandlers") && (line != null) && (numberOfCommandsLeft > 0)) {
				
				if(line.contains("\\label{command:")) {
					numberOfCommandsLeft--;
					
					prefix = "\\label{command:";
					suffix = "}";
					commandName = extractStringFromLine(line, prefix, suffix);
					
					// Alias is directly under the command label
					line = reader.readLine();
					
					if(line.contains("Alias")) {
						alias = getContent(line);
						line = reader.readLine();
					}
					else {
						// There exists no alias
						alias = commandName;
					}
					
					shortDescription = "";
					
					while(!(line = reader.readLine()).contains("\\\\") && (line != null) && !(line.contains("Output"))) {
						shortDescription += line.trim();
					}
					// Delete the two \\
					line = line.substring(0, line.length() - 2);
					shortDescription += line.trim();
					
					shortDescription = cleanLatexDescription(shortDescription);
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
					writer.write(shortDescription);
					writer.newLine();
					writer.write(outputType);
					writer.newLine();
					writer.newLine();
					generateEbiNodeFactory(commandName, alias, shortDescription, outputType);
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
	
	private static void generateEbiNodeFactory(String commandName, String alias, String shortDescription, String outputType) {
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
				// TODO: Change to EbiCommandMetadata attributes
					writer.write("		super(\"" + commandName + "\", \"" + shortDescription + "\", \"" + outputType + "\", " + portType + ".TYPE);");
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
	
	
	 // Extract Content within {}
	 
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
	
	private static String cleanLatexDescription(final String shortDescription) {
	    return shortDescription
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
	
	*/
}
