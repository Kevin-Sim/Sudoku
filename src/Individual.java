import java.util.Random;

public class Individual {

	int[][] chromosome;
	int fitness;
	Random rnd = Parameters.rnd;
	public Individual(boolean initialise) {
		chromosome = new int[9][9];
		if(!initialise) {
			return; 
		}
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				chromosome[row][col] = 1 + rnd.nextInt(9);
			}
		}
		fitness = SudokuChecker.getFitness(chromosome);
		
	}
	public Individual copy() {
		Individual ind = new Individual(false);		
		for (int row = 0; row < 9; row++) {
			for (int col = 0; col < 9; col++) {
				ind.chromosome[row][col] = chromosome[row][col];
			}
		}
		ind.fitness = fitness;
		return ind;
	}
}
