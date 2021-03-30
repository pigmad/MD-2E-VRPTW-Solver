package solver;

import model.AssignmentSecond;
import model.Customer;
import model.Satellite;
import model.Solution;
import model.Instance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de l'algorithme de CW adapté au problème pour la résolution du second niveau. <br>
 * On considère deux types de fusion pour des clients i et j: <br>
 * soit les satellites de routes à fusionner sont identiques alors on conserve la fusion classique : satI,i,j,SatI <br>
 * soit ils sont différents et on obtient : satI,i,satJ,j,satI.
 *
 * @author LASTENNET Dorian
 */
public class ClarkeWrightSecond implements Heuristic {

    private Solver solver;

    public ClarkeWrightSecond(Solver solver) {
        this.solver = solver;
    }

    @Override
    public Solution solve() {
        //calcul de la liste des savings
        List<Saving> savingsList = computeSavings(solver.getInstance());

        //création de la solution initiale où tous les clients sont reliés chacun des satellites par un véhicule
        List<List<AssignmentSecond>> routes = createInitialSolution(solver.getInstance());

        for (Saving saving : savingsList) {
            //Traitement du savings
            savingsTreatment(saving, routes, solver);
        }

        //Suppression des routes restantes si le client est seul dans la tournée
        repairSolutionAloneCustomers(routes, solver);

        //Réparation de la solution si elle utilise plus de camions que disponible
        repairSolutionExceedingVehiclesNumber(routes, solver);
        
        //création de la solution et calcul de la charge des satellites
        Solution solution = new Solution(new ArrayList<>(0), routes);
        solution.setSolutionSatellitesDemand(solver.getInstance());

        return solution;
    }

    /**
     * Fonction de création de la solution initiale pour l'algorithme de CK.
     *
     * @param instance L'instance du problème
     * @return un ensemble de routes où une route est créé pour chaque client
     * vers chaque satellite
     */
    public List<List<AssignmentSecond>> createInitialSolution(Instance instance) {
        List<Satellite> satellites = instance.getSatellites();
        List<Customer> customers = instance.getCustomers();
        List<List<AssignmentSecond>> routes = new ArrayList<>(satellites.size() * customers.size());
        for (Satellite s : satellites) {
            for (Customer c : customers) {
                List<AssignmentSecond> route = new ArrayList<>(2);
                route.add(new AssignmentSecond(c, s));
                route.add(new AssignmentSecond(c));
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
        List<Saving> savings = new ArrayList<>(satellites.size() * satellites.size() * customers.size() * customers.size() - satellites.size() * satellites.size() * customers.size());

        //on crée les savings pour chaques paires satellites i et j et de clients k et l
        for (int i = 0; i < satellites.size(); i++) {
            for (int j = 0; j < satellites.size(); j++) {
                for (int k = 0; k < customers.size(); k++) {
                    for (int l = k + 1; l < customers.size(); l++) {
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
     * méthode classique de CK ou en utilisant un rechargement vers le
     * satellite.
     *
     * @param saving le saving considéré
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void savingsTreatment(Saving saving, List<List<AssignmentSecond>> routes, Solver solver) {
        List<AssignmentSecond> iRoute = findRouteLast(saving.getiSatellite(), saving.getiCustomer(), routes);
        List<AssignmentSecond> jRoute = findRouteFirst(saving.getjSatellite(), saving.getjCustomer(), routes);
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
     * Cette fonction compare la première route de la solution avec un client c
     * et retourne la route où c est en première position si elle existe.
     *
     * @param s le satellite où c est affecté
     * @param c le client à chercher
     * @param routes l'ensemble des routes
     * @return la route ou c est en première position, un tableau vide sinon
     */
    public List<AssignmentSecond> findRouteFirst(Satellite s, Customer c, List<List<AssignmentSecond>> routes) {
        //pour chaque route on compare le premier element avec c et s
        for (List<AssignmentSecond> route : routes) {
            Optional<Satellite> assignSat = route.get(0).getSatellite();
            if (assignSat.isPresent()) {
                if (route.get(0).getCustomer().equals(c) && assignSat.get().equals(s)) {
                    return route;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Fonction de recherche de la route de la solution où le client c est en
     * dernière solution. <br>
     * Cette fonction compare la dernière route de la solution avec un client c
     * et retourne la route où c est en dernière position si elle existe.
     *
     * @param s le satellite où c est affecté
     * @param c le client à chercher
     * @param routes l'ensemble des routes
     * @return la route ou c est en dernière position, un tableau vide sinon
     */
    public List<AssignmentSecond> findRouteLast(Satellite s, Customer c, List<List<AssignmentSecond>> routes) {
        //pour chaque route on compare le dernier element avec c et on cherche si son affectation est a satellite s
        for (List<AssignmentSecond> route : routes) {
            //on compare le dernier séquencement à c
            if (route.get(route.size() - 1).getCustomer().equals(c)) {
                //on parcours la permutation à l'envers jusqu'à l'affectation de c et on compare 
                int index = route.size() - 2;
                boolean sInRoute = true;
                while (sInRoute) {
                    AssignmentSecond assign = route.get(index);
                    if (assign.getCustomer().equals(c)) {
                        Optional<Satellite> assignSat = assign.getSatellite();
                        if (assignSat.isPresent()) {
                            if (assignSat.get().equals(s)) {
                                return route;
                            } else {
                                sInRoute = false;
                            }
                        }
                    }
                    index--;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Fusion classique de l'algorithme de CK avec respect des contraintes
     * capacités et fenêtre de temps.
     *
     * @param saving le saving considéré
     * @param iRoute la route i a fusionner
     * @param jRoute la route j à fusionner
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void mergeRouteClassic(Saving saving, List<AssignmentSecond> iRoute, List<AssignmentSecond> jRoute, List<List<AssignmentSecond>> routes, Solver solver) {
        List<AssignmentSecond> mergedRoute = new ArrayList<>();
        //On merge toute la route i
        mergedRoute.addAll(iRoute);
        //index de l'affectation du client i à sI dans la route i
        int k = iRoute.indexOf(new AssignmentSecond(saving.getiCustomer(), saving.getiSatellite())) + 1;
        // on ajoute les affectations des clients vers le satellite sI de la route j à partir de l'indice k+1
        for (AssignmentSecond a : jRoute) {
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
    public void mergeRouteWithRefill(List<AssignmentSecond> iRoute, List<AssignmentSecond> jRoute, List<List<AssignmentSecond>> routes, Solver solver) {
        List<AssignmentSecond> mergedRoute = new ArrayList<>();
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
     * véhicules utilisés si celui-ci excède le nombre autorisé par l'instance.
     * <br>
     * Si à la fin de l'algorithme la solution renvoyée possède n véhicules, que
     * l'instance en autorise k alors on conserve les k plus grandes tournées et
     * on réinsère les clients des n-k tournées à n'importe quelle position
     * possible dans la solution.
     *
     * @param routes liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairSolutionExceedingVehiclesNumber(List<List<AssignmentSecond>> routes, Solver solver) {
        int exceedNumber = routes.size() - solver.getInstance().getSecondEchelonFleet().getVehiclesNumber();
        if (exceedNumber > 0) {
            //liste contenant les clients à réaffecter 
            List<AssignmentSecond> exceedingClients = new ArrayList<>(exceedNumber);
            //liste contenant les affectations des clients vers leur satellites
            List<AssignmentSecond> exceedingClientsAssignment = new ArrayList<>(exceedNumber);
            //tri des routes dans l'ordre décroisant de leur taille
            Collections.sort(routes, (List a1, List a2) -> a1.size() - a2.size());
            //Extraction des k plus petites routes en surplus
            for (int k = 0; k < exceedNumber; k++) {
                for (AssignmentSecond assign : routes.get(k)) {
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
            Collections.sort(exceedingClients, (AssignmentSecond a1, AssignmentSecond a2) -> Integer.compare(a1.getCustomer().getGlobalSiteID(), a2.getCustomer().getGlobalSiteID()));
            Collections.sort(exceedingClientsAssignment, (AssignmentSecond a1, AssignmentSecond a2) -> Integer.compare(a1.getCustomer().getGlobalSiteID(), a2.getCustomer().getGlobalSiteID()));

            for (int i = 0; i < exceedingClients.size(); i++) {
                int j = 0;
                boolean inserted = false;
                while (j < routes.size() && !inserted) {
                    int k = 1;
                    List<AssignmentSecond> route = routes.get(j);
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
     * Ces clients ont gardé les affectations de la solution initiale et ne sont
     * donc affectés à aucun satellite. <br>
     * On les réinsère en utilisant le satellite le plus proche d'eux et on les
     * réinsère dans la solution.
     *
     * @param routes liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairSolutionAloneCustomers(List<List<AssignmentSecond>> routes, Solver solver) {
        List<Customer> aloneCustomers = new ArrayList<>();
        //Extraction des clients seuls
        for (List<AssignmentSecond> route : routes) {
            if (route.size() == 2) {
                Customer c = route.get(0).getCustomer();
                if (!aloneCustomers.contains(c)) {
                    aloneCustomers.add(c);
                }
            }
        }
        if (!aloneCustomers.isEmpty()) {
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
                List<AssignmentSecond> route = new ArrayList<>();
                route.add(new AssignmentSecond(c, closestSatellite));
                route.add(new AssignmentSecond(c));
                routes.add(route);
            }
        }
    }

    /**
     * Classe imbriquée pour la gestion les savings. <br>
     * On veut conserver les informations des clients et des satellites
     * impliqués dans la calcul du savings.
     *
     * @author LASTENNET Dorian
     */
    public class Saving implements Comparable {

        private final Satellite iSatellite;
        private final Satellite jSatellite;
        private final Customer iCustomer;
        private final Customer jCustomer;
        private final double savingValue;

        public Saving(Satellite sI, Satellite sJ, Customer i, Customer j, double saving) {
            this.iSatellite = sI;
            this.jSatellite = sJ;
            this.iCustomer = i;
            this.jCustomer = j;
            this.savingValue = saving;
        }

        //Accesseurs
        public Satellite getiSatellite() {
            return iSatellite;
        }

        public Satellite getjSatellite() {
            return jSatellite;
        }

        public Customer getiCustomer() {
            return iCustomer;
        }

        public Customer getjCustomer() {
            return jCustomer;
        }

        public double getSavingValue() {
            return savingValue;
        }

        /**
         * Représentation de l'objet en texte.
         *
         * @return texte
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("savings  =(");
            sb.append("satellite sI : ").append(getiSatellite().toString());
            sb.append(" customer i : ").append(getiCustomer().toString());
            sb.append(" satellite sJ : ").append(getjSatellite().toString());
            sb.append(" customer j : ").append(getjCustomer().toString());
            sb.append(" Saving value : ").append(getSavingValue());
            sb.append(")");
            return sb.toString();
        }

        /**
         * Surchage opérateur comparaison.
         *
         * @param o objet à comparer
         * @return booléen indiquant si les objets sont identiques
         */
        @Override
        public int compareTo(Object o) {
            Saving saving = (Saving) o;
            //comparaison
            return Double.compare(this.getSavingValue(), saving.getSavingValue());
        }
    }

}
