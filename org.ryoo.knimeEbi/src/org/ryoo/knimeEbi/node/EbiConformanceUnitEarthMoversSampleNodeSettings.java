package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;
import org.knime.node.parameters.widget.number.NumberInputWidget;

public final class EbiConformanceUnitEarthMoversSampleNodeSettings implements NodeParameters {

    @Widget(title = "Number of traces to sample", description = "Number of traces to sample.")
    @NumberInputWidget
    int m_input2 = 0;

}
