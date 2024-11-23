package reservation_tables;

import java.util.ArrayList;
import java.util.List;

import LoadBuffer.LoadBuffer;

public class ReservationStations {
    private List<ReservationStation> stations; // List to hold all reservation stations

    // Constructor to create 'n' reservation stations dynamically
    public ReservationStations(int n,String s) {
        stations = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            stations.add(new ReservationStation(s+""+ i)); // Generate names A1, A2, ...
        }
    }

    // Get a specific reservation station by index
    public ReservationStation getStation(int index) {
        if (index >= 0 && index < stations.size()) {
            return stations.get(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid station index: " + index);
        }
    }

    // Reset all reservation stations
    public void resetAllStations() {
        for (ReservationStation station : stations) {
            station.reset();
        }
    }

    // Print the state of all reservation stations
    public void printAllStations() {
        for (ReservationStation station : stations) {
            System.out.println(station);
        }
    }
    public ReservationStation get(int i) {
    	return stations.get(i);
    }
    public int size() {
    	return stations.size();
    }

    // Update a specific reservation station with setAll
    public void setStation(int index, boolean busy, String op, String vj, String vk, String qj, String qk, String a,Integer Time) {
        if (index >= 0 && index < stations.size()) {
            stations.get(index).setAll(busy, op, vj, vk, qj, qk, a,Time);
        } else {
            throw new IndexOutOfBoundsException("Invalid station index: " + index);
        }
    }
}
