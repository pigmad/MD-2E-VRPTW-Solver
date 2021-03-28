package model;

/**
 * Classe représentant un client. <br>
 * Une instance de cette classe est un sommet du graphe. <br>
 * Hérite de la classe site.
 * 
 * @see model.Site
 * @author LASTENNET Dorian
 */
public class Customer extends Site {

    private final int timeWindowStart; //date de début de la fenêtre de livraison du client
    private final int timeWindowEnd; //date de fin de la fenêtre de livraison du client
    private final int demandSize; //demande de livraison du client

    public Customer(int siteID, int globalSiteID, int xCoordinate, int yCoordinate, int serviceTime, int timeWindowStart, int timeWindowEnd, int demandSize) {
        super(siteID, globalSiteID, xCoordinate, yCoordinate, serviceTime);
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.demandSize = demandSize;
    }

    //Accesseurs
    public int getTimeWindowStart() {
        return timeWindowStart;
    }

    public int getTimeWindowEnd() {
        return timeWindowEnd;
    }

    public int getDemandSize() {
        return demandSize;
    }

    /**
     * Représentation de l'objet en texte.
     * @return texte
     */
    @Override
    public String toString() {
        return "C" + this.getSiteID();
    }
}
