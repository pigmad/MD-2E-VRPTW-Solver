package model;

/**
 * Classe représentant une flotte de véhicules circulant sur les arcs du graphe
 *
 * @author LASTENNET Dorian
 */
public class Fleet {

    private int id; //identifiant de la flotte
    private int vehiclesNumber; //Le nombre maximal de véhicules utilisable dans la flotte
    private int vehiclesCapacity; //La capacité de chaque véhicule 
    private int vehiclesCost; //Le coût d'utilisation d'un véhicule de la flotte

    public Fleet(int id, int vehiclesNumber, int vehiclesCapacity, int vehiclesCost) {
        this.id = id;
        this.vehiclesNumber = vehiclesNumber;
        this.vehiclesCapacity = vehiclesCapacity;
        this.vehiclesCost = vehiclesCost;
    }

    //Accesseurs
    public int getId() {
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

    public void setId(int id) {
        this.id = id;
    }

    public void setVehiclesNumber(int vehiclesNumber) {
        this.vehiclesNumber = vehiclesNumber;
    }

    public void setVehiclesCapacity(int vehiclesCapacity) {
        this.vehiclesCapacity = vehiclesCapacity;
    }

    public void setVehiclesCost(int vehiclesCost) {
        this.vehiclesCost = vehiclesCost;
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
