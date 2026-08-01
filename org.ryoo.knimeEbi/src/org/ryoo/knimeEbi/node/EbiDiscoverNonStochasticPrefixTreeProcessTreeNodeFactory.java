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

import org.pm4knime.portobject.PetriNetPortObject;
import org.pm4knime.portobject.PetriNetPortObjectSpec;
import org.pm4knime.util.PetriNetUtil;

public class EbiDiscoverNonStochasticPrefixTreeProcessTreeNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi discover-non-stochastic prefix-tree process-tree",
			"Discover a process tree that is a prefix tree of the log.",
			"Discover a process tree that is a prefix tree of the log.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"XLog",
						"XLogPortObject",
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

	public EbiDiscoverNonStochasticPrefixTreeProcessTreeNodeFactory() {
		super(COMMAND_METADATA, EbiDiscoverNonStochasticPrefixTreeProcessTreeNodeFactory::addPorts, EbiDiscoverNonStochasticPrefixTreeProcessTreeNodeFactory::configureModel);
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
            .configure(EbiDiscoverNonStochasticPrefixTreeProcessTreeNodeFactory::configure)
            .execute(EbiDiscoverNonStochasticPrefixTreeProcessTreeNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        output.setOutSpec(0, new PetriNetPortObjectSpec());
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[1];

            ebiInputs[0] = XESUtil.writeLogToXesString(input.getInPortObject(0));

            final String result = CallEbi.call_ebi(
                "Ebi discover-non-stochastic prefix-tree process-tree",
                ".pnml",
                ebiInputs);

            final PetriNetPortObject resultPort = new PetriNetPortObject(
                PetriNetUtil.stringToPetriNet(result));
            output.setOutData(0, resultPort);
        } catch (Exception ex) {
            throw new RuntimeException("Ebi command failed: Ebi discover-non-stochastic prefix-tree process-tree", ex);
        }
    }
}
