package org.ryoo.knimeEbi.scaffolder;

public class EbiCommandMetadataParameter {
	public final String type;
	public final String portType;
	public final String typeDescription;
	public final boolean isPort;
	
	public EbiCommandMetadataParameter(final String type, final String portType, final String typeDescription, final boolean isPort) {
		this.type = type;
		this.isPort = isPort;
		
		if(isPort) {
			this.portType = portType;
			this.typeDescription = "";
		}
		else {
			this.portType = "";
			this.typeDescription = typeDescription;
		}
	}
	
	@Override
	public String toString() {
		return type + ", " + portType + ", " + typeDescription + ", " + isPort;
	}
}
