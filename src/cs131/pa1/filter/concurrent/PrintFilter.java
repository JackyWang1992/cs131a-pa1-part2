package cs131.pa1.filter.concurrent;


public class PrintFilter extends ConcurrentFilter {
    private String line = "";
    public PrintFilter() {
        super();
    }

    public void process() throws InterruptedException {
        line = input.take();
        while (!isDone()) {
            processLine(line);
            line = input.take();
        }
    }

    public String processLine(String line) {
        System.out.println(line);
        return null;
    }

    public boolean isDone() {
        return line.equals(POISON_PILL);
    }

}
