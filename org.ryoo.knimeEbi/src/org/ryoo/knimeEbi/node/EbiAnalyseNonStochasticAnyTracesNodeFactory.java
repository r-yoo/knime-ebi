package org.ryoo.knimeEbi.node;

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

import org.pm4knime.portobject.ProcessTreePortObjectSpec;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;

public class EbiAnalyseNonStochasticAnyTracesNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi analyse-non-stochastic any-traces",
			"Compute whether the model has any traces.",
			"Compute whether the model has any traces.",
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
				"boolean",
				"BufferedDataTable",
				"",
				true
			)
		);

	public EbiAnalyseNonStochasticAnyTracesNodeFactory() {
		super(COMMAND_METADATA, EbiAnalyseNonStochasticAnyTracesNodeFactory::addPorts, EbiAnalyseNonStochasticAnyTracesNodeFactory::configureModel);
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
            .configure(EbiAnalyseNonStochasticAnyTracesNodeFactory::configure)
            .execute(EbiAnalyseNonStochasticAnyTracesNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof ProcessTreePortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid ProcessTreePortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi analyse-non-stochastic any-traces", "Ebi analyse-non-stochastic any-traces", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {}
}
