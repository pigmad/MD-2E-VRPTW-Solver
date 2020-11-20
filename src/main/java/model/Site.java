package model;

import static java.lang.Math.pow; 
import static java.lang.Math.sqrt; 

/**
 * Classe abstraite représentant toutes les entités sommets (les sites) du problème (dépot, satellite, client)
 * Permet mutualiser des opérations entre les sommets, comme le calcul de la distance euclienne entre deux points
 * @author LASTENNET Dorian
 */
public abstract class Site {
    private final int id; //identifiant du site
    private final int xCoordinate; //coordonnée en x
    private final int yCoordinate; //coordonnée en y
    private final int serviceTime; //cout de manutention pour charger/déchargement une cargaison
    
    public Site(int id,int xCoordinate, int yCoordinate, int serviceTime){
        this.id = id;
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.serviceTime = serviceTime;
    }
    
    /**
     * Calcule la distance euclidenne entre deux sommets
     * @param site une implémentation de la classe Site pour le calcul de la distance
     * @return double : la distance euclidienne entre les deux sommets
     */
    public final double computeDistance(Site site){
        return sqrt(pow((double)xCoordinate - site.getxCoordinate(),2) + pow((double)yCoordinate - site.getyCoordinate(),2));
    }
    
    //Accesseurs
    
    public int getId() {
        return id;
    }
    
    public int getxCoordinate() {
        return xCoordinate;
    }

    public int getyCoordinate() {
        return yCoordinate;
    }

    public int getServiceTime() {
        return serviceTime;
    }
}
