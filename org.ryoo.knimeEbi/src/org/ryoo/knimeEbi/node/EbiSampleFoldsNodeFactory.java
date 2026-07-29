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

public class EbiSampleFoldsNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi sample folds",
			"Randomly split a log into a given number of sub-logs, and return a specific one of these sub-logs.",
			"Randomly but reproducibly split a log into a given number of sub-logs. Each trace has a likelihood of 1/folds to end up in any of the folds. Giving the same random seed yields the same split, as long as the same build number of Ebi is used.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"XLog",
						"XLogPortObject",
						"",
						true
					),
					new EbiCommandMetadataParameter(
						"Integer",
						"",
						"The number of folds.",
						false
					),
					new EbiCommandMetadataParameter(
						"Integer",
						"",
						"The random seed.",
						false
					),
					new EbiCommandMetadataParameter(
						"Integer",
						"",
						"The fold to be returned.",
						false
					)
				)
			),
			new EbiCommandMetadataParameter(
				"XLog",
				"XLogPortObject",
				"",
				true
			)
		);

	public EbiSampleFoldsNodeFactory() {
		super(COMMAND_METADATA, EbiSampleFoldsNodeFactory::addPorts, EbiSampleFoldsNodeFactory::configureModel);
	}

    private static void addPorts(final PortsAdder ports) {
        for (EbiCommandMetadataParameter input : COMMAND_METADATA.inputs) {
            if (input.isPort) {
                ports.addInputPort(input.type, input.type, resolvePortType(input.portType));
            }
        }

        EbiCommandMetadataParameter output = COMMAND_METADATA.output;
        ports.addOutputPort(output.type, output.type, resolvePortType(output.portType));
    }

    private static DefaultModel configureModel(final RequireModelParameters model) {
        return model
            .parametersClass(EbiSampleFoldsNodeSettings.class)
            .configure(EbiSampleFoldsNodeFactory::configure)
            .execute(EbiSampleFoldsNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        output.setOutSpec(0, new XLogPortObjectSpec());
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[4];
            final EbiSampleFoldsNodeSettings settings = input.getParameters();

            ebiInputs[0] = XESUtil.writeLogToXesString(input.getInPortObject(0));
            ebiInputs[1] = String.valueOf(settings.m_input1);
            ebiInputs[2] = String.valueOf(settings.m_input2);
            ebiInputs[3] = String.valueOf(settings.m_input3);

            final String result = CallEbi.call_ebi(
                "Ebi sample folds",
                ".xes",
                ebiInputs);

            final XLogPortObject resultPort = new XLogPortObject(
                XLogUtil.loadLog(new ByteArrayInputStream(result.getBytes(StandardCharsets.UTF_8))));
            output.setOutData(0, resultPort);
        } catch (Exception ex) {
            throw new RuntimeException("Ebi command failed: Ebi sample folds", ex);
        }
    }
}
