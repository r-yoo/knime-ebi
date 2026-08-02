package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;

public final class EbiDiscoverDirectlyFollowsGraphNodeSettings implements NodeParameters {

    @Widget(title = "The minimum fraction of traces that should fit the resulting model", description = "The minimum fraction of traces that should fit the resulting model.")
    String m_input1 = "";

}
