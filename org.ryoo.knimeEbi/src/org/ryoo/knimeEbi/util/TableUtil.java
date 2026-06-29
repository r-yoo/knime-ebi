package org.ryoo.knimeEbi.util;

import org.knime.core.data.DataTableSpec;
import org.knime.core.data.DataType;

public class TableUtil {
	public static DataTableSpec createOutputSpec(final String commandName, final String columnName, final DataType columnType) {
    	return new DataTableSpec(
    			commandName,
    			new String[] {columnName},
    			new DataType[] {columnType});
    }
}
