package org.ryoo.knimeEbi.defaultNode;

import java.util.ArrayList;

import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortType;
import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.knime.node.RequirePorts.PortsAdder;
import org.knime.node.DefaultModel;

import org.pm4knime.portobject.XLogPortObject;
import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadata;
import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadataParameter;

//BufferedDataTable.TYPE is the standard table port type.
public class EbiDefaultTwoInputNodeFactory extends DefaultNodeFactory{
	public EbiDefaultTwoInputNodeFactory(EbiCommandMetadata metadata) {
		super(
			   DefaultNode.create()
			   		.name(commandName)
			   		.icon("default.png")
			   		.shortDescription(shortDescription)
			   		.fullDescription(fullDescription)
			   		.sinceVersion(0, 0, 0) // change to real version
			  		.ports(p -> portAdderFunction(p, metadata))
		            .model(m -> m
		                    .withoutParameters()
		                    .configure(EbiDefaultTwoInputNodeFactory::configure)
		                    .execute(EbiDefaultTwoInputNodeFactory::execute))
		            .nodeType(NodeType.Manipulator));
	}
	
	public EbiDefaultTwoInputNodeFactory(ArrayList<EbiCommandMetadataParameter> inputs, EbiCommandMetadataParameter output) {
		this(metadata.commandName, metadata.shortDescription, metadata.fullDescription, metadata.secondInputName, metadata.secondInputPortType, metadata.outputName, metadata.outputPortType);
	}
	
	private static PortsAdder portAdderFunction(PortsAdder p, EbiCommandMetadata metadata) {
		// First, add outputPort checking output attribute
		// With for-loop over inputs array
		// p = p.addInputPort(name, name, portType + ".TYPE");
		/*
		p = p.addInputPort("Event Log", "an event log", XLogPortObject.TYPE);
		p = p.addInputPort(secondInputName, secondInputName, secondInputPortType);
		p = p.addOutputPort(outputName, outputName, outputPortType);*/
	}
	
	// Need to overwrite configure and execute -> static can not be overwritten, just define method with same signature
	public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output)
		throws InvalidSettingsException{}
	
	public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {}
}