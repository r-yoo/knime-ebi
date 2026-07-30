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
		
		source.append(createExecuteMethodSource(metadata, settingsClassName));
		
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

	        case "DfgMsdPortObject" -> // Possibly remove those if I don't find a function like stringToPetrNet
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
	    validatePortMetadata(metadata);

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
	
	private static void validatePortMetadata(final EbiCommandMetadata metadata) {
	    if (metadata == null) {
	        throw new IllegalArgumentException("Metadata must not be null.");
	    }

	    if (metadata.inputs == null) {
	        throw new IllegalArgumentException("Metadata inputs must not be null.");
	    }

	    for (EbiCommandMetadataParameter input : metadata.inputs) {
	        if (input == null) {
	            throw new IllegalArgumentException("Metadata inputs must not contain null.");
	        }

	        if (input.isPort && input.portType.isBlank()) {
	            throw new IllegalArgumentException("Input port type must not be empty.");
	        }
	    }

	    if (metadata.output == null || !metadata.output.isPort) {
	        throw new IllegalArgumentException("Output must be a non-null port parameter.");
	    }

	    if (metadata.output.portType.isBlank()) {
	        throw new IllegalArgumentException("Output port type must not be empty.");
	    }
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
			return "        output.setOutSpec(0, TableUtil.createOutputSpec(\"" + metadata.commandName + "\", \"" + metadata.commandName + "\", StringCell.TYPE));\r\n";
		}
		else {
			return "        output.setOutSpec(0, new " + metadata.output.portType + "Spec());\r\n";
		}	
	}
	
	private static String createExecuteMethodSource(final EbiCommandMetadata metadata, final String settingsClassName) {
		validateExecuteMetadata(metadata, settingsClassName);

		StringBuilder source = new StringBuilder();
		source.append("    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {\r\n");
		source.append("        try {\r\n");
		source.append("            final String[] ebiInputs = new String[").append(metadata.inputs.size()).append("];\r\n");

		if (!metadata.hasNoPrimitiveInputs()) {
			source.append("            final ").append(settingsClassName).append(" settings = input.getParameters();\r\n");
		}

		if (!metadata.inputs.isEmpty()) {
			source.append("\r\n");
		}

		int portIndex = 0;
		for (int metadataIndex = 0; metadataIndex < metadata.inputs.size(); metadataIndex++) {
			EbiCommandMetadataParameter parameter = metadata.inputs.get(metadataIndex);

			if (parameter.isPort) {
				source.append(createPortInputConversionSource(parameter, metadataIndex, portIndex));
				portIndex++;
			}
			else {
				source.append("            ebiInputs[").append(metadataIndex).append("] = String.valueOf(settings.")
					.append(EbiNodeSettingsSourceGenerator.createSettingsFieldName(metadataIndex)).append(");\r\n");
			}
		}

		source.append("\r\n");
		source.append("            final String result = CallEbi.call_ebi(\r\n");
		source.append("                \"").append(JavaSourceUtil.escapeJavaString(metadata.commandName)).append("\",\r\n");
		source.append("                \"").append(JavaSourceUtil.escapeJavaString(getFileExtension(metadata.output.type))).append("\",\r\n");
		source.append("                ebiInputs);\r\n");
		source.append("\r\n");
		source.append(createOutputConversionSource(metadata));
		source.append("        } catch (Exception ex) {\r\n");
		source.append("            throw new RuntimeException(\"Ebi command failed: ")
			.append(JavaSourceUtil.escapeJavaString(metadata.commandName)).append("\", ex);\r\n");
		source.append("        }\r\n");
		source.append("    }\r\n");

		return source.toString();
	}
	
	private static void validateExecuteMetadata(final EbiCommandMetadata metadata, final String settingsClassName) {
		validatePortMetadata(metadata);

		if (!metadata.hasNoPrimitiveInputs() && (settingsClassName == null || settingsClassName.isBlank())) {
			throw new IllegalArgumentException("A settings class name is required for primitive Ebi inputs.");
		}

		for (EbiCommandMetadataParameter parameter : metadata.inputs) {
			if (parameter.isPort) {
				validateSupportedInputPortType(parameter.portType);
			}
		}

		validateSupportedOutputPortType(metadata.output.portType);
		getFileExtension(metadata.output.type);
	}
	
	private static void validateSupportedInputPortType(final String portType) {
		switch (portType) {
			case "XLogPortObject", "PetriNetPortObject", "ProcessTreePortObject":
				return;
			default:
				throw new IllegalArgumentException(
					"Cannot generate input conversion for unsupported port type: " + portType
				);
		}
	}
	
	private static void validateSupportedOutputPortType(final String portType) {
		switch (portType) {
			case "XLogPortObject", "PetriNetPortObject", "ProcessTreePortObject", "BufferedDataTable":
				return;
			default:
				throw new IllegalArgumentException(
					"Cannot generate output conversion for unsupported port type: " + portType
				);
		}
	}
	
	private static String getFileExtension(final String outputType) {
	    if (outputType == null) {
	        throw new IllegalArgumentException("Output type must not be null.");
	    }

	    // TODO: Add also other outputTypes
	    return switch (outputType.toLowerCase()) {
	        case "petrinet"    -> ".pnml";
	        case "xlog"        -> ".xes";
	        case "fraction"    -> ".frac";
	        case "processtree" -> ".ptml";
	        case "string"      -> ".txt";
	        case "boolean"     -> ".bool";
	        case "containsroot_html" -> ".croot";
	        case "logdiv"      -> ".logdiv";
	        case "rootlogdiv"  -> ".rldiv";
	        default -> throw new IllegalArgumentException(
	            "Unsupported output type: " + outputType
	        );
	    };
	}
	
	private static String createPortInputConversionSource(final EbiCommandMetadataParameter parameter,
			final int metadataIndex, final int portIndex) {
		return switch (parameter.portType) {
			case "XLogPortObject" ->
				"            ebiInputs[" + metadataIndex + "] = XESUtil.writeLogToXesString(input.getInPortObject("
					+ portIndex + "));\r\n";

			case "PetriNetPortObject" ->
				"            final PetriNetPortObject inputPort" + portIndex
					+ " = input.getInPortObject(" + portIndex + ");\r\n"
					+ "            final ByteArrayOutputStream inputBuffer" + portIndex
					+ " = new ByteArrayOutputStream();\r\n"
					+ "            PetriNetUtil.exportToStream(inputPort" + portIndex
					+ ".getANet(), inputBuffer" + portIndex + ");\r\n"
					+ "            ebiInputs[" + metadataIndex + "] = inputBuffer" + portIndex
					+ ".toString(StandardCharsets.UTF_8);\r\n";

			case "ProcessTreePortObject" ->
				"            final ProcessTreePortObject inputPort" + portIndex
					+ " = input.getInPortObject(" + portIndex + ");\r\n"
					+ "            ebiInputs[" + metadataIndex + "] = inputPort" + portIndex + ".toText();\r\n";

			default ->
				throw new IllegalArgumentException(
					"Cannot generate input conversion for unsupported port type: " + parameter.portType
				);
		};
	}
	
	private static String createOutputConversionSource(final EbiCommandMetadata metadata) {
		return switch (metadata.output.portType) {
			case "BufferedDataTable" ->
				"            final DataTableSpec spec = TableUtil.createOutputSpec(\r\n"
					+ "                COMMAND_METADATA.commandName,\r\n"
					+ "                COMMAND_METADATA.output.type,\r\n"
					+ "                StringCell.TYPE);\r\n"
					+ "            final BufferedDataContainer container = input.getExecutionContext().createDataContainer(spec);\r\n"
					+ "            container.addRowToTable(new DefaultRow(\"Row0\", new StringCell(result)));\r\n"
					+ "            container.close();\r\n"
					+ "            output.setOutData(0, container.getTable());\r\n";

			case "XLogPortObject" ->
				"            final XLogPortObject resultPort = new XLogPortObject(\r\n"
					+ "                XLogUtil.loadLog(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8))));\r\n"
					+ "            output.setOutData(0, resultPort);\r\n";

			case "PetriNetPortObject" ->
				"            final PetriNetPortObject resultPort = new PetriNetPortObject(\r\n"
					+ "                PetriNetUtil.stringToPetriNet(result));\r\n"
					+ "            output.setOutData(0, resultPort);\r\n";

			case "ProcessTreePortObject" ->
				"            final ProcessTreePortObject resultPort = new ProcessTreePortObject();\r\n"
					+ "            resultPort.loadFromDefault(\r\n"
					+ "                new ProcessTreePortObjectSpec(),\r\n"
					+ "                new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8)));\r\n"
					+ "            output.setOutData(0, resultPort);\r\n";

			default ->
				throw new IllegalArgumentException(
					"Cannot generate output conversion for unsupported port type: " + metadata.output.portType
				);
		};
	}
}
