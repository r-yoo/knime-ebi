package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;

public final class EbiDiscoverNonStochasticInductiveMinerInfrequentNodeSettings implements NodeParameters {

    @Widget(title = "The amount of noise filtering, where 0 means no noise filtering is applied, and 1 means that maximum noise filtering is applied", description = "The amount of noise filtering, where 0 means no noise filtering is applied, and 1 means that maximum noise filtering is applied.")
    String m_input1 = "";

}
