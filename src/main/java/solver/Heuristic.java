package solver;

import model.Solution;

/**
 * 
 * @author LASTENNET Dorian
 */
public interface Heuristic {
    
    public abstract Solution run(Solver solver);
}
