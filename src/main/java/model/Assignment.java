package model;

import java.util.Objects;
import java.util.Optional;

/**
 * Classe représentant l'affectation ou le séquencement (si aucun satellite
 * n'est défini) des satellites et des clients au sein d'une permutation. <br>
 * Une permutation est un ensemble d'instances de cette classe et donne la route
 * d'un véhicule.
 *
 * @author LASTENNET Dorian
 */
public class Assignment {

    private Customer customer; //client de l'affectation
    private Optional<Satellite> satellite; //satellite de l'affectation

    /**
     * Constructeur pour le séquencement.
     *
     * @param customer le client
     */
    public Assignment(Customer customer) {
        this.customer = customer;
        this.satellite = Optional.empty();
    }

    /**
     * Constructeur pour l'affectation.
     *
     * @param customer le client
     * @param satellite le satellite ou le client est affecté
     */
    public Assignment(Customer customer, Satellite satellite) {
        this.customer = customer;
        this.satellite = Optional.of(satellite);
    }

    //Accesseurs
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Optional<Satellite> getSatellite() {
        return satellite;
    }

    public void setSatellite(Satellite satellite) {
        this.satellite = Optional.of(satellite);
    }

    /**
     * Représentation de l'objet en texte.
     *
     * @return texte
     */
    @Override
    public String toString() {
        return satellite.isEmpty() ? customer.toString() : satellite.get().toString() + "_" + customer.toString();
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
        if (!(o instanceof Assignment)) {
            return false;
        }

        Assignment a = (Assignment) o;

        //affectation
        if (this.satellite.isPresent() && a.satellite.isPresent()) {
            return this.satellite.get().equals(a.satellite.get()) && this.customer.equals(a.customer);
        }
        //séquencement
        else if (this.satellite.isEmpty() && a.satellite.isEmpty()) {
            return this.customer.equals(a.customer);
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
        hash = 79 * hash + Objects.hashCode(this.customer);
        hash = 79 * hash + Objects.hashCode(this.satellite);
        return hash;
    }
}
