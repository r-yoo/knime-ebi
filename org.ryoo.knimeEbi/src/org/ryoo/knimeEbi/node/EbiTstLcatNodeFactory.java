package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiTstLcatNodeFactory extends EbiDefaultNodeFactory {
	public EbiTstLcatNodeFactory() {
		super("Ebi test log-categorical-attribute", "Test the hypothesis that the sub-logs defined by the categorical attribute are derived from identical processes.", "text", BufferedDataTable.TYPE);
	}
}