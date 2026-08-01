package org.ryoo.knimeEbi.scaffolder;

import java.util.HashSet;
import java.util.Set;

public final class EbiNodeFactorySourceGenerator {
	
	public static String generate(final EbiCommandMetadata metadata, final String factoryClassName, final String settingsClassName) {
		String newLine = System.lineSeparator();
		StringBuilder source = new StringBuilder();
		
		source.append("package org.ryoo.knimeEbi.node;").append(newLine).append(newLine);
		
		source.append(createImportSectionSource(metadata));
		
		source.append("public class " + factoryClassName + " extends EbiDefaultNodeFactory {").append(newLine);
		
		source.append(createEbiCommandMetadataConstantSource(metadata));
		
		source.append(createConstructorSource(factoryClassName));
		
		source.append(createAddPortsMethodSource(metadata));
		
		source.append(createConfigureModelMethodSource(metadata, factoryClassName, settingsClassName));
		
		source.append(createConfigureMethodSource(metadata));
		
		source.append(EbiNodeExecuteMethodSourceGenerator.generate(metadata, settingsClassName));
		
		source.append("}").append(newLine);
		
		return source.toString();
	}
	
	private static String createImportSectionSource(final EbiCommandMetadata metadata) {
		String newLine = System.lineSeparator();
		String importSectionSource = "import java.io.ByteArrayInputStream;" + newLine
									+ "import java.io.ByteArrayOutputStream;" + newLine
									+ "import java.nio.charset.StandardCharsets;" + newLine
									+ "import java.util.ArrayList;" + newLine
									+ "import java.util.List;" + newLine
									+ newLine
									+ "import org.knime.core.node.InvalidSettingsException;" + newLine
									+ "import org.knime.node.DefaultModel;" + newLine
									+ "import org.knime.node.DefaultModel.RequireModelParameters;" + newLine
									+ "import org.knime.node.RequirePorts.PortsAdder;" + newLine
									+ newLine;
		importSectionSource += "import org.processmining.ebi.CallEbi;" + newLine
							+ "import org.ryoo.knimeEbi.defaultNode.EbiDefaultNodeFactory;" + newLine
							+ "import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadata;" + newLine
							+ "import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadataParameter;" + newLine
							+ "import org.ryoo.knimeEbi.util.*;" + newLine 
							+ newLine;
		
		Set<String> addedPortTypes = new HashSet<>();
		
		for(EbiCommandMetadataParameter parameter : metadata.inputs) {
			if(!parameter.isPort || addedPortTypes.contains(parameter.portType)) {
				continue;
			}
			
			importSectionSource += createPortTypeImportDeclarations(parameter.portType);
			addedPortTypes.add(parameter.portType);
		}
		
		if (metadata.output.isPort && !addedPortTypes.contains(metadata.output.portType)) {
		    importSectionSource += createPortTypeImportDeclarations(metadata.output.portType);
		    addedPortTypes.add(metadata.output.portType);
		}
		
		return importSectionSource;
	}
	
	private static String createPortTypeImportDeclarations(final String portType) {
	    String newLine = System.lineSeparator();

	    return switch (portType) {
	        case "BufferedDataTable" ->
	            "import org.knime.core.data.DataTableSpec;" + newLine
	            + "import org.knime.core.data.def.StringCell;" + newLine
	            + "import org.knime.core.data.def.DefaultRow;" + newLine
	            + "import org.knime.core.node.BufferedDataContainer;" + newLine
	            + "import org.knime.core.node.BufferedDataTable;" + newLine
	            + newLine;

	        case "XLogPortObject" ->
	            "import org.pm4knime.portobject.XLogPortObject;" + newLine
	            + "import org.pm4knime.portobject.XLogPortObjectSpec;" + newLine
	            + "import org.pm4knime.util.XLogUtil;" + newLine
	            + newLine;

	        case "PetriNetPortObject" ->
	            "import org.pm4knime.portobject.PetriNetPortObject;" + newLine
	            + "import org.pm4knime.portobject.PetriNetPortObjectSpec;" + newLine
	            + "import org.pm4knime.util.PetriNetUtil;" + newLine
	            + newLine;

	        case "ProcessTreePortObject" ->
	            "import org.pm4knime.portobject.ProcessTreePortObject;" + newLine
	            + "import org.pm4knime.portobject.ProcessTreePortObjectSpec;" + newLine
	            + newLine;

	        case "DfgMsdPortObject" -> // TODO: Possibly remove those if I don't find a function like stringToPetrNet
	            "import org.pm4knime.portobject.DfgMsdPortObjectSpec;" + newLine
	            + newLine;

	        case "DFMPortObject" -> // Possibly remove those if I don't find a function like stringToPetrNet
	            "import org.pm4knime.portobject.DFMPortObjectSpec;" + newLine
	            + newLine;

	        default ->
	            throw new IllegalArgumentException(portType + " is unsupported!");
	    };
	}
	
	private static String createEbiCommandMetadataConstantSource(final EbiCommandMetadata metadata) {
		if (metadata == null) {
			throw new IllegalArgumentException("Ebi command metadata must not be null.");
		}
		
		if (metadata.output == null) {
			throw new IllegalArgumentException("Ebi command metadata must have an output.");
		}
		
		String newLine = System.lineSeparator();
		StringBuilder source = new StringBuilder();
		
		source.append("\tprivate static final EbiCommandMetadata COMMAND_METADATA =").append(newLine);
		source.append("\t\tnew EbiCommandMetadata(").append(newLine);
		source.append("\t\t\t\"").append(JavaSourceUtil.escapeJavaString(metadata.commandName)).append("\",").append(newLine);
		source.append("\t\t\t\"").append(JavaSourceUtil.escapeJavaString(metadata.shortDescription)).append("\",").append(newLine);
		source.append("\t\t\t\"").append(JavaSourceUtil.escapeJavaString(metadata.fullDescription)).append("\",").append(newLine);
		source.append("\t\t\tnew ArrayList<>(").append(newLine);
		source.append("\t\t\t\tList.of(").append(newLine);
		
		for (int i = 0; i < metadata.inputs.size(); i++) {
			EbiCommandMetadataParameter input = metadata.inputs.get(i);
			source.append(createEbiCommandMetadataParameterSource(input, "\t\t\t\t\t"));
			
			if (i < metadata.inputs.size() - 1) {
				source.append(",");
			}
			
			source.append(newLine);
		}
		
		source.append("\t\t\t\t)").append(newLine);
		source.append("\t\t\t),").append(newLine);
		source.append(createEbiCommandMetadataParameterSource(metadata.output, "\t\t\t"));
		source.append(newLine);
		source.append("\t\t);").append(newLine).append(newLine);
		
		return source.toString();
	}
	
	private static String createEbiCommandMetadataParameterSource(final EbiCommandMetadataParameter parameter, final String indentation) {
		
		if (parameter == null) {
			throw new IllegalArgumentException("Ebi command metadata parameter must not be null.");
		}
		
		String newLine = System.lineSeparator();
		StringBuilder source = new StringBuilder();
		
		source.append(indentation).append("new EbiCommandMetadataParameter(").append(newLine);
		source.append(indentation).append("\t\"").append(JavaSourceUtil.escapeJavaString(parameter.type)).append("\",").append(newLine);
		source.append(indentation).append("\t\"").append(JavaSourceUtil.escapeJavaString(parameter.portType)).append("\",").append(newLine);
		source.append(indentation).append("\t\"").append(JavaSourceUtil.escapeJavaString(parameter.typeDescription)).append("\",").append(newLine);
		source.append(indentation).append("\t").append(parameter.isPort).append(newLine);
		source.append(indentation).append(")");
		
		return source.toString();
	}
	
	private static String createConstructorSource(final String factoryClassName) {
		String newLine = System.lineSeparator();
		
		String constructorSource = "\tpublic "+ factoryClassName + "() {" + newLine
								+ "\t\tsuper(COMMAND_METADATA, " + factoryClassName + "::addPorts, " + factoryClassName + "::configureModel);" + newLine
								+ "\t}" + newLine
								+ newLine;
		
		return constructorSource;
	}
	
	private static String createAddPortsMethodSource(final EbiCommandMetadata metadata) {
	    EbiCommandMetadata.validatePortMetadata(metadata);

	    return """
	            private static void addPorts(final PortsAdder ports) {
	                for (EbiCommandMetadataParameter input : COMMAND_METADATA.inputs) {
	                    if (input.isPort) {
	                        ports.addInputPort(input.type, input.type, resolvePortType(input.portType));
	                    }
	                }

	                EbiCommandMetadataParameter output = COMMAND_METADATA.output;
	                ports.addOutputPort(output.type, output.type, resolvePortType(output.portType));
	            }

	        """;
	}
	
	private static String createConfigureModelMethodSource(final EbiCommandMetadata metadata, final String factoryClassName, final String settingsClassName) {
		if(metadata.hasNoPrimitiveInputs()) {
			return "    private static DefaultModel configureModel(final RequireModelParameters model) {\r\n"
					+ "        return model\r\n"
					+ "            .withoutParameters()\r\n"
					+ "            .configure(" + factoryClassName + "::configure)\r\n"
					+ "            .execute(" + factoryClassName + "::execute);\r\n"
					+ "    }\r\n\r\n";
		}
		else {
			return """
				    private static DefaultModel configureModel(final RequireModelParameters model) {
				        return model
				            .parametersClass(%s.class)
				            .configure(%s::configure)
				            .execute(%s::execute);
				    } 
					
				""".formatted(settingsClassName, factoryClassName, factoryClassName);
		}
		
	}
	
	private static String createConfigureMethodSource(final EbiCommandMetadata metadata) {
		String configureString = "    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) \r\n"
							+ "    	throws InvalidSettingsException {\r\n"
							+ "     \r\n";
		
		int getInPortSpecIndex = 0;
		// TODO: Retrieve settings parameters in configure or not?
		for(EbiCommandMetadataParameter input : metadata.inputs) {
			if(!input.isPort) {
				continue;
			}
			
			configureString += "        if (!(input.getInPortSpec(" + getInPortSpecIndex + ") instanceof " + input.portType + "Spec)) {\r\n"
							 + "            throw new InvalidSettingsException(\"Input is not a valid " + input.portType + "!\");\r\n"
							 + "        }\r\n"
							 + "\r\n";
			
			getInPortSpecIndex++;
		}
		
		configureString += createOutputSpecStatement(metadata);
		
		configureString += "    }\r\n\r\n";
		
		return configureString;
	}
	
	private static String createOutputSpecStatement(EbiCommandMetadata metadata) {
		
		if("BufferedDataTable".equals(metadata.output.portType)) {
			return "        output.setOutSpec(0, TableUtil.createOutputSpec(\"" + metadata.commandName + "\", \"" + metadata.output.type + "\", StringCell.TYPE));\r\n";
		}
		else {
			return "        output.setOutSpec(0, new " + metadata.output.portType + "Spec());\r\n";
		}	
	}
}
