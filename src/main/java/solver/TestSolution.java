package solver;

import model.Satellite;
import model.Customer;
import model.Solution;
import model.Assignment;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author LASTENNET Dorian
 */
public class TestSolution implements Heuristic  {

    @Override
    public Solution run(Solver solver) {
        List<Customer> customers = solver.getInstance().getCustomers();
        List<Satellite> satellites = solver.getInstance().getSatellites();
        List<ArrayList<Assignment>> permutations = new ArrayList<>();
        ArrayList<Assignment> permutation = new ArrayList<>();
        permutation.add(new Assignment(customers.get(0),satellites.get(0)));
        permutation.add(new Assignment(customers.get(0)));
        permutation.add(new Assignment(customers.get(1),satellites.get(1)));
        permutation.add(new Assignment(customers.get(2),satellites.get(1)));
        permutation.add(new Assignment(customers.get(1)));
        permutation.add(new Assignment(customers.get(2)));
        permutations.add(permutation);
        return new Solution(null,permutations);
    }
    
}
