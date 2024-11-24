package RegFile;
import java.util.ArrayList;
import java.util.List;
public class RegFile {
    private String name;      // Name of the register (e.g., R1, R2, ...)
    private String qi;        // Reservation station (e.g., M1), or null if none
    private List<Point> List;

    // Constructor
    public RegFile(String name) {
        this.name = name;
        this.qi = null;       // Default to no reservation station
        this.List = new ArrayList<Point>();
    }

    // Getter for name
    public String getName() {
        return name;
    }

    // Getter for Qi
    public String getQi() {
        return qi;
    }

    // Setter for Qi
    public void setQi(String qi) {
        this.qi = qi;
    }
 // reset for Qi
    public void resetRow() {
        this.qi = null;
        this.List = new ArrayList<Point>(); 
    }

    @Override
    public String toString() {
        return "Register{" +
               "name='" + name + '\'' +
               ", qi=" + (qi == null ? "null" : qi) +
               '}';
    }
    public List<Point> getList(){
    	return List;
    }
    public void add(Point p) {
    	List.add(p);
    }
}

