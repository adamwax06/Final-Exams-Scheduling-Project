import java.io.*;
import java.util.*;


public class ExamScheduler{
    // our conflict graph
    private Graph graph;

    // mapping of classes to their assigned time slots
    private Map<String, Integer> colors;

    // mapping of time slots to their classes
    private Map<Integer, List<String>> colorGroups;

    /**
     * constructs a new exam scheduler object
     */
    public ExamScheduler(){
        graph = new Graph();
        colors = new HashMap<>();
        colorGroups = new HashMap<>();
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
     * create a sample conflict graph for testing
     */
    public void createSampleGraph() {
        // add some sample classes
        String[] classes = {"Math101", "CS101", "Physics101", "English101", "History101"};
        for (String className : classes) {
            addClass(className);
        }
        
        // create some students and add appropriate edges
        addConflict("Math101", "CS101");
        addConflict("Math101", "Physics101");
        addConflict("Math101", "English101");
        addConflict("CS101", "History101");
        addConflict("Physics101", "History101");
        addConflict("English101", "History101");
    }

    /**
     * color the graph following the algorithm below:
     * 1. put all vertices into an “uncolored” set and clear any previous colors. 
     *      Repeat until no uncolored vertices remain
     * 2. select the uncolored vertex with highest saturation
     * 3. assign it the smallest non‐negative color not used by its neighbors
     * 4. remove it from the uncolored set.
     * 5. update the colorGroups mapping for time‐slots
     * 
     * @return map of vertices to their assigned colors
     */
    public Map<String,Integer> colorGraph() {
        colors.clear();
        colorGroups.clear();
    
        // all vertices start uncolored
        Set<String> uncolored = new HashSet<>(graph.getVertices());
    
        while (!uncolored.isEmpty()) {
            String pick = null;
            int bestSat = -1, bestDeg = -1;
    
            // find the uncolored vertex with max saturation, tie‑break on degree
            for (String v : uncolored) {
                // compute saturation = # of distinct neighbor colors
                Set<Integer> neighCols = new HashSet<>();
                for (String n : graph.getNeighbors(v)) {
                    if (colors.containsKey(n)) 
                        neighCols.add(colors.get(n));
                }
                int sat = neighCols.size();
                int deg = graph.getDegree(v);
    
                if (sat > bestSat || (sat == bestSat && deg > bestDeg)) {
                    bestSat = sat;
                    bestDeg = deg;
                    pick = v;
                }
            }
    
            // now color 'pick' with the smallest available color
            Set<Integer> used = new HashSet<>();
            for (String n : graph.getNeighbors(pick)) {
                if (colors.containsKey(n)) used.add(colors.get(n));
            }
            int c = 0;
            while (used.contains(c)) c++;
            colors.put(pick, c);
            uncolored.remove(pick);
        }
    
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

    /**
     * get the colors assigned to vertices
     * 
     * @return map of vertices to colors
     */
    public void readFromFile(String filename)throws FileNotFoundException{
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split("\\|");
            if (parts.length < 2) continue;
            String[] classes = parts[1].split(",");

            // make sure each course is in the graph
            for (String c : classes) {
                String course = c.trim();
                if (!course.isEmpty()) {
                    addClass(course);
                }
            }

            // add conflicts between every pair of courses
            for (int i = 0; i < classes.length; i++) {
                for (int j = i + 1; j < classes.length; j++) {
                    String c1 = classes[i].trim();
                    String c2 = classes[j].trim();
                    if (!c1.isEmpty() && !c2.isEmpty()) {
                        addConflict(c1, c2);
                    }
                }
            }
        }
        scanner.close();
    }

    //public int getMin()
   
}
