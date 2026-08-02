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

import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;
import org.pm4knime.util.PetriNetUtil;

public class EbiDiscoverUniformStochasticLabelledPetriNetNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi discover uniform stochastic-labelled-Petri-net",
			"Give each transition a weight of 1.",
			"Give each transition a weight of 1.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"StochasticLabelledPetriNetSimpleWeights",
						"PetriNetPortObject",
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

	public EbiDiscoverUniformStochasticLabelledPetriNetNodeFactory() {
		super(COMMAND_METADATA, EbiDiscoverUniformStochasticLabelledPetriNetNodeFactory::addPorts, EbiDiscoverUniformStochasticLabelledPetriNetNodeFactory::configureModel);
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
            .configure(EbiDiscoverUniformStochasticLabelledPetriNetNodeFactory::configure)
            .execute(EbiDiscoverUniformStochasticLabelledPetriNetNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof PetriNetPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid PetriNetPortObject!");
        }

        output.setOutSpec(0, new PetriNetPortObjectSpec());
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[1];

            final PetriNetPortObject inputPort0 = input.getInPortObject(0);
            final ByteArrayOutputStream inputBuffer0 = new ByteArrayOutputStream();
            PetriNetUtil.exportToStream(inputPort0.getANet(), inputBuffer0);
            ebiInputs[0] = inputBuffer0.toString(StandardCharsets.UTF_8);

            final String result = CallEbi.call_ebi(
                "Ebi discover uniform stochastic-labelled-Petri-net",
                ".pnml",
                ebiInputs);

            final PetriNetPortObject resultPort = new PetriNetPortObject(
                PetriNetUtil.stringToPetriNet(result));
            output.setOutData(0, resultPort);
        } catch (Exception ex) {
            throw new RuntimeException("Ebi command failed: Ebi discover uniform stochastic-labelled-Petri-net", ex);
        }
    }
}
