package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiFilterTracesLengthNodeSettings implements NodeParameters {

    @Widget(title = "the operator", description = "the operator")
    String m_input1 = "";

    @Widget(title = "the value", description = "the value")
    @NumberInputWidget
    int m_input2 = 0;

}
