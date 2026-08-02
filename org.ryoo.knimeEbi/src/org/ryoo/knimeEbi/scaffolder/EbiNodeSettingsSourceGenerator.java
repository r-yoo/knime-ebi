package org.ryoo.knimeEbi.scaffolder;

public final class EbiNodeSettingsSourceGenerator {
	
	public static String generate(final EbiCommandMetadata metadata, final String settingsClassName) {
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

			source.append("    @Widget(title = \"").append(JavaSourceUtil.escapeJavaString(title))
				.append("\", description = \"").append(JavaSourceUtil.escapeJavaString(description)).append("\")")
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
	
	public static String createSettingsFieldName(final int metadataIndex) {
		return "m_input" + metadataIndex;
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

		if (!JavaSourceUtil.isJavaIdentifier(settingsClassName)) {
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
	
	private static boolean settingUsesNumberInputWidget(final String type) {
		return "Integer".equals(type)
			|| "Byte".equals(type)
			|| "Short".equals(type)
			|| "Double".equals(type);
	}
}
