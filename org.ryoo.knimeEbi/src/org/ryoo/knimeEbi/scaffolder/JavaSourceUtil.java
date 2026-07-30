package org.ryoo.knimeEbi.scaffolder;

public final class JavaSourceUtil {
	
	public static boolean isJavaIdentifier(final String value) {
		if (value.isEmpty() || !Character.isJavaIdentifierStart(value.charAt(0))) {
			return false;
		}

		for (int i = 1; i < value.length(); i++) {
			if (!Character.isJavaIdentifierPart(value.charAt(i))) {
				return false;
			}
		}

		return true;
	}
	
	public static String escapeJavaString(final String value) {
		if (value == null) {
			return "";
		}
		
		return value.replace("\\", "\\\\")
				.replace("\"", "\\\"")
				.replace("\r", "\\r")
				.replace("\n", "\\n")
				.replace("\t", "\\t");
	}
}
