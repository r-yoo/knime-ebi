package org.ryoo.knimeEbi.scaffolder;

import java.util.ArrayList;
import java.util.Objects;

public class EbiCommandMetadata {
	public String commandName; 
	public String shortDescription;
	public String fullDescription;
	public ArrayList<EbiCommandMetadataParameter> inputs;
	public EbiCommandMetadataParameter output;
	
	/* Expected Input:
	Ebi sample folds == 

	public static org.deckfour.xes.model.XLog Ebi_sample_folds__as__extensible_event_stream__to__XLog(PluginContext context, org.deckfour.xes.model.XLog input_0, Integer input_1, Integer input_2, Integer input_3) throws Exception {
		String result = CallEbi.call_ebi("Ebi sample folds", ".xes", new String[] {org.processmining.ebi.objects.EbiEventLog.XLogToEbiString(context, input_0), org.processmining.ebi.objects.EbiInteger.toEbiString(context, input_1), org.processmining.ebi.objects.EbiInteger.toEbiString(context, input_2), org.processmining.ebi.objects.EbiInteger.toEbiString(context, input_3)});
		return org.processmining.ebi.objects.EbiEventLog.EbiStringToXLog(context, result);
	}

	@Plugin(
		name = "Randomly split a log into a given number of sub-logs, and return a specific one of these sub-logs. (input: XLog; output: XLog)",
		level = PluginLevel.PeerReviewed, 
		returnLabels = { "XLog" }, 
		returnTypes = { org.deckfour.xes.model.XLog.class },
		parameterLabels = { "XLog" },
		userAccessible = true,
		categories = { PluginCategory.Discovery, PluginCategory.Analytics, PluginCategory.ConformanceChecking },
		help = "Randomly but reproducibly split a log into a given number of sub-logs. Each trace has a likelihood of 1/folds to end up in any of the folds. Giving the same random seed yields the same split, as long as the same build number of Ebi is used.          For instance, one can perform k-fold cross validation: one would repeatedly apply the folds command with the same seed and the same number of folds, but vary the returned sub-logs. (calls Ebi)"
	)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Call Ebi", requiredParameterLabels = { 0 })
	public org.deckfour.xes.model.XLog prom_Ebi_sample_folds__as__extensible_event_stream__to__XLog(UIPluginContext context, org.deckfour.xes.model.XLog input_0) throws Exception {
		EbiDialog dialog = new EbiDialog();
		dialog.add_input(org.processmining.ebi.objects.EbiInteger.create_input_panel("The number of folds."));
		dialog.add_input(org.processmining.ebi.objects.EbiInteger.create_input_panel("The random seed."));
		dialog.add_input(org.processmining.ebi.objects.EbiInteger.create_input_panel("The fold to be returned."));
		InteractionResult result = context.showWizard("Randomly split a log into a given number of sub-logs, and return a specific one of these sub-logs.", true, true, dialog);

		if (result != InteractionResult.FINISHED) {
			context.getFutureResult(0).cancel(false);
			return null;
		}
		Integer input_1 = dialog.get_parameter_Integer(0);
		Integer input_2 = dialog.get_parameter_Integer(1);
		Integer input_3 = dialog.get_parameter_Integer(2);
		return Ebi_sample_folds__as__extensible_event_stream__to__XLog(context, input_0, input_1, input_2, input_3);
	}
	
	 * */ 
	// TODO: Input -> attributes of this class
	public EbiCommandMetadata (String metadataBlock) { // -> take the first @Plugin block, and skip commands with zero input
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
	
	private static boolean inputIsPrimitive(String parameter) {
		switch(parameter) {
			case "Integer", "String", "Byte", "Short", "Long", "Double", "Character", "Boolean":
				return true;
			default: 
				return false;
		}
	}
	
	private static String extractCommandName(final String metadataBlock) {
	    String[] parts = metadataBlock.split("==", 2);

	    if (parts.length < 2) {
	        throw new IllegalArgumentException("Metadata block has no == separator.");
	    }

	    
	    return parts[0].trim();
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
	
	private static String extractFirstPluginDeclaration(final String metadataBlock) {
	    String[] parts = metadataBlock.split("@Plugin\\s*\\(", 2);

	    if (parts.length < 2) {
	        throw new IllegalArgumentException("Metadata block has no @Plugin declaration.");
	    }

	    return parts[1];
	}
	
	private static String extractQuotedPluginAttribute(final String pluginDeclaration, final String attributeName) {
	    String[] attributeParts = pluginDeclaration.split("\\b" + attributeName + "\\s*=\\s*(?:\\{\\s*)?\"", 2);

	    if (attributeParts.length < 2) {
	        throw new IllegalArgumentException("Plugin declaration has no " + attributeName + " attribute.");
	    }

	    String[] valueParts = attributeParts[1].split("\"", 2);
	    return valueParts[0].trim();
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
	
	private static String getPm4KnimePortType(final String outputType) {
		// TODO: Include all supported files of Ebi, and we need to add specific output parameters in calling Ebi for the stochastic outputs
	    return switch (outputType) {
	        case "XLog" ->
	            "XLogPortObject";

	        case "DirectlyFollowsGraph" ->
	            "DfgMsdPortObject";
	           
	        case "DirectlyFollowsModel" ->
	        	"DFMPortObject";
	            
	        case "AcceptingPetriNet", "PetriNet", "StochasticLabelledPetriNet", "LoLaPetriNet", "StochasticDeterministicFiniteAutomaton", "StochasticNonDeterministicFiniteAutomaton", "BusinessProcessModelAndNotation" ->
	            "PetriNetPortObject";

	        case "ProcessTree", "StochasticProcessTree" ->
	            "ProcessTreePortObject";

	        default ->
	            "BufferedDataTable"; // or skip
	    };
	}
	
	private static String getOutputPortType(final String outputType) {
		if(EbiTableOutputType.isTableCompatible(outputType)) {
			return "BufferedDataTable";
		}
		else {
			return getPm4KnimePortType(outputType);
		}
	}
}
