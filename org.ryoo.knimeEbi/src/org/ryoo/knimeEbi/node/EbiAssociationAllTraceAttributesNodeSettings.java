package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiAssociationAllTraceAttributesNodeSettings implements NodeParameters {

    @Widget(title = "The number of samples taken", description = "The number of samples taken.")
    @NumberInputWidget
    int m_input1 = 0;

}
