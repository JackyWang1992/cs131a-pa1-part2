package cs131.pa1.filter.concurrent;

public class WcFilter extends ConcurrentFilter {
	private int linecount;
	private int wordcount;
	private int charcount;
	
	public WcFilter() {
		super();
	}
	
	public void process() throws InterruptedException {
		if(isDone()) {
			output.offer(processLine(null));
			System.out.println(output.size());
		} else {
			super.process();
			System.out.println("?" + output.size());
		}
	}
	
	public String processLine(String line) {
		//prints current result if ever passed a null
		if(line == null) {
			return linecount + " " + wordcount + " " + charcount;
		} else {
			linecount++;
			String[] wct = line.split(" ");
			wordcount += wct.length;
			String[] cct = line.split("|");
			charcount += cct.length;
			if (isDone()) {
				return linecount + " " + wordcount + " " + charcount;
			}
			return null;
		}
		
//		if(input.isEmpty()) {
//			String[] wct = line.split(" ");
//			wordcount += wct.length;
//			String[] cct = line.split("|");
//			charcount += cct.length;
//			return null;
////			return ++linecount + " " + wordcount + " " + charcount;
//		} else {
//			linecount++;
//			String[] wct = line.split(" ");
//			wordcount += wct.length;
//			String[] cct = line.split("|");
//			charcount += cct.length;
//			return null;
//		}
	}
}
