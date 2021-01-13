package model;

/**
 * Classe représentant un satellite
 * Une instance de cette classe est un sommet du graphe
 * @author LASTENNET Dorian
 */
public class Satellite extends Site {
    public Satellite(int siteID, int globalSiteID, int xCoordinate, int yCoordinate, int serviceTime) {
        super(siteID, globalSiteID, xCoordinate, yCoordinate, serviceTime);
    }
    
    //Surchage 
    
    @Override
    public String toString() {
        return "S" + this.getSiteID();
    }
    
}
