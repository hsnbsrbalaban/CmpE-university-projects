public class Vars {

	public String varName;
	public String varValue;
	
	public Vars(String varName_) {
		varName = varName_;
		varValue = "-1";
	}
	
	public Vars(String varName_, String varValue_) {
		varName = varName_;
		varValue = varValue_;
	}
	
	public boolean isOutput() {
		return varValue.equals("-1");
	}
}