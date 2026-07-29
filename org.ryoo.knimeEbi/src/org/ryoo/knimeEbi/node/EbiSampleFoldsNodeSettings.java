package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiSampleFoldsNodeSettings implements NodeParameters {

    @Widget(title = "The number of folds", description = "The number of folds.")
    @NumberInputWidget
    int m_input1 = 0;

    @Widget(title = "The random seed", description = "The random seed.")
    @NumberInputWidget
    int m_input2 = 0;

    @Widget(title = "The fold to be returned", description = "The fold to be returned.")
    @NumberInputWidget
    int m_input3 = 0;

}
