package solver;

import java.io.IOException;
import model.Assignment;
import model.Instance;
import model.Solution;
import utils.FileManager;
import utils.FileManagerException;
import java.util.ArrayList;
import org.junit.jupiter.api.AfterEach;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 *
 * @author LASTENNET Dorian
 */
class SolverUnitTest {
    private Solver solver;
    private Instance instance;
    private Solution solution;    
    
    void setUpClass(String instanceTestFile) throws FileManagerException, IOException {
        //Read instance from test file
        FileManager fm = new FileManager(instanceTestFile);
        instance = fm.readInstance();
        
        //setup a solver
        solver = new Solver(instance);
        TestSolution solTest = new TestSolution();
        solver.setHeuristic(solTest);
        
        //set up a solution for test instances
        solver.solveInstance();
        solution = solver.getSolution();
    }
    
    @AfterEach
    public void tearDown() {
        instance = null;
        solver = null;
        solution = null;
    }
    
    public Solution setUpSolution(Instance instance) {
        //Create a solution
        ArrayList<ArrayList<Assignment>> permutations = new ArrayList<>();
        ArrayList<Assignment> permutation = new ArrayList<>();
        permutation.add(new Assignment(instance.getCustomers().get(0),instance.getSatellites().get(0)));
        permutation.add(new Assignment(instance.getCustomers().get(0)));
        permutation.add(new Assignment(instance.getCustomers().get(1),instance.getSatellites().get(1)));
        permutation.add(new Assignment(instance.getCustomers().get(2),instance.getSatellites().get(1)));
        permutation.add(new Assignment(instance.getCustomers().get(1)));
        permutation.add(new Assignment(instance.getCustomers().get(2)));
        permutations.add(permutation);
        return new Solution(null,permutations);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', 0.0",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', 0.0",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', 0.0"
    })
    void TestEvaluateFirstEchelon(String testFilename, double expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        double firstEchelonValue = solver.evaluateFirstEchelon(solution);
        assertEquals(expectedValue, firstEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', 135.89341710842052",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', 135.89341710842052",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', 135.89341710842052"
    })
    void TestEvaluateSecondEchelon(String testFilename, double expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        double secondEchelonValue = solver.evaluateSecondEchelon(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', 135.89341710842052",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', 135.89341710842052",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', 135.89341710842052"
    })
    void TestEvaluateSolution(String testFilename, double expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        double solutionValue = solver.evaluateSolution(solution);
        assertEquals(expectedValue, solutionValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', true"
    })
    void TestIsFirstEchelonTimeWindowsRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        boolean firstEchelonValue = solver.isFirstEchelonTimeWindowsRespected(solution);
        assertEquals(expectedValue, firstEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', true"
    })
    void TestIsFirstEchelonCapacitiesRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        boolean firstEchelonValue = solver.isFirstEchelonCapacitiesRespected(solution);
        assertEquals(expectedValue, firstEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', false",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', true"
    })
    void TestIsSecondEchelonTimeWindowsRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        boolean secondEchelonValue = solver.isSecondEchelonTimeWindowsRespected(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', false"
    })
    void TestIsSecondEchelonCapacitiesRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        boolean secondEchelonValue = solver.isSecondEchelonCapacitiesRespected(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
    })
    void TestIsSecondEchelonVehiclesNumberRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        boolean secondEchelonValue = solver.isSecondEchelonCapacitiesRespected(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }
    
    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', false",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', false"
    })
    void TestIsDoableSolution(String testFilename, boolean expectedValue) throws FileManagerException, IOException{
        setUpClass(testFilename);
        boolean solutionValue = solver.isSolutionDoable(solution);
        assertEquals(expectedValue, solutionValue);
    }
}