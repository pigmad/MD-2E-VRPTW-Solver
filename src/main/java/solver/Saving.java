package solver;

import model.Customer;
import model.Satellite;

/**
 * Classe utilisée pour gérer les savingValue 
 On veut conserver les informations des clients et 
 des satellites impliqués dans la calcul du savings
 */
/**
 * Classe utilisée pour gérer les savings. <br>
 * On veut conserver les informations des clients et des satellites impliqués dans la calcul du savings.
 * 
 * @see solver.ClarkeWright
 * @author LASTENNET Dorian
 */
public class Saving implements Comparable {

    private final Satellite iSatellite;
    private final Satellite jSatellite;
    private final Customer iCustomer;
    private final Customer jCustomer;
    private final double savingValue;

    public Saving(Satellite sI, Satellite sJ, Customer i, Customer j, double saving) {
        this.iSatellite = sI;
        this.jSatellite = sJ;
        this.iCustomer = i;
        this.jCustomer = j;
        this.savingValue = saving;
    }

    //Accesseurs
    public Satellite getiSatellite() {
        return iSatellite;
    }

    public Satellite getjSatellite() {
        return jSatellite;
    }

    public Customer getiCustomer() {
        return iCustomer;
    }

    public Customer getjCustomer() {
        return jCustomer;
    }

    public double getSavingValue() {
        return savingValue;
    }

    /**
     * Représentation de l'objet en texte.
     * @return texte
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("savings  =(");
        sb.append("satellite sI : ").append(getiSatellite().toString());
        sb.append(" customer i : ").append(getiCustomer().toString());
        sb.append(" satellite sJ : ").append(getjSatellite().toString());
        sb.append(" customer j : ").append(getjCustomer().toString());
        sb.append(" Saving value : ").append(getSavingValue());
        sb.append(")");
        return sb.toString();
    }

    /**
     * Surchage opérateur comparaison.
     * @param o objet à comparer
     * @return booléen indiquant si les objets sont identiques
     */
    @Override
    public int compareTo(Object o) {
        Saving saving = (Saving) o;
        //comparaison
        return Double.compare(this.getSavingValue(), saving.getSavingValue());
    }
    
}
