package bms;

/**
 * Layered implementations of secondary methods for {@code BMS}.
 * <p>
 * This abstract class will implement all secondary methods using the methods
 * (from {@code BMSKernel}) and {@code Standard} methods.
 * </p>
 */
public abstract class BMSSecondary implements BMS {

    /**
     * The most popular hash code constant 31.
     */
    private static final int HASH = 31;

    /**
     * one hundred.
     */
    private static final int HUNDRED = 100;

    /**
     * The temperature should not over 60 C.
     */
    private static final double THERMAL_THRESHOLD = 60.0;

    /**
     * Estimated minutes required to charge 1% of SOC.
     */
    private static final double CHARGE_RATE_MINUTES_PER_PERCENT = 2.0;

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

    /*
     * Common methods from Object
     */

    /**
     * @return the string representation of this BMS
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("<BMS: ");
        int n = this.size();

        for (int i = 0; i < n; i++) {
            double[] blade = this.bladeAt(i);
            sb.append("[Slot ").append(i).append(": ")
                    .append(blade[VOLTAGE_INDEX]).append("V, ")
                    .append((int) blade[SOC_INDEX]).append("% SOC]");

            if (i < n - 1) {
                sb.append(", ");
            }
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * @param obj
     *            the object to be compared for equality with this BMS
     * @return true if the specified object is equal to this BMS
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof BMS)) {
            return false;
        }

        BMS aBms = (BMS) obj;
        int n = this.size();

        if (n != aBms.size()) {
            return false;
        }

        for (int i = 0; i < n; i++) {
            double[] myBlade = this.bladeAt(i);
            double[] aBlade = aBms.bladeAt(i);

            if (myBlade[VOLTAGE_INDEX] != aBlade[VOLTAGE_INDEX]
                    || myBlade[CURRENT_INDEX] != aBlade[CURRENT_INDEX]
                    || myBlade[TEMP_INDEX] != aBlade[TEMP_INDEX]
                    || myBlade[SOC_INDEX] != aBlade[SOC_INDEX]
                    || myBlade[SOH_INDEX] != aBlade[SOH_INDEX]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return the hash code value for this BMS
     */
    @Override
    public int hashCode() {
        int hash = 0;
        int n = this.size();

        for (int i = 0; i < n; i++) {
            double[] blade = this.bladeAt(i);
            hash = HASH * hash + (int) blade[VOLTAGE_INDEX];
            hash = HASH * hash + (int) blade[SOC_INDEX];
        }

        hash = HASH * hash + n;

        return hash;
    }

    /*
     * Secondary methods
     */

    /**
     * @return the total voltage of the battary pack
     */
    @Override
    public double packVoltage() {
        double total = 0.0;
        int n = this.size();

        for (int i = 0; i < n; i++) {
            total += this.bladeAt(i)[VOLTAGE_INDEX];
        }

        return total;
    }

    /**
     * test all battery one by one and return true if any one is over 55.
     *
     * @return true if a thermal anomaly is detected, false otherwise
     */
    @Override
    public boolean evaluateThermalStatus() {
        int n = this.size();

        for (int i = 0; i < n; i++) {
            if (this.bladeAt(i)[TEMP_INDEX] >= THERMAL_THRESHOLD) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the average SOC as a double
     */
    @Override
    public double averageSOC() {
        int n = this.size();

        if (n == 0) {
            return 0.0;
        }

        int total = 0;

        for (int i = 0; i < n; i++) {
            total += (int) this.bladeAt(i)[SOC_INDEX];
        }

        return (double) total / n;
    }

    /**
     * Estimates the time required to charge the battery pack.
     *
     * @param targetSoc
     *            the desired State of Charge to reach (0-100)
     * @return the estimated time in minutes to reach the target SOC
     */
    @Override
    public int estimateTimeToCharge(int targetSoc) {
        assert targetSoc >= 0
                && targetSoc <= HUNDRED : "Violation of 0 <= targetSoc <= HUNDRED";

        double avg = this.averageSOC();
        if (targetSoc <= avg) {
            return 0;
        }
        return (int) Math
                .round((targetSoc - avg) * CHARGE_RATE_MINUTES_PER_PERCENT);
    }
}
