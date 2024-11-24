package Main;

import reader.*;

public class TomasuloSimulator {
    private mainCode components; // Holds the initialized components
    private int clockCycle;      // Tracks the current clock cycle

    // Constructor
    public TomasuloSimulator(mainCode components) {
        this.components = components;
        this.clockCycle = 1;
    }

    
    
    
    // Run the Tomasulo simulation loop
    public void run() {
        while (!isSimulationComplete()) {
            System.out.println("Clock Cycle: " + clockCycle);

            // Example: Update Execution Table
            if (clockCycle == 1) {
                components.table.setIssue(0, clockCycle);
                Instruction instruction = components.table.getTable(0).getInstruction();
                components.updateReservationStation(instruction.operation, 0, true, instruction.operation, instruction.source1, instruction.source2, null, null, instruction.destination, null);
                components.updateRegister(Integer.parseInt(instruction.destination.substring(1))-1,"M1");
               
            } else if (clockCycle == 2) {
            	components.table.setIssue(1, clockCycle);
            	Instruction instruction = components.table.getTable(1).getInstruction();
                components.updateReservationStation(instruction.operation, 0, true, instruction.operation, instruction.source1, instruction.source2, instruction.source1, instruction.source2, instruction.destination, null);
                components.subtractCycle(clockCycle);
                components.updateRegister(Integer.parseInt(instruction.destination.substring(1))-1,"A1");
            }else {
            	components.subtractCycle(clockCycle);
            }

            

            // Increment clock cycle
            clockCycle++;

            // Print the state of all components
            components.printTables(); 
        }
    }

    // Check if the simulation is complete
    private boolean isSimulationComplete() {
        // Example condition: Stop after 10 clock cycles
        return clockCycle >= 5;
    }
    public static void main(String[] args) {
    	mainCode program = new mainCode(
                11, // Number of registers
                3,  // Number of load buffers
                3,  // Number of add reservation stations
                2,  // Number of multiply reservation stations
                "src/instruction.txt" // Instruction file path
        );
    	TomasuloSimulator t =new TomasuloSimulator(program);
    	t.run();
    }
}
