package utils;

import model.Instance;
import model.Solution;
import model.AssignmentSecond;

import solver.Solver;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author LASTENNET Dorian
 */
class FileManagerTest {

    public Solution stubSolution(Instance instance) {
        List<List<AssignmentSecond>> permutations = new ArrayList<>();
        List<AssignmentSecond> permutation = new ArrayList<>();
        permutation.add(new AssignmentSecond(instance.getCustomers().get(0), instance.getSatellites().get(0)));
        permutation.add(new AssignmentSecond(instance.getCustomers().get(0)));
        permutation.add(new AssignmentSecond(instance.getCustomers().get(1), instance.getSatellites().get(1)));
        permutation.add(new AssignmentSecond(instance.getCustomers().get(2), instance.getSatellites().get(1)));
        permutation.add(new AssignmentSecond(instance.getCustomers().get(1)));
        permutation.add(new AssignmentSecond(instance.getCustomers().get(2)));
        permutations.add(permutation);
        return new Solution(new ArrayList<>(), permutations);
    }

    @Test
    void TestFileValid() throws FileManagerException, IOException {
        FileManager fm = new FileManager("src/test/java/Instances/testInstanceValid-2,2,3.txt");
        Instance instance = fm.readInstance();
        assertEquals(2, instance.getDepots().size());
        assertEquals(2, instance.getSatellites().size());
        assertEquals(3, instance.getCustomers().size());
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceInvalidFilename.txt', 'Nom de fichier non conforme.'",
        "'src/test/java/Instances/testInstanceInvalidFileEntityNumber-2,2,3.txt', 'Nombre entités invalide.'",
        "'src/test/java/Instances/testInstanceInvalidFileCustomerValues-2,2,3.txt', 'Mauvais nombre de paramètres pour les clients.'",
        "'src/test/java/Instances/testInstanceInvalidFileSatelliteValues-2,2,3.txt', 'Mauvais nombre de paramètres pour les satellites.'",
        "'src/test/java/Instances/testInstanceInvalidFileDepotValues-2,2,3.txt', 'Mauvais nombre de paramètres pour les dépots.'",
        "'src/test/java/Instances/testInstanceInvalidFileVehiclesValues-2,2,3.txt', 'Mauvais nombre de paramètres pour les véhicules.'",
        "'src/test/java/Instances/testNonExistingFile-2,2,3.txt', 'Fichier introuvable.'"
    })
    void TestFileInvalidShouldThrowException(String filename, String expectedMessage) {
        FileManager fm = new FileManager(filename);
        Exception exception = assertThrows(FileManagerException.class, () -> {
            fm.readInstance();
        });
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void TestWriteInstanceSolutionToFile() throws FileManagerException, IOException {
        String filename = "src/test/java/Instances/testInstanceValid-2,2,3.txt";
        FileManager fm = new FileManager(filename);
        Instance instance = fm.readInstance();
        Solution solution = stubSolution(instance);
        Solver solver = new Solver(instance, solution, true);
        fm.writeSolution(solver, filename + "-result.txt");
        File file = new File(filename + "-result.txt");
        assertTrue(file.exists());
        file.delete();
    }
    
    @Test
    void TestWriteAllInstancesSolutionsToFile() throws FileManagerException, IOException {
        String filename = "src/test/java/Instances/testInstanceValid-2,2,3.txt";
        FileManager fm = new FileManager(filename);
        Instance instance = fm.readInstance();
        Solution solution = stubSolution(instance);
        Solver solver = new Solver(instance, solution, true);
        fm.writeGlobalSolution("testInstanceValide-2,3,3.txt", 45, solver, filename + "-globalSolution.txt");
        File file = new File(filename + "-globalSolution.txt");
        assertTrue(file.exists());
        file.delete();
    }
}
