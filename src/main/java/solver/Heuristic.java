package solver;

import model.Solution;

/**
 * Interface pour l'implémentation des heuristiques
 * @author LASTENNET Dorian
 */
public interface Heuristic {
    
    public abstract Solution run(Solver solver);
}
