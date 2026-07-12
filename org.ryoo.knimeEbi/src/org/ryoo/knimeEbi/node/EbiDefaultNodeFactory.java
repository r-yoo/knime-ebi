package org.ryoo.knimeEbi.node;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortType;
import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObject;

//BufferedDataTable.TYPE is the standard table port type.
public class EbiDefaultNodeFactory extends DefaultNodeFactory{
	public EbiDefaultNodeFactory(String commandName, String description, String output, PortType portType) {
		super(
			   DefaultNode.create()
			   		.name(commandName)
			   		.icon("default.png")
			   		.shortDescription(description)
			   		.fullDescription(description)
			   		.sinceVersion(0, 0, 0) // change to real version
			  		.ports(p -> p
                         .addInputPort("Event Log", "an event log", XLogPortObject.TYPE)
		                 .addOutputPort(output, output, portType))
		            .model(m -> m
		                    .withoutParameters()
		                    .configure(EbiDefaultNodeFactory::configure)
		                    .execute(EbiDefaultNodeFactory::execute))
		            .nodeType(NodeType.Manipulator));
	}
	
	// Need to overwrite configure and execute -> static can not be overwritten, just define method with same signature
	public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output)
		throws InvalidSettingsException{}
	
	public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {}
}