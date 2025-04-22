import java.io.*;
import java.util.*;


public class Test {
     
    public static void main(String[] args) {
        int min = Integer.MAX_VALUE;
        //for (int i = 0; i<100; i++){
            System.out.println(" run: ");
            //String filename = args[0];
            ExamScheduler scheduler = new ExamScheduler();
            try {
                scheduler.readFromFile("student_courses.txt");
                scheduler.colorGraph();
                System.out.println("Number of time slots needed: " + scheduler.getNumTimeSlots());
                
               /*  System.out.println("Exam Schedule:");
                scheduler.getExamSchedule().forEach((slot, list) ->
                    System.out.println(slot + ": " + String.join(", ", list))
                );*/
                System.out.println("Is valid coloring: " + scheduler.isValidColoring());
            } catch (FileNotFoundException e) {
                System.err.println("File not found:. ");
            }
            if (min>scheduler.getNumTimeSlots()) min = scheduler.getNumTimeSlots();
        //}
        System.out.println("Minimum is: "+ min);
    }
}
