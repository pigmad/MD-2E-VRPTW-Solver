package solver;

import model.Assignment;
import model.Customer;
import model.Satellite;
import model.Instance;
import model.Solution;

import java.util.List;

/**
 *
 * @author LASTENNET Dorian
 */
public class Solver {
    private final Instance instance;
    private Solution solution;

    public Solver(Instance instance) {
        this.instance = instance;
    }
    public Solver(Instance instance, Solution solution) {
        this.instance = instance;
        this.solution = solution;
    }
    
    public Solution solveInstance(Heuristic heuristic){
        solution = heuristic.run(this);
        return solution;
    }
    
    public double evaluateSolution(Solution solution){
        return evaluateFirstEchelon(solution) + evaluateSecondEchelon(solution);
    }
    
    public double evaluateFirstEchelon(Solution solution){
        double firstEchelonTravelCost = 0.0;
        double firstEchelonHandlingCost = 0.0;
        
        return firstEchelonTravelCost + firstEchelonHandlingCost;
    }
    
    public double evaluateSecondEchelon(Solution solution){
        double secondEchelonTravelCostSum = 0.0;
        double secondEchelonHandlingCostSum = 0.0;
        
        int secondEchelonVehicleCost = instance.getSecondEchelonFleet().getVehiclesCost();
        double secondEchelonVehicleUsageCostSum = (double)solution.getSecondEchelonPermutations().size() * secondEchelonVehicleCost;
        
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
    
    public boolean isSolutionDoable(Solution solution){
        return isFirstEchelonCapacitiesRespected(solution) && isFirstEchelonTimeWindowsRespected(solution) && isSecondEchelonCapacitiesRespected(solution) && isSecondEchelonTimeWindowsRespected(solution);
    }
    
    public boolean isFirstEchelonCapacitiesRespected(Solution solution){
        boolean isDoable = true;        
        return isDoable;
    }
    
    public boolean isFirstEchelonTimeWindowsRespected(Solution solution){
        boolean isDoable = true;        
        return isDoable;
    }

    public boolean isSecondEchelonCapacitiesRespected(Solution solution) {
        int usedVehicles = solution.getSecondEchelonPermutations().size();
        boolean isDoable = usedVehicles < instance.getSecondEchelonFleet().getVehiclesNumber();
        int iPermutation = 0;
        while (isDoable && iPermutation < usedVehicles){
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
    
    public boolean isSecondEchelonTimeWindowsRespected(Solution solution) {
        int usedVehicles = solution.getSecondEchelonPermutations().size();
        int secondEchelonVehicleCost = instance.getSecondEchelonFleet().getVehiclesCost();
        boolean isDoable = usedVehicles < instance.getSecondEchelonFleet().getVehiclesNumber();
        int iPermutation = 0;
        while (isDoable && iPermutation < usedVehicles){
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
    
    //Accesseurs

    public Instance getInstance() {
        return instance;
    }
    
    public Solution getSolution() {
        return solution;
    }

    public void setSolution(Solution solution) {
        this.solution = solution;
    }
}
