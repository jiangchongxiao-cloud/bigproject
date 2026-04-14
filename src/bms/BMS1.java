package bms;

/**
 * {@code BMS} represented as a two-dimensional array.
 *
 * @convention $this.rep is not null and [ $this.rep.length is the maximum
 *             physical slot capacity, > 0 ] and [ For any index i, if slot is
 *             empty/bypassed, $this.rep[i] is null ] and [ For any index i, if
 *             active, $this.rep[i] is a double[] of length 5 ]
 *
 * @correspondence this = [ abstract model of BMS where active blades correspond
 *                 to non-null sub-arrays in $this.rep, and size equals count of
 *                 non-null elements ]
 */
public class BMS1 extends BMSSecondary {

    /*
     * Private members ---------------------------------------------------------
     */

    /**
     * Representation of all blade battery slots.
     */
    private double[][] rep;

    /**
     * Number of actively functioning battery blades.
     */
    private int activeSize;

    /**
     * Initial representation.
     *
     * @param capacity
     *            the physical number of slots
     */
    private void createNewRep(int capacity) {
        this.rep = new double[capacity][];
        this.activeSize = 0;
    }

    /*
     * Constructors -----------------------------------------------------------
     */

    /**
     * No-argument constructor.
     */
    public BMS1() {
        this.createNewRep(100);
    }

    /**
     * Constructor specifying physical slot capacity.
     *
     * @param capacity
     *            the physical number of slots in the chassis
     */
    public BMS1(int capacity) {
        assert capacity > 0 : "Violation of: capacity > 0";

        this.createNewRep(capacity);
    }

    /*
     * Standard methods -------------------------------------------------------
     */

    @Override
    public final BMS newInstance() {
        try {
            return (BMS) this.getClass().getConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(
                    "Cannot construct object of type " + this.getClass());
        }
    }

    @Override
    public final void clear() {
        this.createNewRep(100);
    }

    @Override
    public final void transferFrom(BMS source) {
        assert source != null : "Violation of: source is not null";
        assert source != this : "Violation of: source is not this";
        assert source instanceof BMS1 : ""
                + "Violation of: source is of dynamic type BMS1";

        BMS1 localSource = (BMS1) source;
        this.rep = localSource.rep;
        this.activeSize = localSource.activeSize;

        localSource.createNewRep(100);
    }
    /*
     * Kernel methods ---------------------------------------------------------
     */

    // TODO: insertBlade, updateData, removeBlade, size, bladeAt
}
