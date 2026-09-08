package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiTestPermutationTestLogModelNodeSettings implements NodeParameters {

    @Widget(title = "The number of samples/permutations to execute", description = "The number of samples/permutations to execute.")
    @NumberInputWidget
    int m_input2 = 0;

    @Widget(title = "The threshold p-value", description = "The threshold p-value")
    String m_input3 = "";

}
