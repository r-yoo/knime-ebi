package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiTestLogCategoricalAttributeNodeSettings implements NodeParameters {

    @Widget(title = "The trace attribute for which the test is to be performed. The trace attributes of a log can be found using `Ebi info`", description = "The trace attribute for which the test is to be performed. The trace attributes of a log can be found using `Ebi info`.")
    String m_input1 = "";

    @Widget(title = "The number of samples taken", description = "The number of samples taken.")
    @NumberInputWidget
    int m_input2 = 0;

    @Widget(title = "The threshold p-value", description = "The threshold p-value.")
    String m_input3 = "";

}
