package ryoo.mock;

import ryoo.interfaces.ICallEbiService;

public class CallEbiMock implements ICallEbiService{

	@Override
	public String call_ebi(String command_name, String output_format, String[] inputs) {
		return "Hello I am a mock!";
	}

	@Override
	public String get_version() {
		return "Mock v1";
	}
}
