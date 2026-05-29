package org.knime.examples.numberformatter;

import java.io.File;
import java.io.IOException;

import org.knime.core.data.DataCell;
import org.knime.core.data.DataColumnSpec;
import org.knime.core.data.DataColumnSpecCreator;
import org.knime.core.data.DataRow;
import org.knime.core.data.DataTableSpec;
import org.knime.core.data.def.DefaultRow;
import org.knime.core.data.def.StringCell;

import org.knime.core.node.BufferedDataContainer;
import org.knime.core.node.BufferedDataTable;
import org.knime.core.node.CanceledExecutionException;
import org.knime.core.node.ExecutionContext;
import org.knime.core.node.ExecutionMonitor;
import org.knime.core.node.InvalidSettingsException;
import org.knime.core.node.NodeLogger;
import org.knime.core.node.NodeModel;
import org.knime.core.node.NodeSettingsRO;
import org.knime.core.node.NodeSettingsWO;

import org.processmining.ebi.CallEbi;

public class NumberFormatterNodeModel extends NodeModel {

    private static final NodeLogger LOGGER =
            NodeLogger.getLogger(NumberFormatterNodeModel.class);

    protected NumberFormatterNodeModel() {

        // 0 input ports
        // 1 output port
        super(0, 1);
    }

    @Override
    protected BufferedDataTable[] execute(
            final BufferedDataTable[] inData,
            final ExecutionContext exec)
            throws Exception {

        LOGGER.info("Calling Ebi...");

        // Call Ebi
        String result = CallEbi.call_ebi(
                "Ebi itself logo",
                "text",
                new String[0]);

        LOGGER.info("Ebi returned:\n" + result);

        // Create output table structure
        DataColumnSpec[] columnSpecs = new DataColumnSpec[] {
                new DataColumnSpecCreator(
                        "Ebi Output",
                        StringCell.TYPE).createSpec()
        };

        DataTableSpec outputSpec =
                new DataTableSpec(columnSpecs);

        BufferedDataContainer container =
                exec.createDataContainer(outputSpec);

        // Create one row
        DataCell[] cells = new DataCell[] {
                new StringCell(result)
        };

        DataRow row =
                new DefaultRow("Row0", cells);

        container.addRowToTable(row);

        container.close();

        return new BufferedDataTable[] {
                container.getTable()
        };
    }

    @Override
    protected DataTableSpec[] configure(
            final DataTableSpec[] inSpecs)
            throws InvalidSettingsException {

        DataColumnSpec[] columnSpecs = new DataColumnSpec[] {
                new DataColumnSpecCreator(
                        "Ebi Output",
                        StringCell.TYPE).createSpec()
        };

        return new DataTableSpec[] {
                new DataTableSpec(columnSpecs)
        };
    }

    @Override
    protected void saveSettingsTo(final NodeSettingsWO settings) {}

    @Override
    protected void loadValidatedSettingsFrom(
            final NodeSettingsRO settings)
            throws InvalidSettingsException {}

    @Override
    protected void validateSettings(
            final NodeSettingsRO settings)
            throws InvalidSettingsException {}

    @Override
    protected void loadInternals(
            File nodeInternDir,
            ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {}

    @Override
    protected void saveInternals(
            File nodeInternDir,
            ExecutionMonitor exec)
            throws IOException, CanceledExecutionException {}

    @Override
    protected void reset() {}
}