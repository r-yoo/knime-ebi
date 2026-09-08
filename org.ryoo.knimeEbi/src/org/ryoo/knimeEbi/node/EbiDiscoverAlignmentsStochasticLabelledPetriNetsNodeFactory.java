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

import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.util.XLogUtil;

import org.pm4knime.portobject.ProcessTreePortObject;
import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;
import org.pm4knime.util.PetriNetUtil;

public class EbiDiscoverAlignmentsStochasticLabelledPetriNetsNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi discover alignments stochastic-labelled-Petri-nets",
			"Give each transition a weight that matches the aligned occurrences of its label.",
			"Give each transition a weight that matches the aligned occurrences of its label. The model must be livelock-free.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"XLog",
						"XLogPortObject",
						"",
						true
					),
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

	public EbiDiscoverAlignmentsStochasticLabelledPetriNetsNodeFactory() {
		super(COMMAND_METADATA, EbiDiscoverAlignmentsStochasticLabelledPetriNetsNodeFactory::addPorts, EbiDiscoverAlignmentsStochasticLabelledPetriNetsNodeFactory::configureModel);
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
            .configure(EbiDiscoverAlignmentsStochasticLabelledPetriNetsNodeFactory::configure)
            .execute(EbiDiscoverAlignmentsStochasticLabelledPetriNetsNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        if (!(input.getInPortSpec(1) instanceof ProcessTreePortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid ProcessTreePortObject!");
        }

        output.setOutSpec(0, new PetriNetPortObjectSpec());
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[2];

            ebiInputs[0] = XESUtil.writeLogToXesString(input.getInPortObject(0));
            final ProcessTreePortObject inputPort1 = input.getInPortObject(1);
            ebiInputs[1] = inputPort1.toText();

            final String result = CallEbi.call_ebi(
                "Ebi discover alignments stochastic-labelled-Petri-nets",
                ".pnml",
                ebiInputs);

            if (result != null && result.stripLeading().startsWith("Ebi: error:")) {
                throw new IllegalStateException(result.trim());
            }
            final PetriNetPortObject resultPort = new PetriNetPortObject(
                PetriNetUtil.stringToPetriNet(result));
            output.setOutData(0, resultPort);
        } catch (Exception ex) {
            final String detail = ex.getMessage();
            throw new RuntimeException(
                "Ebi command failed: Ebi discover alignments stochastic-labelled-Petri-nets"
                    + (detail == null || detail.isBlank() ? "" : ": " + detail),
                ex);
        }
    }
}
