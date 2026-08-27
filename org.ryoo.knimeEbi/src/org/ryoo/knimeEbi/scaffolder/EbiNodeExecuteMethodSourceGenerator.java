package org.ryoo.knimeEbi.scaffolder;

public final class EbiNodeExecuteMethodSourceGenerator {
	
	public static String generate(final EbiCommandMetadata metadata, final String settingsClassName) {
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
		EbiCommandMetadata.validatePortMetadata(metadata);

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
				"            final String compatiblePtml = PtmlCompatibilityUtil.toPm4KnimePtml(result);\r\n"
					+ "            final ProcessTreePortObject resultPort = new ProcessTreePortObject();\r\n"
					+ "            resultPort.loadFromDefault(\r\n"
					+ "                new ProcessTreePortObjectSpec(),\r\n"
					+ "                new ByteArrayInputStream(compatiblePtml.getBytes(StandardCharsets.UTF_8)));\r\n"
					+ "            output.setOutData(0, resultPort);\r\n";

			default ->
				throw new IllegalArgumentException(
					"Cannot generate output conversion for unsupported port type: " + metadata.output.portType
				);
		};
	}
}
