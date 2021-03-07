package solver;

import model.Assignment;
import model.Customer;
import model.Satellite;
import model.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Implémentation de l'algorithme de Clarke & Wright adapté au problème
 *
 * @author LASTENNET Dorian
 */
public class ClarkeWright implements Heuristic {

    @Override
    public Solution run(Solver solver) {
        List<Satellite> satellites = solver.getInstance().getSatellites();
        List<Customer> customers = solver.getInstance().getCustomers();

        //calcul de la liste des savings
        ArrayList<Saving> savingsList = computeSavings(satellites, customers);
        System.out.println("Taille liste savings : " + savingsList.size() + "\n");

        //création de la solution initiale où tous les clients sont reliés chacun des satellites par un véhicule
        List<ArrayList<Assignment>> permutations = new ArrayList<>();
        for (Satellite s : satellites) {
            for (Customer c : customers) {
                ArrayList<Assignment> permutation = new ArrayList<>();
                permutation.add(new Assignment(c, s));
                permutation.add(new Assignment(c));
                permutations.add(permutation);
            }
        }
        System.out.println("Solution initiale : " + permutations + "\n");

        for (Saving saving : savingsList) {
            //Traitement du savings 
            System.out.println(saving.toString());
            savingsTreatment(saving, permutations, solver);
            System.out.println("");
        }
        System.out.println("Suppression des routes restantes qui ne respectent pas les contraintes");
        repairAloneCustomers(permutations, solver);

        return new Solution(null, permutations);
    }

    /**
     * Fonction qui calcule la liste des savings pour l'instance du problème
     *
     * @param satellites liste des satellites
     * @param customers liste des clients
     * @return liste des savings triée dans l'ordre décroisant
     */
    public ArrayList<Saving> computeSavings(List<Satellite> satellites, List<Customer> customers) {
        ArrayList<Saving> savings = new ArrayList<>();
        //on crée les savings pour chaques paires de clients i,j et pour chacun des satellites
        for (Satellite sI : satellites) {
            for (Satellite sJ : satellites) {
                for (Customer i : customers) {
                    for (Customer j : customers) {
                        if (!i.equals(j)) {
                            Saving saving = new Saving(sI, sJ, i, j);
                            savings.add(saving);
                        }
                    }
                }
            }
        }
        //on trie les savings dans l'ordre décroissant
        Collections.sort(savings, Collections.reverseOrder());
        return savings;
    }

    /**
     * Traite un saving en essayant de fusionner les routes concernées selon la
     * méthode classique de Clark & Wright et utilisant un rechargement vers le
     * satellite
     *
     * @param saving le saving considéré
     * @param permutations la liste des permutations
     * @param solver le solveur contenant l'instance du problème
     */
    public void savingsTreatment(Saving saving, List<ArrayList<Assignment>> permutations, Solver solver) {
        ArrayList<Assignment> iRoute = findRouteLast(saving.sI, saving.i, permutations);
        ArrayList<Assignment> jRoute = findRouteFirst(saving.sJ, saving.j, permutations);
        boolean isRouteMerged = false;
        if (!iRoute.equals(jRoute) && !iRoute.isEmpty() && !jRoute.isEmpty()) {
            System.out.println("Fusion considérée : " + iRoute + " + " + jRoute);

            System.out.println("Fusion classique C&W : ");
            isRouteMerged = mergeRouteClassic(iRoute, jRoute, permutations, solver);

            if (!isRouteMerged) {
                System.out.println("\nFusion avec rechargement satellite : ");
                isRouteMerged = mergeRouteWithRefill(iRoute, jRoute, permutations, solver);
            }
        }
    }

    /**
     * Fonction appelée en fin d'algorithme chargée de réaffecter les clients
     * qui n'ont pas été integrée dans des routes complètes Ces clients ont
     * gardé les affectations de la solution initiale et ne sont donc affectés à
     * aucun satellite
     *
     * @param permutations liste des permutations
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairAloneCustomers(List<ArrayList<Assignment>> permutations, Solver solver) {
        System.out.println(permutations);
        ArrayList<Customer> aloneCustomers = new ArrayList<>();
        //Extraction des clients seuls
        for (ArrayList<Assignment> route : permutations) {
            if (route.size() == 2) {
                Customer c = route.get(0).getCustomer();
                if (!aloneCustomers.contains(c)) {
                    aloneCustomers.add(c);
                }
            }
        }
        //On retire les routes où les clients sont seuls
        permutations.removeIf(route -> route.size() == 2);
        //On cherche le satellite le plus proche des clients seuls et on réinsère les clients dans la solution
        for (Customer c : aloneCustomers) {
            Satellite closestSatellite = solver.getInstance().getSatellites().get(0);
            for (Satellite s : solver.getInstance().getSatellites()) {
                if (s.computeDistance(c) < closestSatellite.computeDistance(c)) {
                    closestSatellite = s;
                }
            }
            ArrayList<Assignment> permutation = new ArrayList<>();
            permutation.add(new Assignment(c, closestSatellite));
            permutation.add(new Assignment(c));
            permutations.add(permutation);
        }
    }

    /**
     * Fusion classique de l'algorithme de Clarke & Wright avec respect des
     * contraintes capacités et fenêtre de temps
     *
     * @param iRoute la route i a fusionner
     * @param jRoute la route j à fusionner
     * @param permutations la liste des permutations
     * @param solver le solveur contenant l'instance du problème
     * @return booléen indiquant si les routes ont été fusionnée
     */
    public boolean mergeRouteClassic(ArrayList<Assignment> iRoute, ArrayList<Assignment> jRoute, List<ArrayList<Assignment>> permutations, Solver solver) {
        boolean isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(iRoute);
        boolean isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(iRoute);
        System.out.println("Contraintes temporelles respectées route i : " + isTimeDoable);
        System.out.println("Contraintes capacités respectées route i : " + isCapacityDoable);

        isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(jRoute);
        isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(jRoute);
        System.out.println("Contraintes temporelles respectées route j : " + isTimeDoable);
        System.out.println("Contraintes capacités respectées route j : " + isCapacityDoable);

        ArrayList<Assignment> mergedRoute = new ArrayList<>();
        for (Assignment a : iRoute) {
            if (a.getSatellite() != null) {
                mergedRoute.add(a);
            }
        }
        mergedRoute.addAll(jRoute);
        for (Assignment a : iRoute) {
            if (a.getSatellite() == null) {
                mergedRoute.add(a);
            }
        }

        System.out.println("Route fusionnée : " + mergedRoute);
        isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(mergedRoute);
        isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(mergedRoute);
        System.out.println("Contraintes temporelles respectées : " + isTimeDoable);
        System.out.println("Contraintes capacités respectées : " + isCapacityDoable);
        if (isTimeDoable && isCapacityDoable) {
            //suppresion route i
            permutations.remove(iRoute);
            //suppresion route j
            permutations.remove(jRoute);
            //suppresion de toutes les routes qui contiennent i et j et un satellite différent de celui affectée
            permutations.removeIf(route -> !route.get(0).getSatellite().equals(iRoute.get(0)) && route.get(0).getCustomer().equals(iRoute.get(0).getCustomer()));
            permutations.removeIf(route -> !route.get(0).getSatellite().equals(jRoute.get(0)) && route.get(0).getCustomer().equals(jRoute.get(0).getCustomer()));
            //ajout de la route fusionnée
            permutations.add(mergedRoute);

            System.out.println("Nouvelle solution : " + permutations);
        }
        return isTimeDoable && isCapacityDoable;
    }

    /**
     *
     * @param iRoute
     * @param jRoute
     * @param permutations
     * @param solver
     */
    public boolean mergeRouteWithRefill(ArrayList<Assignment> iRoute, ArrayList<Assignment> jRoute, List<ArrayList<Assignment>> permutations, Solver solver) {
        boolean isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(iRoute);
        boolean isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(iRoute);
        System.out.println("Contraintes temporelles respectées route i : " + isTimeDoable);
        System.out.println("Contraintes capacités respectées route i : " + isCapacityDoable);

        isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(jRoute);
        isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(jRoute);
        System.out.println("Contraintes temporelles respectées route j : " + isTimeDoable);
        System.out.println("Contraintes capacités respectées route j : " + isCapacityDoable);

        ArrayList<Assignment> mergedRoute = new ArrayList<>();
        mergedRoute.addAll(iRoute);
        mergedRoute.addAll(jRoute);
        System.out.println("Route fusionnée : " + mergedRoute);
        isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(mergedRoute);
        isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(mergedRoute);
        System.out.println("Contraintes temporelles respectées : " + isTimeDoable);
        System.out.println("Contraintes capacités respectées : " + isCapacityDoable);
        if (isTimeDoable && isCapacityDoable) {
            //suppresion route i
            permutations.remove(iRoute);
            //suppresion route j
            permutations.remove(jRoute);
            //suppresion de toutes les routes qui contiennent i et j et un satellite différent de celui affectée
            permutations.removeIf(route -> !route.get(0).getSatellite().equals(iRoute.get(0)) && route.get(0).getCustomer().equals(iRoute.get(0).getCustomer()));
            permutations.removeIf(route -> !route.get(0).getSatellite().equals(jRoute.get(0)) && route.get(0).getCustomer().equals(jRoute.get(0).getCustomer()));
            //ajout de la route fusionnée
            permutations.add(mergedRoute);
            System.out.println("Nouvelle solution : " + permutations);
        }
        return isTimeDoable && isCapacityDoable;
    }

    /**
     *
     * @param s
     * @param c
     * @param permutations
     * @return
     */
    public ArrayList<Assignment> findRouteFirst(Satellite s, Customer c, List<ArrayList<Assignment>> permutations) {
        Iterator<ArrayList<Assignment>> routeIt = permutations.iterator();
        while (routeIt.hasNext()) {
            ArrayList<Assignment> route = routeIt.next();
            Iterator<Assignment> assignmentIt = route.iterator();
            boolean found = true;
            while (assignmentIt.hasNext() && found) {
                Assignment assignment = assignmentIt.next();
                if (assignment.getSatellite() == null) {
                    found = route.get(0).getSatellite().equals(s) && assignment.getCustomer().equals(c);
                    if (found) {
                        return route;
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     *
     * @param s
     * @param c
     * @param permutations
     * @return
     */
    public ArrayList<Assignment> findRouteLast(Satellite s, Customer c, List<ArrayList<Assignment>> permutations) {
        Iterator<ArrayList<Assignment>> routeIt = permutations.iterator();
        while (routeIt.hasNext()) {
            ArrayList<Assignment> route = routeIt.next();
            boolean sInRoute = route.get(0).getSatellite().equals(s);
            boolean cInRoute = route.get(route.size() - 1).getCustomer().equals(c);
            if (sInRoute && cInRoute) {
                return route;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Classe imbriqué utilisée pour gérer les savings On veut conserver les
     * informations des clients et du satellites impliqués dans la calcul du
     * savings
     */
    public class Saving implements Comparable {

        public Satellite sI;
        public Satellite sJ;
        public Customer i;
        public Customer j;
        public double saving;

        public Saving(Satellite sI, Satellite sJ, Customer i, Customer j) {
            this.sI = sI;
            this.sJ = sJ;
            this.i = i;
            this.j = j;
            if (sI.equals(sJ)) {
                this.saving = sI.computeDistance(i) + sI.computeDistance(j) - i.computeDistance(j);
            } else {
                this.saving = sI.computeDistance(i) + sJ.computeDistance(j) - i.computeDistance(sJ) - j.computeDistance(sI);
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("savings  =(");
            sb.append("satellite sI : ").append(sI.toString());
            sb.append(" satellite sJ : ").append(sJ.toString());
            sb.append(" customer i : ").append(i.toString());
            sb.append(" customer j : ").append(j.toString());
            sb.append(" Saving value : ").append(saving);
            sb.append(")");
            return sb.toString();
        }

        @Override
        public int compareTo(Object o) {
            Saving saving = (Saving) o;
            //comparaison
            return Double.compare(this.saving, saving.saving);
        }
    }
}
