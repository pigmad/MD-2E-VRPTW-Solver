package model;

/**
 * Classe représentant un dépôt
 * Une instance de cette classe est un sommet du graphe
 * @author LASTENNET Dorian
 */
public class Depot extends Site{
    public Depot(int id, int xCoordinate, int yCoordinate, int serviceTime){
        super(id, xCoordinate, yCoordinate, serviceTime);
    }
    
    @Override
    public String toString() {
        return "D" + this.getId();
    }
}
