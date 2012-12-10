package UltimateRace;
/**
 * Ultimate Race
 * F.Doan
 */

import java.util.ArrayList;
import java.util.HashMap;

public class BuildTrack {

	int segmentLength = RaceTrack.segmentLength;
	int index = 0;
	int fieldOfView = RaceTrack.fieldOfView;             
	int level;
	int curve;
	int hill;
	double carZ;
	boolean isCurve = false;
	Car car;
	Car.State state;

	public ArrayList<RoadSegment> segments = new ArrayList<RoadSegment>();
	public static HashMap<Integer,Boolean> curChk = new HashMap<Integer,Boolean>();
	
	public BuildTrack(double carZ, Car c) {
		
		level = Game.level;
		this.carZ = carZ;
		buildTrack();
		car = c;
	}
	
	public void buildTrack() {
		segments.clear();
		curve = 2 * level;
		hill  = 10 * level;
		
		straightSegments(25);
		curveSegments(35,35,0);
		curveSegments(35,-35,0);
		hillSegments(45,35);
		hillSegments(45,-35);
		straightSegments(35);
		Scurve(75);
		straightSegments(35);
		curveSegments(25,-45,-2);
		straightSegments(35);
		Scurve(45);
		rollingHills(45);
		straightSegments(25);
		Scurve(75);
		straightSegments(35);
		hillSegments(25,25);
		hillSegments(25,-25);
		straightSegments(25);		
				
	}
	
	private void straightSegments(double runL) {
		createSegments(runL,runL,runL,0,0);
	}
	
	private void hillSegments(double runL, double h) {
		createSegments(runL,runL,runL,0,h);
	}
	
	private void curveSegments(double runL, double h, double c) {
		createSegments(runL,runL,runL,c,h);
	}
	
	private void rollingHills(double runL) {
		createSegments(runL,runL,runL,0,hill/2);
		createSegments(runL,runL,runL,0,-hill);
		createSegments(runL,runL,runL,curve,hill);
		createSegments(runL,runL,runL,0,0);
		createSegments(runL,runL,runL,-curve,hill/2);
		createSegments(runL,runL,runL,0,0);
	}
	
	private void Scurve(double runL) {
		createSegments(runL, runL, runL,  -curve, 0);
		createSegments(runL, runL, runL,  curve+2,hill+10);
		createSegments(runL, runL, runL,   curve,   -hill);
		createSegments(runL, runL, runL,  -curve, hill+15);
		createSegments(runL, runL, runL,  -(curve+2), -(hill+5));
	}

	private void createSegments (double eIn, double runLength, double eOut,
			double x, double y) {
		RoadSegment s;
		double total = eIn + eOut + runLength;
		double sY = previousY();		
		double eY = sY + y * segmentLength;
		
		if (y < 0) { state = Car.State.DOWN; }
		else if (y > 0) { state = Car.State.UP; }
		else state = Car.State.STRAIGHT;
		
		for (int i=0; i < eIn; i++) {
			if (x != 0) curChk.put(index,true);
			else curChk.put(index, false);
			s = new RoadSegment(state,isCurve);
			s.lowerY = previousY();
			s.upperY = easeOut(sY,eY,(double)i/total);
			s.x = easeIn(0,x,(double)i/eIn);
			s.lowerZ = index * segmentLength;
			s.upperZ = (index+1) * segmentLength;
			s.index = index;
			segments.add(s);
			index++;
			if (i+3 > eIn && x != 0) isCurve = true;
		}
		
		if (x != 0) {
			isCurve = true;
		}
		
		for (int i=0; i < runLength; i++) {
			if (x != 0) curChk.put(index,true);
			else curChk.put(index, false);
			s = new RoadSegment(state,isCurve);
			s.lowerY = previousY();
			s.upperY = easeOut(sY,eY,((double)i+eIn)/total);
			s.x = x;
			s.lowerZ = index * segmentLength;
			s.upperZ = (index+1) * segmentLength;
			s.index = index;
			segments.add(s);
			index++;			
		}
		
		isCurve = false;
		
		for (int i=0; i < eOut; i++) {
			if (x != 0) curChk.put(index,true);
			else curChk.put(index, false);
			s = new RoadSegment(state,isCurve);
			s.lowerY = previousY();
			s.upperY = easeOut(sY,eY,((double)i+eIn+runLength)/total);
			s.x = easeOut(x,0,(double)i/eOut);
			s.lowerZ = index * segmentLength;
			s.upperZ = (index+1) * segmentLength;
			s.index = index;
			segments.add(s);
			index++;			
		}
        
	}

	private double previousY() {
		if (index > 0) {
			return segments.get(index-1).upperY;
		}
		return 0;		
	}

	private double easeIn(double a, double b, double percent) { 
		return a + (b-a)*Math.pow(percent,2);
	}
		
	private double easeOut(double a, double b, double percent) { 
		return a + (b-a)*((-Math.cos(percent*Math.PI)/2) + 0.5);
	}
	
}
