package solver;

import model.Assignment;
import model.Customer;
import model.Satellite;
import model.Solution;
import model.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Implémentation de l'algorithme de CW adapté au problème. <br>
 * On considère deux types de fusion pour des clients i et j: <br>
 * soit les satellites de routes à fusionner sont identiques alors on conserve la fusion classique <br>
 * soit ils sont différents et on obtient satI,i,Sj,j,Si.
 *
 * @author LASTENNET Dorian
 */
public class ClarkeWright implements Heuristic {

    @Override
    public Solution run(Solver solver) {
        //calcul de la liste des savings
        List<Saving> savingsList = computeSavings(solver.getInstance());

        //création de la solution initiale où tous les clients sont reliés chacun des satellites par un véhicule
        List<List<Assignment>> routes = createInitialSolution(solver.getInstance());

        for (Saving saving : savingsList) {
            //Traitement du savings
            savingsTreatment(saving, routes, solver);
        }

        //Suppression des routes restantes si le client est seul dans la tournée
        repairSolutionAloneCustomers(routes, solver);

        //Réparation de la solution si elle utilise plus de camions que disponible
        repairSolutionExceedingVehiclesNumber(routes, solver);

        return new Solution(new ArrayList<>(), routes);
    }

    /**
     * Fonction de création de la solution initiale pour l'algorithme de CK.
     *
     * @param instance L'instance du problème
     * @return un ensemble de route où une route est créé pour chaque client
     * vers chaque satellite
     */
    public List<List<Assignment>> createInitialSolution(Instance instance) {
        List<Satellite> satellites = instance.getSatellites();
        List<Customer> customers = instance.getCustomers();
        List<List<Assignment>> routes = new ArrayList<>(satellites.size() * customers.size());
        for (Satellite s : satellites) {
            for (Customer c : customers) {
                List<Assignment> route = new ArrayList<>(2);
                route.add(new Assignment(c, s));
                route.add(new Assignment(c));
                routes.add(route);
            }
        }
        return routes;
    }

    /**
     * Fonction qui calcule la liste des savings pour l'instance du problème.
     *
     * @param instance L'instance du problème
     * @return liste des savings triée dans l'ordre décroisant
     */
    public List<Saving> computeSavings(Instance instance) {
        List<Satellite> satellites = instance.getSatellites();
        List<Customer> customers = instance.getCustomers();
        List<Saving> savings = new ArrayList<>(satellites.size()*satellites.size()*customers.size()*customers.size()-satellites.size()*satellites.size()*customers.size());

        //on crée les savings pour chaques paires satellites i et j et de clients k et l
        for (int i = 0; i < satellites.size(); i++) {
            for (int j = 0; j < satellites.size(); j++) {
                for (int k = 0; k < customers.size(); k++) {
                    for (int l = k +1; l < customers.size(); l++) {
                        double savingValue;
                        if (satellites.get(i).equals(satellites.get(j))) {
                            savingValue = instance.getDistance(satellites.get(i), customers.get(k)) + instance.getDistance(satellites.get(i), customers.get(l)) - instance.getDistance(customers.get(k), customers.get(l));
                        } else {
                            savingValue = instance.getDistance(satellites.get(i), customers.get(k)) + instance.getDistance(satellites.get(j), customers.get(l)) - instance.getDistance(customers.get(k), satellites.get(j)) - instance.getDistance(customers.get(l), satellites.get(i));
                        }
                        savings.add(new Saving(satellites.get(i), satellites.get(j), customers.get(k), customers.get(l), savingValue));
                        savings.add(new Saving(satellites.get(i), satellites.get(j), customers.get(l), customers.get(k), savingValue));
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
     * méthode classique de CK ou en utilisant un rechargement vers le satellite.
     *
     * @param saving le saving considéré
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void savingsTreatment(Saving saving, List<List<Assignment>> routes, Solver solver) {
        List<Assignment> iRoute = findRouteLast(saving.getiSatellite(), saving.getiCustomer(), routes);
        List<Assignment> jRoute = findRouteFirst(saving.getjSatellite(), saving.getjCustomer(), routes);
        if (!iRoute.equals(jRoute) && !iRoute.isEmpty() && !jRoute.isEmpty()) {
            if (saving.getiSatellite().equals(saving.getjSatellite())) {
                mergeRouteClassic(saving, iRoute, jRoute, routes, solver);
            } else {
                mergeRouteWithRefill(iRoute, jRoute, routes, solver);
            }
        }
    }

    /**
     * Fonction de recherche de la route de la solution où le client c est en
     * première position. <br>
     * Cette fonction compare la première route de la solution
     * avec un client c et retourne la route où c est en première position si
     * elle existe.
     *
     * @param s le satellite où c est affecté
     * @param c le client à chercher
     * @param routes l'ensemble des routes
     * @return la route ou c est en première position, un tableau vide sinon
     */
    public List<Assignment> findRouteFirst(Satellite s, Customer c, List<List<Assignment>> routes) {
        //pour chaque route on récupère la première assignation sans client
        for (List<Assignment> route : routes) {
            Iterator<Assignment> assignmentIt = route.iterator();
            boolean found = true;
            //pour chaque element de la route
            while (assignmentIt.hasNext() && found) {
                Assignment assignment = assignmentIt.next();
                //on passe toutes les affectations et on compare le premier client de la route
                if (assignment.getSatellite().isEmpty()) {
                    //si on ne trouve pas on passera à la route suivante
                    found = assignment.getCustomer().equals(c) && route.contains(new Assignment(c, s));
                    if (found) {
                        return route;
                    }
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Fonction de recherche de la route de la solution où le client c est en
     * dernière solution. <br>
     * Cette fonction compare la dernière route de la solution
     * avec un client c et retourne la route où c est en dernière position si
     * elle existe.
     *
     * @param s le satellite où c est affecté
     * @param c le client à chercher
     * @param routes l'ensemble des routes
     * @return la route ou c est en dernière position, un tableau vide sinon
     */
    public List<Assignment> findRouteLast(Satellite s, Customer c, List<List<Assignment>> routes) {
        //pour chaque route on compare le dernier element avec c
        for (List<Assignment> route : routes) {
            boolean sInRoute = route.lastIndexOf(new Assignment(c, s)) >= 0;
            boolean cInRoute = route.get(route.size() - 1).getCustomer().equals(c);
            if (sInRoute && cInRoute) {
                return route;
            }
        }
        return new ArrayList<>();
    }

    /**
     * Fusion classique de l'algorithme de CK avec respect des
     * contraintes capacités et fenêtre de temps.
     *
     * @param saving le saving considéré
     * @param iRoute la route i a fusionner
     * @param jRoute la route j à fusionner
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void mergeRouteClassic(Saving saving, List<Assignment> iRoute, List<Assignment> jRoute, List<List<Assignment>> routes, Solver solver) {
        List<Assignment> mergedRoute = new ArrayList<>();
        //On merge toute la route i
        mergedRoute.addAll(iRoute);
        //index de l'affectation du client i à sI dans la route i
        int k = iRoute.lastIndexOf(new Assignment(saving.getiCustomer(), saving.getiSatellite()));
        // on ajoute les affectations des clients vers le satellite sI de la route j à partir de l'indice k
        for (Assignment a : jRoute) {
            if (a.getSatellite().isPresent()) {
                mergedRoute.add(k, a);
                k++;
            } else {
                break;
            }
        }
        // le reste de la route j est ajoutée à la fin de la séquence
        for (int i = mergedRoute.size() - iRoute.size(); i < jRoute.size(); i++) {
            mergedRoute.add(jRoute.get(i));
        }
        boolean isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(mergedRoute);
        boolean isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(mergedRoute);
        if (isTimeDoable && isCapacityDoable) {
            //suppresion route i
            routes.remove(iRoute);
            //suppresion route j
            routes.remove(jRoute);
            //suppresion de toutes les routes qui contiennent i et j et un satellite différent de celui affectée
            routes.removeIf(route -> !route.get(0).getSatellite().get().equals(iRoute.get(0).getSatellite().get()) && route.get(0).getCustomer().equals(iRoute.get(0).getCustomer()));
            routes.removeIf(route -> !route.get(0).getSatellite().get().equals(jRoute.get(0).getSatellite().get()) && route.get(0).getCustomer().equals(jRoute.get(0).getCustomer()));
            //ajout de la route fusionnée
            routes.add(mergedRoute);
        }
    }

    /**
     * Fusion autorisant un rechargment au satellite avec respect des
     * contraintes capacités et fenêtre de temps.
     *
     * @param iRoute la route i a fusionner
     * @param jRoute la route j à fusionner
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void mergeRouteWithRefill(List<Assignment> iRoute, List<Assignment> jRoute, List<List<Assignment>> routes, Solver solver) {
        List<Assignment> mergedRoute = new ArrayList<>();
        mergedRoute.addAll(iRoute);
        mergedRoute.addAll(jRoute);
        boolean isTimeDoable = solver.isSecondEchelonPermutationTimeWindowsRespected(mergedRoute);
        boolean isCapacityDoable = solver.isSecondEchelonPermutationCapacitiesRespected(mergedRoute);
        if (isTimeDoable && isCapacityDoable) {
            //suppresion route i
            routes.remove(iRoute);
            //suppresion route j
            routes.remove(jRoute);
            //suppresion de toutes les routes qui contiennent i et j et un satellite différent de celui affectée
            routes.removeIf(route -> !route.get(0).getSatellite().get().equals(iRoute.get(0).getSatellite().get()) && route.get(0).getCustomer().equals(iRoute.get(0).getCustomer()));
            routes.removeIf(route -> !route.get(0).getSatellite().get().equals(jRoute.get(0).getSatellite().get()) && route.get(0).getCustomer().equals(jRoute.get(0).getCustomer()));
            //ajout de la route fusionnée
            routes.add(mergedRoute);
        }
    }

    /**
     * Fonction appelée en fin d'algorithme chargée de réduire le nombre de
     * véhicules utilisés si celui-ci excède le nombre autorisé par l'instance. <br>
     * Si à la fin de l'algorithme la solution renvoyée possède n véhicules, 
     * que l'instance en autorise k alors on conserve les k 
     * plus grandes tournées et on réinsère les clients
     * des n-k tournées à n'importe quelle position possible dans la solution.
     *
     * @param routes liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairSolutionExceedingVehiclesNumber(List<List<Assignment>> routes, Solver solver) {
        int exceedNumber = routes.size() - solver.getInstance().getSecondEchelonFleet().getVehiclesNumber();
        if (exceedNumber > 0) {
            //liste contenant les clients à réaffecter 
            List<Assignment> exceedingClients = new ArrayList<>(exceedNumber);
            //liste contenant les affectations des clients vers leur satellites
            List<Assignment> exceedingClientsAssignment = new ArrayList<>(exceedNumber);
            //tri des routes dans l'ordre décroisant de leur taille
            Collections.sort(routes, (List a1, List a2) -> a1.size() - a2.size());
            //Extraction des k plus petites routes en surplus
            for (int k = 0; k < exceedNumber; k++) {
                for (Assignment assign : routes.get(k)) {
                    if (assign.getSatellite().isEmpty()) {
                        exceedingClients.add(assign);
                    } else {
                        exceedingClientsAssignment.add(assign);
                    }
                }
            }
            //On retire les k plus petites routes de la solution
            routes.subList(0, exceedNumber).clear();
            //On trie les clients et leur affectations pour acceder aux elements avec le même indice
            Collections.sort(exceedingClients, (Assignment a1, Assignment a2) -> Integer.compare(a1.getCustomer().getGlobalSiteID(), a2.getCustomer().getGlobalSiteID()));
            Collections.sort(exceedingClientsAssignment, (Assignment a1, Assignment a2) -> Integer.compare(a1.getCustomer().getGlobalSiteID(), a2.getCustomer().getGlobalSiteID()));

            for (int i = 0; i < exceedingClients.size(); i++) {
                int j = 0;
                boolean inserted = false;
                while (j < routes.size() && !inserted) {
                    int k = 1;
                    List<Assignment> route = routes.get(j);
                    while (k < route.size() && !inserted) {
                        if (route.get(k).getSatellite().isEmpty()) {
                            route.add(k, exceedingClients.get(i));
                            //On cherche la première position où on peut insérer une affectation
                            int assignmentIndex = k;
                            while (route.get(assignmentIndex).getSatellite().isEmpty()) {
                                assignmentIndex--;
                            }
                            route.add(assignmentIndex + 1, exceedingClientsAssignment.get(i));
                            //insertion réussie, on passe au prochain site
                            if (solver.isSecondEchelonPermutationCapacitiesRespected(route) && solver.isSecondEchelonPermutationTimeWindowsRespected(route)) {
                                inserted = true;
                            } //insertion échouée, on retire l'insertion et on passe à la prochaine position
                            else {
                                Customer c = exceedingClients.get(i).getCustomer();
                                route.removeIf(assign -> assign.getCustomer().equals(c));
                            }
                        }
                        k++;
                    }
                    j++;
                }
            }
        }
    }

    /**
     * Fonction appelée en fin d'algorithme chargée de réaffecter les clients
     * qui n'ont pas été integrée dans des routes complètes. <br>
     * Ces clients ont gardé les affectations de la solution initiale et 
     * ne sont donc affectés à aucun satellite. <br>
     * On les réinsère en utilisant le satellite le plus proche
     * d'eux et on les réinsère dans la solution.
     *
     * @param routes liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairSolutionAloneCustomers(List<List<Assignment>> routes, Solver solver) {
        List<Customer> aloneCustomers = new ArrayList<>();
        //Extraction des clients seuls
        for (List<Assignment> route : routes) {
            if (route.size() == 2) {
                Customer c = route.get(0).getCustomer();
                if (!aloneCustomers.contains(c)) {
                    aloneCustomers.add(c);
                }
            }
        }
        if (aloneCustomers.isEmpty()) {
            //On retire les routes où les clients sont seuls
            routes.removeIf(route -> route.size() == 2);
            //On cherche le satellite le plus proche des clients seuls et on réinsère les clients dans la solution
            for (Customer c : aloneCustomers) {
                Satellite closestSatellite = solver.getInstance().getSatellites().get(0);
                for (Satellite s : solver.getInstance().getSatellites()) {
                    if (solver.getInstance().getDistance(s, c) < solver.getInstance().getDistance(closestSatellite, c)) {
                        closestSatellite = s;
                    }
                }
                List<Assignment> route = new ArrayList<>();
                route.add(new Assignment(c, closestSatellite));
                route.add(new Assignment(c));
                routes.add(route);
            }
        }
    }
}
