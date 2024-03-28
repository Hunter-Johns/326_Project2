package core_algorithms;

import csp_problems.CSPProblem.Variable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * A generic solver for CSP of the Alldiff constraints.
 * This solver implements the following techniques:
 *   backtracking search +
 *   maintaining arc consistency (MAC) +
 *   minimum-remaining-values (MRV)
 *Note: MAC essentially means to apply AC-3 in every step of the backtracking search process.
 *
 * @param <X> the data type of the "names" of variables
 *  *  *     (e.g., for Sudoku, we could use Strings such as "A3", "B8", etc. to name squares of the 9x9 board.)
 * @param <V> the data type of values.
 *     (e.g., for Sudoku, a value is an integer between 1 and 9.)
 */
public abstract class BacktrackingSearch <X, V> {
    private List<Variable<X,V>> variableList;
    private List<Variable<X,V>> variableListClone = null;

    public  record Arc<X,V>(Variable<X,V> head, Variable<X,V> tail){}

    public BacktrackingSearch(List<Variable<X,V>> nodeList){
        this.variableList = nodeList;
    }
    public boolean AC3(Queue<Arc<X,V>> arcs){
        while(!arcs.isEmpty()) {
            Arc<X, V> a = arcs.remove();
            if (revise(a.head(), a.tail(), arcs)) {
                if (a.tail().domain().isEmpty()) {
                    return false;
                } else {
                    for (Variable<X,V> nei : getNeighborsOf(a.tail())) {
                        arcs.add(new Arc<>(a.tail(), nei));
                    }
                }
            }
        }
        return true;
    }

    public boolean search(){
        Variable<X,V> n = selectUnassigned();
        if(n == null){
            return true;
        }
        while(!n.domain().isEmpty()){
            //select a value to be assigned to this name
            V value = n.domain().remove(0);
            //make a deep clone of the nodeList in case
            // we will need to back track later
            this.deepClone();
            //assign the selected 'value' to n
            n.domain().clear();
            n.domain().add(value);
            Queue<Arc<X,V>> arcs = new LinkedList<>();
            //get all the arcs that may be affected by this assignment,
            // which are arcs where n is the head.
            for(Variable<X,V> nei : getNeighborsOf(n)){
                arcs.add(new Arc<>(n,nei));
            }
            //constraint propagation using the AC-3 algorithm
            if (AC3(arcs)){
                search();
            }else{
                //constraint propagation failed, i.e.,
                // we reached a dead end;
                // back track and consider other possible values
                // from n's domain
                this.revert();
            }
        }
        return false;
    }

    /**
     * Create a deep clone of the nodeList
     * (Deep clone means to clone every element of the list.)
     */
    public void deepClone(){
        variableListClone = new ArrayList<>();
        for(Variable<X,V> node : variableList){
            Variable<X,V> nodeClone =
                    new Variable<>(node.name(), node.domain());
            variableListClone.add(nodeClone);
        }
    }

    /**
     * Revert the nodeList to the deep clone copy
     */
    public void revert(){
        variableList = variableListClone;
    }

    /**
     * @param n
     * @return all the neighbors of 'name' in a queue
     */
    public abstract List<Variable<X,V>> getNeighborsOf(Variable<X,V> n);

    public abstract boolean revise(Variable<X,V> head, Variable<X,V> tail, Queue<Arc<X,V>> arcs);

    /**
     * Implements the minimum-remaining-values (MRV) heuristic
     * @return an unassigned node from the nodeList, or
     *         null if all nodes have been assigned.
     */
    public abstract Variable<X,V> selectUnassigned();

}
