package org.ryoo.knimeEbi.scaffolder;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
	
	public static void main(String[] args) {
		try {
			createTest();
		} catch (IOException e) {
			System.out.println("Error calling function createTest().");
			e.printStackTrace();
		}
	}
}
