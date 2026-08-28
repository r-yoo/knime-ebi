package org.ryoo.knimeEbi.entryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.processmining.ebi.CallEbi;
import org.ryoo.knimeEbi.scaffolder.EbiNodeGenerator;

public class Main {
	public static void main(String[] args) {
		EbiNodeGenerator.generateEbiNodes();
		
//		Path eventLogPath = Path.of( 
//			"src",
//			"org",
//			"ryoo",
//			"knimeEbi",
//			"scaffolder",
//			"TicketingManagementLog.xes"
//		);
//		
//		try {
//			String xesContent = Files.readString(eventLogPath, StandardCharsets.UTF_8);
//			CallEbi.call_ebi("Ebi discover-non-stochastic flower process-tree", "pnml", new String[] {xesContent});
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
