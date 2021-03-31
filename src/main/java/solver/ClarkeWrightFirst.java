package solver;

import model.Satellite;
import model.Depot;
import model.Solution;
import model.Instance;
import model.AssignmentFirst;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Implémentation de l'algorithme de CW adapté au problème pour la résolution du premier niveau. <br>
 * On considère deux types de fusion pour des satellites i et j: <br>
 * soit les depots de routes à fusionner sont identiques alors on conserve la fusion classique : depI, i, j, depI <br>
 * soit ils sont différents et on obtient : depI,i,depJ,j,depI 
 *
 * @author LASTENNET Dorian
 */
public class ClarkeWrightFirst implements Heuristic {

    private Solver solver;

    public ClarkeWrightFirst(Solver solver) {
        this.solver = solver;
    }

    @Override
    public Solution solve() {
        //calcul de la liste des savings
        List<Saving> savingsList = computeSavings(solver.getInstance());

        //création de la solution initiale où tous les satellites sont reliés chacun des depots par un véhicule
        List<List<AssignmentFirst>> routes = createInitialSolution(solver.getInstance());

        for (Saving saving : savingsList) {
            //Traitement du savings
            savingsTreatment(saving, routes, solver);
        }

        //Suppression des routes restantes si le satellites est seul dans la tournée
        repairSolutionAloneSatellites(routes, solver);

        //Réparation de la solution si elle utilise plus de camions que disponible
        repairSolutionExceedingVehiclesNumber(routes, solver);
        
        //création de la solution et calcul de la charge des satellites
        Solution solution = new Solution(routes, solver.getSolution().getSecondEchelonPermutations());
        solution.setSolutionSatellitesDemand(solver.getInstance());

        return solution;
    }

    /**
     * Fonction de création de la solution initiale pour l'algorithme de CK.
     *
     * @param instance L'instance du problème
     * @return un ensemble de routes où une route est créé pour chaque satellite
     * vers chaque depot
     */
    public List<List<AssignmentFirst>> createInitialSolution(Instance instance) {
        List<Depot> depots = instance.getDepots();
        List<Satellite> satellites = instance.getSatellites();
        List<List<AssignmentFirst>> routes = new ArrayList<>(depots.size() * satellites.size());
        for (Depot d : depots) {
            for (Satellite s : satellites) {
                List<AssignmentFirst> route = new ArrayList<>(2);
                route.add(new AssignmentFirst(s, d));
                route.add(new AssignmentFirst(s));
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
        List<Depot> depots = instance.getDepots();
        List<Satellite> satellites = instance.getSatellites();
        List<Saving> savings = new ArrayList<>(depots.size() * depots.size() * satellites.size() * satellites.size() - depots.size() * depots.size() * satellites.size());

        //on crée les savings pour chaques paires depots i et j et de satellites k et l
        for (int i = 0; i < depots.size(); i++) {
            for (int j = 0; j < depots.size(); j++) {
                for (int k = 0; k < satellites.size(); k++) {
                    for (int l = k + 1; l < satellites.size(); l++) {
                        double savingValue;
                        if (depots.get(i).equals(depots.get(j))) {
                            savingValue = instance.getDistance(depots.get(i), satellites.get(k)) + instance.getDistance(depots.get(i), satellites.get(l)) - instance.getDistance(satellites.get(k), satellites.get(l));
                        } else {
                            savingValue = instance.getDistance(depots.get(i), satellites.get(k)) + instance.getDistance(depots.get(j), satellites.get(l)) - instance.getDistance(satellites.get(k), depots.get(j)) - instance.getDistance(satellites.get(l), depots.get(i));
                        }
                        savings.add(new Saving(depots.get(i), depots.get(j), satellites.get(k), satellites.get(l), savingValue));
                        savings.add(new Saving(depots.get(i), depots.get(j), satellites.get(l), satellites.get(k), savingValue));
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
     * méthode classique de CK ou en utilisant un rechargement vers le depot.
     *
     * @param saving le saving considéré
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void savingsTreatment(Saving saving, List<List<AssignmentFirst>> routes, Solver solver) {
        List<AssignmentFirst> iRoute = findRouteLast(saving.getiDepot(), saving.getiSatellite(), routes);
        List<AssignmentFirst> jRoute = findRouteFirst(saving.getjDepot(), saving.getjSatellite(), routes);
        if (!iRoute.equals(jRoute) && !iRoute.isEmpty() && !jRoute.isEmpty()) {
            if (saving.getiDepot().equals(saving.getjDepot())) {
                mergeRouteClassic(saving, iRoute, jRoute, routes, solver);
            } else {
                mergeRouteWithRefill(iRoute, jRoute, routes, solver);
            }
        }
    }

    /**
     * Fonction de recherche de la route de la solution où le satellite s est en
     * première position. <br>
     * Cette fonction compare la première route de la solution avec un 
     * et retourne la route où c est en première position si elle existe.
     *
     * @param d le depot où s est affecté
     * @param s le satellite à chercher
     * @param routes l'ensemble des routes
     * @return la route ou c est en première position, un tableau vide sinon
     */
    public List<AssignmentFirst> findRouteFirst(Depot d, Satellite s, List<List<AssignmentFirst>> routes) {
        //pour chaque route on compare le premier element avec c et s
        for (List<AssignmentFirst> route : routes) {
            Optional<Depot> assignDep = route.get(0).getDepot();
            if (assignDep.isPresent()) {
                if (route.get(0).getSatellite().equals(s) && assignDep.get().equals(d)) {
                    return route;
                }
            }
        }
        return new ArrayList<>();
    }

    /**
     * Fonction de recherche de la route de la solution où le satellite s est en
     * dernière position. <br>
     * Cette fonction compare la dernière route de la solution avec un satellite s
     * et retourne la route où s est en dernière position si elle existe.
     *
     * @param d le depot où s est affecté
     * @param s le satellite à chercher
     * @param routes l'ensemble des routes
     * @return la route ou s est en dernière position, un tableau vide sinon
     */
    public List<AssignmentFirst> findRouteLast(Depot d, Satellite s, List<List<AssignmentFirst>> routes) {
        //pour chaque route on compare le dernier element avec s et on cherche si son affectation est au depot d
        for (List<AssignmentFirst> route : routes) {
            //on compare le dernier séquencement à s
            if (route.get(route.size() - 1).getSatellite().equals(s)) {
                //on parcours la permutation à l'envers jusqu'à l'affectation de s et on compare 
                int index = route.size() - 2;
                boolean sInRoute = true;
                while (sInRoute) {
                    AssignmentFirst assign = route.get(index);
                    if (assign.getSatellite().equals(s)) {
                        Optional<Depot> assignDep = assign.getDepot();
                        if (assignDep.isPresent()) {
                            if (assignDep.get().equals(d)) {
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
     * Fusion classique de l'algorithme de CK avec respect des contraintes capacités.
     *
     * @param saving le saving considéré
     * @param iRoute la route i a fusionner
     * @param jRoute la route j à fusionner
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void mergeRouteClassic(Saving saving, List<AssignmentFirst> iRoute, List<AssignmentFirst> jRoute, List<List<AssignmentFirst>> routes, Solver solver) {
        List<AssignmentFirst> mergedRoute = new ArrayList<>();
        //On merge toute la route i
        mergedRoute.addAll(iRoute);
        //index de l'affectation du depot i à dI dans la route i
        int k = iRoute.indexOf(new AssignmentFirst(saving.getiSatellite(), saving.getiDepot())) + 1;
        // on ajoute les affectations des satellites vers le depot dI de la route j à partir de l'indice k+1
        for (AssignmentFirst a : jRoute) {
            if (a.getDepot().isPresent()) {
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
        boolean isCapacityDoable = solver.isFirstEchelonPermutationCapacitiesRespected(mergedRoute);
        if (isCapacityDoable) {
            //suppresion route i
            routes.remove(iRoute);
            //suppresion route j
            routes.remove(jRoute);
            //suppresion de toutes les routes qui contiennent i et j et un depot différent de celui affectée
            routes.removeIf(route -> !route.get(0).getDepot().get().equals(iRoute.get(0).getDepot().get()) && route.get(0).getSatellite().equals(iRoute.get(0).getSatellite()));
            routes.removeIf(route -> !route.get(0).getDepot().get().equals(jRoute.get(0).getDepot().get()) && route.get(0).getSatellite().equals(jRoute.get(0).getSatellite()));
            //ajout de la route fusionnée
            routes.add(mergedRoute);
        }
    }

    /**
     * Fusion autorisant un rechargment au depot avec respect des
     * contraintes capacités.
     *
     * @param iRoute la route i a fusionner
     * @param jRoute la route j à fusionner
     * @param routes la liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void mergeRouteWithRefill(List<AssignmentFirst> iRoute, List<AssignmentFirst> jRoute, List<List<AssignmentFirst>> routes, Solver solver) {
        List<AssignmentFirst> mergedRoute = new ArrayList<>();
        mergedRoute.addAll(iRoute);
        mergedRoute.addAll(jRoute);
        boolean isCapacityDoable = solver.isFirstEchelonPermutationCapacitiesRespected(mergedRoute);
        if (isCapacityDoable) {
            //suppresion route i
            routes.remove(iRoute);
            //suppresion route j
            routes.remove(jRoute);
            //suppresion de toutes les routes qui contiennent i et j et un depot différent de celui affectée
            routes.removeIf(route -> !route.get(0).getDepot().get().equals(iRoute.get(0).getDepot().get()) && route.get(0).getSatellite().equals(iRoute.get(0).getSatellite()));
            routes.removeIf(route -> !route.get(0).getDepot().get().equals(jRoute.get(0).getDepot().get()) && route.get(0).getSatellite().equals(jRoute.get(0).getSatellite()));
            //ajout de la route fusionnée
            routes.add(mergedRoute);
        }
    }

    /**
     * Fonction appelée en fin d'algorithme chargée de réduire le nombre de
     * véhicules utilisés si celui-ci excède le nombre autorisé par l'instance. <br>
     * Si à la fin de l'algorithme la solution renvoyée possède n véhicules, que
     * l'instance en autorise k alors on conserve les k plus grandes tournées et
     * on réinsère les satellites des n-k tournées à n'importe quelle position
     * possible dans la solution.
     *
     * @param routes liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairSolutionExceedingVehiclesNumber(List<List<AssignmentFirst>> routes, Solver solver) {
        int exceedNumber = routes.size() - solver.getInstance().getSecondEchelonFleet().getVehiclesNumber();
        if (exceedNumber > 0) {
            //liste contenant les satellites à réaffecter 
            List<AssignmentFirst> exceedingSatellites = new ArrayList<>(exceedNumber);
            //liste contenant les affectations des satelliets vers leur depots
            List<AssignmentFirst> exceedingSatellitesAssignment = new ArrayList<>(exceedNumber);
            //tri des routes dans l'ordre décroisant de leur taille
            Collections.sort(routes, (List a1, List a2) -> a1.size() - a2.size());
            //Extraction des k plus petites routes en surplus
            for (int k = 0; k < exceedNumber; k++) {
                for (AssignmentFirst assign : routes.get(k)) {
                    if (assign.getDepot().isEmpty()) {
                        exceedingSatellites.add(assign);
                    } else {
                        exceedingSatellitesAssignment.add(assign);
                    }
                }
            }
            //On retire les k plus petites routes de la solution
            routes.subList(0, exceedNumber).clear();
            //On trie les satelittes et leur affectations pour acceder aux elements avec le même indice
            Collections.sort(exceedingSatellites, (AssignmentFirst a1, AssignmentFirst a2) -> Integer.compare(a1.getSatellite().getGlobalSiteID(), a2.getSatellite().getGlobalSiteID()));
            Collections.sort(exceedingSatellitesAssignment, (AssignmentFirst a1, AssignmentFirst a2) -> Integer.compare(a1.getSatellite().getGlobalSiteID(), a2.getSatellite().getGlobalSiteID()));

            for (int i = 0; i < exceedingSatellites.size(); i++) {
                int j = 0;
                boolean inserted = false;
                while (j < routes.size() && !inserted) {
                    int k = 1;
                    List<AssignmentFirst> route = routes.get(j);
                    while (k < route.size() && !inserted) {
                        if (route.get(k).getDepot().isEmpty()) {
                            route.add(k, exceedingSatellites.get(i));
                            //On cherche la première position où on peut insérer une affectation
                            int assignmentIndex = k;
                            while (route.get(assignmentIndex).getDepot().isEmpty()) {
                                assignmentIndex--;
                            }
                            route.add(assignmentIndex + 1, exceedingSatellitesAssignment.get(i));
                            //insertion réussie, on passe au prochain site
                            if (solver.isFirstEchelonPermutationCapacitiesRespected(route)) {
                                inserted = true;
                            } //insertion échouée, on retire l'insertion et on passe à la prochaine position
                            else {
                                Satellite c = exceedingSatellites.get(i).getSatellite();
                                route.removeIf(assign -> assign.getSatellite().equals(c));
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
     * Fonction appelée en fin d'algorithme chargée de réaffecter les satellites
     * qui n'ont pas été integrés dans des routes complètes. <br>
     * Ces satellites ont gardé les affectations de la solution initiale et ne sont
     * donc affectés à aucun depot. <br>
     * On les réinsère en utilisant le satellite le plus proche d'eux et on les
     * réinsère dans la solution.
     *
     * @param routes liste des routes
     * @param solver le solveur contenant l'instance du problème
     */
    public void repairSolutionAloneSatellites(List<List<AssignmentFirst>> routes, Solver solver) {
        List<Satellite> aloneSatellites = new ArrayList<>();
        //Extraction des satellites seuls
        for (List<AssignmentFirst> route : routes) {
            if (route.size() == 2) {
                Satellite c = route.get(0).getSatellite();
                if (!aloneSatellites.contains(c)) {
                    aloneSatellites.add(c);
                }
            }
        }
        if (!aloneSatellites.isEmpty()) {
            //On retire les routes où les satellites sont seuls
            routes.removeIf(route -> route.size() == 2);
            //On cherche le depot le plus proche des satellites seuls et on réinsère les satellites dans la solution
            for (Satellite c : aloneSatellites) {
                Depot closestDepot = solver.getInstance().getDepots().get(0);
                for (Depot s : solver.getInstance().getDepots()) {
                    if (solver.getInstance().getDistance(s, c) < solver.getInstance().getDistance(closestDepot, c)) {
                        closestDepot = s;
                    }
                }
                List<AssignmentFirst> route = new ArrayList<>();
                route.add(new AssignmentFirst(c, closestDepot));
                route.add(new AssignmentFirst(c));
                routes.add(route);
            }
        }
    }

    /**
     * Classe imbriquée utilisée pour gérer les savings. <br>
     * On veut conserver les informations des depots et des satellites
     * impliqués dans la calcul du savings.
     *
     * @author LASTENNET Dorian
     */
    public class Saving implements Comparable {

        private final Depot iDepot;
        private final Depot jDepot;
        private final Satellite iSatellite;
        private final Satellite jSatellite;
        private final double savingValue;

        public Saving(Depot sI, Depot sJ, Satellite i, Satellite j, double saving) {
            this.iDepot = sI;
            this.jDepot = sJ;
            this.iSatellite = i;
            this.jSatellite = j;
            this.savingValue = saving;
        }

        //Accesseurs
        public Depot getiDepot() {
            return iDepot;
        }

        public Depot getjDepot() {
            return jDepot;
        }

        public Satellite getiSatellite() {
            return iSatellite;
        }

        public Satellite getjSatellite() {
            return jSatellite;
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
            sb.append("depot dI : ").append(getiDepot().toString());
            sb.append(" satellite i : ").append(getiSatellite().toString());
            sb.append(" depot dJ : ").append(getjDepot().toString());
            sb.append(" satellite j : ").append(getjSatellite().toString());
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
