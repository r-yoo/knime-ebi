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
            .parametersClass(EbiTestLogCategoricalAttributeNodeSettings.class)
            .configure(EbiTestLogCategoricalAttributeNodeFactory::configure)
            .execute(EbiTestLogCategoricalAttributeNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi test log-categorical-attribute", "string", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[4];
            final EbiTestLogCategoricalAttributeNodeSettings settings = input.getParameters();

            ebiInputs[0] = XESUtil.writeLogToXesString(input.getInPortObject(0));
            ebiInputs[1] = String.valueOf(settings.m_input1);
            ebiInputs[2] = String.valueOf(settings.m_input2);
            ebiInputs[3] = String.valueOf(settings.m_input3);

            final String result = CallEbi.call_ebi(
                "Ebi test log-categorical-attribute",
                ".txt",
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
                "Ebi command failed: Ebi test log-categorical-attribute"
                    + (detail == null || detail.isBlank() ? "" : ": " + detail),
                ex);
        }
    }
}
