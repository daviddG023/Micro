package RegFile;

public class Point {
    private int x; // x-coordinate
    private String y; // y-coordinate
    private String z; // y-coordinate
    

    // Constructor
    public Point(int x, String y,String z) {
        this.x = x;
        this.y = y;
        this.z = z;
        
    }

    // Getter for x
    public int getX() {
        return x;
    }

    // Setter for x
    public void setX(int x) {
        this.x = x;
    }

    // Getter for y
    public String getY() {
        return y;
    }

    // Setter for y
    public void setY(String y) {
        this.y = y;
    }
    // Getter for z
    public String getZ() {
        return z;
    }

    // Setter for z
    public void setZ(String z) {
        this.z = z;
    }
 


    @Override
    public String toString() {
        return "Point{" +
               "x=" + x +
               ", y=" + y +
               ", z=" + z +
               '}';
    }
}
