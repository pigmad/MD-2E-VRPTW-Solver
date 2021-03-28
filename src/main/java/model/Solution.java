package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe qui contient la solution du problème. <br>
 * Une solution est un ensemble de permutations. <br>
 * Une permutation est un ensemble d'affectation. 
 *
 * @see Assignment
 * @author LASTENNET Dorian
 */
public class Solution {

    private List<List<Assignment>> firstEchelonPermutations;
    private List<List<Assignment>> secondEchelonPermutations;

    public Solution() {
        this.firstEchelonPermutations = new ArrayList<>();
        this.secondEchelonPermutations = new ArrayList<>();
    }

    public Solution(List<List<Assignment>> firstEchelonPermutation, List<List<Assignment>> secondEchelonPermutation) {
        this.firstEchelonPermutations = firstEchelonPermutation;
        this.secondEchelonPermutations = secondEchelonPermutation;
    }

    //Accesseurs
    
    public List<List<Assignment>> getFirstEchelonPermutations() {
        return firstEchelonPermutations;
    }

    public void setFirstEchelonPermutations(List<List<Assignment>> firstEchelonPermutations) {
        this.firstEchelonPermutations = firstEchelonPermutations;
    }

    public List<List<Assignment>> getSecondEchelonPermutations() {
        return secondEchelonPermutations;
    }

    public void setSecondEchelonPermutations(List<List<Assignment>> secondEchelonPermutations) {
        this.secondEchelonPermutations = secondEchelonPermutations;
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
        for (List<Assignment> route : firstEchelonPermutations) {
            sb.append(route.toString()).append(',');
            sb.append(System.getProperty(propertySeparator));
        }
        sb.append(']').append(System.getProperty(propertySeparator));
        sb.append(", Permutations du second niveau =[");
        for (List<Assignment> route : secondEchelonPermutations) {
            sb.append(route.toString()).append(',');
            sb.append(System.getProperty(propertySeparator));
        }
        sb.append(']').append('}');
        return sb.toString();
    }

}
