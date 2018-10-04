package cs131.pa1.filter.concurrent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import cs131.pa1.filter.Filter;


public abstract class ConcurrentFilter extends Filter implements Runnable {

    protected LinkedBlockingQueue<String> input;
    protected LinkedBlockingQueue<String> output;
    protected Thread prevThread;

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

    public void setPrevThread(Thread t) {
        this.prevThread = t;
    }

    public Filter getNext() {
        return next;
    }

    public void process() throws InterruptedException {
        String line = input.take();
        String processedLine = processLine(line);
        if (processedLine != null) {
            output.put(processedLine);
        }
    }

    @Override
    public boolean isDone() {
        if (prevThread == null) {
            return input.size() == 0;
        }
        return !prevThread.isAlive() && input.size() == 0;
    }

    protected abstract String processLine(String line);

    public void run() {
        try {
            process();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
