package org.ryoo.knimeEbi.scaffolder;

public class EbiCommandMetadataParameter {
	public String type;
	public String portType;
	public String typeDescription;
	public boolean isPort;
	
	public EbiCommandMetadataParameter(String type, String portType, String typeDescription, boolean isPort) {
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
