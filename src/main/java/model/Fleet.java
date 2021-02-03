package model;

/**
 * Classe représentant une flotte de véhicules circulant sur les arcs du graphe
 * @author LASTENNET Dorian
 */
public class Fleet {
    private final int id; //identifiant de la flotte
    private final int vehiclesNumber; //Le nombre maximal de véhicules utilisable dans la flotte
    private final int vehiclesCapacity; //La capacité de chaque véhicule 
    private final int vehiclesCost; //Le coût d'utilisation d'un véhicule de la flotte

    public Fleet(int id, int vehiclesNumber, int vehiclesCapacity, int vehiclesCost) {
        this.id = id;
        this.vehiclesNumber = vehiclesNumber;
        this.vehiclesCapacity = vehiclesCapacity;
        this.vehiclesCost = vehiclesCost;
    }
       
    //Accesseurs
    
    public int getId(){
        return id;
    }

    public int getVehiclesNumber() {
        return vehiclesNumber;
    }
    
    public int getVehiclesCapacity() {
        return vehiclesCapacity;
    }

    public int getVehiclesCost() {
        return vehiclesCost;
    }
    
    //Surchage

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("D").append(id);
        sb.append("=[vehiclesNumber=").append(vehiclesNumber);
        sb.append(", vehiclesCapacity=").append(vehiclesCapacity);
        sb.append(", vehiclesCost=").append(vehiclesCost);
        sb.append("]");
        return sb.toString();
    }
}
