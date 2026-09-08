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

public class EbiReduceLabelledPetriNetNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi reduce labelled-petri-net",
			"Reduce a labelled Petri net language-equivalently.",
			"Reduce a labelled Petri net language-equivalently, using adapted Murata rules, which remove certain places and silent transitions.",
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

	public EbiReduceLabelledPetriNetNodeFactory() {
		super(COMMAND_METADATA, EbiReduceLabelledPetriNetNodeFactory::addPorts, EbiReduceLabelledPetriNetNodeFactory::configureModel);
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
            .configure(EbiReduceLabelledPetriNetNodeFactory::configure)
            .execute(EbiReduceLabelledPetriNetNodeFactory::execute);
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
                "Ebi reduce labelled-petri-net",
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
                "Ebi command failed: Ebi reduce labelled-petri-net"
                    + (detail == null || detail.isBlank() ? "" : ": " + detail),
                ex);
        }
    }
}
