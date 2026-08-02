package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiConformanceMarkovianNodeSettings implements NodeParameters {

    @Widget(title = "The order of the Markovian abstraction (length of subtraces)", description = "The order of the Markovian abstraction (length of subtraces).")
    @NumberInputWidget
    int m_input2 = 0;

    @Widget(title = "The stochastic conformance measure to be applied to the abstractions", description = "The stochastic conformance measure to be applied to the abstractions.")
    String m_input3 = "";

}
