package cs131.pa1.filter.concurrent;

public class PwdFilter extends ConcurrentFilter {
	PwdFilter() {
		super();
	}
	
	public void process() throws InterruptedException {
		output.put(processLine(""));
		output.put(POISON_PILL);
	}
	
	public String processLine(String line) {
		return ConcurrentREPL.currentWorkingDirectory;
	}
}
