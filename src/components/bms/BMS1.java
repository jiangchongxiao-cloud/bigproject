package components.bms;

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
     * Default size of a blade pack.
     */
    private static final int DEFAULT_SIZE = 100;

    /**
     * Length of the sub array.
     */
    private static final int LENGTH_DATA = 5;

    /**
     * Index of voltage in the returned blade array.
     */
    private static final int VOLTAGE_INDEX = 0;

    /**
     * Index of current in the returned blade array.
     */
    private static final int CURRENT_INDEX = 1;

    /**
     * Index of temprature in the returned blade array.
     */
    private static final int TEMP_INDEX = 2;

    /**
     * Index of soc in the returned blade array.
     */
    private static final int SOC_INDEX = 3;

    /**
     * Index of soh in the returned blade array.
     */
    private static final int SOH_INDEX = 4;

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
        this.createNewRep(DEFAULT_SIZE);
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
        this.createNewRep(DEFAULT_SIZE);
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

        localSource.createNewRep(DEFAULT_SIZE);
    }

    /*
     * Kernel methods ---------------------------------------------------------
     */

    @Override
    public final void insertBlade(int index, double voltage, double current,
            double temperature, int soc, double soh) {
        assert index >= 0 : "Violation of: index >= 0";
        assert index < this.rep.length : "Violation of: index < max capacity";
        assert this.rep[index] == null : "Violation of: slot is currently empty";

        double[] newBlade = { voltage, current, temperature, soc, soh };
        this.rep[index] = newBlade;

        this.activeSize++;
    }

    @Override
    public final void updateData(int index, double newVoltage,
            double newCurrent, double newTemperature, int newSoc,
            double newSoh) {
        assert index >= 0 : "Violation of: index >= 0";
        assert index < this.rep.length : "Violation of: index < max capacity";
        assert this.rep[index] != null : "Violation of: slot contains an active battery";

        this.rep[index][VOLTAGE_INDEX] = newVoltage;
        this.rep[index][CURRENT_INDEX] = newCurrent;
        this.rep[index][TEMP_INDEX] = newTemperature;
        this.rep[index][SOC_INDEX] = newSoc;
        this.rep[index][SOH_INDEX] = newSoh;
    }

    @Override
    public final double[] removeBlade(int index) {
        assert index >= 0 : "Violation of: index >= 0";
        assert index < this.rep.length : "Violation of: index < max capacity";
        assert this.rep[index] != null : "Violation of: slot contains an active battery";

        double[] removedData = this.rep[index];

        this.rep[index] = null;
        this.activeSize--;

        return removedData;
    }

    @Override
    public final int size() {
        return this.activeSize;
    }

    @Override
    public final double[] bladeAt(int index) {
        assert index >= 0 : "Violation of: index >= 0";
        assert index < this.rep.length : "Violation of: index < max capacity";
        assert this.rep[index] != null : "Violation of: slot contains an active battery";

        double[] copiedBlade = new double[LENGTH_DATA];

        for (int i = 0; i < LENGTH_DATA; i++) {
            copiedBlade[i] = this.rep[index][i];
        }

        return copiedBlade;
    }
}
