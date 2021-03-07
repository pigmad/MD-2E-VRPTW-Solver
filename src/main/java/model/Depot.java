package model;

/**
 * Classe représentant un dépôt Une instance de cette classe est un sommet du
 * graphe Hérite de @see model.Site
 *
 * @author LASTENNET Dorian
 */
public class Depot extends Site {

    public Depot(int siteID, int globalSiteID, int xCoordinate, int yCoordinate, int serviceTime) {
        super(siteID, globalSiteID, xCoordinate, yCoordinate, serviceTime);
    }

    @Override
    public String toString() {
        return "D" + this.getSiteID();
    }
}
