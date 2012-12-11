package UltimateRace;
/**
 * Ultimate Race
 * 
 * F.Doan
 */

public class RoadSegment implements Comparable<RoadSegment>, java.io.Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int index;
	long time;
	
	double x;       // curve
	double hill;
	double roadCenter; // center x value for road segment bottom edge
	double rumbleLeft;
	double rumbleRight;
	double grassLeft;
	double grassRight;
	
	double lowerY;  // world bottom y
	double upperY;  // world upper y
	double lowerZ;  // world lower z
	double upperZ;  // world upper z
	
	double lowerCamZ;
	double upperCamZ;
		
	double lowerScreenX = 0;
	double lowerScreenY = 0;
	double lowerScreenW = 0;
	double upperScreenX = 0;
	double upperScreenY = 0;
	double upperScreenW = 0;
	
	boolean curve = false;
	Car.State state;;
	
	
	public RoadSegment(Car.State s, boolean c, double hv) { 
		state = s; 
		curve = c; 
		hill = hv;
	}
	
	public int compareTo(RoadSegment other) {
		return Double.compare(index,other.index);
	}

}
