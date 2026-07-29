package org.ryoo.knimeEbi.scaffolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.processmining.ebi.CallEbi;

public class EbiNodeGenerator {
	private static final Pattern PLUGIN_DECLARATION_PATTERN = Pattern.compile("(?m)^\\s*@Plugin\\s*\\(");
	
	/*
	 * Extracts only Ebi commands that are available in Java.
	 * Immediately scaffold from output of Ebi itself java
	 * original text (iterate through split elements) -> metadata parameter call EbiCommandMetadata constructor
	 */
	public static void generateEbiNodes() {
		System.out.println("Starting generating Ebi Nodes...");
		
		String ebiItselfJavaOutput = CallEbi.call_ebi("Ebi itself java", ".txt", new String[0]);
		
		String [] ebiCommandsMetadataBlocks = ebiItselfJavaOutput.split("// == command");

		// Iterate from 2nd element
		for (int i = 1; i < ebiCommandsMetadataBlocks.length; i++) {
			String metadataBlock = ebiCommandsMetadataBlocks[i].trim();
			
			if (!containsPluginDeclaration(metadataBlock)) {
		        String commandName = metadataBlock.split("==", 2)[0].trim();
		        System.out.println("Skipping command without plugin declaration: " + commandName);
		        continue;
		    }
			
			// For checking metadata extraction
			EbiCommandMetadata metadata = new EbiCommandMetadata(metadataBlock);
			System.out.println(metadata.toString());
			
			String factoryClassName = toClassNamePrefix(metadata.commandName) + "NodeFactory";
			String settingsClassName = toClassNamePrefix(metadata.commandName) + "NodeSettings";
			
			generateEbiNodeFactory(metadata, factoryClassName, settingsClassName);
			
			if(!metadata.hasNoPrimitiveInputs()) {
				generateEbiNodeSettings(metadata, settingsClassName);
			}
			
			// Adding Node Factory to plugin.xml
			try {
				String fullyQualifiedFactoryClassName = "org.ryoo.knimeEbi.node." + factoryClassName;
				
				addNodeToPlugin(fullyQualifiedFactoryClassName);
			} catch (IOException e) {
				System.out.println("Error adding " + factoryClassName + " to plugin.xml");
				e.printStackTrace();
			}
		}
		
		System.out.println("Generated all Ebi Nodes.");
	}
	
	private static boolean containsPluginDeclaration(final String metadataBlock) {
	    return metadataBlock != null && PLUGIN_DECLARATION_PATTERN.matcher(metadataBlock).find();
	}
	
	private static void generateEbiNodeFactory(final EbiCommandMetadata metadata, final String factoryClassName, final String settingsClassName) {
		if(metadata.inputs == null || metadata.inputs.size() == 0) {
			System.out.println(metadata.commandName + " is a itself type command...");
			System.out.println("Node Factory will not be created...");
			return;
		}
		
		String ebiNodeFactorySource = createEbiNodeFactorySource(metadata, factoryClassName, settingsClassName);
		
		try {
			Files.writeString(
					Path.of("src", "org", "ryoo", "knimeEbi", "node", factoryClassName + ".java"),
				    ebiNodeFactorySource,
				    StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			System.out.println("Error writing " + factoryClassName + ".java");
			e.printStackTrace();
		}
	}
	
	private static void generateEbiNodeSettings(final EbiCommandMetadata metadata, final String settingsClassName) {
		if(metadata.inputs == null || metadata.inputs.size() == 0) {
			System.out.println(metadata.commandName + " is a itself type command...");
			System.out.println("Node Settings will not be created...");
			return;
		}

		String ebiNodeSettingsSource = createEbiNodeSettingsSource(metadata, settingsClassName);

		try {
			Files.writeString(
					Path.of("src", "org", "ryoo", "knimeEbi", "node", settingsClassName + ".java"),
				    ebiNodeSettingsSource,
				    StandardCharsets.UTF_8
			);
		} catch (IOException e) {
			System.out.println("Error writing " + settingsClassName + ".java");
			e.printStackTrace();
		}
	}

	private static String createEbiNodeSettingsSource(final EbiCommandMetadata metadata, final String settingsClassName) {
		validateSettingsMetadata(metadata, settingsClassName);

		String newLine = System.lineSeparator();
		StringBuilder source = new StringBuilder();

		source.append("package org.ryoo.knimeEbi.node;").append(newLine).append(newLine);
		source.append("import org.knime.node.parameters.NodeParameters;").append(newLine);
		source.append("import org.knime.node.parameters.Widget;").append(newLine);

		if (hasNumericSettingsInput(metadata)) {
			source.append("import org.knime.node.parameters.widget.number.NumberInputWidget;").append(newLine);
		}

		source.append(newLine);
		source.append("public final class ").append(settingsClassName).append(" implements NodeParameters {")
			.append(newLine).append(newLine);

		int settingsIndex = 0;
		for (int metadataIndex = 0; metadataIndex < metadata.inputs.size(); metadataIndex++) {
			EbiCommandMetadataParameter parameter = metadata.inputs.get(metadataIndex);

			if (parameter.isPort) {
				continue;
			}

			String title = createSettingsWidgetTitle(parameter, settingsIndex);
			String description = createSettingsWidgetDescription(metadata, parameter, settingsIndex);

			source.append("    @Widget(title = \"").append(escapeJavaString(title))
				.append("\", description = \"").append(escapeJavaString(description)).append("\")")
				.append(newLine);

			if (settingUsesNumberInputWidget(parameter.type)) {
				source.append("    @NumberInputWidget").append(newLine);
			}

			source.append("    ").append(createSettingsFieldDeclaration(parameter, metadataIndex))
				.append(newLine).append(newLine);
			settingsIndex++;
		}

		source.append("}").append(newLine);
		return source.toString();
	}

	private static void validateSettingsMetadata(final EbiCommandMetadata metadata, final String settingsClassName) {
		if (metadata == null) {
			throw new IllegalArgumentException("Metadata must not be null.");
		}

		if (metadata.inputs == null) {
			throw new IllegalArgumentException("Metadata inputs must not be null.");
		}

		if (settingsClassName == null || settingsClassName.isBlank()) {
			throw new IllegalArgumentException("Settings class name must not be empty.");
		}

		if (!isJavaIdentifier(settingsClassName)) {
			throw new IllegalArgumentException("Invalid settings class name: " + settingsClassName);
		}

		if (metadata.hasNoPrimitiveInputs()) {
			throw new IllegalArgumentException("Cannot create a settings class without settings inputs.");
		}

		for (EbiCommandMetadataParameter parameter : metadata.inputs) {
			if (parameter == null) {
				throw new IllegalArgumentException("Metadata inputs must not contain null.");
			}

			if (!parameter.isPort) {
				validateSupportedSettingsType(parameter.type);
			}
		}
	}

	private static boolean isJavaIdentifier(final String value) {
		if (value.isEmpty() || !Character.isJavaIdentifierStart(value.charAt(0))) {
			return false;
		}

		for (int i = 1; i < value.length(); i++) {
			if (!Character.isJavaIdentifierPart(value.charAt(i))) {
				return false;
			}
		}

		return true;
	}

	private static void validateSupportedSettingsType(final String type) {
		if (type == null || type.isBlank()) {
			throw new IllegalArgumentException("Ebi settings type must not be empty.");
		}

		switch (type) {
			case "Integer", "String", "Byte", "Short", "Long", "Double", "Character", "Boolean", "BigFraction":
				return;
			default:
				throw new IllegalArgumentException("Unsupported Ebi settings type: " + type);
		}
	}

	private static boolean hasNumericSettingsInput(final EbiCommandMetadata metadata) {
		for (EbiCommandMetadataParameter parameter : metadata.inputs) {
			if (!parameter.isPort && settingUsesNumberInputWidget(parameter.type)) {
				return true;
			}
		}

		return false;
	}

	private static boolean settingUsesNumberInputWidget(final String type) {
		return "Integer".equals(type)
			|| "Byte".equals(type)
			|| "Short".equals(type)
			|| "Double".equals(type);
	}

	private static String createSettingsWidgetTitle(final EbiCommandMetadataParameter parameter,
			final int settingsIndex) {
		if (parameter.typeDescription != null && !parameter.typeDescription.isBlank()) {
			return parameter.typeDescription.trim().replaceFirst("[.!?]+$", "");
		}

		return parameter.type + " input " + (settingsIndex + 1);
	}

	private static String createSettingsWidgetDescription(final EbiCommandMetadata metadata,
			final EbiCommandMetadataParameter parameter, final int settingsIndex) {
		if (parameter.typeDescription != null && !parameter.typeDescription.isBlank()) {
			return parameter.typeDescription.trim();
		}

		return "The " + parameter.type + " value for input " + (settingsIndex + 1)
			+ " of " + metadata.commandName + ".";
	}

	private static String createSettingsFieldDeclaration(final EbiCommandMetadataParameter parameter,
			final int metadataIndex) {
		String fieldName = createSettingsFieldName(metadataIndex);

		return switch (parameter.type) {
			case "Integer", "Byte", "Short" -> "int " + fieldName + " = 0;";
			case "Double" -> "double " + fieldName + " = 0.0;";
			case "Boolean" -> "boolean " + fieldName + " = false;";
			case "String", "Long", "Character", "BigFraction" -> "String " + fieldName + " = \"\";";
			default -> throw new IllegalArgumentException("Unsupported Ebi settings type: " + parameter.type);
		};
	}
	
	private static String createEbiNodeFactorySource(final EbiCommandMetadata metadata, final String factoryClassName, final String settingsClassName) {
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
		source.append("\t\t\t\"").append(escapeJavaString(metadata.commandName)).append("\",").append(newLine);
		source.append("\t\t\t\"").append(escapeJavaString(metadata.shortDescription)).append("\",").append(newLine);
		source.append("\t\t\t\"").append(escapeJavaString(metadata.fullDescription)).append("\",").append(newLine);
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
		source.append(indentation).append("\t\"").append(escapeJavaString(parameter.type)).append("\",").append(newLine);
		source.append(indentation).append("\t\"").append(escapeJavaString(parameter.portType)).append("\",").append(newLine);
		source.append(indentation).append("\t\"").append(escapeJavaString(parameter.typeDescription)).append("\",").append(newLine);
		source.append(indentation).append("\t").append(parameter.isPort).append(newLine);
		source.append(indentation).append(")");
		
		return source.toString();
	}
	
	private static String escapeJavaString(final String value) {
		if (value == null) {
			return "";
		}
		
		return value.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\t", "\\t");
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
	
	/*
	 * Expected output:
	 * public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output)
		throws InvalidSettingsException{
			...
		}
	 * */
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
	
	/*
	 * Expected output:
	 * public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
	 * 		...
	 * }
	 * */
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
					.append(createSettingsFieldName(metadataIndex)).append(");\r\n");
			}
		}

		source.append("\r\n");
		source.append("            final String result = CallEbi.call_ebi(\r\n");
		source.append("                \"").append(escapeJavaString(metadata.commandName)).append("\",\r\n");
		source.append("                \"").append(escapeJavaString(getFileExtension(metadata.output.type))).append("\",\r\n");
		source.append("                ebiInputs);\r\n");
		source.append("\r\n");
		source.append(createOutputConversionSource(metadata));
		source.append("        } catch (Exception ex) {\r\n");
		source.append("            throw new RuntimeException(\"Ebi command failed: ")
			.append(escapeJavaString(metadata.commandName)).append("\", ex);\r\n");
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

	private static String createSettingsFieldName(final int metadataIndex) {
		return "m_input" + metadataIndex;
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
	
	private static String toClassNamePrefix(final String commandName) {
	    String[] words = commandName.trim().split("[^A-Za-z0-9]+");

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
	            + "            category-path=\"/\"" + newline // TODO: Specify category
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
}
