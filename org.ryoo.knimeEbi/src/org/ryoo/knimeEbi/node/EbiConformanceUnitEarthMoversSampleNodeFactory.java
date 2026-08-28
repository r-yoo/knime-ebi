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

import org.pm4knime.portobject.XLogPortObject;
import org.pm4knime.portobject.XLogPortObjectSpec;
import org.pm4knime.util.XLogUtil;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;

public class EbiConformanceUnitEarthMoversSampleNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi conformance unit-earth-movers-sample",
			"Compute unit-earth movers' stochastic conformance, which is 1 - the total variation distance, if both inputs need to be sampled.",
			"Compute unit-earth movers' stochastic conformance, which is 1 - the total variation distance, if both inputs need to be sampled. If one input is a log or a finite stochastic language, then use `uemsc`.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"StochasticLabelledPetriNetSimpleWeights",
						"PetriNetPortObject",
						"",
						true
					),
					new EbiCommandMetadataParameter(
						"XLog",
						"XLogPortObject",
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

	public EbiConformanceUnitEarthMoversSampleNodeFactory() {
		super(COMMAND_METADATA, EbiConformanceUnitEarthMoversSampleNodeFactory::addPorts, EbiConformanceUnitEarthMoversSampleNodeFactory::configureModel);
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
            .parametersClass(EbiConformanceUnitEarthMoversSampleNodeSettings.class)
            .configure(EbiConformanceUnitEarthMoversSampleNodeFactory::configure)
            .execute(EbiConformanceUnitEarthMoversSampleNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof PetriNetPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid PetriNetPortObject!");
        }

        if (!(input.getInPortSpec(1) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi conformance unit-earth-movers-sample", "fraction", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[3];
            final EbiConformanceUnitEarthMoversSampleNodeSettings settings = input.getParameters();

            final PetriNetPortObject inputPort0 = input.getInPortObject(0);
            final ByteArrayOutputStream inputBuffer0 = new ByteArrayOutputStream();
            PetriNetUtil.exportToStream(inputPort0.getANet(), inputBuffer0);
            ebiInputs[0] = inputBuffer0.toString(StandardCharsets.UTF_8);
            ebiInputs[1] = XESUtil.writeLogToXesString(input.getInPortObject(1));
            ebiInputs[2] = String.valueOf(settings.m_input2);

            final String result = CallEbi.call_ebi(
                "Ebi conformance unit-earth-movers-sample",
                ".frac",
                ebiInputs);

            if (result != null && result.stripLeading().startsWith("Ebi: error:")) {
                throw new IllegalStateException(result.trim());
            }
            final DataTableSpec spec = TableUtil.createOutputSpec(
                COMMAND_METADATA.commandName,
                COMMAND_METADATA.output.type,
                StringCell.TYPE);
            final BufferedDataContainer container = input.getExecutionContext().createDataContainer(spec);
            container.addRowToTable(new DefaultRow("Row0", new StringCell(result)));
            container.close();
            output.setOutData(0, container.getTable());
        } catch (Exception ex) {
            final String detail = ex.getMessage();
            throw new RuntimeException(
                "Ebi command failed: Ebi conformance unit-earth-movers-sample"
                    + (detail == null || detail.isBlank() ? "" : ": " + detail),
                ex);
        }
    }
}
