package model;

/**
 * Classe représentant une flotte de véhicules circulant sur les arcs du graphe.
 *
 * @author LASTENNET Dorian
 */
public class Fleet {
    private final int id; //utile seulement pour l'affichage
    private final int vehiclesNumber;
    private final int vehiclesCapacity;
    private final int vehiclesCost;

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

    /**
     * Représentation de l'objet en texte.
     * @return texte
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("F").append(id);
        sb.append("=[Nombre de véhicules=").append(vehiclesNumber);
        sb.append(", Capacité du véhicule=").append(vehiclesCapacity);
        sb.append(", Cout du véhicule=").append(vehiclesCost);
        sb.append("]");
        return sb.toString();
    }
}
