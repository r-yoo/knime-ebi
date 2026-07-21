package org.ryoo.knimeEbi.defaultNode;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortType;
import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadata;
import org.knime.node.DefaultModel;

public class EbiDefaultZeroInputNodeFactory extends DefaultNodeFactory{
	public EbiDefaultZeroInputNodeFactory(String commandName, String description, String outputName, PortType outputPortType) {
		super(
			   DefaultNode.create()
			   		.name(commandName)
			   		.icon("default.png")
			   		.shortDescription(description)
			   		.fullDescription(description)
			   		.sinceVersion(0, 0, 0) // change to real version
			  		.ports(p -> p
		                 .addOutputPort(outputName, outputName, outputPortType))
		            .model(m -> m
		                    .withoutParameters()
		                    .configure(EbiDefaultZeroInputNodeFactory::configure)
		                    .execute(EbiDefaultZeroInputNodeFactory::execute))
		            .nodeType(NodeType.Source));
	}
	
	public EbiDefaultZeroInputNodeFactory(EbiCommandMetadata metadata) {
		this(metadata.commandName, metadata.description, metadata.outputName, metadata.outputPortType);
	}
	
	// Need to overwrite configure and execute -> static can not be overwritten, just define method with same signature
	public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output)
		throws InvalidSettingsException{}
	
	public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {}
}
