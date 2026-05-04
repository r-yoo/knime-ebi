package ryoo.knimeintegration;

import ryoo.interfaces.ICallEbiService;
import ryoo.mock.CallEbiMock;

import java.util.*;

public class Main {

	public static void main(String[] args) {
		boolean useMock = Arrays.asList(args).contains("use_mock"); // Search for "use_mock" in program arguments of main 
		
		System.out.println("Mock enabled: " + useMock);
		
		System.out.println("Testing Ebi...");
		
		ICallEbiService callEbiService = useMock ? new CallEbiMock() : new CallEbiWrapper();
		
		String result = callEbiService.get_logo();
        
		System.out.println("Result:");
		
		System.out.println(result);
	}

}
