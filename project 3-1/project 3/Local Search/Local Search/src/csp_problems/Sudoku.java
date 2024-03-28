package csp_problems;
import java.io.*;
import java.util.*;
public class Sudoku implements CSPProblem<String,Integer> {
    private final Map<String, Variable<String,Integer>> allVariables;
    //mapping for a variable's name to the set of neighbors of the variable
    private final Map<String,Set<String>> neighbors = new HashMap<>();
    //name of the file that contains the test case.
    private final String filename;
    public Sudoku(String filename) {
        this.filename = filename;
        allVariables = getAllVariables();
//for each row, get the neighbors of each variable in that row
        for (int i=0; i<9; i++) {
//build the row neighbor set (all variables in the same row)
            Set<String> rowNeighbors = new HashSet<>();
            for (int j=0; j<9; j++) {
                String name = i + String.valueOf(j);
                rowNeighbors.add(name);
            }
            for(int j=0; j<9; j++){
                String name = i + String.valueOf(j);
                neighbors.put(name, new HashSet<>(rowNeighbors));
            }
        }
//for each column, get the neighbors of each variable in that column
        for (int j=0; j<9; j++) {
//build the column neighbor set (all variables in the same column)
            Set<String> columnNeighbors = new HashSet<>();
            for (int i=0; i<9; i++) {
                String name = i + String.valueOf(j);
                columnNeighbors.add(name);
            }
            for (int i=0; i<9; i++) {
                String name = i + String.valueOf(j);
                neighbors.get(name).addAll(columnNeighbors);
            }
        }
        for(int i=0; i<3; i++) {
            for (int j=0; j<3; j++) {
//build the box neighbor set (all variables in the same 3x3 box)
                Set<String> boxNeighbors = new HashSet<>();
                for (int x=0; x<3; x++) {
                    for (int y=0; y<3; y++) {
                        String name = String.valueOf(i * 3 + x) + (j * 3 + y);
                        boxNeighbors.add(name);
                    }
                }
                for (int x=0; x<3; x++) {
                    for (int y=0; y<3; y++) {
                        String name = String.valueOf(i * 3 + x) + (j * 3 + y);
                        neighbors.get(name).addAll(boxNeighbors);
                    }
                }
            }
        }
//remove a node from its neighbor set.
        for(Map.Entry<String,Set<String>> e : neighbors.entrySet()){
            e.getValue().remove(e.getKey());
// System.out.println(e.getValue().size());
        }
    }
    public Map<String,Variable<String,Integer>> getAllVariables() {
        Map<String,Variable<String,Integer>> allVariables = new HashMap<>();
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            List<Integer> defaultDomain = List.of(1,2,3,4,5,6,7,8,9);
//i: row number; j: column number
            for (int i=0; i<9; i++) {
                if ((line = in.readLine()) != null) {
                    String[] numbers = line.trim().split(" ");
                    for (int j=0; j<9; j++) {
                        String name = i +String.valueOf(j);
                        int number = Integer.parseInt(numbers[j]);
                        Variable<String, Integer> v;
                        if (number>0 && number<10) {
                            v = new Variable<>(name, new
                                    LinkedList<>(List.of(number)));
                        } else {
                            v = new Variable<>(name, new
                                    LinkedList<>(defaultDomain));
                        }
                        allVariables.put(name,v);
                    }
                } else {
                    for (int j=0; j<9; j++) {
                        String name = i +String.valueOf(j);
                        Variable<String,Integer> v =
                                new Variable<>(name, new
                                        LinkedList<>(defaultDomain));
                        allVariables.put(name,v);
                    }
                }
            }
            return allVariables;
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return null;
    }
    public void printPuzzle(Map<String,Variable<String,Integer>> allVariables) {
        for (int i=0; i<9; i++) {
            for (int j=0; j<9; j++) {
                if (allVariables.get(i+String.valueOf(j)).domain().size() > 1) {
                    System.out.print("[ ]");
                } else {
                    System.out.print("["+allVariables.get(i+String.valueOf(j)).domain().get(0)+"]");
                }
            }
            System.out.print("\r\n");
        }
    }
    /**
     * Given the name of a variable, return the names of its neighbors
     * @param name the name of a variable
     * @return List of names of the neighbors
     */
    public List<String> getNeighborsOf(String name){
        return(new ArrayList<>(neighbors.get(name)));
    }
    /**
     * return a list of names of the variables
     * whose values have been pre-assigned as part of the problem
     * @return the list of names of the pre-assigned variables
     */
    public List<String> getAssigned() {
        List<String> assigned = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (allVariables.get(i + String.valueOf(j)).domain().size() == 1) {
                    assigned.add(i + String.valueOf(j));
                }
            }
        }
        return assigned;
    }
}
