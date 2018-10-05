package cs131.pa1.filter.concurrent;


public class PrintFilter extends ConcurrentFilter {
    public PrintFilter() {
        super();
    }

    public void process() throws InterruptedException {
        while (!isDone()) {
            processLine(input.take());
        }
    }

    public String processLine(String line) {
        if (!line.equals(POISON_PILL)){
            System.out.println(line);
        }
        return null;
    }

}
