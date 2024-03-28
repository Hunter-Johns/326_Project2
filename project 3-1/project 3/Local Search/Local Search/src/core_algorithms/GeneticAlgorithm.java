package core_algorithms;
//implement elements that are independent of any specific problem

import java.util.*;

public abstract class GeneticAlgorithm<G> {
    private final int MAX_GEN;
    private final double MUTATION_RATE;
    private final double ELITISM;

    public GeneticAlgorithm(int maxGenerations, double mutationRate, double elitism){
        this.MAX_GEN = maxGenerations;
        this.MUTATION_RATE = mutationRate;
        this.ELITISM = elitism;
    }

    public Individual<G> evolve(List<Individual<G>> initialPopulation){
        List<Individual<G>> population= initialPopulation;
        for(int generation = 1; generation <= MAX_GEN; generation++) {
            List<Individual<G>> offspring = new ArrayList<>();
            for (int i = 0; i < population.size(); i++) {
                Individual<G> parent1 = selectAParent(population);
                Individual<G> parent2 = selectAParent(population, parent1);
                Individual<G> child = reproduce(parent1, parent2);
                if (new Random().nextDouble() <= MUTATION_RATE) {
                    child = mutate(child);
                }
                offspring.add(child);
            }
            Collections.sort(population);
            Collections.sort(offspring);
            List<Individual<G>> nextGeneration = new ArrayList<>();
            int e = (int) (ELITISM * population.size());
            for (int i = 0; i < e; i++) {
                nextGeneration.add(population.get(i));
            }
            for (int i = 0; i < population.size() - e; i++) {
                nextGeneration.add(offspring.get(i));
            }//end of outer for loop
            population = nextGeneration;
        }

        Collections.sort(population);
        return population.getFirst();
    }

    public abstract Individual<G> reproduce(Individual<G> parent1, Individual<G> parent2);
    public abstract Individual<G> mutate(Individual<G> individual);
    public abstract double calculateFitnessScore(List<G> chromosome);

    public Individual<G> selectAParent(List<Individual<G>> population){
        //need to implement cumulative score
        Individual<G> parent = population.getLast();
        double cumulativeSum = 0.0;
        Random random = new Random();

        for (Individual<G> gIndividual : population) {
            cumulativeSum += gIndividual.getFitnessScore();
        }

        double randomDouble = random.nextDouble(cumulativeSum);

        cumulativeSum = 0.0;
        for (Individual<G> individual : population) {
            cumulativeSum += individual.getFitnessScore();
            if (randomDouble <= cumulativeSum) {
                parent = individual;
                break;
                }
            }

        return parent;
    }

    public Individual<G> selectAParentIdea2(List<Individual<G>> population, Integer k){
        Individual<G> parent;
        Random random = new Random();
        List<Individual<G>> selectedParentList = null;
        List<Integer> randomNumberList = null;
        Integer randomlyGeneratedNumber;

        for (int i = 0; i<k; i++){
            randomlyGeneratedNumber = random.nextInt(population.size());
            randomNumberList.add(randomlyGeneratedNumber);
            if (!randomNumberList.contains(randomlyGeneratedNumber)) {
                selectedParentList.add(population.get(randomlyGeneratedNumber));
            }
            else{i--;}
        }

        parent = selectedParentList.getFirst();

        for (int i = 0; i<k; i++){
            if (selectedParentList.get(i).getFitnessScore() > parent.getFitnessScore()){
                parent = selectedParentList.get(i);
            }
        }
        return parent;
    }


    //optional (avoid the parents being the same individual)
    public Individual<G> selectAParent(List<Individual<G>> population, Individual<G> individual){
        Individual<G> selectedParent = selectAParent(population);
        while (selectedParent == individual){
            selectedParent = selectAParent(population);
        }
        return selectedParent;
    }

    public Individual<G> selectAParentIdea2(List<Individual<G>> population, Individual<G> individual, Integer k){
        Individual<G> selectedParent = selectAParentIdea2(population, k);
        while (selectedParent == individual){
            selectedParent = selectAParentIdea2(population, k);
        }
        return selectedParent;
    }
}
