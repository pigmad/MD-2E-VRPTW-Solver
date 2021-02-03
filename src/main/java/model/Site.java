package model;

import static java.lang.Math.pow; 
import static java.lang.Math.sqrt; 
import static java.lang.Math.round;

/**
 * Classe abstraite représentant toutes les entités sommets (les sites) du problème (dépot, satellite ou client)
 * Permet de mutualiser des opérations entre les sommets, comme le calcul de la distance euclienne entre deux points
 * @author LASTENNET Dorian
 */
public abstract class Site {
    private final int siteID; //identifiant du site parmi les sites de mêmes types
    private final int globalSiteID; //identifiant du site parmi tous les sites
    private final int xCoordinate; //coordonnée en x
    private final int yCoordinate; //coordonnée en y
    private final int serviceTime; //cout de manutention pour charger/déchargement une cargaison
    
    public Site(int siteID, int globalSiteID, int xCoordinate, int yCoordinate, int serviceTime){
        this.siteID = siteID;
        this.globalSiteID = globalSiteID;
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
    
    public int getSiteID() {
        return siteID;
    }

    public int getGlobalSiteID() {
        return globalSiteID;
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
    
    //Surchage
    
    @Override
    public boolean equals(Object o) { 
  
        // l'objet est comparé avec lui même 
        if (o == this) { 
            return true; 
        } 
  
        // l'objet comparé n'est pas de la même classe
        if (!(o instanceof Site)) { 
            return false; 
        } 
          
        Site s = (Site) o; 
          
        // L'id global est unique, on l'utilise comme comparaison 
        return Integer.compare(globalSiteID, s.globalSiteID) == 0;
    } 
}
