package org.ryoo.knimeEbi.node.EbiAnaVar;

import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.pm4knime.portobject.XLogPortObject;

public class EbiAnaVarNodeFactory extends DefaultNodeFactory{
	
	public EbiAnaVarNodeFactory() {
		   super(
				   DefaultNode.create()
				   		.name("Ebi analyse variety")
				   		.icon("../default.png")
				   		.shortDescription("Compute the variety of a stochastic language.") // <- Descriptions take directly from Ebi
				   		.fullDescription("Compute the variety of a stochastic language. That is, the average distance between two arbitrary traces in the language.")
				   		.sinceVersion(2, 0, 0)
				  		.ports(p -> p
	                            .addInputPort("Event Log", "an event log", XLogPortObject.TYPE)
			                    .addOutputTable("Variety", "average distance between two arbitrary traces in the language as a fraction"))
			            .model(m -> m
			                    .withoutParameters()
			                    .configure(EbiAnaVarNodeModel::configure)
			                    .execute(EbiAnaVarNodeModel::execute))
			            .nodeType(NodeType.Manipulator));
	   }
}
