package cs131.pa1.filter.concurrent;

import cs131.pa1.filter.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ConcurrentREPL {

    static String currentWorkingDirectory;

    public static void main(String[] args) {
        currentWorkingDirectory = System.getProperty("user.dir");
        Scanner s = new Scanner(System.in);
        System.out.print(Message.WELCOME);
        String command;
        while (true) {
            //obtaining the command from the user
            System.out.print(Message.NEWCOMMAND);
            command = s.nextLine();
            if (command.equals("exit")) {
                break;
            } else if (!command.trim().equals("")) {
                //building the filters list from the command
                ConcurrentFilter filterlist = ConcurrentCommandBuilder.createFiltersFromCommand(command);
                ConcurrentFilter cur = filterlist;

                while (cur != null && cur.getNext() != null) {
                    Thread t = new Thread(cur);
                    t.start();
                    cur = (ConcurrentFilter) cur.getNext();
                    cur.setPrevThread(t);
                }
                
                Thread printThread = new Thread(cur);
                printThread.start();
                try {printThread.join();} catch (InterruptedException e ) {}

//				while(filterlist != null) {
//					filterlist.process();
//					filterlist = (ConcurrentFilter) filterlist.getNext();
//				}
            }
        }
        s.close();
        System.out.print(Message.GOODBYE);
    }
}