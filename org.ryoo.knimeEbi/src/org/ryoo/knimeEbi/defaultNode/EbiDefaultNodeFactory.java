package org.ryoo.knimeEbi.defaultNode;

import java.util.function.Consumer;
import java.util.function.Function;

import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.port.PortType;
import org.knime.node.DefaultNode;
import org.knime.node.DefaultNodeFactory;
import org.knime.node.RequirePorts.PortsAdder;
import org.knime.node.DefaultModel;
import org.knime.node.DefaultModel.RequireModelParameters;

import org.pm4knime.portobject.DFMPortObject; // Delete if not needed
import org.pm4knime.portobject.DfgMsdPortObject; // Delete if not needed
import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.XLogPortObject;

import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadata;
import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadataParameter;

//BufferedDataTable.TYPE is the standard table port type.
// TODO: Change parameter to EbiCommandMetadata and add RequiredModelParametersFunction
public class EbiDefaultNodeFactory extends DefaultNodeFactory {
	public EbiDefaultNodeFactory(final EbiCommandMetadata metadata, final Consumer<PortsAdder> portConfigurer, final Function<RequireModelParameters, DefaultModel> modelConfigurer) {
		super(
			   DefaultNode.create()
			   		.name(metadata.commandName)
			   		.icon("default.png") // TODO: change to printed Ebi-logo
			   		.shortDescription(metadata.shortDescription)
			   		.fullDescription(metadata.fullDescription)
			   		.sinceVersion(0, 0, 0) // TODO: change to real version
			  		.ports(portConfigurer)
		            .model(modelConfigurer)
		            .nodeType(NodeType.Manipulator));
	}
	
	protected static PortType resolvePortType(final String portTypeName) {
	    if (portTypeName == null || portTypeName.isBlank()) {
	        throw new IllegalArgumentException("Port type name must not be empty.");
	    }

	    return switch (portTypeName) {
	        case "BufferedDataTable" -> BufferedDataTable.TYPE;
	        case "XLogPortObject" -> XLogPortObject.TYPE;
	        case "PetriNetPortObject" -> PetriNetPortObject.TYPE;
	        case "ProcessTreePortObject" -> ProcessTreePortObject.TYPE;
	        case "DfgMsdPortObject" -> DfgMsdPortObject.TYPE; // TODO: Probably remove and put it clearly into Future Work
	        case "DFMPortObject" -> DFMPortObject.TYPE; // Probably remove and put it clearly into Future Work
	        default -> throw new IllegalArgumentException("Unsupported port type: " + portTypeName);
	    };
	}
}