package cs131.pa1.filter.concurrent;

public class PrintFilter extends ConcurrentFilter {
	public PrintFilter() {
		super();
	}
	
	public void process() throws InterruptedException {
		while(!isDone()) {
			processLine(input.take());
		}
	}
	
	public String processLine(String line) {
		System.out.println(line);
		return null;
	}
	
	public boolean isDone() {
		if (prevThread == null) {
			return input.isEmpty();
		} else {
			System.out.print("print:   ");
			System.out.print(!prevThread.isAlive() + "  ");
			System.out.print(input.isEmpty() + "\n");
			return !prevThread.isAlive() && input.isEmpty();
		}
	}
}
