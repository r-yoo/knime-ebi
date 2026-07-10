package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiAnansInftNodeFactory extends EbiDefaultNodeFactory {
	public EbiAnansInftNodeFactory() {
		super("Ebi analyse-non-stochastic infinitely-many-traces", "Compute whether the model has infinitely many traces. The computation may not terminate if the model is unbounded.", "bool", BufferedDataTable.TYPE);
	}
}