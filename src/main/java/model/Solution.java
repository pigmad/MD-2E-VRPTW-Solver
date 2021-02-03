package model;

import java.util.List;
import java.util.ArrayList;


/**
 * Classe qui contient la solution du problème
 * Une solution est un ensemble de permutation
 * Une permutation est un ensemble d'affectation : @see model.Assignment
 * @author LASTENNET Dorian
 */
public class Solution {
    private List<ArrayList<Assignment>> firstEchelonPermutations;
    private List<ArrayList<Assignment>> secondEchelonPermutations;

    public Solution(List<ArrayList<Assignment>> firstEchelonPermutation, List<ArrayList<Assignment>> secondEchelonPermutation) {
        this.firstEchelonPermutations = firstEchelonPermutation;
        this.secondEchelonPermutations = secondEchelonPermutation;
    }
    
    //Accesseurs

    public List<ArrayList<Assignment>> getFirstEchelonPermutations() {
        return firstEchelonPermutations;
    }

    public void setFirstEchelonPermutations(List<ArrayList<Assignment>> firstEchelonPermutations) {
        this.firstEchelonPermutations = firstEchelonPermutations;
    }
    
    public List<ArrayList<Assignment>> getSecondEchelonPermutations() {
        return secondEchelonPermutations;
    }

    public void setSecondEchelonPermutations(List<ArrayList<Assignment>> secondEchelonPermutations) {
        this.secondEchelonPermutations = secondEchelonPermutations;
    }
    
    //Surchage 

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Solution={");
        sb.append("Permutations du premier niveau =").append(firstEchelonPermutations);
        sb.append(", Permutations du second niveau =").append(secondEchelonPermutations);
        sb.append('}');
        return sb.toString();
    }
    
    
    
}
