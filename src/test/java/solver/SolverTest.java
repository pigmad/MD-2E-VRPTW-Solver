package solver;

import java.io.IOException;
import model.Instance;
import model.Solution;
import utils.FileManager;
import utils.FileManagerException;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Classe de test JUnit pour la classe solver.
 * 
 * @author LASTENNET Dorian
 */
class SolverTest {

    private Solver solver;
    private Instance instance;
    private Solution solution;

    void setUpTestData(String instanceTestFile) throws FileManagerException, IOException {
        //Read instance from test file
        FileManager fm = new FileManager(instanceTestFile);
        instance = fm.readInstance();

        //setup a solver
        solver = new Solver(true, instance);
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

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', 0.0",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', 0.0",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', 0.0"
    })
    void TestEvaluateFirstEchelon(String testFilename, double expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        double firstEchelonValue = solver.evaluateFirstEchelon(solution);
        assertEquals(expectedValue, firstEchelonValue);
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', 106",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', 106",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', 106"
    })
    void TestEvaluateSecondEchelon(String testFilename, double expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        double secondEchelonValue = solver.evaluateSecondEchelon(solution);
        assertEquals(expectedValue, Math.round(secondEchelonValue));
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', 106",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', 106",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', 106"
    })
    void TestEvaluateSolution(String testFilename, double expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        double solutionValue = solver.evaluateSolution(solution);
        assertEquals(expectedValue, Math.round(solutionValue));
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', true"
    })
    void TestIsFirstEchelonCapacitiesRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        boolean firstEchelonValue = solver.isFirstEchelonCapacitiesRespected(solution);
        assertEquals(expectedValue, firstEchelonValue);
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', false",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', true"
    })
    void TestIsSecondEchelonTimeWindowsRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        boolean secondEchelonValue = solver.isSecondEchelonTimeWindowsRespected(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', false"
    })
    void TestIsSecondEchelonCapacitiesRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        boolean secondEchelonValue = solver.isSecondEchelonCapacitiesRespected(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }

    @ParameterizedTest
    @CsvSource({"'src/test/java/Instances/testInstanceValid-2,2,3.txt', true"})
    void TestIsSecondEchelonVehiclesNumberRespected(String testFilename, boolean expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        boolean secondEchelonValue = solver.isSecondEchelonCapacitiesRespected(solution);
        assertEquals(expectedValue, secondEchelonValue);
    }

    @ParameterizedTest
    @CsvSource({
        "'src/test/java/Instances/testInstanceValid-2,2,3.txt', true",
        "'src/test/java/Instances/testInstanceTimeInvalid-2,2,3.txt', false",
        "'src/test/java/Instances/testInstanceCapacityInvalid-2,2,3.txt', false"
    })
    void TestIsDoableSolution(String testFilename, boolean expectedValue) throws FileManagerException, IOException {
        setUpTestData(testFilename);
        boolean solutionValue = solver.isSolutionDoable(solution);
        assertEquals(expectedValue, solutionValue);
    }
}