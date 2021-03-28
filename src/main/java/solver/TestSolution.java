package solver;

import model.Satellite;
import model.Customer;
import model.Solution;
import model.Assignment;

import java.util.ArrayList;
import java.util.List;

/**
 * Cette classe est une classe de test qui renvoie une solution fixe. <br>
 * <b> A N'UTILISER QU'AVEC DES INSTANCES DE TEST </b>
 * 
 * @author LASTENNET Dorian
 */
public class TestSolution implements Heuristic  {

    @Override
    public Solution run(Solver solver) {
        List<Customer> customers = solver.getInstance().getCustomers();
        List<Satellite> satellites = solver.getInstance().getSatellites();
        List<List<Assignment>> permutations = new ArrayList<>();
        List<Assignment> permutation = new ArrayList<>();
        permutation.add(new Assignment(customers.get(0),satellites.get(0)));
        permutation.add(new Assignment(customers.get(0)));
        permutation.add(new Assignment(customers.get(1),satellites.get(1)));
        permutation.add(new Assignment(customers.get(2),satellites.get(1)));
        permutation.add(new Assignment(customers.get(1)));
        permutation.add(new Assignment(customers.get(2)));
        permutations.add(permutation);
        return new Solution(new ArrayList<>(),permutations);
    }
}
