package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConcurrentREPL {

    static String currentWorkingDirectory;
    private static List<String> bgJobs;
    //list that help us to kill commands
    private static List<ConcurrentFilter> bgFilters;
    //the list of last filters which help us to check whether background jobs are finished
    private static List<ConcurrentFilter> lastFilters;

    public static void main(String[] args) {
        init();
        Scanner s = new Scanner(System.in);
        String command;

        while (true) {
            //obtaining the command from the user
            System.out.print(Message.NEWCOMMAND);
            command = s.nextLine().trim();

            if (command.equals("exit")) {
                break;
            } else if (command.endsWith("&")) {
                addBgJobs(command);
            } else if (command.startsWith("kill")) {
                kill(command);
            } else if (command.equals("repl_jobs")) {
                replJobs();
            } else if (!command.equals("")) {
                //building the filters list from the command
                ConcurrentFilter filterList = ConcurrentCommandBuilder.createFiltersFromCommand(command);
                filterList = startCurrentFilter(filterList);
                //since it's normal concurrent filters, let the last filter join.
                try {
                    if (filterList != null) {
                        filterList.getThread().join();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        s.close();
        System.out.print(Message.GOODBYE);
    }

    private static void init() {
        //initialize all the objects used in this project
        currentWorkingDirectory = System.getProperty("user.dir");
        bgJobs = new ArrayList<>();
        bgFilters = new ArrayList<>();
        lastFilters = new ArrayList<>();
        System.out.print(Message.WELCOME);
    }

    private static void addBgJobs(String command) {
        //add background job to background jobs list
        bgJobs.add(command);
        String[] bgCommands = command.split("&");
        ConcurrentFilter filterList = ConcurrentCommandBuilder.createFiltersFromCommand(bgCommands[0]);
        //add the first filter of one line of commands to the background filters list in order to track
        bgFilters.add(filterList);
        filterList = startCurrentFilter(filterList);
        //add the last filter of one line of commands to the background filters
        // list to track whether the command is finished or not
        lastFilters.add(filterList);
    }

    private static ConcurrentFilter startCurrentFilter(ConcurrentFilter filter) {
        //actual start of the concurrent thread
        while (filter != null && filter.getNext() != null) {
            Thread t = new Thread(filter);
            t.start();
            filter.setThread(t);
            filter = (ConcurrentFilter) filter.getNext();
        }
        Thread th = new Thread(filter);
        th.start();
        if (filter != null ){
            filter.setThread(th);
        }
        return filter;
    }

    private static void kill(String command) {
        //kill commands that are still running by interrupt the threads of filters.
        int index;
        String[] commandsToKill = command.split("\\s+");
        if (commandsToKill.length == 1) {
            System.out.printf(Message.REQUIRES_PARAMETER.toString(), command);
            return;
        }
        try {
            index = Integer.parseInt(commandsToKill[1]);
            if (commandsToKill.length > 2 || (index > bgJobs.size())) {
                System.out.printf(Message.INVALID_PARAMETER.toString(), command);
                return;
            }
        } catch (NumberFormatException e) {
            System.out.printf(Message.INVALID_PARAMETER.toString(), command);
            return;
        }

        ConcurrentFilter curr = bgFilters.get(index - 1);
        while (curr != null) {
            if (curr.getThread() != null) {
                curr.getThread().interrupt();
            }
            curr = (ConcurrentFilter) curr.getNext();
        }
        bgJobs.set(index - 1, null);
    }

    private static void replJobs() {
        //to know how to print the running jobs, if the job is finished, it will automatically be deleted.
        for (int j = 0; j < lastFilters.size(); j++) {
            if (lastFilters.get(j).getThread() != null && !lastFilters.get(j).getThread().isAlive()) {
                bgJobs.set(j, null);
            }
        }
        for (int i = 0; i < bgJobs.size(); i++) {
            if (bgJobs.get(i) != null) {
                System.out.println("\t" + (i + 1) + "." + " " + bgJobs.get(i));
            }
        }
    }
}
