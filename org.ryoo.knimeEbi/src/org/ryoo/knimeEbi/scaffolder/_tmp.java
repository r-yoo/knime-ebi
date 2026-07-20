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
	
//	public static void generateEbiNodes() throws IOException { // generateEbiNodes, so immediately scaffold -> change to only parsing from 
//		Path inputPath = Path.of( 
//			"src",
//			"org",
//			"ryoo",
//			"knimeEbi",
//			"scaffolder",
//			"ebi-manual.txt"
//		);
//	 
//		Path outputPath = Path.of(
//	        "src",
//	        "org",
//	        "ryoo",
//	        "knimeEbi",
//	        "scaffolder",
//	        "ebi-commands.txt"
//	    );
//			
//		try(BufferedReader reader = Files.newBufferedReader(inputPath, StandardCharsets.UTF_8);
//			BufferedWriter writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8)){
//			String line;
//			
//			String prefix = "\\def\\numberofcommands{";
//			String suffix = "}";
//			
//			// Search for \numberofcommands
//			while(!(line = reader.readLine()).contains("\\numberofcommands") && (line != null)) {}
//			
//			int numberOfCommandsLeft = 0;
//			if(line != null) {
//				String numberOfCommandsString = extractStringFromLine(line, prefix, suffix);
//				if(numberOfCommandsString != null) {
//					numberOfCommandsLeft = Integer.parseInt(numberOfCommandsString);
//				}
//			}
//			
//			// Search for \ebicommands
//			while(!(line = reader.readLine()).contains("\\ebicommands") && (line != null)) {}
//			System.out.println("\\ebicommands found");
//			
//			String commandName = "";
//			String alias = "";
//			String description = "";
//			String outputType = "";
//			
//			// Read each line until \ebifilehandlers
//			while(!(line = reader.readLine()).contains("\\ebifilehandlers") && (line != null) && (numberOfCommandsLeft > 0)) {
//				
//				if(line.contains("\\label{command:")) {
//					numberOfCommandsLeft--;
//					
//					prefix = "\\label{command:";
//					suffix = "}";
//					commandName = extractStringFromLine(line, prefix, suffix);
//					
//					// Alias is directly under the command label
//					line = reader.readLine();
//					
//					if(line.contains("Alias")) {
//						alias = getContent(line);
//						line = reader.readLine();
//					}
//					else {
//						// There exists no alias
//						alias = commandName;
//					}
//					
//					description = "";
//					
//					while(!(line = reader.readLine()).contains("\\\\") && (line != null) && !(line.contains("Output"))) {
//						description += line.trim();
//					}
//					// Delete the two \\
//					line = line.substring(0, line.length() - 2);
//					description += line.trim();
//					
//					description = cleanLatexDescription(description);
//				}
//				
//				if(line.contains("\\noindent Output:")) {
//					outputType = extractOutputType(line);
//				}
//				
//				if(line.contains("This command is not available in Java and ProM") && commandName != null) {
//					continue;
//				}
//				else if(line.contains("This command is available in Java and ProM") && commandName != null) {
//					writer.write(commandName);
//					writer.newLine();
//					writer.write(alias);
//					writer.newLine();
//					writer.write(description);
//					writer.newLine();
//					writer.write(outputType);
//					writer.newLine();
//					writer.newLine();
//					generateEbiNodeFactory(commandName, alias, description, outputType);
//				}
//			}
//			
//			if(numberOfCommandsLeft == 0) {
//				System.out.println("All ebi commands found.");
//			} else if(numberOfCommandsLeft > 0) {
//				System.out.println("Not all ebi commands found.");
//			} else {
//				System.out.println("Error in counting number of commands.");
//			}
//			
//		} catch(IOException e) {
//			System.out.println("Error reading ebi-manual.txt.");
//			e.printStackTrace();
//		}
//		
//	}
}
