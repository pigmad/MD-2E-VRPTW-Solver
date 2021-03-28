package solver;

import model.Solution;

/**
 * Classe abstraite pour l'implémentation des heuristiques.
 *
 * @author LASTENNET Dorian
 */
public interface Heuristic {
    public abstract Solution solve();
}
