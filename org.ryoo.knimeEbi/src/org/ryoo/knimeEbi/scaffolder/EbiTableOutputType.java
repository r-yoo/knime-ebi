package org.ryoo.knimeEbi.scaffolder;

public enum EbiTableOutputType {
    FRACTION("fraction"),
    TEXT("text"),
    BOOL("bool"),
    ROOT("root"),
    LOGARITHM("logarithm"),
    ROOTLOG("rootlog");

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