package ryoo.knimeintegration;

import ryoo.interfaces.ICallEbiService;
import ryoo.mock.CallEbiMock;

import java.util.*;

public class Main {

	public static void main(String[] args) {
		boolean useMock = Arrays.asList(args).contains("use_mock");
		
		System.out.println("Mock enabled: " + useMock);
		
		System.out.println("Testing Ebi...");
		
		String[] inputs = {"hello"};
		
		ICallEbiService callEbiService = useMock ? new CallEbiMock() : new CallEbiWrapper();
		
		String result = callEbiService.get_version();
        
		System.out.println("Result: " + result);
		
	}

}
