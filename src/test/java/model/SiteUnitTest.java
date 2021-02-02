package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import static org.junit.jupiter.api.Assertions.*;

import static java.lang.Math.sqrt; 

/**
 *
 * @author LASTENNET Dorian
 */
@TestInstance(Lifecycle.PER_CLASS)
class SiteUnitTest {
    private Depot depot1;
    private Depot depot2;
    private Satellite satellite1;
    private Customer customer1;
    
    @BeforeAll
    void setUpClass() {
        depot1 = new Depot(1,0,8,20,0);
        depot2 = new Depot(2,1,13,20,0);
        satellite1 = new Satellite(1,2,-10,10,10);
        customer1 = new Customer(1,3,-50,-10,10,0,50,70);
    }

    @Test
    void TestComputeDistanceBetweenSiteImpl(){
        long computedDistance = depot1.computeDistance(depot2);
        long expectedDistance = 5;
        assertEquals(expectedDistance, computedDistance);
    }
    
    @Test
    void TestComputeDistanceOrderDontMatter(){
        long computedDistance = depot1.computeDistance(depot2);
        long otherOrderComputedDistance = depot2.computeDistance(depot1);
        assertEquals(otherOrderComputedDistance, computedDistance);
    }
    
    @Test
    void TestComputeDistanceBetweenSameSiteImpl(){
        long computedDistance = depot1.computeDistance(depot1);
        long expectedDistance = 0;
        assertEquals(expectedDistance, computedDistance);
    }
    
    @Test
    void TestComputeDistanceBetweenDepotSattelite(){
        long computedDistance = depot1.computeDistance(satellite1);
        long expectedDistance = 21;
        assertEquals(expectedDistance,computedDistance);
    }
    
    @Test
    void TestComputeDistanceBetweenSatelliteCustomer(){
        long computedDistance = satellite1.computeDistance(customer1);
        long expectedDistance = 45;
        assertEquals(expectedDistance,computedDistance);
    }
}
