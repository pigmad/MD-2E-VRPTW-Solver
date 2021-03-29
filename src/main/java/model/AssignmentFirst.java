package model;

import java.util.Objects;
import java.util.Optional;

/**
 * Classe représentant l'affectation ou le séquencement (si aucun depot
 * n'est défini) des depots et des satellites au sein d'une permutation pour le premier niveau. <br>
 * Une permutation est un ensemble d'instances de cette classe et donne la route
 * d'un véhicule.
 *
 * @author LASTENNET Dorian
 */
public class AssignmentFirst {

    private Satellite satellite; //satellite de l'affectation
    private Optional<Depot> depot; //depot où le satellite est affecté

    /**
     * Constructeur pour le séquencement.
     *
     * @param satellite le satellite
     */
    public AssignmentFirst(Satellite satellite) {
        this.satellite = satellite;
        this.depot = Optional.empty();
    }

    /**
     * Constructeur pour l'affectation.
     *
     * @param satellite le satellite
     * @param depot le depot ou le client est affecté
     */
    public AssignmentFirst(Satellite satellite, Depot depot) {
        this.satellite = satellite;
        this.depot = Optional.of(depot);
    }

    //Accesseurs
    public Satellite getSatellite() {
        return satellite;
    }

    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }

    public Optional<Depot> getDepot() {
        return depot;
    }

    public void setDepot(Optional<Depot> depot) {
        this.depot = depot;
    }
    

    /**
     * Représentation de l'objet en texte.
     *
     * @return texte
     */
    @Override
    public String toString() {
        return depot.isEmpty() ? satellite.toString() : depot.get().toString() + "_" + satellite.toString();
    }

    /**
     * Surchage opérateur d'égalité.
     *
     * @param o objet à comparer
     * @return booléen indiquant si les objets sont identiques
     */
    @Override
    public boolean equals(Object o) {

        // l'objet est comparé avec lui même 
        if (o == this) {
            return true;
        }

        // l'objet comparé n'est pas de la même classe
        if (!(o instanceof AssignmentFirst)) {
            return false;
        }

        AssignmentFirst a = (AssignmentFirst) o;

        //affectation
        if (this.depot.isPresent() && a.depot.isPresent()) {
            return this.depot.get().equals(a.depot.get()) && this.satellite.equals(a.satellite);
        }
        //séquencement
        else if (this.depot.isEmpty() && a.depot.isEmpty()) {
            return this.satellite.equals(a.satellite);
        } 
        else {
            return false;
        }
    }

    /**
     * Surchage obligatoire si surchage de equals
     * @return hash de l'objet
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.satellite);
        hash = 79 * hash + Objects.hashCode(this.depot);
        return hash;
    }

    
    
}
