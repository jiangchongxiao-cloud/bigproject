import components.bms.BMS;
import components.bms.BMS1;

/**
 * Use Case 2: Maintenance of the EV in Use Case 1. Demonstrates physical blade
 * swapping, data updating, and targeted deep copy data extraction for mechanic
 * repairs.
 */
public final class BatteryPackRepair {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private BatteryPackRepair() {
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        // Reconstruct the overheated chassis from Use Case 1
        BMS carBMS = new BMS1(3);
        carBMS.insertBlade(0, 3.1, 45.0, 42.0, 43, 0.98);
        carBMS.insertBlade(1, 3.0, 46.0, 45.0, 44, 0.98);
        // Blade 2's current is huge, tempurature is high
        carBMS.insertBlade(2, 3.0, 52.1, 62.5, 42, 0.97);

        System.out.println(">>> [DIAGNOSTIC] Connecting to vehicle BMS...");
        System.out.println("Initial Thermal Threat Detected: "
                + carBMS.evaluateThermalStatus());

        System.out.println("\n>>> Removing overheated blade at Slot 2...");
        double[] extractedData = carBMS.removeBlade(2);
        System.out.println("Active blades remaining: " + carBMS.size());

        System.out.println("\n>>> Analyzing Extracted Blade...");
        System.out.println("Current state -> Voltage: " + extractedData[0]
                + "V | Temp: " + extractedData[2] + "C");

        // Remove the blade and repair it
        System.out.println(">>> [REPAIR] Applying repairation...");
        extractedData[2] = 25.0; //Cool down
        extractedData[1] = 0.0; // Cut off current
        extractedData[0] = 3.15; // Healthy voltage

        // Put it back to the slot
        System.out.println(">>> Installing the recovered blade into Slot 2...");
        carBMS.insertBlade(2, extractedData[0], extractedData[1],
                extractedData[2], (int) extractedData[3], extractedData[4]);

        // Print the result
        System.out.println("\n>>> Post-repair system check...");
        System.out.println("New Pack Voltage: " + carBMS.packVoltage() + " V");
        System.out.println(
                "Thermal Threat Detected: " + carBMS.evaluateThermalStatus());
        System.out.println(">>> System nominal. Vehicle is safe to release.");
    }
}
