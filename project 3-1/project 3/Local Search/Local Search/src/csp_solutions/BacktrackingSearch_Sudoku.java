package csp_solutions;
import core_algorithms.BacktrackingSearch;
import csp_problems.*;
import csp_problems.CSPProblem.Variable;

import java.util.ArrayList;

public class BacktrackingSearch_Sudoku extends BacktrackingSearch<String,Integer>{
    public BacktrackingSearch_Sudoku(Sudoku problem){
        super(problem);
    }
    /**
     * To revise an arc: for each value in tail's domain, there must be a value in
     head's domain that's different
     * if not, delete the value from the tail's domain
     * @param head head of the arc to be revised
     * @param tail tail of the arc to be revised
     * @return true if the tail has been revised (lost some values), false
    otherwise
     */
    public boolean revise(String head, String tail) {
        boolean revised = false;
        Variable<String, Integer> headVar = getAllVariables().get(head);
        Variable<String, Integer> tailVar = getAllVariables().get(tail);
        for (Integer tailValue : new ArrayList<>(tailVar.domain())) {
            boolean found = false;
            for (Integer headValue : headVar.domain()) {
                if (!tailValue.equals(headValue)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                tailVar.domain().remove(tailValue);
                revised = true;
            }
        }
        return revised;
    }
    /**
     * Implementing the minimum-remaining-values(MRV) ordering heuristic.
     * @return the variable with the smallest domain among all the unassigned
    variables;
     * null if all variables have been assigned
     */
    public String selectUnassigned() {
                    String selectedVariable = null;
                    int minRemainingValues = Integer.MAX_VALUE;
                    for (String variable : getAllVariables().keySet()) {
                        if (!assigned(variable)) {
                            int remainingValues = getAllVariables().get(variable).domain().size();
                            if (remainingValues < minRemainingValues) {
                                minRemainingValues = remainingValues;
                                selectedVariable = variable;
                            }
                        }
                    }
        return selectedVariable;
    }
    /**
     * @param args (no command-line argument is needed to run this program)
     */
    public static void main(String[] args) {
        String filename = "./SudokuTestCases/TestCase9.txt";
        Sudoku problem = new Sudoku(filename);
        BacktrackingSearch_Sudoku agent = new BacktrackingSearch_Sudoku(problem);
        System.out.println("loading puzzle from " + filename + "...");
        problem.printPuzzle(problem.getAllVariables());
        if(agent.initAC3() && agent.search()){
            System.out.println("Solution found:");
            problem.printPuzzle(agent.getAllVariables());
        }else{
            System.out.println("Unable to find a solution.");
        }
    }
}