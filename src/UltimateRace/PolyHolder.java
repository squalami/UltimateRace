package UltimateRace;
/**
 * Ultimate Race
 * 
 * F.Doan
 */

import java.awt.Color;
import java.util.ArrayList;
import jig.engine.util.Vector2D;

public class PolyHolder implements Comparable<PolyHolder> {
	public static enum Type { GRASS, RUMBLE, ROAD, SEPARATOR }
	int index;
	int segmentIndex;
	Type T;	
	ArrayList<Vector2D> points;	
	Color C;	
	PolyHolder () { }
	@Override
	public int compareTo(PolyHolder other) {
		return Double.compare(index,other.index);
	}
}
