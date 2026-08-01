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

public class EbiAssociationTraceAttributeNodeFactory extends EbiDefaultNodeFactory {
	private static final EbiCommandMetadata COMMAND_METADATA =
		new EbiCommandMetadata(
			"Ebi association trace-attribute",
			"Compute the association between the process and a trace attribute.",
			"Compute the association between the process and a trace attribute.",
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
						"The trace attribute for which association is to be computed. The trace attributes of a log can be found using `Ebi info`.",
						false
					),
					new EbiCommandMetadataParameter(
						"Integer",
						"",
						"The number of samples.",
						false
					)
				)
			),
			new EbiCommandMetadataParameter(
				"containsroot_html",
				"BufferedDataTable",
				"",
				true
			)
		);

	public EbiAssociationTraceAttributeNodeFactory() {
		super(COMMAND_METADATA, EbiAssociationTraceAttributeNodeFactory::addPorts, EbiAssociationTraceAttributeNodeFactory::configureModel);
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
            .parametersClass(EbiAssociationTraceAttributeNodeSettings.class)
            .configure(EbiAssociationTraceAttributeNodeFactory::configure)
            .execute(EbiAssociationTraceAttributeNodeFactory::execute);
    }

    public static void configure(final DefaultModel.ConfigureInput input, final DefaultModel.ConfigureOutput output) 
    	throws InvalidSettingsException {
     
        if (!(input.getInPortSpec(0) instanceof XLogPortObjectSpec)) {
            throw new InvalidSettingsException("Input is not a valid XLogPortObject!");
        }

        output.setOutSpec(0, TableUtil.createOutputSpec("Ebi association trace-attribute", "containsroot_html", StringCell.TYPE));
    }

    public static void execute(final DefaultModel.ExecuteInput input, final DefaultModel.ExecuteOutput output) {
        try {
            final String[] ebiInputs = new String[3];
            final EbiAssociationTraceAttributeNodeSettings settings = input.getParameters();

            ebiInputs[0] = XESUtil.writeLogToXesString(input.getInPortObject(0));
            ebiInputs[1] = String.valueOf(settings.m_input1);
            ebiInputs[2] = String.valueOf(settings.m_input2);

            final String result = CallEbi.call_ebi(
                "Ebi association trace-attribute",
                ".croot",
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
            throw new RuntimeException("Ebi command failed: Ebi association trace-attribute", ex);
        }
    }
}
