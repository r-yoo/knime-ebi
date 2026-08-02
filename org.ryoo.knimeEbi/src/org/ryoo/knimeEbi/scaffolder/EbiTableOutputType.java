package org.ryoo.knimeEbi.scaffolder;

public enum EbiTableOutputType { // -> all to .txt output format and then KNIME table
    FRACTION("fraction"),
    STRING("string"),
    BOOL("boolean"),
    ROOT("root"),
    CONTAINS_HTML("containsroot_html"),
    LOGARITHM("logarithm"),
    ROOTLOG("rootlog"),
    ROOTLOGDIV("rootlogdiv"),
    LOGDIV("logdiv");
	

    private final String ebiName;

    EbiTableOutputType(final String ebiName) {
        this.ebiName = ebiName;
    }

    public String getEbiName() {
        return ebiName;
    }

    public static boolean isTableCompatible(final String output) {
    	if (output == null) {
            return false;
        }

        String normalizedOutput = output.trim().toLowerCase();
        
        for (EbiTableOutputType type : values()) {
            if (type.ebiName.equals(normalizedOutput)) {
                return true;
            }
        }

        return false;
    }
}