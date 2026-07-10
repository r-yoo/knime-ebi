package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiTstBtstNodeFactory extends EbiDefaultNodeFactory {
	public EbiTstBtstNodeFactory() {
		super("Ebi test bootstrap-test", "Test the hypothesis that the logs are derived from identical processes.", "text", BufferedDataTable.TYPE);
	}
}