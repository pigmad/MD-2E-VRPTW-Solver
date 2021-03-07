package main;

import model.Instance;
import model.Solution;
import solver.Solver;
import solver.ClarkeWright;
import utils.FileManager;
import utils.FileManagerException;

import java.io.File;   
import java.io.IOException;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JFileChooser;

/**
 *
 * @author LASTENNET Dorian
 */
public class MainClarkeAndWright {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, FileManagerException {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Merci de choisir un fichier d'instance à lire");
        fileChooser.setCurrentDirectory(new File(System.getProperty("user.dir")));
        FileNameExtensionFilter filter = new FileNameExtensionFilter("TEXT FILES","txt", "text");
        fileChooser.setFileFilter(filter);
        int result = fileChooser.showOpenDialog(null);
        //int result = JFileChooser.APPROVE_OPTION;
        if (result == JFileChooser.APPROVE_OPTION) {
            String instanceFile = fileChooser.getSelectedFile().getAbsolutePath();
            
            //String instanceFile = "C:\\Users\\laste\\OneDrive\\Bureau\\DI5\\PRD\\Dev\\MD-2E-VRPTW-Solver\\Ca1-2,3,15.txt";
            //String instanceFile = "C:\\Users\\laste\\OneDrive\\Bureau\\DI5\\PRD\\Dev\\MD-2E-VRPTW-Solver\\Ca1-6,4,15.txt";
            //String instanceFile = "C:\\Users\\laste\\OneDrive\\Bureau\\DI5\\PRD\\Dev\\MD-2E-VRPTW-Solver\\Ca2-2,3,15.txt";
            //String instanceFile = "C:\\Users\\laste\\OneDrive\\Bureau\\DI5\\PRD\\Dev\\MD-2E-VRPTW-Solver\\Ca2-3,5,15.txt";
            //String instanceFile = "C:\\Users\\laste\\OneDrive\\Bureau\\DI5\\PRD\\Dev\\MD-2E-VRPTW-Solver\\Ca2-6,4,15.txt";
            System.out.println("Vous avez sélectionné le fichier : " + instanceFile);
            
            //lecture fichier et construction de l'instance
            FileManager fm = new FileManager(instanceFile);
            Instance instance = fm.readInstance();
            System.out.println(instance.toString());

            //instantiation d'un solveur
            Solver solver = new Solver(instance);
            ClarkeWright clarkWright = new ClarkeWright();
            solver.setHeuristic(clarkWright);
            
            //récuperation de la solution
            solver.solveInstance();
            Solution solution = solver.getSolution();
            System.out.println(solution);

            System.out.println("Fonction Obj : "+solver.evaluateSolution(solution));
            System.out.println("Contraintes temporelles respectées : "+(solver.isSecondEchelonTimeWindowsRespected(solution)?"oui":"non"));
            System.out.println("Contraintes capacités respectées : "+(solver.isSecondEchelonCapacitiesRespected(solution)?"oui":"non"));
            System.out.println("Contraintes véhicules respectées : "+(solver.isSecondEchelonVehiclesNumberRespected(solution)?"oui":"non"));
            
            fm.writeSolution(solver,instanceFile+"-result.txt");
        }    
    }
}
