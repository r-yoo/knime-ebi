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

import org.pm4knime.portobject.XLogPortObjectSpec;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.StringCell;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;

public class EbiTestLogCategoricalAttributeNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi test log-categorical-attribute",
			"Test the hypothesis that the sub-logs defined by the categorical attribute are derived from identical processes.",
			"Test the hypothesis that the sub-logs defined by the categorical attribute are derived from identical processes.",
			new ArrayList<>(
				List.of(
					new EbiCommandMetadataParameter(
						"XLog",
						"XLogPortObject",
						"",
						true
					),
					new EbiCommandMetadataParameter(
						"String",
						"",
						"The trace attribute for which the test is to be performed. The trace attributes of a log can be found using `Ebi info`.",
						false
					),
					new EbiCommandMetadataParameter(
						"Integer",
						"",
						"The number of samples taken.",
						false
					),
					new EbiCommandMetadataParameter(
						"BigFraction",
						"",
						"The threshold p-value.",
						false
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

	public EbiTestLogCategoricalAttributeNodeFactory() {
		super(COMMAND_METADATA, EbiTestLogCategoricalAttributeNodeFactory::addPorts, EbiTestLogCategoricalAttributeNodeFactory::configureModel);
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
            .parametersClass(EbiTestLogCategoricalAttributeNodeSettings.class)
            .configure(EbiTestLogCategoricalAttributeNodeFactory::configure)
            .execute(EbiTestLogCategoricalAttributeNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi test log-categorical-attribute", "Ebi test log-categorical-attribute", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {}
}
