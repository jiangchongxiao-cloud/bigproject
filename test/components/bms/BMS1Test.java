package components.bms;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Test;

/**
 * JUnit test fixture for {@code BMS1} kernel methods and constructors.
 */
public class BMS1Test {
    /**
     * Acceptable error for double.
     */
    private static final double DELTA = 0.0001;

    /*
     * Constructor Tests--------------------------------------------------------
     */

    /**
     * Test the size of default constructor.
     */
    @Test
    public void testDefaultConstructor() {
        BMS bms = new BMS1();
        assertEquals("Default constructor should initialize with active size 0",
                0, bms.size());
    }

    /**
     * Test constructor size 1.
     */
    @Test
    public void testCapacityConstructorSmall() {
        BMS bms = new BMS1(1);
        assertEquals(
                "Capacity constructor should initialize with active size 0", 0,
                bms.size());
    }

    /**
     * Test constructor size 500.
     */
    @Test
    public void testCapacityConstructorBig() {
        final int bigSize = 500;
        BMS bms = new BMS1(bigSize);
        assertEquals(
                "Capacity constructor should initialize with active size 0", 0,
                bms.size());
    }

    /*
     * Standard Methods Tests---------------------------------------------------
     */

    /*
     * newInstance Tests
     */
    /**
     * Test using newInstance from an empty object.
     */
    @Test
    public void testNewInstanceEmpty() {
        BMS bms1 = new BMS1();
        BMS bms2 = bms1.newInstance();

        assertEquals("New instance must be empty", 0, bms2.size());
        assertEquals("Original instance must remain untouched", 0, bms1.size());
        assertNotSame("New instance must have a different memory address", bms1,
                bms2);
    }

    /**
     * Test using newInstance from a nonempty object.
     */
    @Test
    public void testNewInstanceNonEmpty() {
        BMS bms1 = new BMS1();
        bms1.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        BMS bms2 = bms1.newInstance();

        assertEquals("New instance must be completely empty", 0, bms2.size());
        assertEquals("Original instance must remain untouched", 1, bms1.size());
        assertNotSame("New instance must have a different memory address", bms1,
                bms2);
    }

    /*
     * clear Test
     */
    /**
     * Test clear function.
     */
    @Test
    public void testClear() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        bms.insertBlade(5, 3.1, 10.0, 26.0, 75, 0.98);
        bms.clear();
        assertEquals("Clear should reset size to 0", 0, bms.size());
    }

    /*
     * transferFrom Test
     */
    /**
     * Test transferFrom function.
     */
    @Test
    public void testTransferFromRoutine() {
        BMS source = new BMS1();
        source.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        BMS dest = new BMS1();
        dest.insertBlade(2, 3.5, 12.0, 20.0, 100, 1.0);

        dest.transferFrom(source);

        assertEquals("Destination should absorb the exact size of the source",
                1, dest.size());
        assertEquals("Source must be cleared (size 0)", 0, source.size());
    }

    /*
     * Kernel Methods Tests-----------------------------------------------------
     */

    /*
     * insertBlade and size Tests
     */
    /**
     * Test inserting a blade.
     */
    @Test
    public void testInsertBladeAndSizetToEmpty() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.5, 25.0, 90, 1.0);
        assertEquals("Size should reflect a single insertion", 1, bms.size());
    }

    /**
     * Test inserting 2 blades to 2 random places.
     */
    @Test
    public void testInsertBladeAndSizeRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(17, 3.2, 10.5, 25.0, 90, 1.0);
        bms.insertBlade(99, 3.1, 10.5, 25.0, 90, 1.0);
        assertEquals("Size should reflect multiple insertions", 2, bms.size());
    }

    /*
     * removeBlade Tests
     */
    /**
     * Test removing a blade to get an empty BMS.
     */
    @Test
    public void testRemoveBladeToEmpty() {
        BMS bms = new BMS1();
        bms.insertBlade(5, 3.2, 10.0, 25.0, 80, 1.0);
        double[] removed = bms.removeBlade(5);

        double[] expected = { 3.2, 10.0, 25.0, 80.0, 1.0 };
        assertArrayEquals("Removed data must match inserted data exactly",
                expected, removed, DELTA);
        assertEquals("Size should decrease to 0 after removing the only blade",
                0, bms.size());
    }

    /**
     * Test removing a blade to get an empty BMS.
     */
    @Test
    public void testRemoveBladeRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(5, 3.2, 10.0, 25.0, 80, 1.0);
        bms.insertBlade(17, 3.2, 10.5, 25.0, 90, 1.0);
        double[] removed = bms.removeBlade(5);

        double[] expected = { 3.2, 10.0, 25.0, 80.0, 1.0 };
        assertArrayEquals("Removed data must match inserted data exactly",
                expected, removed, DELTA);
        assertEquals("Size should decrease to 0 after removing the only blade",
                1, bms.size());
    }

    /*
     * updateBlade Test
     */
    /**
     * Test updating one blade.
     */
    @Test
    public void testUpdateDataRoutine() {
        BMS bms = new BMS1();
        bms.insertBlade(0, 3.2, 10.0, 25.0, 80, 1.0);
        bms.updateData(0, 3.5, 12.0, 28.0, 95, 0.99);

        double[] expected = { 3.5, 12.0, 28.0, 95.0, 0.99 };
        assertArrayEquals("Data must be perfectly updated in place", expected,
                bms.bladeAt(0), DELTA);
    }

    /*
     * bladeAt Test
     */
    /**
     * Test bladeAt and test whether violent modification casues aliasing.
     */
    @Test
    public void testBladeAtDeepCopyBoundary() {
        BMS bms = new BMS1();
        bms.insertBlade(2, 3.2, 10.5, 25.0, 90, 1.0);

        double[] fetchedBlade = bms.bladeAt(2);
        fetchedBlade[0] = 800.0;

        double[] secureBlade = bms.bladeAt(2);
        assertEquals("Deep copy failed! Original chassis data was compromised.",
                3.2, secureBlade[0], DELTA);
    }
}
