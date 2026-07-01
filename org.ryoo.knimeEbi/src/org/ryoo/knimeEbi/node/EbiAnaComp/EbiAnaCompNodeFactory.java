package org.ryoo.knimeEbi.node.EbiAnaComp;

import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.pm4knime.portobject.XLogPortObject;

/**
 * This is an example implementation of the node factory of the
 * "EbiAnaComp" node.
 *
 * @author 
 */
public class EbiAnaCompNodeFactory extends DefaultNodeFactory {

   public EbiAnaCompNodeFactory() {
	   super(
			   DefaultNode.create()
			   		.name("Ebi analyse completeness")
			   		.icon("default.png")
			   		.shortDescription("Estimate the completeness of an event log using species discovery.") // <- Descriptions take directly from Ebi
			   		.fullDescription("This node estimates the completeness of an event log using species discovery.")
			   		.sinceVersion(2, 0, 0)
			  		.ports(p -> p
                            .addInputPort("Event Log", "an event log", XLogPortObject.TYPE)
		                    .addOutputTable("Completeness", "estimated completeness as a fraction"))
		            .model(m -> m
		                    .withoutParameters()
		                    .configure(EbiAnaCompNodeModel::configure)
		                    .execute(EbiAnaCompNodeModel::execute))
		            .nodeType(NodeType.Manipulator));
   }

}

