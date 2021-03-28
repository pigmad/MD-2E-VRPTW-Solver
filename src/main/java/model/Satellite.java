package model;

/**
 * Classe représentant un satellite. <br>
 * Une instance de cette classe est un sommet du graphe. <br>
 * Hérite de la classe site. <br>
 * 
 * @see model.Site
 * @author LASTENNET Dorian
 */
public class Satellite extends Site {

    public Satellite(int siteID, int globalSiteID, int xCoordinate, int yCoordinate, int serviceTime) {
        super(siteID, globalSiteID, xCoordinate, yCoordinate, serviceTime);
    }

    /**
     * Représentation de l'objet en texte.
     * @return texte
     */
    @Override
    public String toString() {
        return "S" + this.getSiteID();
    }

}
