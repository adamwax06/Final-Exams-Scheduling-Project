import java.util.*;
import java.io.*;

public class Run{
    public static void main(String[] args) {
        try {
            // create a new scheduler
            ExamScheduler scheduler = new ExamScheduler();
            
            // create a sample graph
            scheduler.createGraph();
            
            // color the graph
            scheduler.colorGraph();
            
            // print the schedule
            System.out.println("Number of time slots needed: " + scheduler.getNumTimeSlots());
            System.out.println("Exam Schedule:");
            Map<String, List<String>> schedule = scheduler.getExamSchedule();
            for (Map.Entry<String, List<String>> entry : schedule.entrySet()) {
                System.out.println(entry.getKey() + ": " + String.join(", ", entry.getValue()));
            }
            
            // validate the coloring
            System.out.println("\nIs valid coloring: " + scheduler.isValidColoring());
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}