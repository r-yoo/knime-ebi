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

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;

public class EbiAnalyseNonStochasticActivitiesNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi analyse-non-stochastic activities",
			"Shows the activities that are declared in the object.",
			"Shows the activities that are declared in the object.",
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
				"string",
				"BufferedDataTable",
				"",
				true
			)
		);

	public EbiAnalyseNonStochasticActivitiesNodeFactory() {
		super(COMMAND_METADATA, EbiAnalyseNonStochasticActivitiesNodeFactory::addPorts, EbiAnalyseNonStochasticActivitiesNodeFactory::configureModel);
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
            .withoutParameters()
            .configure(EbiAnalyseNonStochasticActivitiesNodeFactory::configure)
            .execute(EbiAnalyseNonStochasticActivitiesNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof ProcessTreePortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid ProcessTreePortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi analyse-non-stochastic activities", "Ebi analyse-non-stochastic activities", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[1];

            final ProcessTreePortObject inputPort0 = input.getInPortObject(0);
            ebiInputs[0] = inputPort0.toText();

            final String result = CallEbi.call_ebi(
                "Ebi analyse-non-stochastic activities",
                ".txt",
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
            throw new RuntimeException("Ebi command failed: Ebi analyse-non-stochastic activities", ex);
        }
    }
}
