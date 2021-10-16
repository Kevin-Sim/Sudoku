import java.util.ArrayList;
import java.util.Random;

public class EA {

	static Random rnd = Parameters.rnd;

	public static void main(String[] args) {

		ArrayList<Individual> population = new ArrayList<>();
		for (int i = 0; i < Parameters.populationSize; i++) {
			Individual ind = new Individual(true);
			population.add(ind);
		}
		Individual best = getBest(population);
		int gen = 0;

		while (gen < 100000000 && best.fitness > 0) {
			gen++;
			Individual parent1 = select(population);
			Individual parent2 = select(population);
			Individual child = crossover(parent1, parent2);
			child = mutate(child);
			evaluate(child);
			replace(child, population);
			best = getBest(population);
			System.out.println("" + gen + "\t" + best.fitness);
		}

		// fix but doesn't guarantee solvable
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (SudokuChecker.getConflicts(row, col, best.chromosome) > 0) {
					best.chromosome[row][col] = 0;
				}
			}
		}
		best.fitness = SudokuChecker.getFitness(best.chromosome);
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				System.out.print(best.chromosome[row][col] + "\t");
			}
			System.out.println();
		}
		System.out.println(best.fitness);
		SudokuChecker.board = best.chromosome;
		Gui.main(null);
	}

	private static void replace(Individual child, ArrayList<Individual> population) {
		int worstIdx = getWorstIdx(population);
		if(child.fitness < population.get(worstIdx).fitness) {
			population.set(worstIdx, child);
		}
	}

	private static int getWorstIdx(ArrayList<Individual> population) {
		int worstFitness = population.get(0).fitness;
		int worstIdx = 0;
		for(int i = 1; i < population.size(); i++) {
			if(population.get(i).fitness > worstFitness) {
				worstFitness = population.get(i).fitness;
				worstIdx = i;
			}
		}
		return worstIdx;
	}

	private static void evaluate(Individual child) {
		child.fitness = SudokuChecker.getFitness(child.chromosome);
	}

	private static Individual mutate(Individual child) {
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if(rnd.nextDouble() > Parameters.mutationProbability) {
					child.chromosome[row][col] = 1 + rnd.nextInt(9);
				}
			}
		}
		return child;
	}

	private static Individual crossover(Individual parent1, Individual parent2) {
		Individual child = new Individual(false);
		int r = rnd.nextInt(9);
		int c = rnd.nextInt(9);
		Individual currentParent = parent1;
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				if (row == r && col == c) {
					currentParent = parent2;
				}
				child.chromosome[row][col] = currentParent.chromosome[row][col];
			}
		}
		return child;
	}

	private static Individual select(ArrayList<Individual> population) {
		Individual best = population.get(rnd.nextInt(population.size()));
		for (int i = 1; i < Parameters.tournamentSize; i++) {
			Individual candidate = population.get(rnd.nextInt(population.size()));
			if (candidate.fitness < best.fitness) {
				best = candidate;
			}
		}
		return best.copy();
	}

	private static Individual getBest(ArrayList<Individual> population) {
		Individual best = population.get(0);
		for (int i = 1; i < population.size(); i++) {
			if (population.get(i).fitness < best.fitness) {
				best = population.get(i);
			}
		}
		return best.copy();
	}

}
