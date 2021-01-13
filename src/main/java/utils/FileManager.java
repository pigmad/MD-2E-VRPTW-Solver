package utils;

import model.Depot;
import model.Satellite;
import model.Customer;
import model.Fleet;
import model.Instance;
import model.Solution;
import solver.Solver;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFileChooser;


/**
 * 
 * @author LASTENNET Dorian
 */
public class FileManager {
    private String instanceFilename;
    private String resultFilename;

    public FileManager(String instanceFilename) {
        this.instanceFilename = instanceFilename;
    }
    
    /**
     * Fonction de lecture des fichiers d'instances. 
     * Cette fonction instantie tous les objets nécessaires nécessaires à la résolution du problème
     * Les fichiers doivent contenir dans leur nom 3 entiers 
     * séparés par des ',' donnant le nombre de dépots, de satellites et de clients et être dans le format suivant :
     * Pour chaque client: x    y   dateLivraisonMin    dateLivraisonMax    demande    tempsDeService
     * Pour chaque satellite: x    y   tempsDeService
     * Pour chaque dépot: x    y   tempsDeService
     * Toutes les valeurs doivent être entières
     * @return une instance de la classe Instance contenant les valeurs lues dans le fichier 
     * @throws FileManagerException si une erreur se produit pendant la lecture
     */
    public Instance readInstance() throws FileManagerException, IOException {
        int depotNumber;
        int satelliteNumber;
        int customerNumber;
        ArrayList<Depot> depots = new ArrayList<>();
        ArrayList<Satellite> satellites = new ArrayList<>();
        ArrayList<Customer> customers = new ArrayList<>();
        //instantiation
        Fleet firstEchelonFleet = new Fleet(1,10,200,50);
        Fleet secondEchelonFleet = new Fleet(2,20,50,25);
        //Pattern pour match le nombre de chacun des sites dans le nom du fichier 
        final Pattern FILENAMEPATERN = Pattern.compile("\\d,\\d,\\d");
        //Pattern pour match chaque élément d'une ligne du fichier
        final Pattern LINEPATERN = Pattern.compile("-?\\d+");
        
        Matcher filenameMatcher = FILENAMEPATERN.matcher(instanceFilename);
        if ((int)filenameMatcher.results().count() != 1){
            throw new FileManagerException("Nom de fichier non conforme.");
        }
        filenameMatcher.reset(instanceFilename).find();
        String[] match = filenameMatcher.group().split(",");
        depotNumber = Integer.parseInt(match[0]);
        satelliteNumber = Integer.parseInt(match[1]);
        customerNumber = Integer.parseInt(match[2]);
        try (BufferedReader reader = new BufferedReader(new FileReader(instanceFilename))) {
            FileReader input = new FileReader(instanceFilename);
            String line;
            LineNumberReader linesNumber = new LineNumberReader(input);
            if ((int)linesNumber.lines().count() != depotNumber+satelliteNumber+customerNumber){
                linesNumber.close();
                throw new FileManagerException("Nombre d'entités invalide.");
            }
            linesNumber.close();
            
            int globalID = 0;
            
            for(int i=0; i<customerNumber; i++){
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();

                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int)lineMatcher.results().count() != 6){
                    throw new FileManagerException("Mauvais nombre de paramètres pour les clients.");
                }
                lineMatcher.reset(line);
                while(lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                Customer customer = new Customer(i+1, globalID, allMatches.get(0), allMatches.get(1), allMatches.get(5), allMatches.get(2), allMatches.get(3), allMatches.get(4));
                customers.add(customer);
                globalID++;
            }

            for(int i=0; i<satelliteNumber; i++){
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();
                
                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int)lineMatcher.results().count() != 3){
                    throw new FileManagerException("Mauvais nombre de paramètres pour les satellites.");
                }
                lineMatcher.reset(line);
                while(lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                Satellite satellite = new Satellite(i+1, globalID, allMatches.get(0), allMatches.get(1), allMatches.get(2));
                satellites.add(satellite);
                globalID++;
            }

            for(int i=0; i<depotNumber; i++){
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();
                
                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int)lineMatcher.results().count() != 3){
                    throw new FileManagerException("Mauvais nombre de paramètres pour les dépôts.");
                }
                lineMatcher.reset(line);
                while(lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                Depot depot = new Depot(i+1, globalID, allMatches.get(0), allMatches.get(1), allMatches.get(2));
                depots.add(depot);
                globalID++;
            }
        }
        catch (FileNotFoundException e){
             throw new FileManagerException("Fichier introuvable.", e);
        }
        catch (IOException e) {
            throw new FileManagerException("Erreur de lecture.", e);
        }
        return new Instance(depots, satellites, customers, firstEchelonFleet, secondEchelonFleet);
    }
    
    /**
     * Fonction d'écriture d'une solution
     * @param solution la solution à écrire
     * @throws FileManagerException si une erreur d'écriture se produit
     */
    public void writeSolution(Solver solver, String filename) throws FileManagerException{
        try (PrintWriter writer = new PrintWriter(new File(filename))) {
            Solution solution = solver.getSolution();
            writer.println("Fonction Obj : "+solver.evaluateSolution(solution));
            writer.println("Contraintes temporelles respectées : "+(solver.isSecondEchelonTimeWindowsRespected(solution)?"oui":"non"));
            writer.println("Contraintes capacités respectées : "+(solver.isSecondEchelonCapacitiesRespected(solution)?"oui":"non"));
            writer.println(solution);
        }
        catch (IOException e) {
            throw new FileManagerException("Erreur d'écriture.", e);
        }
    }
    
    //Accesseurs

    public String getInstanceFilename() {
        return instanceFilename;
    }

    public void setInstanceFilename(String instanceFilename) {
        this.instanceFilename = instanceFilename;
    }

    public String getResultFilename() {
        return resultFilename;
    }

    public void setResultFilename(String resultFilename) {
        this.resultFilename = resultFilename;
    }
}
