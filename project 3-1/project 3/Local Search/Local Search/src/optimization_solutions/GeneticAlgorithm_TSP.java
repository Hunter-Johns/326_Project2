package optimization_solutions;

import core_algorithms.GeneticAlgorithm;
import core_algorithms.Individual;
import optimization_problems.TSP;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//Implement elements of the algorithm that are problem specific
public class GeneticAlgorithm_TSP extends GeneticAlgorithm<Integer> {
    private final TSP problem;

    public GeneticAlgorithm_TSP(int maxGen, double mutationRate, double elitism, TSP problem){
        super(maxGen, mutationRate, elitism);
        this.problem = problem;
    }

    public double calculateFitnessScore(List<Integer> chromosome) {
        return 1/ problem.cost(chromosome);
    }

    public Individual<Integer> reproduce(Individual<Integer> p1, Individual<Integer> p2) {
        int size = p1.getChromosome().size();
        int start = new Random().nextInt(size);
        int end = new Random().nextInt(size - start) + start;

        List<Integer> childChromosome = new ArrayList<>(Collections.nCopies(size, -1));
        for (int i = start; i <= end; i++) {
            childChromosome.set(i, p1.getChromosome().get(i));
        }

        List<Integer> usedChromosomes = new ArrayList<>();
        for (int i = 0; i < childChromosome.size(); i++){
            if(childChromosome.get(i) != -1) {
                usedChromosomes.add(childChromosome.get(i));
            }
        }


        for (int i = 0; i < size; i++){
            if(childChromosome.get(i) == -1){
                for (int j = 0; j<p2.getChromosome().size(); j++){
                    if(!usedChromosomes.contains(p2.getChromosome().get(j))){
                        childChromosome.set(i, p2.getChromosome().get(j));
                        usedChromosomes.add(p2.getChromosome().get(j));
                        break;
                    }
                }
            }
        }

        // Check if any value is still -1
        if (childChromosome.contains(-1)) {
            System.out.println("Not all genes were assigned during reproduction.");
        }

        return new Individual<>(childChromosome, calculateFitnessScore(childChromosome));
    }



    public Individual<Integer> mutate(Individual<Integer> i) {
        // Swap mutation, where two randomly selected genes in the chromosome are swapped.
        List<Integer> chromosome = new ArrayList<>(i.getChromosome());
        int index1 = new Random().nextInt(chromosome.size());
        int index2;
        do {
            index2 = new Random().nextInt(chromosome.size());
        } while (index2 == index1);

        Collections.swap(chromosome, index1, index2);
        return new Individual<>(chromosome, calculateFitnessScore(chromosome));
    }

    public List<Individual<Integer>> generateInitialPopulation(int populationSize, int numCities){
        List<Individual<Integer>> population = new ArrayList<>(populationSize);
        for (int i = 0; i<populationSize; i++){
            List<Integer> chromosome = new ArrayList<>(numCities);
            for(int j = 0; j<numCities; j++){
                chromosome.add(j);
            }
            Collections.shuffle(chromosome);
            Individual<Integer> individual = new Individual<>(chromosome, calculateFitnessScore(chromosome));
            population.add(individual);
        }
        return population;
    }


    public static void main(String[] args) {
        int MAX_GEN = 200;
        double MUTATION_RATE = 0.05;
        int POPULATION_SIZE = 1000;
        int NUM_CITIES = 26; //5, 6, 17, 26 are accepted
        double ELITISM = 0.2;

        TSP problem = new TSP(NUM_CITIES);
        GeneticAlgorithm_TSP agent = new GeneticAlgorithm_TSP(MAX_GEN, MUTATION_RATE, ELITISM, problem);

        Individual<Integer> best = agent.evolve(agent.generateInitialPopulation(POPULATION_SIZE, NUM_CITIES));

        System.out.println(best);
        System.out.println(problem.cost(best.getChromosome()));
    }
}
