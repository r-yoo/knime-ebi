package org.ryoo.knimeEbi.node;

import org.knime.core.node.BufferedDataTable;

public class EbiDinsPfxtPtreeNodeFactory extends EbiDefaultNodeFactory {
	public EbiDinsPfxtPtreeNodeFactory() {
		super("Ebi filter traces empty", "Discover a process tree that is a prefix tree of the log.", "XES event log", BufferedDataTable.TYPE);
	}
}