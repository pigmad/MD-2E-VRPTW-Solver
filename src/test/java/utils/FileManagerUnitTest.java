package utils;

import model.Instance;
import model.Solution;
import model.Assignment;

import solver.Solver;

import java.io.IOException;
import java.io.File;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author LASTENNET Dorian
 */
class FileManagerUnitTest {

    public Solution stubSolution(Instance instance) {
        //Create a solution
        ArrayList<ArrayList<Assignment>> permutations = new ArrayList<>();
        ArrayList<Assignment> permutation = new ArrayList<>();
        permutation.add(new Assignment(instance.getCustomers().get(0), instance.getSatellites().get(0)));
        permutation.add(new Assignment(instance.getCustomers().get(0)));
        permutation.add(new Assignment(instance.getCustomers().get(1), instance.getSatellites().get(1)));
        permutation.add(new Assignment(instance.getCustomers().get(2), instance.getSatellites().get(1)));
        permutation.add(new Assignment(instance.getCustomers().get(1)));
        permutation.add(new Assignment(instance.getCustomers().get(2)));
        permutations.add(permutation);
        return new Solution(null, permutations);
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
        "'src/test/java/Instances/testInstanceInvalidFilename.txt'",
        "'src/test/java/Instances/testInstanceInvalidEntityNumber-2,3,3.txt'",
        "'src/test/java/Instances/testInstanceInvalidFileCustomerValues-2,2,3.txt'",
        "'src/test/java/Instances/testInstanceInvalidFileSatellitesValues-2,2,3.txt'",
        "'src/test/java/Instances/testInstanceInvalidFileDepotValues-2,2,3.txt'"
    })
    void TestFileInvalidShouldThrowException(String filename) {
        FileManager fm = new FileManager(filename);
        assertThrows(FileManagerException.class, () -> {
            fm.readInstance();
        });
    }

    void TestWriteToFile() throws FileManagerException, IOException {
        String filename = "src/test/java/Instances/testInstanceValid-2,2,3.txt";
        FileManager fm = new FileManager(filename);
        Instance instance = fm.readInstance();
        Solution solution = stubSolution(instance);
        Solver solver = new Solver(null, solution);
        fm.writeSolution(solver, filename);
        File file = new File(filename);
        assertTrue(file.exists());
        file.delete();
    }
}
