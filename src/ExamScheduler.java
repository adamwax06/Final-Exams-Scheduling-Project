import java.util.*;
import java.io.*;

public class ExamScheduler{
    // our conflict graph
    private Graph graph;

    // mapping of classes to their assigned time slots
    private Map<String, Integer> colors;

    // mapping of time slots to their classes
    private Map<Integer, List<String>> colorGroups;

    // mapping of students to their classes
    private Map<String, Set<String>> studentCourses;

    /**
     * constructs a new exam scheduler object
     */
    public ExamScheduler(){
        graph = new Graph();
        colors = new HashMap<>();
        colorGroups = new HashMap<>();
        studentCourses = new HashMap<>();
    }

    /**
     * add a new class
     * 
     * @param className class to be added
     */
    public void addClass(String className){
        graph.addVertex(className);
    }

    /**
     * adds a conflict(edge)
     */
    public void addConflict(String class1, String class2){
        graph.addEdge(class1, class2);
    }

    /**
     * create a conflict graph for testing
     */
    public void createGraph() {
        try{
        // read student course registrations
        Scanner studentInput = new Scanner(new File("../data/studentCourses.txt"));
        while(studentInput.hasNextLine()) {
            String line = studentInput.nextLine().trim();
            String[] parts = line.split("\\|");
            
            if (parts.length == 2) {
                String studentId = parts[0];
                String[] courses = parts[1].split(",");
                
                // Add student's courses to the map
                Set<String> courseSet = new HashSet<>(Arrays.asList(courses));
                studentCourses.put(studentId, courseSet);
                
                // Create edges between all pairs of courses taken by this student
                for (int i = 0; i < courses.length; i++) {
                    for (int j = i + 1; j < courses.length; j++) {
                        addConflict(courses[i], courses[j]);
                    }
                }
            }
        }
        studentInput.close();
        }
        catch(Exception e){
            System.out.println("There was an IO error.");
        }
    }

    /**
     * color the graph following the algorithm below:
     * 1. arbitrarily pick a vertex and color it
     * 2. go to neighbors and give different colors
     * 3. check neighbors; if adjacent neighbors share a color, change one
     * 4. if not fully colored, repeat process with uncolored vertices
     * 
     * @return map of vertices to their assigned colors
     */
    public Map<String, Integer> colorGraph() {
        // clear any existing coloring
        colors.clear();
        colorGroups.clear();
        
        if (graph.isEmpty()) {
            return colors;
        }
        
        // get all vertices
        List<String> vertices = new ArrayList<>(graph.getVertices());
        
        // sort vertices by degree (number of conflicts) in descending order
        vertices.sort((v1, v2) -> Integer.compare(graph.getDegree(v2), graph.getDegree(v1)));
        
        // process all vertices with the algorithm
        while (!vertices.isEmpty()) {
            // step 1: Arbitrarily pick a vertex and color it
            String current = vertices.remove(0);
                
            // get colors used by neighbors
            Set<Integer> neighborColors = new HashSet<>();
            for (String neighbor : graph.getNeighbors(current)) {
                if (colors.containsKey(neighbor)) {
                    neighborColors.add(colors.get(neighbor));
                }
            }
            
            // assign smallest available color
            int color = 0;
            while (neighborColors.contains(color)) {
                color++;
            }
            
            colors.put(current, color);
            
            // step 2: go to neighbors and give different colors
            for (String neighbor : graph.getNeighbors(current)) {
                if (!colors.containsKey(neighbor)) {
                    // get colors used by neighbor's neighbors
                    Set<Integer> neighborOfNeighborColors = new HashSet<>();
                    for (String n : graph.getNeighbors(neighbor)) {
                        if (colors.containsKey(n)) {
                            neighborOfNeighborColors.add(colors.get(n));
                        }
                    }
                    
                    // assign smallest available color different from current
                    int neighborColor = 0;
                    while (neighborOfNeighborColors.contains(neighborColor) || neighborColor == color) {
                        neighborColor++;
                    }
                    
                    colors.put(neighbor, neighborColor);
                }
            }
            
            // step 3: Check for conflicts between neighbors
            resolveConflicts();
            
            // step 4: If not fully colored, repeat with uncolored vertices
            vertices.removeIf(v -> colors.containsKey(v));
        }
        
        // update color groups
        updateColorGroups();
        
        return colors;
    }
    
    /**
     * check and resolve color conflicts between adjacent vertices
     */
    private void resolveConflicts() {
        boolean conflictFound = true;
        
        while (conflictFound) {
            conflictFound = false;
            
            // check all edges for conflicts
            for (String u : graph.getVertices()) {
                if (!colors.containsKey(u)) continue;
                
                for (String v : graph.getNeighbors(u)) {
                    if (colors.containsKey(v) && colors.get(u).equals(colors.get(v))) {
                        // conflict found, resolve by changing v's color
                        conflictFound = true;
                        
                        // find a new color for v
                        Set<Integer> neighborColors = new HashSet<>();
                        for (String neighbor : graph.getNeighbors(v)) {
                            if (colors.containsKey(neighbor)) {
                                neighborColors.add(colors.get(neighbor));
                            }
                        }
                        
                        int newColor = 0;
                        while (neighborColors.contains(newColor)) {
                            newColor++;
                        }
                        
                        colors.put(v, newColor);
                    }
                }
            }
        }
    }
    
    /**
     * update the mapping of colors to vertices
     */
    private void updateColorGroups() {
        colorGroups.clear();
        
        for (Map.Entry<String, Integer> entry : colors.entrySet()) {
            String vertex = entry.getKey();
            Integer color = entry.getValue();
            
            if (!colorGroups.containsKey(color)) {
                colorGroups.put(color, new ArrayList<>());
            }
            
            colorGroups.get(color).add(vertex);
        }
    }
    
    /**
     * get the exam schedule based on the graph coloring
     * 
     * @return map of time slots to lists of classes
     */
    public Map<String, List<String>> getExamSchedule() {
        Map<String, List<String>> schedule = new HashMap<>();
        
        for (Map.Entry<Integer, List<String>> entry : colorGroups.entrySet()) {
            int color = entry.getKey();
            List<String> classes = entry.getValue();
            
            schedule.put("Time Slot " + (color + 1), classes);
        }
        
        return schedule;
    }
    
    /**
     * get the number of time slots needed
     * 
     * @return number of distinct colors used
     */
    public int getNumTimeSlots() {
        return colorGroups.size();
    }
    
    /**
     * check if the graph is properly colored
     * 
     * @return true if the coloring is valid, false otherwise
     */
    public boolean isValidColoring() {
        for (String u : graph.getVertices()) {
            if (!colors.containsKey(u)) {
                return false; 
            }
            
            int colorU = colors.get(u);
            
            for (String v : graph.getNeighbors(u)) {
                if (!colors.containsKey(v) || colors.get(v) == colorU) {
                    return false; 
                }
            }
        }
        
        return true;
    }
    
    /**
     * get the underlying graph
     * 
     * @return the conflict graph
     */
    public Graph getGraph() {
        return graph;
    }
    
    /**
     * get the colors assigned to vertices
     * 
     * @return map of vertices to colors
     */
    public Map<String, Integer> getColors() {
        return colors;
    }
    
    public static void main(String[] args) {
        try {
            // create a new scheduler
            ExamScheduler scheduler = new ExamScheduler();
            
            // create a sample graph
            scheduler.createGraph();
            
            // print the graph
            System.out.println("Conflict Graph:");
            System.out.println(scheduler.getGraph().toString());
            System.out.println();
            
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