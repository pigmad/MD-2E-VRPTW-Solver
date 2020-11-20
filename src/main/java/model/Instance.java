package model;

import java.util.List;
import java.util.ArrayList;


/**
 *
 * @author LASTENNET Dorian
 */
public class Instance {
    private final List<Depot> depots;
    private final List<Satellite> satellites;
    private final List<Customer> customers;
    private final Fleet firstEchelonFleet;
    private final Fleet secondEchelonFleet;

    public Instance(List<Depot> depots, List<Satellite> satellites, List<Customer> customers, Fleet firstEchelonFleet, Fleet secondEchlonFleet) {
        this.depots = depots;
        this.satellites = satellites;
        this.customers = customers;
        this.firstEchelonFleet = firstEchelonFleet;
        this.secondEchelonFleet = secondEchlonFleet;
    }
    
    //Accesseurs
    
    public List<Depot> getDepots() {
        return new ArrayList<>(depots);
    }

    public List<Satellite> getSatellites() {
        return new ArrayList<>(satellites);
    }

    public List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public Fleet getFirstEchelonFleet() {
        return firstEchelonFleet;
    }

    public Fleet getSecondEchelonFleet() {
        return secondEchelonFleet;
    }    
    
    //Surchage
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Instance{depots=").append(depots);
        sb.append(", satellites=").append(satellites);
        sb.append(", clients=").append(customers);
        sb.append(", Flotte premier niveau=").append(firstEchelonFleet);
        sb.append(", Flotte second niveau=").append(secondEchelonFleet);
        sb.append('}');
        return sb.toString();
    }
}
