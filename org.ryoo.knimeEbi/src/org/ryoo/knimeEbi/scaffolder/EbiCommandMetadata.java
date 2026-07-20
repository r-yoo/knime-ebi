package org.ryoo.knimeEbi.scaffolder;

import org.knime.core.node.port.PortType;

public class EbiCommandMetadata {
	public String commandName; 
	public String description;
	public String secondInputName; // only the second ebi parameter, empty if no second parameter
	public PortType secondInputPortType;
	public String outputName;
	public PortType outputPortType;
	
	public EbiCommandMetadata (String metadata) { // -> take the first @Plugin block
		/* Expected Input:
		 * == command Ebi analyse completeness == 

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
}
