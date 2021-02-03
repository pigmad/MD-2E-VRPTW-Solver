package solver;

import model.Assignment;
import model.Customer;
import model.Satellite;
import model.Instance;
import model.Solution;

import java.util.List;

/**
 * Classe solveur chargé de gérer la résolution de l'instance lue
 * @author LASTENNET Dorian
 */
public class Solver {
    private final Instance instance;
    private Solution solution;
    private Heuristic heuristic;

    public Solver(Instance instance) {
        this.instance = instance;
    }
    public Solver(Instance instance, Solution solution) {
        this.instance = instance;
        this.solution = solution;
    }
    
    public void solveInstance(){
        solution = heuristic.run(this);
    }
    
    /**
     * Fonction d'évaluation d'une solution
     * @param solution
     * @return la valeur de la fonction objectif pour la solution
     */
    public long evaluateSolution(Solution solution){
        return evaluateFirstEchelon(solution) + evaluateSecondEchelon(solution);
    }
    
    /**
     * Fonction d'évaluation du premier niveau
     * @param solution
     * @return la valeur de la fonction objectif pour le premier niveau de la solution
     */
    public long evaluateFirstEchelon(Solution solution){
        long firstEchelonTravelCost = 0;
        long firstEchelonHandlingCost = 0;
        
        return firstEchelonTravelCost + firstEchelonHandlingCost;
    }
    
    /**
     * Fonction d'évaluation du second niveau
     * @param solution
     * @return la valeur de la fonction objectif pour le second niveau de la solution
     */
    public long evaluateSecondEchelon(Solution solution){
        long secondEchelonTravelCostSum = 0;
        long secondEchelonHandlingCostSum = 0;
        
        int secondEchelonVehicleCost = instance.getSecondEchelonFleet().getVehiclesCost();
        long secondEchelonVehicleUsageCostSum = (long)solution.getSecondEchelonPermutations().size() * secondEchelonVehicleCost;
        
        for(List<Assignment> permutation : solution.getSecondEchelonPermutations()){
            int permutationSize = permutation.size();
            for (int iAssignment=0; iAssignment<permutationSize; iAssignment++){
                //Assignation courante
                Customer currentCustomer = permutation.get(iAssignment).getCustomer();
                Satellite currentSatellite = permutation.get(iAssignment).getSatellite();
                //Permutation suivante
                Customer nextCustomer;
                Satellite nextSatellite;
                //Si on atteint la fin de la permutation alors le véhicule doit retourner à son point d'origine
                if (iAssignment==permutationSize-1){
                    nextCustomer = permutation.get(0).getCustomer();
                    nextSatellite = permutation.get(0).getSatellite();
                }
                else{
                    nextCustomer = permutation.get(iAssignment+1).getCustomer();
                    nextSatellite = permutation.get(iAssignment+1).getSatellite();
                }
                /**
                 * Si aucun satellite dans la permutation
                 * alors on viens d'un client
                 * on calcule le cout de trajet du client dans la permutation i au satellite dans la permutation i 
                 * ou au client i+1 et le cout de déchargement du véhicule
                 **/
                if (currentSatellite == null){
                    secondEchelonTravelCostSum += nextSatellite == null ? instance.getDistance(currentCustomer, nextCustomer) : instance.getDistance(currentCustomer, nextSatellite);
                    secondEchelonHandlingCostSum += currentCustomer.getServiceTime();
                }
                /**
                 * Si il y a un satellite dans la permutation
                 * il représente l'affectation d'un client au satellite
                 * on calcule le cout de trajet du client dans la permutation i au satellite dans la permutation i 
                 * ou au client i+1 et le cout de chargement du véhicule
                 **/
                else{
                    secondEchelonTravelCostSum += nextSatellite == null ? instance.getDistance(currentSatellite, nextCustomer) : instance.getDistance(currentSatellite, nextSatellite);
                    secondEchelonHandlingCostSum += currentSatellite.getServiceTime();
                }
            }
        }
        return secondEchelonTravelCostSum + secondEchelonHandlingCostSum + secondEchelonVehicleUsageCostSum;
    }
    
    /**
     * Fonction d'évaluation de la faisabilité de la solution
     * @param solution
     * @return booléen indiquant la faisabilité de la solution
     */
    public boolean isSolutionDoable(Solution solution){
        return isFirstEchelonCapacitiesRespected(solution) &&
                isFirstEchelonTimeWindowsRespected(solution) &&
                isFirstEchelonVehiclesNumberRespected(solution) &&
                isSecondEchelonCapacitiesRespected(solution) &&
                isSecondEchelonTimeWindowsRespected(solution) &&
                isSecondEchelonVehiclesNumberRespected(solution);
    }
    
    /**
     * Fonction d'évaluation du respect de la contrainte de capacité pour le premier niveau
     * @param solution
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isFirstEchelonCapacitiesRespected(Solution solution){
        boolean isDoable = true;        
        return isDoable;
    }
    
    /**
     * Fonction d'évaluation du respect de la contrainte de fenêtre de temps pour le premier niveau
     * @param solution
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isFirstEchelonTimeWindowsRespected(Solution solution){
        boolean isDoable = true;        
        return isDoable;
    }

    /**
     * Fonction d'évaluation du respect de la contrainte de nombre de véhicules pour le premier niveau
     * @param solution
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isFirstEchelonVehiclesNumberRespected(Solution solution){
        return true;
        //return solution.getFirstEchelonPermutations().size() <= instance.getFirstEchelonFleet().getVehiclesNumber();
    }
    
    /**
     * Fonction d'évaluation du respect de la contrainte de capacité pour le second niveau
     * @param solution
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isSecondEchelonCapacitiesRespected(Solution solution) {
        boolean isDoable = true;
        int iPermutation = 0;
        while (isDoable && iPermutation < solution.getSecondEchelonPermutations().size()){
            List<Assignment> permutation = solution.getSecondEchelonPermutations().get(iPermutation);
            int permutationSize = permutation.size();
            int fleetLoad=0;
            int iAssignment = 0;
            while (isDoable && iAssignment < permutationSize){
                //Permutation courante
                Customer currentCustomer = permutation.get(iAssignment).getCustomer();
                Satellite currentSatellite = permutation.get(iAssignment).getSatellite();
                if (currentSatellite == null){
                    fleetLoad -= currentCustomer.getDemandSize();
                }
                else{
                    fleetLoad += currentCustomer.getDemandSize();
                }
                isDoable =  fleetLoad <= instance.getSecondEchelonFleet().getVehiclesCapacity();
                iAssignment++;
            }
            iPermutation++;
        }
        return isDoable;
    }
    
    /**
     * Fonction d'évaluation du respect de la contrainte de fenêtre de temps pour le second niveau 
     * @param solution
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isSecondEchelonTimeWindowsRespected(Solution solution) {
        boolean isDoable = true;
        int iPermutation = 0;
        while (isDoable && iPermutation < solution.getSecondEchelonPermutations().size()){
            List<Assignment> permutation = solution.getSecondEchelonPermutations().get(iPermutation);
            int permutationSize = permutation.size();
            double currentTime = 0.0; //interdépendance avec niveau 1 ????
            int iAssignment = 0;
            while (isDoable && iAssignment < permutationSize-1){
                boolean isTimeWindowRespected = true;
                //Permutation courante
                Customer currentCustomer = permutation.get(iAssignment).getCustomer();
                Satellite currentSatellite = permutation.get(iAssignment).getSatellite();
                //Permutatiotn suivante
                Customer nextCustomer = permutation.get(iAssignment+1).getCustomer();
                Satellite nextSatellite = permutation.get(iAssignment+1).getSatellite();
                /**
                 * Si aucun satellite dans la permutation
                 * alors on viens d'un client
                 * on calcule le cout de trajet du client dans la permutation i au satellite dans la permutation i 
                 * ou au client i+1
                 **/
                if (currentSatellite == null){
                    if (nextSatellite == null){
                        currentTime += currentCustomer.computeDistance(nextCustomer);
                        isTimeWindowRespected = currentTime >= nextCustomer.getTimeWindowStart() && currentTime <= nextCustomer.getTimeWindowEnd();
                    }
                    else{
                        currentTime += currentCustomer.computeDistance(nextSatellite);
                    }
                }
                /**
                 * Si il y a un satellite dans la permutation
                 * il représente l'affectation d'un client au satellite
                 * on calcule le cout de trajet du client dans la permutation i au satellite dans la permutation i 
                 * ou au client i+1
                 **/
                else{
                    if (nextSatellite == null){
                        currentTime += currentSatellite.computeDistance(nextCustomer);
                        isTimeWindowRespected = currentTime >= nextCustomer.getTimeWindowStart() && currentTime <= nextCustomer.getTimeWindowEnd();
                    }
                    else{
                        currentTime += currentSatellite.computeDistance(nextSatellite);
                    }
                }
                isDoable = isTimeWindowRespected;
                iAssignment++;
            }
            iPermutation++;
        }
        return isDoable;
    }   
    
    /**
     * Fonction d'évaluation du respect de la contrainte de nombre de véhicules pour le second niveau
     * @param solution
     * @return booléen indiquant la faisabilité de la contrainte
     */
    public boolean isSecondEchelonVehiclesNumberRespected(Solution solution){
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
