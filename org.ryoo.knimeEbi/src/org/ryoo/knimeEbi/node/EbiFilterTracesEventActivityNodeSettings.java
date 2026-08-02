package org.ryoo.knimeEbi.node;

import org.knime.node.parameters.NodeParameters;
import org.knime.node.parameters.Widget;

public final class EbiFilterTracesEventActivityNodeSettings implements NodeParameters {

    @Widget(title = "which event(s) in the trace should be the activity", description = "which event(s) in the trace should be the activity")
    String m_input1 = "";

    @Widget(title = "the activity the filter targets", description = "the activity the filter targets")
    String m_input2 = "";

}
