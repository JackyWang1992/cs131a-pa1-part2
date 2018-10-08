package cs131.pa1.filter.concurrent;

import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;


public abstract class ConcurrentFilter extends Filter implements Runnable {

    LinkedBlockingQueue<String> input;
    LinkedBlockingQueue<String> output;
    //the string which denote the previous thread is done
    static final String POISON_PILL = "END OF THREAD";
    String line = "";
    //private field that record the specific thread of this filter
    private Thread thread;

    @Override
    public void setPrevFilter(Filter prevFilter) {
        prevFilter.setNextFilter(this);
    }

    @Override
    public void setNextFilter(Filter nextFilter) {
        if (nextFilter instanceof ConcurrentFilter) {
            ConcurrentFilter sequentialNext = (ConcurrentFilter) nextFilter;
            this.next = sequentialNext;
            sequentialNext.prev = this;
            if (this.output == null) {
                this.output = new LinkedBlockingQueue<>();
            }
            sequentialNext.input = this.output;
        } else {
            throw new RuntimeException("Should not attempt to link dissimilar filter types.");
        }
    }

    Filter getNext() {
        return next;
    }

    public void process() throws InterruptedException {
        while (!isDone()) {
            line = input.take();
            String processedLine = processLine(line);
            if (processedLine != null) {
                output.put(processedLine);
            }
        }
        //when process finished, put poison pill at last
        output.put(POISON_PILL);
    }

    public boolean isDone() {
    	return line.equals(POISON_PILL);
    }

    protected abstract String processLine(String line);

    public void run() {
        try {
            process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void setThread(Thread t){
        thread = t;
    }

    Thread getThread(){
        return this.thread;
    }
}
