package model;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.lang.Math.sqrt;

/**
 * Classe de test JUnit pour les classes sites.
 * 
 * @author LASTENNET Dorian
 */
class SiteTest {

    private Customer customer;
    private Satellite satellite;
    private Depot depot1;
    private Depot depot2;
    
    @BeforeEach
    void setUp() {
        customer = new Customer(1, 0, 5, 10, 10, 15, 25, 10);
        satellite = new Satellite(1, 1, 8, 20, 10);
        depot1 = new Depot(1, 2, 8, 30, 10);
        depot2 = new Depot(2, 3, 13, 30, 10);
    }

    @Test
    void TestComputeDistanceBetweenSiteImpl() {
        double computedDistance = depot1.computeDistance(depot2);
        double expectedDistance = sqrt(25.0);
        assertEquals(expectedDistance, computedDistance);
    }

    @Test
    void TestComputeDistanceOrderDontMatter() {
        double computedDistance = depot1.computeDistance(depot2);
        double otherOrderComputedDistance = depot2.computeDistance(depot1);
        assertEquals(otherOrderComputedDistance, computedDistance);
    }

    @Test
    void TestComputeDistanceBetweenSameSiteImpl() {
        double computedDistance = depot1.computeDistance(depot1);
        double expectedDistance = 0.0;
        assertEquals(expectedDistance, computedDistance);
    }

    @Test
    void TestComputeDistanceBetweenDepotSatellite() {
        double computedDistance = depot1.computeDistance(satellite);
        double expectedDistance = sqrt(100);
        assertEquals(expectedDistance, computedDistance);
    }

    @Test
    void TestComputeDistanceBetweenSatelliteCustomer() {
        double computedDistance = satellite.computeDistance(customer);
        double expectedDistance = sqrt(109);
        assertEquals(expectedDistance, computedDistance);
    }
}
