package solver;

import model.Assignment;
import model.Customer;
import model.Satellite;
import model.Instance;
import model.Solution;

import java.util.List;
import java.util.Optional;

/**
 * Classe solveur chargé de gérer la résolution de l'instance lue.
 *
 * @author LASTENNET Dorian
 */
public class Solver {

    private final Instance instance;
    private Solution solution;
    private Heuristic heuristic;

    public Solver(Instance instance) {
        this.instance = instance;
        this.solution = new Solution();
    }

    public Solver(Instance instance, Solution solution) {
        this.instance = instance;
        this.solution = solution;
    }

    public void solveInstance() {
        solution = heuristic.run(this);
    }

    /**
     * Fonction d'évaluation d'une solution.
     *
     * @param solution la solution à évaluer
     * @return la valeur de la fonction objectif pour la solution
     */
    public double evaluateSolution(Solution solution) {
        return evaluateFirstEchelon(solution) + evaluateSecondEchelon(solution);
    }

    /**
     * Fonction d'évaluation du premier niveau.
     *
     * @param solution la solution à évaluer
     * @return la valeur de la fonction objectif pour le premier niveau de la
     * solution
     */
    public double evaluateFirstEchelon(Solution solution) {
        double firstEchelonTravelCost = 0.0;
        double firstEchelonHandlingCost = 0.0;

        return firstEchelonTravelCost + firstEchelonHandlingCost;
    }

    /**
     * Fonction d'évaluation du second niveau.
     *
     * @param solution la solution à évaluer
     * @return la valeur de la fonction objectif pour le second niveau de la solution
     */
    public double evaluateSecondEchelon(Solution solution) {
        double secondEchelonTravelCostSum = 0.0;
        double secondEchelonHandlingCostSum = 0.0;

        int secondEchelonVehicleCost = instance.getSecondEchelonFleet().getVehiclesCost();
        double secondEchelonVehicleUsageCostSum = (double) solution.getSecondEchelonPermutations().size() * secondEchelonVehicleCost;

        for (List<Assignment> route : solution.getSecondEchelonPermutations()) {
            int routeSize = route.size();
            for (int iAssignment = 0; iAssignment < routeSize; iAssignment++) {
                //Assignation courante
                Customer currentCustomer = route.get(iAssignment).getCustomer();
                Optional<Satellite> currentSatellite = route.get(iAssignment).getSatellite();
                //Permutation suivante
                Customer nextCustomer;
                Optional<Satellite> nextSatellite;
                //Si on atteint la fin de la route alors le véhicule doit retourner à son point d'origine
                if (iAssignment == routeSize - 1) {
                    nextCustomer = route.get(0).getCustomer();
                    nextSatellite = route.get(0).getSatellite();
                } else {
                    nextCustomer = route.get(iAssignment + 1).getCustomer();
                    nextSatellite = route.get(iAssignment + 1).getSatellite();
                }
                /**
                 * Si aucun satellite dans la route alors on viens d'un
                 * client on calcule le cout de trajet du client dans la
                 * route i au satellite dans la route i ou au client
                 * i+1 et le cout de déchargement du véhicule
                 *
                 */
                if (currentSatellite.isEmpty()) {
                    secondEchelonTravelCostSum += nextSatellite.isEmpty() ? instance.getDistance(currentCustomer, nextCustomer) : instance.getDistance(currentCustomer, nextSatellite.get());
                    secondEchelonHandlingCostSum += currentCustomer.getServiceTime();
                } /**
                 * Si il y a un satellite dans la route il représente
                 * l'affectation d'un client au satellite on calcule le cout de
                 * trajet du client dans la route i au satellite dans la
                 * route i ou au client i+1 et le cout de chargement du
                 * véhicule
                 *
                 */
                else {
                    secondEchelonTravelCostSum += nextSatellite.isEmpty() ? instance.getDistance(currentSatellite.get(), nextCustomer) : instance.getDistance(currentSatellite.get(), nextSatellite.get());
                    secondEchelonHandlingCostSum += currentSatellite.get().getServiceTime();
                }
            }
        }
        return secondEchelonTravelCostSum + secondEchelonHandlingCostSum + secondEchelonVehicleUsageCostSum;
    }

    /**
     * Fonction d'évaluation de la faisabilité de la solution.
     *
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la solution
     */
    public boolean isSolutionDoable(Solution solution) {
        return isFirstEchelonCapacitiesRespected(solution)
                && isFirstEchelonVehiclesNumberRespected(solution)
                && isSecondEchelonCapacitiesRespected(solution)
                && isSecondEchelonTimeWindowsRespected(solution)
                && isSecondEchelonVehiclesNumberRespected(solution);
    }

    /**
     * Fonction d'évaluation du respect de la contrainte de capacité pour le premier niveau.
     *
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isFirstEchelonCapacitiesRespected(Solution solution) {
        boolean isDoable = true;
        return isDoable;
    }

    /**
     * Fonction d'évaluation du respect de la contrainte de nombre de véhicules pour le premier niveau.
     *
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isFirstEchelonVehiclesNumberRespected(Solution solution) {
        return solution.getFirstEchelonPermutations().size() <= instance.getFirstEchelonFleet().getVehiclesNumber();
    }
    
    /**
     * Fonction d'évaluation du respect de la contrainte que tous les clients 
     * doivent être livrés par un véhicule unique.
     * 
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean areAllCustomersDelivered(Solution solution){
        return solution.getSecondEchelonPermutations()
                .stream()
                .flatMap(route -> route.stream())
                .filter(assign -> assign.getSatellite().isPresent())
                .distinct()
                .count() == instance.getCustomers().size();
    }

    /**
     * Fonction d'évaluation du respect de la contrainte de capacité pour le
     * second niveau.
     *
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isSecondEchelonCapacitiesRespected(Solution solution) {
        boolean isDoable = true;
        int iPermutation = 0;
        while (isDoable && iPermutation < solution.getSecondEchelonPermutations().size()) {
            isDoable = isSecondEchelonPermutationCapacitiesRespected(solution.getSecondEchelonPermutations().get(iPermutation));
            iPermutation++;
        }
        return isDoable;
    }

    /**
     * Fonction d'évaluation de la faisabilité des capacités d'une route.
     *
     * @param route la route à évaluer
     * @return booléen indiquant si la route est faisable
     */
    public boolean isSecondEchelonPermutationCapacitiesRespected(List<Assignment> route) {
        boolean isDoable = true;
        int routeSize = route.size();
        int fleetLoad = 0;
        int iAssignment = 0;
        while (isDoable && iAssignment < routeSize) {
            //Permutation courante
            Customer currentCustomer = route.get(iAssignment).getCustomer();
            Optional<Satellite> currentSatellite = route.get(iAssignment).getSatellite();
            //Il s'agit de l'affectation d'un client donc on charge le camion
            if (currentSatellite.isPresent()) {
                fleetLoad += currentCustomer.getDemandSize();
                
            } else {
                fleetLoad -= currentCustomer.getDemandSize();
            }
            isDoable = fleetLoad <= instance.getSecondEchelonFleet().getVehiclesCapacity();
            iAssignment++;
        }
        return isDoable;
    }

    /**
     * Fonction d'évaluation du respect de la contrainte de fenêtre de temps
     * pour le second niveau.
     *
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isSecondEchelonTimeWindowsRespected(Solution solution) {
        boolean isDoable = true;
        int iPermutation = 0;
        while (isDoable && iPermutation < solution.getSecondEchelonPermutations().size()) {
            isDoable = isSecondEchelonPermutationTimeWindowsRespected(solution.getSecondEchelonPermutations().get(iPermutation));
            iPermutation++;
        }
        return isDoable;
    }

    /**
     * Fonction d'évaluation de la faisabilité des fenêtres de temps d'une route.
     *
     * @param route la route à évaluer
     * @return booléen indiquant si la route est faisable
     */
    public boolean isSecondEchelonPermutationTimeWindowsRespected(List<Assignment> route) {
        boolean isDoable = true;
        int routeSize = route.size();
        double currentTime = 0.0;
        int iAssignment = 0;
        while (isDoable && iAssignment < routeSize - 1) {
            boolean isTimeWindowRespected = true;
            //Permutation courante
            Customer currentCustomer = route.get(iAssignment).getCustomer();
            Optional<Satellite> currentSatellite = route.get(iAssignment).getSatellite();
            //Permutatiotn suivante
            Customer nextCustomer = route.get(iAssignment + 1).getCustomer();
            Optional<Satellite> nextSatellite = route.get(iAssignment + 1).getSatellite();
            //Si aucun satellite dans la route alors on viens d'un client
            if (currentSatellite.isEmpty()) {
                //trajet client->client
                if (nextSatellite.isEmpty()) {
                    //arrivée au plus tôt du camion au client
                    double startServiceTime = Math.max(nextCustomer.getTimeWindowStart(), currentTime + instance.getDistance(currentCustomer, nextCustomer));
                    isTimeWindowRespected = startServiceTime <= nextCustomer.getTimeWindowEnd();
                    currentTime = startServiceTime + nextCustomer.getServiceTime();
                } //trajet client->satellite
                else {
                    currentTime += instance.getDistance(currentCustomer, nextSatellite.get()) + nextSatellite.get().getServiceTime();
                }
            }
            //Si satellite dans la route alors on viens d'un satellite
            else {
                //trajet satellite->client
                if (nextSatellite.isEmpty()) {
                    //arrivée au plus tôt du camion au client
                    double startServiceTime = Math.max(nextCustomer.getTimeWindowStart(), currentTime + instance.getDistance(currentSatellite.get(), nextCustomer));
                    isTimeWindowRespected = startServiceTime <= nextCustomer.getTimeWindowEnd();
                    currentTime = startServiceTime + nextCustomer.getServiceTime();
                } //trajet satellite->satellite
                else {
                    currentTime += instance.getDistance(currentSatellite.get(), nextSatellite.get()) + nextSatellite.get().getServiceTime();
                }
            }
            isDoable = isTimeWindowRespected;
            iAssignment++;
        }
        return isDoable;
    }

    /**
     * Fonction d'évaluation du respect de la contrainte de nombre de véhicules
     * pour le second niveau.
     *
     * @param solution la solution à évaluer
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isSecondEchelonVehiclesNumberRespected(Solution solution) {
        return solution.getSecondEchelonPermutations().size() <= instance.getSecondEchelonFleet().getVehiclesNumber();
    }

    //Accesseurs
    public Instance getInstance() {
        return instance;
    }

    public void setHeuristic(Heuristic heuristic) {
        this.heuristic = heuristic;
    }

    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
