package org.ryoo.knimeEbi.scaffolder;

import java.util.ArrayList;
import java.util.Objects;

public class EbiCommandMetadata {
	public final String commandName; 
	public final String shortDescription;
	public final String fullDescription;
	public final ArrayList<EbiCommandMetadataParameter> inputs = new ArrayList<>();
	public final EbiCommandMetadataParameter output;
	
	
	// Original text for each Ebi command (Metadata block) -> Extract metadata of each command and assign to attributes of this class
	public EbiCommandMetadata (String metadataBlock) {
		commandName = extractCommandName(metadataBlock);
		
	    
	    String firstPluginDeclaration = extractFirstPluginDeclaration(metadataBlock);
	    shortDescription = extractPluginName(firstPluginDeclaration);
	    fullDescription = extractPluginHelp(firstPluginDeclaration);
	   
	    
	    String outputType = extractPluginReturnLabel(firstPluginDeclaration);
	    String outputPortType = getOutputPortType(outputType);
	    output = new EbiCommandMetadataParameter(outputType, outputPortType, "", true);
	    
	   
	    String[] inputParameterSplit = metadataBlock.trim().split("\\{", 2);
	    String[] parameterTypes = extractParameterClasses(inputParameterSplit[0].trim());
	    
	    String firstPluginVariantDeclaration = extractFirstPluginVariantDeclaration(metadataBlock);
	    String[] dialogInputTypeDescriptions = extractDialogInputLabels(firstPluginVariantDeclaration);
	    
	    int currDialogInput = 0;
	    for(String parameter : parameterTypes) {
	    	String inputType = parameter;
	    	
	    	if(inputIsPrimitive(parameter)) {
	    		inputs.add(new EbiCommandMetadataParameter(inputType, "", dialogInputTypeDescriptions[currDialogInput], false));
	    		currDialogInput++;
	    	}
	    	else {
	    		String inputPortType = getPm4KnimePortType(parameter);
	    		inputs.add(new EbiCommandMetadataParameter(inputType, inputPortType, "", true));
	    	}
	    }
	}

	// Explicit constructor
	public EbiCommandMetadata(final String commandName,
	        final String shortDescription,
	        final String fullDescription,
	        final ArrayList<EbiCommandMetadataParameter> inputs,
	        final EbiCommandMetadataParameter output) {

	    this.commandName = commandName;
	    this.shortDescription = shortDescription;
	    this.fullDescription = fullDescription;
	    this.inputs.addAll(inputs);
	    this.output = output;
	}
	
	@Override
	public String toString() {
	    String newline = System.lineSeparator();
	    StringBuilder result = new StringBuilder();

	    result.append("commandName=")
	          .append(Objects.toString(commandName, ""))
	          .append(newline)
	          .append("shortDescription=")
	          .append(Objects.toString(shortDescription, ""))
	          .append(newline)
	          .append("fullDescription=")
	          .append(Objects.toString(fullDescription, ""));

	    if (inputs != null) {
	        for (int i = 0; i < inputs.size(); i++) {
	            result.append(newline)
	                  .append("inputs[")
	                  .append(i)
	                  .append("]=")
	                  .append(Objects.toString(inputs.get(i), ""));
	        }
	    }

	    result.append(newline)
	          .append("output=")
	          .append(Objects.toString(output, ""));

	    return result.toString();
	}
	
	public boolean hasNoPrimitiveInputs() {
		for(EbiCommandMetadataParameter input : this.inputs) {
			if(!input.isPort) {
				return false;
			}
		}
		
		return true;
	} 
	
	private static String extractCommandName(final String metadataBlock) {
	    String[] parts = metadataBlock.split("==", 2);

	    if (parts.length < 2) {
	        throw new IllegalArgumentException("Metadata block has no == separator.");
	    }

	    
	    return parts[0].trim();
	}
	
	private static String extractFirstPluginDeclaration(final String metadataBlock) {
	    String[] parts = metadataBlock.split("@Plugin\\s*\\(", 2);

	    if (parts.length < 2) {
	        throw new IllegalArgumentException("Metadata block has no @Plugin declaration.");
	    }

	    return parts[1];
	}
	
	private static String extractPluginName(final String pluginDeclaration) {
	    String completeName = extractQuotedPluginAttribute(pluginDeclaration, "name");
	    return completeName.split("\\s*\\(input:", 2)[0].trim();
	}
	
	private static String extractPluginHelp(final String pluginDeclaration) {
	    String completeHelp = extractQuotedPluginAttribute(pluginDeclaration, "help");
	    String mainHelp = completeHelp.split("\\s{2,}", 2)[0].trim();
	    return mainHelp.replaceFirst("\\s*\\(calls Ebi\\)\\s*$", "").trim();
	}
	
	private static String extractPluginReturnLabel(final String pluginDeclaration) {
	    return extractQuotedPluginAttribute(pluginDeclaration, "returnLabels");
	}
	
	private static String extractQuotedPluginAttribute(final String pluginDeclaration, final String attributeName) {
	    String[] attributeParts = pluginDeclaration.split("\\b" + attributeName + "\\s*=\\s*(?:\\{\\s*)?\"", 2);

	    if (attributeParts.length < 2) {
	        throw new IllegalArgumentException("Plugin declaration has no " + attributeName + " attribute.");
	    }

	    String[] valueParts = attributeParts[1].split("\"", 2);
	    return valueParts[0].trim();
	}
	
	private static String[] extractParameterClasses(final String methodDeclaration) {
	    int openingParenthesis = methodDeclaration.indexOf('(');
	    int closingParenthesis = methodDeclaration.indexOf(')', openingParenthesis);

	    if (openingParenthesis < 0 || closingParenthesis < 0) {
	        throw new IllegalArgumentException("Invalid method declaration.");
	    }

	    String parametersText = methodDeclaration.substring(openingParenthesis + 1, closingParenthesis);
	    String[] parameters = parametersText.split("\\s*,\\s*");

	    return java.util.Arrays.stream(parameters)
	        .map(String::trim)
	        .filter(parameter -> !parameter.isEmpty())
	        .filter(parameter -> !parameter.startsWith("PluginContext "))
	        .map(EbiCommandMetadata::extractSimpleParameterType)
	        .toArray(String[]::new);
	}

	private static String extractSimpleParameterType(final String parameter) {
	    String fullTypeName = parameter.split("\\s+")[0];
	    int lastDot = fullTypeName.lastIndexOf('.');
	    return fullTypeName.substring(lastDot + 1);
	}
	
	private static String extractFirstPluginVariantDeclaration(final String metadataBlock) {
	    String[] variantParts = metadataBlock.split("@PluginVariant\\s*\\(", 2);

	    if (variantParts.length < 2) {
	        throw new IllegalArgumentException("Metadata block has no @PluginVariant declaration.");
	    }

	    return variantParts[1].split("@PluginVariant\\s*\\(", 2)[0];
	}
	
	private static String[] extractDialogInputLabels(final String pluginVariantDeclaration) {
	    String[] inputParts = pluginVariantDeclaration.split("create_input_panel\\s*\\(\\s*\"");
	    String[] labels = new String[inputParts.length - 1];

	    for (int i = 1; i < inputParts.length; i++) {
	        String[] valueParts = inputParts[i].split("\"\\s*\\)", 2);

	        if (valueParts.length < 2) {
	            throw new IllegalArgumentException("A create_input_panel call has no closing quote or parenthesis.");
	        }

	        labels[i - 1] = valueParts[0].trim();
	    }

	    return labels;
	}
	
	private static boolean inputIsPrimitive(String parameter) {
		switch(parameter) {
			case "Integer", "String", "Byte", "Short", "Long", "Double", "Character", "Boolean", "BigFraction":
				return true;
			default: 
				return false;
		}
	}
	
	private static String getOutputPortType(final String outputType) {
		if(EbiTableOutputType.isTableCompatible(outputType)) {
			return "BufferedDataTable";
		}
		else {
			return getPm4KnimePortType(outputType);
		}
	}
	
	private static String getPm4KnimePortType(final String outputType) {
	    return switch (outputType) {
	        case "XLog" ->
	            "XLogPortObject";

	        case "DirectlyFollowsGraph" -> // TODO: Probably remove and put it to Future Work
	            "DfgMsdPortObject";
	           
	        case "DirectlyFollowsModel" -> // TODO: Probably remove and put it to Future Work
	        	"DFMPortObject";
	            
	        case "AcceptingPetriNet", "PetriNet", "StochasticLabelledPetriNet", "LoLaPetriNet", 
	        	"StochasticDeterministicFiniteAutomaton", "StochasticNonDeterministicFiniteAutomaton", "BusinessProcessModelAndNotation", 
	        	"StochasticLabelledPetriNetSimpleWeights"->
	            "PetriNetPortObject";

	        case "ProcessTree", "StochasticProcessTree", "EfficientTree" ->
	            "ProcessTreePortObject";

	        default ->
	            "BufferedDataTable"; // or skip
	    };
	}
}
