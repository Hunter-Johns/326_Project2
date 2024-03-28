package core_algorithms;

import java.util.Random;

/**
 * An implementation of the simulated annealing algorithm
 * Assume that the optimal solution has the *lowest* cost.
 * <p>
 * S: the data type of states;
 */
public abstract class SimulatedAnnealing<S> {
    private long time;
    private double temp;

    private final Problem<S> problem;

    public SimulatedAnnealing(long initTime, double initTemp, Problem<S> p){
        this.time = initTime;
        this.temp = initTemp;
        this.problem = p;
    }

    //generate the new (lower) temperature
    public abstract double schedule(long time, double temp);

    public void search(){
        S state = problem.getInitState();
        while (temp > 0){
            S newState = problem.generateNewState(state);
            double deltaCost = problem.cost(newState) - problem.cost(state);
            if (accept(deltaCost, temp)){
                state = newState;
            }
            time ++;
            temp = schedule(time, temp);
        }
        System.out.println(state);
        System.out.println("Total distance: " + problem.cost(state));
    }

    public boolean accept(double delta, double temp){
        if (delta < 0){
            return true;
        }
        else{
            double probability = Math.exp((0-delta)/temp);
            Random r = new Random();
            return probability > r.nextDouble();
        }
    }

}
