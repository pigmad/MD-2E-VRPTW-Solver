package model;

/**
 * Classe représentant l'affectation ou le séquencement (si aucun satellite
 * n'est défini) des satellites et des clients au sein d'une permutation. Une
 * permutation est un ensemble d'instances de cette classe
 *
 * @author LASTENNET Dorian
 */
public class Assignment {

    private Customer customer;
    private Satellite satellite;

    public Assignment(Customer customer) {
        this.customer = customer;
        this.satellite = null;
    }

    public Assignment(Customer customer, Satellite satellite) {
        this.customer = customer;
        this.satellite = satellite;
    }

    //Accesseurs
    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Satellite getSatellite() {
        return satellite;
    }

    public void setSatellite(Satellite satellite) {
        this.satellite = satellite;
    }

    //Surchage 
    @Override
    public String toString() {
        return satellite == null ? customer.toString() : satellite.toString() + "_" + customer.toString();
    }
}
