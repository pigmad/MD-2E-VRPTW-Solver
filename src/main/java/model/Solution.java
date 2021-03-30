package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Classe qui contient la solution du problème. <br>
 * Une solution est un ensemble de permutations. <br>
 * Une permutation est un ensemble d'affectation. 
 *
 * @see AssignmentSecond
 * @author LASTENNET Dorian
 */
public class Solution {

    private List<List<AssignmentFirst>> firstEchelonPermutations;
    private List<List<AssignmentSecond>> secondEchelonPermutations;
    //contient la demande des satellites une fois le second échelon résolu indexé par le siteID du satellite 
    private List<Integer> secondEchelonCapacity; 

    public Solution() {
        this.firstEchelonPermutations = new ArrayList<>();
        this.secondEchelonPermutations = new ArrayList<>();
        this.secondEchelonCapacity  = new ArrayList<>();
    }

    public Solution(List<List<AssignmentFirst>> firstEchelonPermutation, List<List<AssignmentSecond>> secondEchelonPermutation) {
        this.firstEchelonPermutations = firstEchelonPermutation;
        this.secondEchelonPermutations = secondEchelonPermutation;
        this.secondEchelonCapacity  = new ArrayList<>();
    }

    //Accesseurs
    
    public List<List<AssignmentFirst>> getFirstEchelonPermutations() {
        return firstEchelonPermutations;
    }

    public void setFirstEchelonPermutations(List<List<AssignmentFirst>> firstEchelonPermutations) {
        this.firstEchelonPermutations = firstEchelonPermutations;
    }

    public List<List<AssignmentSecond>> getSecondEchelonPermutations() {
        return secondEchelonPermutations;
    }

    public void setSecondEchelonPermutations(List<List<AssignmentSecond>> secondEchelonPermutations) {
        this.secondEchelonPermutations = secondEchelonPermutations;
    }

    public List<Integer> getSecondEchelonCapacity() {
        return secondEchelonCapacity;
    }

    public void setSecondEchelonCapacity(List<Integer> secondEchelonCapacity) {
        this.secondEchelonCapacity = secondEchelonCapacity;
    }
    
    /**
     * Calcule la somme des demandes clients par satellite
     * après qu'une solution soit trouvée pour le second niveau
     * @param instance instance du problème
     */
    public void setSolutionSatellitesDemand(Instance instance){
        List<Integer> list = new ArrayList<>(Collections.nCopies(instance.getSatellites().size(), 0));
        for (List<AssignmentSecond> route : secondEchelonPermutations){
            for(AssignmentSecond assign : route){
                Optional<Satellite> assignSat = assign.getSatellite();
                if(assignSat.isPresent()){
                    list.set(assignSat.get().getSiteID()-1, list.get(assignSat.get().getSiteID()-1)+assign.getCustomer().getDemandSize());
                }
            }
        }
        secondEchelonCapacity = list;
    }

    /**
     * Représentation de l'objet en texte.
     * @return texte
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String propertySeparator = "line.separator";
        sb.append("Solution={").append(System.getProperty(propertySeparator));
        sb.append("Permutations du premier niveau =[").append(System.getProperty(propertySeparator));
        for (List<AssignmentFirst> route : firstEchelonPermutations) {
            sb.append(route.toString()).append(',');
            sb.append(System.getProperty(propertySeparator));
        }
        sb.append(']').append(System.getProperty(propertySeparator));
        sb.append(", Permutations du second niveau =[");
        for (List<AssignmentSecond> route : secondEchelonPermutations) {
            sb.append(route.toString()).append(',');
            sb.append(System.getProperty(propertySeparator));
        }
        sb.append(']').append('}');
        return sb.toString();
    }

}
