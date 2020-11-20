package model;

/**
 * Classe représentant un satellite
 * Une instance de cette classe est un sommet du graphe
 * @author LASTENNET Dorian
 */
public class Satellite extends Site {
    public Satellite(int id, int xCoordinate, int yCoordinate, int serviceTime){
        super(id, xCoordinate, yCoordinate, serviceTime);
    }

    //Surchage 
    
    @Override
    public String toString() {
        return "S" + this.getId();
    }
    
}
