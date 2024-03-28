package core_algorithms;
import csp_problems.CSPProblem;
import csp_problems.CSPProblem.Variable;
import java.util.*;
/**
 * A generic solver for CSPs of the Alldiff type of constraints.
 * This solver implements the following techniques:
 * backtracking search +
 * AC-3 +
 * maintaining arc consistency (MAC) +
 * minimum-remaining-values (MRV)
 *Note: MAC just means to apply AC-3 in every step of the backtracking search
 process.
 *
 * @param <X> the data type of the "names" of variables
 * * * (e.g., for Sudoku, we could use Strings such as "03", "75", etc.
 * to name the squares of the 9x9 board, where the first digit specifies
 * the row # and the second the column #.)
 * @param <V> the data type of values.
 * (e.g., in Sudoku, values should be integers between 1 and 9.)
 */
public abstract class BacktrackingSearch <X, V> {
    /**
     * The data type that represents an arc in the AC-3 algorithm
     * @param head
     * @param tail
     * @param <X>
     */
    public record Arc<X>(X head, X tail){
        @Override
        public boolean equals(Object o){
            if (this == o){
                return true;
            }
            if (o == null){
                return false;
            }
            if(getClass() != o.getClass()){
                return false;
            }
            Arc<?> arc = (Arc<?>) o;
            return Objects.equals(head, arc.head) &&
                    Objects.equals(tail, arc.tail);
        }
    }
    private Map<X,Variable<X,V>> allVariables;
    //keeps track of the variables that have been assigned so far
    private final Set<X> assigned;
    private final CSPProblem<X,V> problem;
    public BacktrackingSearch(CSPProblem<X,V> problem){
        this.problem = problem;
        this.allVariables = problem.getAllVariables();
//populate the assigned set with the names of any pre-assigned variables
        this.assigned = new HashSet<>(problem.getAssigned());
    }
    /**
     * An implementation of the AC-3 algorithm; see textbook, Figure 6.3 on page
     186
     * Note that the revise() is a separate method that you will need to
     * implement in BacktrackingSearch_Sudoku.java
     * @param arcs the list of arcs for which consistency will be maintained
     * @return false if consistency could not be maintained, true otherwise
     */
    public boolean AC3(Queue<Arc<X>> arcs){
        while (!arcs.isEmpty()) {
            Arc<X> arc = arcs.poll();
            if (revise(arc.head(), arc.tail())) {
// If domain revised, add arcs from this node to the queue
                for (X neighbor : problem.getNeighborsOf(arc.head())) {
                    arcs.add(new Arc<>(neighbor, arc.head()));
                }
            }
// If any domain becomes empty, return false
            if (allVariables.get(arc.head()).domain().isEmpty()) {
                return false;
            }
        }
        return true; // If all arcs are consistent
    }
    /**
     * Performs the AC-3 algorithm at the very beginning if some variables have
     been
     * pre-assigned values as part of the problem.
     * @return
     */
    public boolean initAC3(){
                Queue<Arc<X>> arcs = new LinkedList<>();
        for(X v : allVariables.keySet()){
            for(X n : problem.getNeighborsOf(v)){
                arcs.add(new Arc<>(v,n));
            }
        }
        return AC3(arcs);
    }
    /**
     * An implementation of the backtracking search with maintaining arc
     consistency (MAC)
     * @return
     */
    public boolean search(){
        X n = selectUnassigned();
        if(n == null){
            return true;
        }
        assigned.add(n);
// System.out.println(n+", "+assigned.size());
        while(!allVariables.get(n).domain().isEmpty()) {
//select a value to be assigned to this variable
            V value = allVariables.get(n).domain().remove(0);
//make a deep clone of the nodeList in case
// we will need to back track later
            Map<X,Variable<X,V>> allVariablesClone = deepClone();
//assign the selected 'value' to the variable n
            allVariables.get(n).domain().clear();
            allVariables.get(n).domain().add(value);
            Queue<Arc<X>> arcs = new LinkedList<>();
//get all the arcs that could potentially be affected by this assignment
// i.e., all the arcs where n is the head.
            for (X nei : problem.getNeighborsOf(n)) {
                arcs.add(new Arc<>(n, nei));
            }
//constraint propagation using the AC-3 algorithm
            if (AC3(arcs) && search()) {
                return true;
            } else {
//System.out.println(n+" reverting");
                revert(allVariablesClone);
            }
        }
// System.out.println(n+" failed");
        assigned.remove(n);
        return false;
    }
    /**
     * Create a deep clone of the allVariables map in case we will need to back
     track in future
     * (Deep clone means to clone every element of the list.)
     */
    public Map<X,Variable<X,V>> deepClone(){
        Map<X,Variable<X,V>> allVariablesClone = new HashMap<>();
        for(Variable<X,V> var : allVariables.values()){
//deep clone the variable domain
            Variable<X,V> varClone =
                    new Variable<>(var.name(), new LinkedList<>(var.domain()));
            allVariablesClone.put(var.name(),varClone);
        }
        return allVariablesClone;
    }
    /**
     * Revert the allVariables map to the deep clone copy
     */
    public void revert(Map<X,Variable<X,V>> allVariablesClone){
        allVariables = allVariablesClone;
    }
    public Map<X,Variable<X, V>> getAllVariables() {
        return allVariables;
    }
    /**
     * Check if the variable of the given name has been assigned a value already.
     * @param name name of the variable whose assignment will be checked
     * @return true if assigned, false otherwise
     */
    public boolean assigned(X name){
        return assigned.contains(name);
    }
//the two abstract methods below should be implemented in BacktrackingSearch_Sudoku.java
    public abstract boolean revise(X head, X tail);
    public abstract X selectUnassigned();
}