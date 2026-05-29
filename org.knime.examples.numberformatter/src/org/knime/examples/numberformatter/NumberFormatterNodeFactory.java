package org.knime.examples.numberformatter;

import org.knime.core.node.NodeDialogPane;
import org.knime.core.node.NodeFactory;
import org.knime.core.node.NodeView;

public class NumberFormatterNodeFactory
        extends NodeFactory<NumberFormatterNodeModel> {

    @Override
    public NumberFormatterNodeModel createNodeModel() {

        // Create node model
        return new NumberFormatterNodeModel();
    }

    @Override
    public int getNrNodeViews() {

        // No visualization
        return 0;
    }

    @Override
    public NodeView<NumberFormatterNodeModel> createNodeView(
            final int viewIndex,
            final NumberFormatterNodeModel nodeModel) {

        // No view
        return null;
    }

    @Override
    public boolean hasDialog() {

        // No dialog/settings window
        return false;
    }

    @Override
    public NodeDialogPane createNodeDialogPane() {

        // No dialog
        return null;
    }
}