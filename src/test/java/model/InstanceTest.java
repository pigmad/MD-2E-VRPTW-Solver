package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import static java.lang.Math.sqrt;

/**
 * Classe de test JUnit pour la classe instance.
 * 
 * @author LASTENNET Dorian
 */
class InstanceTest {

    private Instance instance;

    @BeforeEach
    void setUp() {
        ArrayList<Customer> customers = new ArrayList<>(3);
        customers.add(new Customer(1, 0, 5, 10, 10, 15, 25, 10));
        customers.add(new Customer(2, 1, 10, 10, 10, 0, 25, 10));

        ArrayList<Satellite> satellites = new ArrayList<>(2);
        satellites.add(new Satellite(1, 3, 8, 20, 10));
        satellites.add(new Satellite(2, 4, 13, 20, 10));

        ArrayList<Depot> depots = new ArrayList<>(2);
        depots.add(new Depot(1, 5, 8, 30, 10));
        depots.add(new Depot(2, 6, 13, 30, 10));

        instance = new Instance(depots, satellites, customers, null, null);
    }

    @Test
    void TestDistanceMatrixDiagMustBe0() {
        Depot depot = instance.getDepots().get(0);
        Satellite satellite = instance.getSatellites().get(0);
        Customer customer = instance.getCustomers().get(0);

        double computedDistance = instance.getDistance(depot, depot);
        double expectedDistance = 0;
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(satellite, satellite);
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(customer, customer);
        assertEquals(expectedDistance, computedDistance);
    }

    @Test
    void TestDistanceMatrixIsSymmetric() {
        Depot depot1 = instance.getDepots().get(0);
        Depot depot2 = instance.getDepots().get(1);

        Satellite satellite1 = instance.getSatellites().get(0);
        Satellite satellite2 = instance.getSatellites().get(1);

        Customer customer1 = instance.getCustomers().get(0);
        Customer customer2 = instance.getCustomers().get(1);

        double computedDistance = instance.getDistance(depot1, depot2);
        double symmetricDistance = instance.getDistance(depot2, depot1);
        assertEquals(computedDistance, symmetricDistance);

        computedDistance = instance.getDistance(satellite1, satellite2);
        symmetricDistance = instance.getDistance(satellite2, satellite1);
        assertEquals(computedDistance, symmetricDistance);

        computedDistance = instance.getDistance(customer1, customer2);
        symmetricDistance = instance.getDistance(customer2, customer1);
        assertEquals(computedDistance, symmetricDistance);
    }

    @Test
    void TestDistanceMatrixAccessAndComputation() {
        Depot depot = instance.getDepots().get(0);

        Satellite satellite = instance.getSatellites().get(0);

        Customer customer = instance.getCustomers().get(0);

        double computedDistance = instance.getDistance(depot, satellite);
        double expectedDistance = sqrt(100);
        assertEquals(expectedDistance, computedDistance);

        computedDistance = instance.getDistance(satellite, customer);
        expectedDistance = sqrt(109);
        assertEquals(expectedDistance, computedDistance);
    }
}
