package Main;

import RegFile.Point;
import RegFile.RegFiles;
import LoadBuffer.LoadBuffers;
import reservation_tables.*;
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
    public void updateReservationStation(String type, boolean busy, String op, String vj, String vk, String qj, String qk, String a, Integer Time) {
    // Determine the type of Reservation Station (ADD or MUL)
    ReservationStations stations = type.equals("ADD") ? addStations : mulStations;
    Integer cycleCount = type.equals("ADD") ? 2 : 2;
    int stationIndex = stations.getFirstEmpty();
    String s = type.equals("ADD") ?"A":"M";
    updateRegister(Integer.parseInt(a.substring(1))-1,s+""+(stationIndex+1));

    
    // Validate station index
    if (stationIndex < stations.size()) {
        // Check qj in RegFiles
    	System.out.println("this is the qj and the qk"+qj+qk);
        boolean qjFound = qj != null && checkRegisterExists(qj);

        // Check qk in RegFiles
        boolean qkFound = qk != null && checkRegisterExists(qk);
        
        if(qjFound && qkFound) {
        	stations.setStation(stationIndex, busy, op, null, null,regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(),a, Time);
        	regFile.add(Integer.parseInt(qj.substring(1))-1,new Point(stationIndex,"qj",vj,type));
        	regFile.add(Integer.parseInt(qk.substring(1))-1,new Point(stationIndex,"qk",vk,type));
        	return;
        }
        if (qjFound || qkFound) {
            // Both qj and qk found in RegFiles
        	if(qjFound) {
        		stations.setStation(stationIndex, busy, op, null, vk, regFile.get(Integer.parseInt(qj.substring(1))-1).getQi(), null, a, Time);
        		regFile.add(Integer.parseInt(qj.substring(1))-1,new Point(stationIndex,"qj",vj,type));
//        		System.out.println(regFile.get((Integer.parseInt(qj.substring(1))-1)).getList());
        	}
        	if(qkFound) {
        		stations.setStation(stationIndex, busy, op, vj, null, null, regFile.get(Integer.parseInt(qk.substring(1))-1).getQi(), a, Time);
        		regFile.add(Integer.parseInt(qk.substring(1))-1,new Point(stationIndex,"qk",vk,type));
//        		System.out.println(regFile.get((Integer.parseInt(qk.substring(1))-1)).getList());
        	}
        } else {
        	stations.setStation(stationIndex, busy, op, vj, vk, qj, qk, a, Time);
        }
        if(stations.get(stationIndex).getVj()!=null&&stations.get(stationIndex).getVk()!=null) {
        	stations.setTime(stationIndex, cycleCount);
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
            return registerIndex >= 0 && registerIndex < regFile.size()&&regFile.get(registerIndex).getQi()!=null;
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

    public void subtractCycle(int clockCycle) {
    // Loop over addStations
    	ArrayList<Integer> writeBack = new ArrayList<>();
	    for (int i = 0; i < addStations.size(); i++) {
	        ReservationStation station = addStations.get(i);
	        Integer time = station.getTime();
	        if (time != null) {
	        	if(time==2&&table.get(i).getIssue()==clockCycle)//change i adjust time
	        		continue;
	        	if(time == 2) {//adjust time 
	        		table.setExecutionStart(i, clockCycle);
	        	}
	            // Subtract 1 from time
	            if (time == 0) {
	            	table.setExecutionEnd(i, clockCycle);
	            	writeBack.add(i);
	            	List<Point> l= regFile.get(Integer.parseInt(addStations.get(i).getA().substring(1))-1).getList();
	            	for(Point p:l) {
	            		ReservationStations stations = p.getOp().equals("ADD") ? addStations : mulStations;
	            		if(p.getY().equals("qj"))
	            			stations.setStationJ(p.getX(), regFile.get(Integer.parseInt(p.getZ().substring(1))-1).getName());
	            		if(p.getY().equals("qK"))
	            			stations.setStationK(p.getX(), regFile.get(Integer.parseInt(p.getZ().substring(1))-1).getName());
	            		if(stations.get(p.getX()).getVj()!=null&&stations.get(p.getX()).getVk()!=null) {
	                    	stations.setTime(p.getX(), clockCycle);
	                    }
	            		
	            	}
	            	regFile.resetRow(Integer.parseInt(addStations.getStation(i).getA().substring(1))-1);
	            	addStations.getStation(i).reset(); // Reset the station before removal (if needed)
	            	// Time has reached zero, perform necessary actions (empty for now)
	            }
	            if(time!=0){
	            	time = time - 1;
	            	station.setTime(time);
	            }
	        }
	    }
	
	    // Loop over mulStations
	    for (int i = 0; i < mulStations.size(); i++) {
	        ReservationStation station = mulStations.get(i);
	        Integer time = station.getTime();
	        if (time != null) {
	        	if(time == 2) {table.setExecutionStart(i, clockCycle);}
	            // Subtract 1 from time
	            time = time - 1;
	            station.setTime(time);
	            if (time == 0) {
	            	table.setExecutionEnd(i, clockCycle);
	            	List<Point> l= regFile.get(Integer.parseInt(mulStations.get(i).getA().substring(1))-1).getList();
	            	
	            	for(Point p:l) {
	            		ReservationStations stations = p.getOp().equals("ADD") ? addStations : mulStations;
	            		System.out.println(p);
	            		if(p.getY().equals("qj"))
	            			stations.setStationJ(p.getX(), regFile.get(Integer.parseInt(p.getZ().substring(1))-1).getName());
	            		if(p.getY().equals("qK"))
	            			stations.setStationK(p.getX(), regFile.get(Integer.parseInt(p.getZ().substring(1))-1).getName());
	            	}
	            	regFile.resetRow(Integer.parseInt(mulStations.getStation(i).getA().substring(1))-1);
	                mulStations.getStation(i).reset(); // Reset the station before removal (if needed)
	                // Time has reached zero, perform necessary actions (empty for now)
	            }
	        }
	    }
	    //writeBack needs to be changed 
	    for(int write:writeBack) {
	    	table.setWriteBack(write, clockCycle+1);
	    }
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

        program.updateReservationStation("A", true, "ADD", "R1", "R2", "Q1", "Q2", null,10); // Update A1

        // Print updated tables
        program.printTables();
    }
}
