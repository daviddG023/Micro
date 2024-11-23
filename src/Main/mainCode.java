package Main;

import RegFile.RegFiles;
import LoadBuffer.LoadBuffers;
import reservation_tables.ReservationStations;
import reader.*;
import executionTable.*;
import java.util.ArrayList;
import java.util.List;

public class mainCode {

    public static void main(String[] args) {
        // 1. Initialize the Instruction Table
    	mipsReader parser = new mipsReader();
        List<Instruction> instructionTable;
        List<ExecutionEntry> Execution = new ArrayList<>();
        ExecutionTable table = new ExecutionTable() ;
		try {
			instructionTable = parser.parseFile("src/instruction.txt");
			System.out.println("Instruction Table:");
	        for (Instruction instr : instructionTable) {
	        	Execution.add(new ExecutionEntry(instr));
	        }
	        table.addEntries(Execution);
	        table.printTable();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // 2. Initialize the Register File (11 registers)
        RegFiles regFile = new RegFiles(11);

        // Simulate setting a Qi field (e.g., R3 set to M1)
        regFile.getRegister(2).setQi("M1"); // R3 corresponds to index 2

        // Print the register file
        System.out.println("\nRegister File:");
        regFile.printAllRegisters();

        // 3. Initialize Load Buffers (3 buffers)
        LoadBuffers loadBuffers = new LoadBuffers(3);

        // Print the load buffers
        System.out.println("\nLoad Buffers:");
        loadBuffers.printAllBuffers();

        // 4. Initialize Reservation Stations (3 Add stations and 2 Multiply stations)
        ReservationStations addStations = new ReservationStations(3,"A"); // Add Reservation Stations (A1, A2, A3)
        ReservationStations mulStations = new ReservationStations(2,"M"); // Multiply Reservation Stations (M1, M2)

        // Simulate updating a reservation station
        mulStations.setStation(0, true, "MUL", "R[R1]", "R[R2]", null, null, null); // Set M1

        // Print Add Reservation Stations
        System.out.println("\nAdd Reservation Stations:");
        addStations.printAllStations();

        // Print Multiply Reservation Stations
        System.out.println("\nMultiply Reservation Stations:");
        mulStations.printAllStations();
    }
}

