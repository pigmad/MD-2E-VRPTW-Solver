package solver;

import model.AssignmentSecond;
import model.Depot;
import model.Satellite;
import model.Customer;
import model.Solution;

import java.util.ArrayList;
import java.util.List;
import model.AssignmentFirst;


/**
 * Classe de test qui renvoie une solution fixe. <br>
 * <b> A N'UTILISER QU'AVEC LES INSTANCES DE TEST DANS /test/instances </b>
 * 
 * @author LASTENNET Dorian
 */
public class TestSolution implements Heuristic  {
    private Solver solver;
    
    public TestSolution(Solver solver){
        this.solver = solver;
    }

    @Override
    public Solution solve() {
        //récupération des données
        List<Depot> depots = solver.getInstance().getDepots();
        List<Satellite> satellites = solver.getInstance().getSatellites();
        List<Customer> customers = solver.getInstance().getCustomers();
        
        //création de la permutation pour le second niveau
        List<List<AssignmentSecond>> permutationsSecond = new ArrayList<>();
        List<AssignmentSecond> permutationSecond = new ArrayList<>();
        permutationSecond.add(new AssignmentSecond(customers.get(0),satellites.get(0)));
        permutationSecond.add(new AssignmentSecond(customers.get(0)));
        permutationSecond.add(new AssignmentSecond(customers.get(1),satellites.get(1)));
        permutationSecond.add(new AssignmentSecond(customers.get(2),satellites.get(1)));
        permutationSecond.add(new AssignmentSecond(customers.get(1)));
        permutationSecond.add(new AssignmentSecond(customers.get(2)));
        permutationsSecond.add(permutationSecond);
        
        //création de la permutation pour le second niveau
        List<List<AssignmentFirst>> permutationsFirst = new ArrayList<>();
        List<AssignmentFirst> permutationFirst = new ArrayList<>();
        permutationFirst.add(new AssignmentFirst(satellites.get(0),depots.get(0)));
        permutationFirst.add(new AssignmentFirst(satellites.get(1),depots.get(0)));
        permutationFirst.add(new AssignmentFirst(satellites.get(0)));
        permutationFirst.add(new AssignmentFirst(satellites.get(1)));
        permutationsFirst.add(permutationFirst);
        
        //création de la solution et calcul de la charge des satellites
        Solution solution = new Solution(permutationsFirst,permutationsSecond);
        solution.setSolutionSatellitesDemand(solver.getInstance());
        
        return solution;
    }
}
