package bms;

import components.standard.Standard;

/**
 * BMS kernel component with primary methods. Simulates the underlying hardware
 * polling and state management of a CTP (Cell-to-Pack) Blade Battery
 * architecture.
 */
public interface BMSKernel extends Standard<BMS> {

        /**
         * Inserts a battery blade at the specified structural index with
         * initial sensor data.
         *
         * @param index
         *                the index of each piece of battery
         * @param voltage
         *                the initial voltage
         * @param current
         *                the initial current in battery
         * @param temperature
         *                the initial temperature in Celsius
         * @param soc
         *                the state of Charge (0-100)
         * @param soh
         *                the state of Health (0.0-1.0)
         * @requires 0 <= index and [index is within the maximum capacity of the
         *           pack] and [the slot at index is currently empty]
         * @ensures [the slot at index is marked as placed] and [the
         *          corresponding hardware sensors are initialized with the
         *          provided values]
         */
        void insertBlade(int index, double voltage, double current,
                        double temperature, int soc, double soh);

        /**
         * Updates the readings of an existing blade. Capable of tracking
         * extreme high-current metrics during 600kW fast-charging scenarios.
         *
         * @param index
         *                the index of each piece of battery
         * @param newVoltage
         *                the updated voltage reading
         * @param newCurrent
         *                the updated current reading in battery
         * @param newTemperature
         *                the updated temperature in Celsius
         * @param newSoc
         *                the updated state of Charge (0-100)
         * @param newSoh
         *                the new state of Health (0.0-1.0)
         * @requires 0 <= index and [index is within the maximum capacity of the
         *           pack] and [the slot at index contains a physically placed
         *           battery]
         * @ensures [the hardware sensor data at the specified index is updated
         *          to the new values]
         */
        void updateData(int index, double newVoltage, double newCurrent,
                        double newTemperature, int newSoc, double newSoh);

        /**
         * Physically isolates and removes the blade at the given structural
         * index.
         *
         * @param index
         *                the physical slot index
         * @return an array containing the final data in an array: [voltage,
         *         current, temperature, soc, soh]
         * @requires 0 <= index and [index is within the maximum capacity of the
         *           pack] and [the slot at index contains a physically placed
         *           battery]
         * @ensures [the slot at index is marked as empty] and [all sensor data
         *          for that slot is cleared to 0]
         */
        double[] removeBlade(int index);

        /**
         * Reports the number of battery blades currently in this battery pack.
         *
         * @return the number of blades
         */
        int size();

        /**
         * Returns a copy of the battery data at the specified index without
         * physically removing it from the battery pack.
         *
         * @param index
         *                the physical slot index of the battery to observe
         * @return an array containing the blade's sensor data
         * @requires 0 <= index and [index is within the maximum capacity of the
         *           pack] and [the slot at index contains a physically placed
         *           battery]
         * @ensures [this = #this] and [the returned array contains the exact
         *          sensor data of the specified blade]
         */
        double[] bladeAt(int index);
}
