package ryoo.knimeintegration;

import org.processmining.ebi.CallEbi;

import ryoo.interfaces.ICallEbiService;

public class CallEbiWrapper implements ICallEbiService{

	@Override
	public String call_ebi(String command_name, String output_format, String[] inputs) {
		return CallEbi.call_ebi(command_name, output_format, inputs);
	}

	@Override
	public String get_logo(){
		return call_ebi("Ebi itself logo", "text", new String[0]);
	}
	
}
