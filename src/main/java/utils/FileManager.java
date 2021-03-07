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
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe de gestion de fichier chargée de vérifier les fichiers d'instances
 * données, de récuperer les données et de créer l'instance du problème utilisé
 * pour la résolution
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
     * Fonction de lecture des fichiers d'instances. Cette fonction instantie
     * tous les objets nécessaires nécessaires à la résolution du problème Les
     * fichiers doivent contenir dans leur nom 3 entiers séparés par des ','
     * donnant le nombre de dépots, de satellites et de clients et être dans le
     * format suivant : Pour chaque client: x y dateLivraisonMin
     * dateLivraisonMax demande tempsDeService Pour chaque satellite: x y
     * tempsDeService Pour chaque dépot: x y tempsDeService une ligne pour la
     * flotte de premier niveau contenant : le nombre de véhicules, la capacité
     * des véhicules et le coût d'un véhicule une ligne pour la flotte de second
     * niveau contenant : le nombre de véhicules, la capacité des véhicules et
     * le coût d'un véhicule Toutes les valeurs doivent être entières
     *
     * @return une instance de la classe Instance contenant les valeurs lues
     * dans le fichier
     * @throws FileManagerException si une erreur se produit pendant la lecture
     */
    public Instance readInstance() throws FileManagerException, IOException {
        int depotNumber;
        int satelliteNumber;
        int customerNumber;
        ArrayList<Depot> depots = new ArrayList<>();
        ArrayList<Satellite> satellites = new ArrayList<>();
        ArrayList<Customer> customers = new ArrayList<>();
        Fleet firstEchelonFleet = new Fleet(0, 0, 0, 0);
        Fleet secondEchelonFleet = new Fleet(0, 0, 0, 0);
        //Pattern pour match le nombre de chacun des sites dans le nom du fichier 
        final Pattern FILENAMEPATERN = Pattern.compile("\\d+,\\d+,\\d+");
        //Pattern pour match chaque élément d'une ligne du fichier
        final Pattern LINEPATERN = Pattern.compile("-?\\d+");

        Matcher filenameMatcher = FILENAMEPATERN.matcher(instanceFilename);
        if ((int) filenameMatcher.results().count() != 1) {
            throw new FileManagerException("Nom de fichier non conforme.");
        }
        filenameMatcher.reset(instanceFilename).find();
        String[] match = filenameMatcher.group().split(",");
        depotNumber = Integer.parseInt(match[0]);
        satelliteNumber = Integer.parseInt(match[1]);
        customerNumber = Integer.parseInt(match[2]);
        try ( BufferedReader reader = new BufferedReader(new FileReader(instanceFilename))) {
            int lines = countLines(instanceFilename);
            if (lines != depotNumber + satelliteNumber + customerNumber + 2) {
                throw new FileManagerException("Nombre d'entités invalide.");
            }

            String line;
            int globalID = 0;

            //lecture des clients
            for (int i = 0; i < customerNumber; i++) {
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();

                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int) lineMatcher.results().count() != 6) {
                    throw new FileManagerException("Mauvais nombre de paramètres pour les clients.");
                }
                lineMatcher.reset(line);
                while (lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                Customer customer = new Customer(i + 1, globalID, allMatches.get(0), allMatches.get(1), allMatches.get(5), allMatches.get(2), allMatches.get(3), allMatches.get(4));
                customers.add(customer);
                globalID++;
            }

            //lecture des satellites
            for (int i = 0; i < satelliteNumber; i++) {
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();

                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int) lineMatcher.results().count() != 3) {
                    throw new FileManagerException("Mauvais nombre de paramètres pour les satellites.");
                }
                lineMatcher.reset(line);
                while (lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                Satellite satellite = new Satellite(i + 1, globalID, allMatches.get(0), allMatches.get(1), allMatches.get(2));
                satellites.add(satellite);
                globalID++;
            }

            //lecture des dépôts
            for (int i = 0; i < depotNumber; i++) {
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();

                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int) lineMatcher.results().count() != 3) {
                    throw new FileManagerException("Mauvais nombre de paramètres pour les dépôts.");
                }
                lineMatcher.reset(line);
                while (lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                Depot depot = new Depot(i + 1, globalID, allMatches.get(0), allMatches.get(1), allMatches.get(2));
                depots.add(depot);
                globalID++;
            }

            for (int i = 1; i < 3; i++) {
                ArrayList<Integer> allMatches = new ArrayList<>();
                line = reader.readLine();

                Matcher lineMatcher = LINEPATERN.matcher(line);
                if ((int) lineMatcher.results().count() != 3) {
                    throw new FileManagerException("Mauvais nombre de paramètres pour les véhicules.");
                }
                lineMatcher.reset(line);
                while (lineMatcher.find()) {
                    allMatches.add(Integer.parseInt(lineMatcher.group()));
                }
                if (i == 1) {
                    firstEchelonFleet = new Fleet(1, allMatches.get(0), allMatches.get(1), allMatches.get(2));
                } else {
                    secondEchelonFleet = new Fleet(2, allMatches.get(0), allMatches.get(1), allMatches.get(2));
                }
            }
        } catch (FileNotFoundException e) {
            throw new FileManagerException("Fichier introuvable.", e);
        } catch (IOException e) {
            throw new FileManagerException("Erreur de lecture.", e);
        }
        return new Instance(depots, satellites, customers, firstEchelonFleet, secondEchelonFleet);
    }

    /**
     * Fonction d'écriture d'une solution
     *
     * @param solution la solution à écrire
     * @throws FileManagerException si une erreur d'écriture se produit
     */
    public void writeSolution(Solver solver, String filename) throws FileManagerException {
        try ( PrintWriter writer = new PrintWriter(new File(filename))) {
            Solution solution = solver.getSolution();
            writer.println("Fonction Obj : " + solver.evaluateSolution(solution));
            writer.println("Contraintes temporelles respectées : " + (solver.isSecondEchelonTimeWindowsRespected(solution) ? "oui" : "non"));
            writer.println("Contraintes capacités respectées : " + (solver.isSecondEchelonCapacitiesRespected(solution) ? "oui" : "non"));
            writer.println(solution);
        } catch (IOException e) {
            throw new FileManagerException("Erreur d'écriture.", e);
        }
    }

    /**
     * Fonction qui compte le nombre de lignes non vides (caractères espaces
     * inclus) dans le fichier en paramètre
     *
     * @param fileName fichier
     * @return nombre de ligne
     * @throws FileNotFoundException
     * @throws IOException
     */
    public int countLines(String fileName) throws FileNotFoundException, IOException {
        int lines = 0;
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        String line = reader.readLine();
        while (line != null) {
            if (!line.isBlank()) {
                lines++;
            }
            line = reader.readLine();
        }
        return lines;
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
