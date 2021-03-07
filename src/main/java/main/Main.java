package main;

import model.Instance;
import model.Solution;
import solver.Solver;
import solver.TestSolution;
import utils.FileManager;
import utils.FileManagerException;

import java.io.File;
import java.io.IOException;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

/**
 *
 * @author LASTENNET Dorian
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileManagerException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Merci de choisir un fichier d'instance à lire");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES", "txt", "text");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            String instanceFile = fileChooser.getSelectedFile().getAbsolutePath();
            System.out.println("Vous avez sélectionné le fichier : " + instanceFile);

            //lecture fichier et construction de l'instance
            FileManager fm = new FileManager(instanceFile);
            Instance instance = fm.readInstance();
            System.out.println(instance.toString());

            //instantiation d'un solveur
            Solver solver = new Solver(instance);
            TestSolution solTest = new TestSolution();
            solver.setHeuristic(solTest);

            //récuperation de la solution
            solver.solveInstance();
            Solution solution = solver.getSolution();
            System.out.println(solution);

            System.out.println("Fonction Obj : " + solver.evaluateSolution(solution));
            System.out.println("Contraintes temporelles respectées : " + (solver.isSecondEchelonTimeWindowsRespected(solution) ? "oui" : "non"));
            System.out.println("Contraintes capacités respectées : " + (solver.isSecondEchelonCapacitiesRespected(solution) ? "oui" : "non"));
            System.out.println("Contraintes véhicules respectées : " + (solver.isSecondEchelonVehiclesNumberRespected(solution) ? "oui" : "non"));

            fm.writeSolution(solver, instanceFile + "-result.txt");
        }
    }
}
