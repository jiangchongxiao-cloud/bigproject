import components.bms.BMS;
import components.bms.BMS1;

/**
 * Use Case 1: Vehicle Dashboard. Demonstrates read-only monitoring, and safety
 * alerts during the driving of an Electric Vehicle.
 */
public final class VehicleDashboard {

    /**
     * Private constructor.
     */
    private VehicleDashboard() {
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        // Set a pack with 3 slot.
        // (It is 100 to 250 slots in the real world, but 3 is enough for a demo)
        BMS carBMS = new BMS1(3);

        // Print the loading page of IVI system
        System.out
                .println(">>> [SYSTEM BOOT] Vehicle Dashboard Initializing...");

        // Demonstrate battery pack assembly (3 blades)
        carBMS.insertBlade(0, 3.2, 15.0, 25.0, 85, 1.0);
        carBMS.insertBlade(1, 3.2, 14.8, 26.0, 82, 0.99);
        carBMS.insertBlade(2, 3.1, 15.2, 25.5, 80, 0.98);

        // Battery pack detected
        System.out.println(
                ">>> Active battery blades detected: " + carBMS.size());
        // Driving for a while
        System.out.println("\n>>> [TRANSIT MODE] Vehicle is now in motion...");
        System.out.println(">>> Accelerating... High current draw detected.");

        // Detector update data to domain controller constantly
        carBMS.updateData(0, 3.1, 45.0, 42.0, 43, 0.98);
        carBMS.updateData(1, 3.0, 46.0, 45.0, 44, 0.98);
        // Blade 2's current is huge, tempurature is high
        carBMS.updateData(2, 3.0, 52.1, 62.5, 42, 0.97);

        // Get overall information
        System.out.println(">>> Updating real-time chassis telemetry...");
        double packVoltage = carBMS.packVoltage();
        double avgSOC = carBMS.averageSOC();
        boolean isThermalDanger = carBMS.evaluateThermalStatus();
        int timeTo80 = carBMS.estimateTimeToCharge(80);

        // Print at IVI's screen
        System.out.println("\n=== DASHBOARD METRICS ===");
        System.out.println("Voltage : " + packVoltage + " V");
        System.out.println("Charge: " + avgSOC + " %");

        if (timeTo80 > 0) {
            System.out.println(
                    "Estimated charge time to 80%: " + timeTo80 + " seconds");
        } else {
            System.out.println(
                    "Battery charge is sufficient for standard travel.");
        }

        System.out.println("-------------------------");
        if (isThermalDanger) {
            System.out.println("[WARNING] CRITICAL TEMPERATURE DETECTED!");
        } else {
            System.out.println("[OK] Thermal Status Normal.");
        }
        System.out.println("=========================\n");
    }
}
