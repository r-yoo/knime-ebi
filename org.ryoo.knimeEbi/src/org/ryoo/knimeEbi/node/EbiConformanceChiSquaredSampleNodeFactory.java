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

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;

public class EbiConformanceChiSquaredSampleNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi conformance chi-squared-sample",
			"Compute chi-squared stochastic conformance, if both inputs need to be sampled.",
			"Compute chi-square stochastic conformance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `cssc`.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"StochasticLabelledPetriNetSimpleWeights",
						"PetriNetPortObject",
						"",
						true
					),
					new EbiCommandMetadataParameter(
						"StochasticLabelledPetriNetSimpleWeights",
						"PetriNetPortObject",
						"",
						true
					),
					new EbiCommandMetadataParameter(
						"Integer",
						"",
						"Number of traces to sample.",
						false
					)
				)
			),
			new EbiCommandMetadataParameter(
				"fraction",
				"BufferedDataTable",
				"",
				true
			)
		);

	public EbiConformanceChiSquaredSampleNodeFactory() {
		super(COMMAND_METADATA, EbiConformanceChiSquaredSampleNodeFactory::addPorts, EbiConformanceChiSquaredSampleNodeFactory::configureModel);
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
            .parametersClass(EbiConformanceChiSquaredSampleNodeSettings.class)
            .configure(EbiConformanceChiSquaredSampleNodeFactory::configure)
            .execute(EbiConformanceChiSquaredSampleNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof PetriNetPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid PetriNetPortObject!");
        }

        if (!(input.getInPortSpec(1) instanceof PetriNetPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid PetriNetPortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi conformance chi-squared-sample", "fraction", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[3];
            final EbiConformanceChiSquaredSampleNodeSettings settings = input.getParameters();

            final PetriNetPortObject inputPort0 = input.getInPortObject(0);
            final ByteArrayOutputStream inputBuffer0 = new ByteArrayOutputStream();
            PetriNetUtil.exportToStream(inputPort0.getANet(), inputBuffer0);
            ebiInputs[0] = inputBuffer0.toString(StandardCharsets.UTF_8);
            final PetriNetPortObject inputPort1 = input.getInPortObject(1);
            final ByteArrayOutputStream inputBuffer1 = new ByteArrayOutputStream();
            PetriNetUtil.exportToStream(inputPort1.getANet(), inputBuffer1);
            ebiInputs[1] = inputBuffer1.toString(StandardCharsets.UTF_8);
            ebiInputs[2] = String.valueOf(settings.m_input2);

            final String result = CallEbi.call_ebi(
                "Ebi conformance chi-squared-sample",
                ".frac",
                ebiInputs);

            final DataTableSpec spec = TableUtil.createOutputSpec(
                COMMAND_METADATA.commandName,
                COMMAND_METADATA.output.type,
                StringCell.TYPE);
            final BufferedDataContainer container = input.getExecutionContext().createDataContainer(spec);
            container.addRowToTable(new DefaultRow("Row0", new StringCell(result)));
            container.close();
            output.setOutData(0, container.getTable());
        } catch (Exception ex) {
            throw new RuntimeException("Ebi command failed: Ebi conformance chi-squared-sample", ex);
        }
    }
}
