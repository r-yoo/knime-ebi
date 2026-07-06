package org.ryoo.knimeEbi.scaffolder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.processmining.ebi.CallEbi;

public class NodeFactoryFileGenerator {
	
	public static void createTest() throws IOException {
		Path outputPath = Path.of(
			"src",
			"org",
			"ryoo",
			"knimeEbi",
			"node",
			"test", // read function name directly from Ebi
			"TestFactory.java" // read function name directly from Ebi
		);
		
		Files.createDirectories(outputPath.getParent());
		
		try(BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)){
			writer.write("package org.ryoo.knimeEbi.node.test;");
			writer.newLine();
			writer.newLine();
			writer.write("public class TestFactory {");
			writer.newLine();
				writer.write("	public static void main(String[] args) {");
				writer.newLine();
					writer.write("		System.out.println(\"Hello World!\");");
					writer.newLine();
				writer.write("	}");
			writer.newLine();
			writer.write("}");
		} catch (IOException e) {
			System.out.println("Error creating NodeFactory.");
			e.printStackTrace();
		}
	}
	
	public static void readTest() throws IOException {
		Path inputPath = Path.of( 
			"src",
			"org",
			"ryoo",
			"knimeEbi",
			"node",
			"test", // read function name from a specific scaffolder folder
			"TestFactory.java" // read function name from a specific scaffolder folder
		);
	 
			
		try(BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8)){
			String line;
			while((line = reader.readLine()) != null) {
				System.out.println(line);
			}
		} catch(IOException e) {
			System.out.println("Error reading NodeFactory.");
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
	
	public static void extractEbiCommands() throws IOException {
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
			
			// Search for \numberofcommands
			String prefix = "\\def\\numberofcommands{";
			String suffix = "}";
			
			while(!(line = reader.readLine()).contains("\\numberofcommands") && (line != null)) {}
			
			int numberOfCommands = 0;
			if(line != null) {
				String numberOfCommandsString = extractStringFromLine(line, prefix, suffix);
				if(numberOfCommandsString != null) {
					numberOfCommands = Integer.parseInt(numberOfCommandsString);
				}
			}
			
			// Search for \ebicommands
			while(!(line = reader.readLine()).contains("\\ebicommands") && (line != null)) {}
			System.out.println("ebicommands found");
			
			// Read each line until \ebifilehandlers
			String commandName = null;
			prefix = "\\label{command:";
			suffix = "}";
			
			while(!(line = reader.readLine()).contains("\\ebifilehandlers") && (line != null) && (numberOfCommands > 0)) {
				
				if(line.contains("\\label{command:")) {
					numberOfCommands--;
					commandName = extractStringFromLine(line, prefix, suffix);
				}
				
				if(line.contains("This command is available in Java and ProM") && commandName != null) {
					writer.write(commandName);
					writer.newLine();
				}
				
			}
			
			if(numberOfCommands == 0) {
				System.out.println("All ebi commands found.");
			} else if(numberOfCommands > 0) {
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
		
		// Just parse from ebi-manual.txt -> search for 1.2 Java/ProM plug-in -> check all commands available in Java
//		String manual = CallEbi.call_ebi("Ebi itself manual", "text", new String[0]);
//		
//		Path outputPath = Path.of(
//			"src",
//			"org",
//			"ryoo",
//			"knimeEbi",
//			"scaffolder",
//			"ebi-manual.txt" // \ebicommands to \ebifilehandlers
//		);
//		
//		try {
//			Files.writeString(outputPath, manual, StandardCharsets.UTF_8);
//		} catch (IOException e) {
//			System.out.println("Error writing manual to txt-file.");
//			e.printStackTrace();
//		} // <- separate to function Ebi print manual into txt
		
		try {
		    extractEbiCommands();
		} catch (IOException e) {
		    System.out.println("Error extracting Ebi commands.");
		    e.printStackTrace();
		}
	}
}
