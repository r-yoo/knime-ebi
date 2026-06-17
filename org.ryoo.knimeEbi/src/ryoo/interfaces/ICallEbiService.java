package ryoo.interfaces;

public interface ICallEbiService {
	
	public String call_ebi(String command_name, String output_format, String[] inputs);
	
	public String get_logo();
}
