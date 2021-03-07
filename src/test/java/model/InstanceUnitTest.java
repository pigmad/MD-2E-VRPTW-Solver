package model;

import static java.lang.Math.sqrt;
import java.util.ArrayList;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.TestInstance;

/**
 *
 * @author LASTENNET Dorian
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class InstanceUnitTest {

    private ArrayList<Depot> depots;
    private ArrayList<Satellite> satellites;
    private ArrayList<Customer> customers;
    private Instance instance;

    @BeforeAll
    void setUpClass() {
        depots = new ArrayList<>();
        depots.add(new Depot(1, 3, 8, 20, 0));
        depots.add(new Depot(2, 2, 13, 20, 0));

        satellites = new ArrayList<>();
        satellites.add(new Satellite(1, 1, -10, 10, 10));

        customers = new ArrayList<>();
        customers.add(new Customer(1, 0, -50, -10, 10, 0, 50, 70));

        instance = new Instance(depots, satellites, customers, null, null);
        System.err.println(instance.getDistanceMatrix());
    }

    @Test
    public void TestDistanceMatrixDiagMustBe0() {
        Depot depot0 = instance.getDepots().get(0);
        Depot depot1 = instance.getDepots().get(1);
        Satellite satellite1 = instance.getSatellites().get(0);
        Customer customer1 = instance.getCustomers().get(0);
        double computedDistance = instance.getDistance(depot0, depot0);
        double expectedDistance = 0;
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(depot1, depot1);
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(satellite1, satellite1);
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(customer1, customer1);
        assertEquals(expectedDistance, computedDistance);
    }

    @Test
    public void TestDistanceMatrixIsSymmetric() {
        Depot depot0 = instance.getDepots().get(0);
        Depot depot1 = instance.getDepots().get(1);

        double computedDistance = instance.getDistance(depot0, depot1);
        double symmetricDistance = instance.getDistance(depot1, depot0);
        assertEquals(computedDistance, symmetricDistance);
    }

    @Test
    public void TestDistanceMatrixAccessAndComputation() {
        Depot depot0 = instance.getDepots().get(0);
        Depot depot1 = instance.getDepots().get(1);
        Satellite satellite1 = instance.getSatellites().get(0);
        double computedDistance = instance.getDistance(depot0, depot1);
        double expectedDistance = sqrt(25.0);
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(satellite1, depot1);
        expectedDistance = sqrt(424.0);
        assertEquals(expectedDistance, computedDistance);
    }
}
