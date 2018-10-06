package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConcurrentREPL {

    static String currentWorkingDirectory;
    static List<String> bgJobs;
    static List<ConcurrentFilter> bgFilters;
    static List<ConcurrentFilter> lastFilters;

    public static void main(String[] args) {
        currentWorkingDirectory = System.getProperty("user.dir");
        bgJobs = new ArrayList<>();
        bgFilters = new ArrayList<>();
        lastFilters = new ArrayList<>();
        Scanner s = new Scanner(System.in);
        System.out.print(Message.WELCOME);
        String command;

        while (true) {
            //obtaining the command from the user
            System.out.print(Message.NEWCOMMAND);
            command = s.nextLine();

            if (command.equals("exit")) {
                break;
            } else if (command.trim().endsWith("&")) {
                bgJobs.add(command.trim());
                String[] bgCmds = command.split("&");
                String bgCmd = bgCmds[0];

                ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(bgCmds[0]);
                bgFilters.add(filterlist);

                while (filterlist != null && filterlist.getNext() != null) {
                    Thread t = new Thread(filterlist);
                    t.start();
                    filterlist.setThread(t);
                    filterlist = (ConcurrentFilter) filterlist.getNext();
                }
                lastFilters.add(filterlist);
                Thread th = new Thread(filterlist);
                th.start();
                filterlist.setThread(th);
            } else if (command.trim().startsWith("kill")) {
                kill(command);
            } else if (command.trim().equals("repl_jobs")) {
              replJobs();
            } else if (!command.trim().equals("")) {
                //building the filters list from the command
                ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
                while (filterlist != null && filterlist.getNext() != null) {
                    Thread t = new Thread(filterlist);
                    t.start();
                    filterlist.setThread(t);
                    filterlist = (ConcurrentFilter) filterlist.getNext();
                }

                Thread th = new Thread(filterlist);
                th.start();
                try {
                    th.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        s.close();
        System.out.print(Message.GOODBYE);
    }

    public void runCommand(String commands){
        ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(commands);
        while (filterlist != null && filterlist.getNext() != null) {
            Thread t = new Thread(filterlist);
            t.start();
            filterlist = (ConcurrentFilter) filterlist.getNext();
        }
    }

    public void joinLastFilter(ConcurrentFilter lastfilter) {
        Thread th = new Thread(lastfilter);
        th.start();
        try {
            th.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean kill(String command) {
        int kindex;
        String[] kcommands = command.split("\\s+");
        if (kcommands.length == 1) {
            System.out.printf(Message.REQUIRES_PARAMETER.toString(), command);
            return false;
        }
        try {
            kindex = Integer.parseInt(kcommands[1]);
            if (kcommands.length > 2 || (kindex > bgJobs.size())){
                System.out.printf(Message.INVALID_PARAMETER.toString(), command);
                return false;
            }
        } catch (NumberFormatException e) {
            System.out.printf(Message.INVALID_PARAMETER.toString(), command);
            return false;
        }
        ConcurrentFilter curr = bgFilters.get(kindex - 1);
        while (curr != null) {
            if (curr.getThread()!= null) {
                curr.getThread().interrupt();
            }
            curr = (ConcurrentFilter) curr.getNext();
        }
        bgJobs.set(kindex - 1, null);
        return true;
    }

    private static void replJobs() {
        for (int j = 0; j < lastFilters.size(); j++) {
            if(lastFilters.get(j).getThread()!= null && !lastFilters.get(j).getThread().isAlive()) {
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
