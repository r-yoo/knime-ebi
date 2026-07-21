package org.ryoo.knimeEbi.scaffolder;

import java.util.Objects;

import org.knime.core.node.port.PortType;

public class EbiCommandMetadata {
	public String commandName; 
	public String description;
	public String firstInputName; // empty if no parameter
	public PortType firstInputPortType; // empty if no parameter
	public String secondInputName; // empty if no second parameter
	public PortType secondInputPortType; // empty if no second parameter
	public String outputName;
	public PortType outputPortType;
	
	public EbiCommandMetadata (String metadata) { // -> take the first @Plugin block, and skip commands with zero input
		/* Expected Input:
		 * Ebi analyse completeness == 

	public static org.apache.commons.math3.fraction.BigFraction Ebi_analyse_completeness__as__fraction__to__fraction(PluginContext context, org.deckfour.xes.model.XLog input_0) throws Exception {
		String result = CallEbi.call_ebi("Ebi analyse completeness", ".frac", new String[] {org.processmining.ebi.objects.EbiEventLog.XLogToEbiString(context, input_0)});
		return org.processmining.ebi.objects.EbiFraction.fromEbiString(context, result);
	}

	@Plugin(
		name = "Estimate the completeness of an event log using species discovery. (input: XLog; output: fraction)",
		level = PluginLevel.PeerReviewed, 
		returnLabels = { "fraction" }, 
		returnTypes = { org.apache.commons.math3.fraction.BigFraction.class },
		parameterLabels = { "XLog" },
		userAccessible = true,
		categories = { PluginCategory.Discovery, PluginCategory.Analytics, PluginCategory.ConformanceChecking },
		help = "Estimate the completeness of an event log using species discovery. (calls Ebi)"
	)
	@UITopiaVariant(affiliation = IMMiningDialog.affiliation, author = IMMiningDialog.author, email = IMMiningDialog.email)
	@PluginVariant(variantLabel = "Call Ebi", requiredParameterLabels = { 0 })
	public org.apache.commons.math3.fraction.BigFraction prom_Ebi_analyse_completeness__as__fraction__to__fraction(PluginContext context, org.deckfour.xes.model.XLog input_0) throws Exception {
		return Ebi_analyse_completeness__as__fraction__to__fraction(context, input_0);
	}
		 * */ 
	}
	
	// TODO: Input -> attributes of this class
	// TODO: Hilfsfunktionen auslagern
	public String toTextBlock() {
	    String newline = System.lineSeparator();

	    return String.join(
	        newline,
	        "commandName=" + Objects.toString(commandName, ""),
	        "description=" + Objects.toString(description, ""),
	        "firstInputName=" + Objects.toString(firstInputName, ""),
	        "firstInputPortType=" + Objects.toString(firstInputPortType, ""),
	        "secondInputName=" + Objects.toString(secondInputName, ""),
	        "secondInputPortType=" + Objects.toString(secondInputPortType, ""),
	        "outputName=" + Objects.toString(outputName, ""),
	        "outputPortType=" + Objects.toString(outputPortType, "")
	    );
	}
	
	private static String getPm4KnimePortType(final String outputName) {
		// TODO: Include all supported files of Ebi, and we need to add specific output parameters in calling Ebi for the stochastic outputs
	    return switch (outputName) {
	        case "XLog" ->
	            "XLogPortObject";

	        case "BusinessProcessModelAndNotation" -> 
	            "BpmnPortObject";

	        case "DirectlyFollowsGraph" ->
	            "DfgMsdPortObject";
	           
	        case "DirectlyFollowsModel" ->
	        	"DFMPortObject";
	            
	        case "AcceptingPetriNet", "PetriNet", "StochasticLabelledPetriNet", "LoLaPetriNet", "StochasticDeterministicFiniteAutomaton", "StochasticNonDeterministicFiniteAutomaton" ->
	            "PetriNetPortObject";

	        case "ProcessTree", "StochasticProcessTree" ->
	            "ProcessTreePortObject";

	        default ->
	            "BufferedDataTable"; // or skip/TODO
	    };
	}
	
	private static String getOutputPortType(final String outputName) {
		if(EbiTableOutputType.isTableCompatible(outputName)) {
			 return "BufferedDataTable";
		}
		else {
			return getPm4KnimePortType(outputName);
		}
	}
}
