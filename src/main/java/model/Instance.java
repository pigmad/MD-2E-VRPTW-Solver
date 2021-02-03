package model;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Classe contenant toutes les données du problème à résoudre
 * Classe instancié par la classe de lecture de fichier @see utils.FileManager
 * @author LASTENNET Dorian
 */
public class Instance {
    private final List<Depot> depots;
    private final List<Satellite> satellites;
    private final List<Customer> customers;
    private final Fleet firstEchelonFleet;
    private final Fleet secondEchelonFleet;
    private final List<List<Long>> distanceMatrix;

    public Instance(List<Depot> depots, List<Satellite> satellites, List<Customer> customers, Fleet firstEchelonFleet, Fleet secondEchlonFleet) {
        this.depots = depots;
        this.satellites = satellites;
        this.customers = customers;
        this.firstEchelonFleet = firstEchelonFleet;
        this.secondEchelonFleet = secondEchlonFleet;
        this.distanceMatrix = new ArrayList<>();
        
        //Calcul de la matrice de distance
        List<Site> sites = Stream.of(customers, satellites, depots)
                                .flatMap(x -> x.stream())
                                .collect(Collectors.toList());
        for (Site siteLigne : sites) {
            ArrayList<Long> distances = new ArrayList<>();
            for(Site siteColonne : sites){
                distances.add(siteLigne.computeDistance(siteColonne));
            }
            distanceMatrix.add(distances);
        }
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

    public List<List<Long>> getDistanceMatrix() {
        return distanceMatrix;
    }

    public double getDistance(Site startSite, Site arrivalSite){
        return distanceMatrix.get(startSite.getGlobalSiteID()).get(arrivalSite.getGlobalSiteID());
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
