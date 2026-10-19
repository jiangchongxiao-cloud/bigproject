package components.bms;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * JUnit test fixture for {@code BMSSecondary} abstract class logic.
 */
public class BMSTest {

    /**
     * Acceptable error for double.
     */
    private static final double DELTA = 0.0001;

    /*
     * Secondary Methods Tests
     */

    // ---------------- packVoltage Tests --------------------------------------
    /**
     * Test a pack with 2 blades.
     */
    @Test
    public void testPackVoltageRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        bms.insertBlade(1, 3.1, 10.0, 26.0, 80, 0.99);
        // 3.2 + 3.1 = 6.3
        assertEquals("Pack voltage should sum all active blades", 6.3,
                bms.packVoltage(), DELTA);
    }

    /**
     * Test a pack with 1 blade.
     */
    @Test
    public void testPackVoltageBoundarySingleBlade() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 4.2, 0.0, 20.0, 100, 1.0);
        assertEquals("Pack voltage should equal the single blade's voltage",
                4.2, bms.packVoltage(), DELTA);
    }

    // ---------------- evaluateThermalStatus Tests ----------------------------
    /**
     * Test under safe temperature.
     */
    @Test
    public void testEvaluateThermalStatusRoutineSafe() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        bms.insertBlade(1, 3.2, 10.0, 35.0, 80, 1.0);
        assertFalse("Should be safe under normal temperatures",
                bms.evaluateThermalStatus());
    }

    /**
     * Test boundary temperature 60.
     */
    @Test
    public void testEvaluateThermalStatusBoundaryExactLimit() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 59.9, 80, 1.0);
        assertFalse(
                "59.9 should still be considered safe (strictly > 60 triggers warning)",
                bms.evaluateThermalStatus());
    }

    /**
     * Test above safe temperature.
     */
    @Test
    public void testEvaluateThermalStatusRoutineDanger() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        bms.insertBlade(1, 3.2, 10.0, 60.1, 80, 1.0);
        assertTrue("Should trigger danger status if any blade > 60.0",
                bms.evaluateThermalStatus());
    }

    // ---------------- averageSOC Tests ---------------------------------------
    /**
     * Test average of 2 blades.
     */
    @Test
    public void testAverageSOCRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 100, 1.0);
        bms.insertBlade(1, 3.1, 10.0, 26.0, 50, 0.98);
        assertEquals("Average SOC of 100 and 50 is 75", 75.0, bms.averageSOC(),
                DELTA);
    }

    /**
     * Test average of 2 empty blades.
     */
    @Test
    public void testAverageSOCBoundaryAllZero() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.0, 0.0, 25.0, 0, 0.9);
        bms.insertBlade(1, 3.0, 0.0, 25.0, 0, 0.9);
        assertEquals("Average SOC of completely drained batteries should be 0",
                0.0, bms.averageSOC(), DELTA);
    }

    // ---------------- estimateTimeToCharge Tests -----------------------------
    /**
     * Test charging a pack with 1 blade.
     */
    @Test
    public void testEstimateTimeToChargeOne() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 50, 1.0);

        int time = bms.estimateTimeToCharge(80);
        assertTrue("Charging from 50 to 80 should take positive time",
                time > 0);
    }

    /**
     * Test charging a pack with 3 blade.
     */
    @Test
    public void testEstimateTimeToChargeRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 50, 1.0);
        bms.insertBlade(1, 3.0, 0.0, 25.0, 0, 0.9);
        bms.insertBlade(2, 3.0, 0.0, 25.0, 0, 0.9);

        int time = bms.estimateTimeToCharge(80);
        assertTrue("Charging from 50 to 80 should take positive time",
                time > 0);
    }

    /**
     * Test charging to the same soc.
     */
    @Test
    public void testEstimateTimeToChargeBoundaryFull() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);

        int time = bms.estimateTimeToCharge(80);
        assertEquals("Charging to current SOC should take 0 seconds", 0, time);
    }

    /**
     * Test charging to a lower soc.
     */
    @Test
    public void testEstimateTimeToChargeLower() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);

        int time = bms.estimateTimeToCharge(70);
        assertEquals("Charging to current SOC should take 0 seconds", 0, time);
    }

    /*
     * Common Object Methods Tests----------------------------------------------
     */
    /**
     * Test 2 same packs.
     */
    @Test
    public void testEqualsRoutineTrue() {
        BMS bms1 = new BMS1();
        bms1.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        BMS bms2 = new BMS1();
        bms2.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        assertTrue("Logically identical BMS packs should be equal",
                bms1.equals(bms2));
    }

    /**
     * Test 2 packs with 2 same blades but have different layout.
     */
    @Test
    public void testEqualsBoundaryDifferentStructure() {
        BMS bms1 = new BMS1();
        bms1.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        bms1.insertBlade(1, 3.0, 0.0, 25.0, 0, 0.9);
        BMS bms2 = new BMS1();
        bms2.insertBlade(0, 3.0, 0.0, 25.0, 0, 0.9);
        bms2.insertBlade(1, 3.2, 10.0, 25.0, 80, 1.0);
        assertFalse("Different physical layout should not be equal",
                bms1.equals(bms2));
    }

    /**
     * Test toString function.
     */
    @Test
    public void testToStringRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        String result = bms.toString();
        assertTrue("toString should output physical structure string",
                result.length() > 0);
    }

    /**
     * Test hashCode function.
     */
    @Test
    public void testHashCode() {
        BMS bms1 = new BMS1();
        bms1.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        BMS bms2 = new BMS1();
        bms2.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        assertEquals("Equal objects must have equal hash codes",
                bms1.hashCode(), bms2.hashCode());
    }
}
