package model;

/**
 * Classe représentant un dépôt. <br>
 * Une instance de cette classe est un sommet du graphe <br>
 * Hérite de la classe site.
 * 
 * @see model.Site
 * @author LASTENNET Dorian
 */
public class Depot extends Site {

    public Depot(int siteID, int globalSiteID, int xCoordinate, int yCoordinate, int serviceTime) {
        super(siteID, globalSiteID, xCoordinate, yCoordinate, serviceTime);
    }

    /**
     * Représentation de l'objet en texte.
     * @return texte
     */
    @Override
    public String toString() {
        return "D" + this.getSiteID();
    }
}
