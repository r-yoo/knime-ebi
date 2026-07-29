package org.ryoo.knimeEbi.entryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.processmining.ebi.CallEbi;
import org.ryoo.knimeEbi.scaffolder.NodeFactoryFileGenerator;

public class Main {
	public static void main(String[] args) {
		NodeFactoryFileGenerator.generateEbiNodeFactories();
		/*
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
		}*/
	}
}
