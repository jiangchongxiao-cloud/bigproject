
package bladebattery;

/**
 * An example of the BladeBatteryArray component. The component simulates the
 * CTP architecture on electric vehicles into software state management(BMS,
 * Battery Management System).
 */
public class BladeBatteryArray {
    /**
     * The maximum batteries that can be placed.
     */
    private final int maxCapacity;

    /**
     * Record voltage of each battery.
     */
    private double[] voltages;

    /**
     * Record temperature in Celsius.
     */
    private double[] temperatures;

    /**
     * Record SOC, an int from 0 to 100.
     */
    private int[] soc;

    /**
     * Record if a battery is placed here.
     */
    private boolean[] placed;

    /**
     * Total batteries currently installed.
     */
    private int count;

    /**
     * Create arrays for all data.
     *
     * @param capacity
     */
    public BladeBatteryArray(int capacity) {
        this.maxCapacity = capacity;
        this.voltages = new double[capacity];
        this.temperatures = new double[capacity];
        this.soc = new int[capacity];
        this.placed = new boolean[capacity];
        this.count = 0;
    }

    // --- Kernel Method ---

    /**
     * Inserts a blade cell at the specified structural index.
     *
     * @param index
     * @param voltage
     * @param temperature
     * @param currentSOC
     */
    public void insertBlade(int index, double voltage, double temperature,
            int currentSOC) {
        if (index >= 0 && index < this.maxCapacity && !this.placed[index]) {
            this.voltages[index] = voltage;
            this.temperatures[index] = temperature;
            this.soc[index] = currentSOC;
            this.placed[index] = true;
            this.count++;
        }
    }

    /**
     * Removes the blade at the given physical index.
     *
     * @param index
     * @return an array with voltage, temperature, SOC.
     */
    public double[] removeBlade(int index) {
        if (index >= 0 && index < this.maxCapacity && this.placed[index]) {

            double[] data = { this.voltages[index], this.temperatures[index],
                    (double) this.soc[index] };

            this.placed[index] = false;
            this.voltages[index] = 0.0;
            this.temperatures[index] = 0.0;
            this.soc[index] = 0;
            this.count--;

            return data;
        }
        return null;
    }

    /**
     * Updates the sensor readings including Voltage, Temperature, SOC of an
     * existing blade. This simulates the BMS polling data without physically
     * removing the module.
     *
     * @param index
     * @param newVoltage
     * @param newTemperature
     * @param newSoc
     */
    public void updateData(int index, double newVoltage, double newTemperature,
            int newSoc) {
        if (index >= 0 && index < this.maxCapacity && this.placed[index]) {
            this.voltages[index] = newVoltage;
            this.temperatures[index] = newTemperature;
            this.soc[index] = newSoc;
        } else {
            System.out.println("[Error]empty or invalid slot: " + index);
        }
    }

    // --- Secondary Method ---

    /**
     * Calculates total voltage assuming a physical series connection logic.
     *
     * @return the total voltage of the package
     */
    public double packVoltage() {
        double total = 0.0;
        for (int i = 0; i < this.maxCapacity; i++) {
            if (this.placed[i]) {
                total += this.voltages[i];
            }
        }
        return total;
    }

    /**
     * Scans for any cell exceeding the 60 C safety threshold.
     *
     * @return whether the tempature is too high
     */
    public boolean isThermalRunaway() {
        for (int i = 0; i < this.maxCapacity; i++) {
            if (this.placed[i] && this.temperatures[i] > 60.0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the average State of Charge for the entire array.
     *
     * @return the SOC of the package
     */
    public double averageSOC() {
        double totalSOC = 0.0;
        for (int i = 0; i < this.maxCapacity; i++) {
            if (this.placed[i]) {
                totalSOC += this.soc[i];
            }
        }
        return totalSOC / this.count;
    }

    /**
     * Test all functions. Random datas are provided by AI.
     *
     * @param args
     */
    public static void main(String[] args) {
        BladeBatteryArray pack = new BladeBatteryArray(100);

        // Phase 1: Installation
        pack.insertBlade(0, 3.2, 25.1, 98);
        pack.insertBlade(1, 3.2, 25.5, 95);
        pack.insertBlade(2, 3.2, 24.8, 97);
        System.out.println("\n Install 3 battery as an example.");
        System.out.println("[Data] Pack Voltage: " + pack.packVoltage() + "V");
        System.out.println("[Data] Average SOC: " + pack.averageSOC() + "%");

        System.out.println("\n Simulating data change during driving");
        pack.updateData(0, 3.1, 35.2, 88);
        pack.updateData(1, 3.1, 36.1, 85);
        pack.updateData(2, 3.1, 30.0, 90);

        System.out.println("Current Voltage: " + pack.packVoltage() + "V");
        System.out.println("Current SOC: " + pack.averageSOC() + "%");

        System.out.println("If a battery is too hot");
        pack.insertBlade(3, 3.8, 85.5, 45);
        System.out.println(
                "[Warning] Faulty module inserted at physical index 3.");

        boolean danger = pack.isThermalRunaway();
        System.out.println("[Analysis] Thermal Runaway Detected: " + danger);

        if (danger) {
            System.out.println("Executing emergency protocol. Index 3");
            double[] removedData = pack.removeBlade(3);
            if (removedData != null) {
                System.out.println("[Log] Extracted module -> Voltage: "
                        + removedData[0] + "V, Temp: " + removedData[1]
                        + "C, SOC: " + removedData[2] + "%");
            }
        }

        System.out
                .println("\n[Analysis] Post-isolation Thermal Runaway Status: "
                        + pack.isThermalRunaway());
        System.out.println("Final Pack Voltage: " + pack.packVoltage() + "V");
    }
}
