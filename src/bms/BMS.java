package bms;

/**
 * BMS enhanced interface with secondary methods. Provides advanced telemetry
 * analytics, thermal management, and non-linear charging estimations for the
 * battery pack.
 */
public interface BMS extends BMSKernel {

    /**
     * Calculates the total voltage of the battery pack, assuming a physical
     * series connection.
     *
     * @return the total aggregated voltage of all currently installed battery
     *         modules
     * @ensures packVoltage = [sum of voltages of all placed batteries]
     */
    double packVoltage();

    /**
     * Evaluates the thermal safety status across the entire battery array.
     * Scans for critical temperature thresholds (60.0 Celsius) that indicate a
     * potential thermal runaway event.
     *
     * @return true if any module exceeds the critical safety threshold, false
     *         otherwise
     * @ensures evaluateThermalStatus = [true iff there exists at least one
     *          placed battery with temperature > 60.0]
     */
    boolean evaluateThermalStatus();

    /**
     * Calculates the average State of Charge (SOC) for the entire functional
     * array.
     *
     * @return the average SOC percentage
     * @requires [at least one battery is currently placed in the array]
     * @ensures averageSOC = [the sum of all SOCs divided by the total number of
     *          placed batteries]
     */
    double averageSOC();

    /**
     * Estimates the remaining time required to reach a target SOC, utilizing
     * non-linear charging models adapted for high-current fast-charging
     * architectures.
     *
     * @param targetSoc
     *            the desired State of Charge percentage (0-100)
     * @return the estimated time in seconds to reach the target SOC
     * @requires 0 <= targetSoc <= 100 and targetSoc > [current average SOC]
     * @ensures [the returned value represents a mathematically modeled
     *          estimation based on current flow and temperatures]
     */
    int estimateTimeToCharge(int targetSoc);
}
