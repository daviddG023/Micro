package Main;

import RegFile.RegFiles;
import LoadBuffer.LoadBuffers;
import reservation_tables.ReservationStations;
import reader.*;
import executionTable.*;
import java.util.ArrayList;
import java.util.List;

public class mainCode {
    ExecutionTable table;
    RegFiles regFile;
    LoadBuffers loadBuffers;
    ReservationStations addStations;
    ReservationStations mulStations;

    // Constructor to initialize components
    public mainCode(int regNum, int loadNum, int addNum, int mulNum, String instructionFilePath) {
        // Initialize Execution Table
        table = new ExecutionTable();

        // Initialize Register File
        regFile = new RegFiles(regNum);

        // Initialize Load Buffers
        loadBuffers = new LoadBuffers(loadNum);

        // Initialize Reservation Stations
        addStations = new ReservationStations(addNum, "A"); // Add Reservation Stations (A1, A2, A3)
        mulStations = new ReservationStations(mulNum, "M"); // Multiply Reservation Stations (M1, M2)

        // Initialize Instruction Table and Execution Entries
        try {
            mipsReader parser = new mipsReader();
            List<Instruction> instructionTable = parser.parseFile(instructionFilePath);

            // Convert Instructions into Execution Entries and add to Execution Table
            List<ExecutionEntry> Execution = new ArrayList<>();
            for (Instruction instr : instructionTable) {
                Execution.add(new ExecutionEntry(instr));
            }
            table.addEntries(Execution);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Method to update an entry in the Execution Table
    public void updateExecutionEntry(int index, Integer issue, Integer execStart, Integer execEnd, Integer writeBack) {
    	if (index < table.size()) {
    	    ExecutionEntry entry = table.get(index);
            if (issue != null) entry.setIssue(issue);
            if (execStart != null) entry.setExecutionStart(execStart);
            if (execEnd != null) entry.setExecutionEnd(execEnd);
            if (writeBack != null) entry.setWriteBack(writeBack);
        } else {
            System.out.println("Invalid Execution Table index: " + index);
        }
    }

    // Method to update a specific register's Qi field
    public void updateRegister(int registerIndex, String qi) {
        regFile.getRegister(registerIndex).setQi(qi);
    }

    // Method to update a Load Buffer
    public void updateLoadBuffer(int bufferIndex, boolean busy, String address) {
        if (bufferIndex < loadBuffers.size()) {
            loadBuffers.get(bufferIndex).setBusy(busy);
            loadBuffers.get(bufferIndex).setAddress(address);
        } else {
            System.out.println("Invalid Load Buffer index: " + bufferIndex);
        }
    }

    // Method to update a Reservation Station
    public void updateReservationStation(String type, int stationIndex, boolean busy, String op, String vj, String vk, String qj, String qk, String a, Integer Time) {
    // Determine the type of Reservation Station (ADD or MUL)
    ReservationStations stations = type.equals("ADD") ? addStations : mulStations;

    // Validate station index
    if (stationIndex < stations.size()) {
        // Check qj in RegFiles
        boolean qjFound = qj != null && checkRegisterExists(qj);

        // Check qk in RegFiles
        boolean qkFound = qk != null && checkRegisterExists(qk);

        if (qjFound || qkFound) {
            // Both qj and qk found in RegFiles
        	if(qjFound)
        		stations.setStation(stationIndex, busy, op, null, vk, qj, null, a, Time);
        	if(qkFound)
        		stations.setStation(stationIndex, busy, op, vj, null, null, qk, a, Time);
        } else {
        	stations.setStation(stationIndex, busy, op, vj, vk, qj, qk, a, Time);
        }
    } else {
        System.out.println("Invalid Reservation Station index: " + stationIndex);
    }
}
    private boolean checkRegisterExists(String register) {
        try {
            // Extract the numeric part of the register (e.g., "1" from "R1")
            int registerIndex = Integer.parseInt(register.substring(1)) - 1;

            // Validate the register index is within bounds of RegFiles
            return registerIndex >= 0 && registerIndex < regFile.size();
        } catch (NumberFormatException | StringIndexOutOfBoundsException e) {
            // Handle invalid register format (e.g., "R" with no number or non-numeric input)
            System.out.println("Invalid register format: " + register);
            return false;
        }
    }


    // Method to print all tables
    public void printTables() {
        System.out.println("Execution Table:");
        table.printTable();

        System.out.println("\nRegister File:");
        regFile.printAllRegisters();

        System.out.println("\nLoad Buffers:");
        loadBuffers.printAllBuffers();

        System.out.println("\nAdd Reservation Stations:");
        addStations.printAllStations();

        System.out.println("\nMultiply Reservation Stations:");
        mulStations.printAllStations();
    }

    // Main method
    public static void main(String[] args) {
        // Initialize mainCode with parameters
        mainCode program = new mainCode(
            11, // Number of registers
            3,  // Number of load buffers
            3,  // Number of add reservation stations
            2,  // Number of multiply reservation stations
            "src/instruction.txt" // Instruction file path
        );

        // Example updates
        program.updateExecutionEntry(0, 1, 2, null, null); // Set issue and start for first entry
        program.updateExecutionEntry(0, null, null, 7, 8); // Set end and write-back for first entry

        program.updateRegister(2, "M1"); // Set Qi for R3 to M1
        program.updateLoadBuffer(0, true, "0x200"); // Set L1 buffer to busy and set address

        program.updateReservationStation("A", 0, true, "ADD", "R1", "R2", "Q1", "Q2", null,10); // Update A1

        // Print updated tables
        program.printTables();
    }
}
