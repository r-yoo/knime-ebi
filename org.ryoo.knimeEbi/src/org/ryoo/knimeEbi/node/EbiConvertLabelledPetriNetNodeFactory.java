package org.ryoo.knimeEbi.node;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.knime.core.node.InvalidSettingsException;
import org.knime.node.DefaultModel;
import org.knime.node.DefaultModel.RequireModelParameters;
import org.knime.node.RequirePorts.PortsAdder;

import org.processmining.ebi.CallEbi;
import org.ryoo.knimeEbi.defaultNode.EbiDefaultNodeFactory;
import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadata;
import org.ryoo.knimeEbi.scaffolder.EbiCommandMetadataParameter;
import org.ryoo.knimeEbi.util.*;

import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;
import org.pm4knime.util.PetriNetUtil;

public class EbiConvertLabelledPetriNetNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi convert labelled-petri-net",
			"Convert an object to a labelled Petri net.",
			"Convert an object to a labelled Petri net.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"EfficientTree",
						"ProcessTreePortObject",
						"",
						true
					)
				)
			),
			new EbiCommandMetadataParameter(
				"PetriNet",
				"PetriNetPortObject",
				"",
				true
			)
		);

	public EbiConvertLabelledPetriNetNodeFactory() {
		super(COMMAND_METADATA, EbiConvertLabelledPetriNetNodeFactory::addPorts, EbiConvertLabelledPetriNetNodeFactory::configureModel);
	}

    private static void addPorts(final PortsAdder ports) {
  int inputPortIndex = 1;

        for (EbiCommandMetadataParameter input : COMMAND_METADATA.inputs) {
            if (input.isPort) {
                String portName = "Input " + inputPortIndex + " " + input.type;
                ports.addInputPort(portName, input.type, resolvePortType(input.portType));

                inputPortIndex++;
            }
        }

        EbiCommandMetadataParameter output = COMMAND_METADATA.output;
        ports.addOutputPort("Output " + output.type, output.type, resolvePortType(output.portType));
    }

    private static DefaultModel configureModel(final RequireModelParameters model) {
        return model
            .withoutParameters()
            .configure(EbiConvertLabelledPetriNetNodeFactory::configure)
            .execute(EbiConvertLabelledPetriNetNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof ProcessTreePortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid ProcessTreePortObject!");
        }

        output.setOutSpec(0, new PetriNetPortObjectSpec());
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[1];

            final ProcessTreePortObject inputPort0 = input.getInPortObject(0);
            ebiInputs[0] = inputPort0.toText();

            final String result = CallEbi.call_ebi(
                "Ebi convert labelled-petri-net",
                ".pnml",
                ebiInputs);

            final PetriNetPortObject resultPort = new PetriNetPortObject(
                PetriNetUtil.stringToPetriNet(result));
            output.setOutData(0, resultPort);
        } catch (Exception ex) {
            throw new RuntimeException("Ebi command failed: Ebi convert labelled-petri-net", ex);
        }
    }
}
