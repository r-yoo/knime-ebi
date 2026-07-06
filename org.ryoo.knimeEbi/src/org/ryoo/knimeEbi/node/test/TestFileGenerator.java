package org.ryoo.knimeEbi.node.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class TestFileGenerator {
	
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
}
