package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiAssociationTraceAttributeNodeSettings implements NodeParameters {

    @Widget(title = "The trace attribute for which association is to be computed. The trace attributes of a log can be found using `Ebi info`", description = "The trace attribute for which association is to be computed. The trace attributes of a log can be found using `Ebi info`.")
    String m_input1 = "";

    @Widget(title = "The number of samples", description = "The number of samples.")
    @NumberInputWidget
    int m_input2 = 0;

}
