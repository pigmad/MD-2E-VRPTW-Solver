package main;

import model.Instance;
import model.Solution;
import solver.Solver;
import solver.ClarkeWrightFirst;
import solver.ClarkeWrightSecond;
import utils.FileManager;
import utils.FileManagerException;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

/**
 * Classe principale.
 * 
 * @author LASTENNET Dorian
 */
public class Main {

    /**
     * Programme principal. <br>
     * Projet console.
     * 
     * @param args paramètres de la ligne de commandes (non utilisé)
     */
    public static void main(String[] args)  {
        JFileChooser fileChooser = new JFileChooser();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy_HH-mm");
        String globalTimestamp = sdf.format(new Timestamp(System.currentTimeMillis()));
        fileChooser.setDialogTitle("Merci de choisir un fichier d'instance à lire");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        //filtre sur les fichiers texte
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);
        //possibilité de selectionner plusieurs fichiers d'instances
        fileChooser.setMultiSelectionEnabled(true);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File[] files = fileChooser.getSelectedFiles();

            for (File file : files) {
                try {
                    String instanceFile = file.getAbsolutePath();
                    System.out.println("Vous avez sélectionné le fichier : " + instanceFile);
                    
                    //lecture fichier et construction de l'instance
                    FileManager fm = new FileManager(instanceFile);
                    Instance instance = fm.readInstance();
                    System.out.println(instance.toString());
                    
                    //instantiation d'un solveur
                    Solver solver = new Solver(instance, true);
                    //heuristique résolution second échelon
                    ClarkeWrightSecond secondCK = new ClarkeWrightSecond(solver);
                    //heuristique résolution premier échelon
                    ClarkeWrightFirst firstCK = new ClarkeWrightFirst(solver);
                    
                    //timer d'exécution
                    long startTime = System.nanoTime();
                    //résolution du second échelon
                    solver.solveInstance(secondCK);
                    //on affecte les demandes aux satellites
                    solver.setSolutionSatellitesDemand();
                    solver.solveInstance(firstCK);
                    Solution solution = solver.getSolution();
                    System.out.println(solution);
                    //fin timer
                    long elapsedTime = (System.nanoTime() - startTime) / 1000000;
                    
                    System.out.println("Temps écoulé : " + elapsedTime + "ms");
                    System.out.println("Fonction Obj : " + solver.evaluateSolution(solution));
                    System.out.println("Contraintes temporelles respectées : " + (solver.isSecondEchelonTimeWindowsRespected(solution) ? "oui" : "non"));
                    System.out.println("Contraintes capacités respectées : " + (solver.isSecondEchelonCapacitiesRespected(solution) ? "oui" : "non"));
                    System.out.println("Contraintes véhicules respectées : " + (solver.isSecondEchelonVehiclesNumberRespected(solution) ? "oui" : "non"));
                    System.out.println("Contraintes clients livrées : " + (solver.areAllCustomersDelivered(solution) ? "oui" : "non"));
                    
                    //solution détaillée pour le fichier
                    fm.writeSolution(solver, instanceFile + "-result.txt");
                    
                    //solution globale pour l'ensemble des fichiers
                    fm.writeGlobalSolution(file.getName().replace(".txt", ""), elapsedTime, solver, file.getParent() + "\\Results-" + globalTimestamp + ".txt");
                } catch (FileManagerException ex) {
                    System.err.println(ex.toString());
                } 
            }
        }
    }
}
