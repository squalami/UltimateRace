package UltimateRace;
/**
 * Ultimate Race
 * RaceTrack.java
 * 
 * F.Doan
 */
import java.awt.Color;
import java.util.ArrayList;
import jig.engine.GameFrame;
import jig.engine.RenderingContext;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class RaceTrack extends VanillaAARectangle {
	
	static int cameraHeight  = 500;
	static int roadWidth     = 300;            
	static int segmentLength = 25;  
	static int fieldOfView = 135;  
	static int drawDistance   = 150;             
	static int rumbleLength  = 3; 
	
	int trackLength    = 0;            
	int lanes = 2;             
	
	double screenX = 0;
	double screenY = 0;	
	double screenW = 0;
	double cameraX = 0;
	double cameraY = 0;
	double cameraZ = 0;
	double worldX  = 0;
	double worldY  = 0;
	double worldZ  = 0;
	double curZpos = 0;               
	double preZpos = 0;               

	double cameraDepth = 1 / Math.tan((fieldOfView/2) * Math.PI/180);
	double carZ  = cameraHeight * cameraDepth;
	double carX;
	double carY;
	double centrifugal  = 0.3;             	
	double maxSpeed = 20; 
	double offRoadDecel   = -maxSpeed/2;     
	double currentLapTime = 0;               
	double lastLapTime    = 0;  
	
	
	BuildTrack track;
	Vector2D carPos, carPrePos;
	Car car;
	
	long time = 0;
	int curIndex = 0;
	int totalIndex = 0;
	int updateDelay = 0;
	RoadSegment curSegment;
	
	boolean startTrack = true;
	Road Rd;
	
	ArrayList<PolyHolder> grasses;
	ArrayList<PolyHolder> rumbles;
	ArrayList<PolyHolder> road;
	
	public RaceTrack(String sprite, GameFrame gameframe, Car car, Grass g, Road rd, Rumbles r) {
		super(sprite);
		// TODO Auto-generated constructor stub		
		track = new BuildTrack(carZ);
		totalIndex = track.segments.size();
		System.out.println("track size:"+totalIndex);
		trackLength = track.segments.size() * segmentLength;
		this.car = car;
		carPos = car.getPosition();
		carPrePos = carPos;
		preZpos = curZpos;
		grasses = g.grasses;
		rumbles = r.rumbles;
		this.Rd = rd;
		road = rd.road;
		curIndex = rd.curIndex;
	}

	@Override
	public void update(long deltaMs) {
		double dx = 0.002;
		double preCarX = carX;
		time++;
		curIndex = Rd.curIndex;
		updateDelay = (int) (27 - (maxSpeed * car.speed * 10));

		carPos = car.getPosition();
		carZ  = cameraHeight * cameraDepth;

		if (curIndex + 2 > totalIndex) {
			curZpos = 0;
		}
		
		if (carPos.getX() != carPrePos.getX() || startTrack) {
			road.clear();
			rumbles.clear();
			grasses.clear();
		    updateTrack(deltaMs);
		    carPrePos = carPos;
		    startTrack = false;

		}

		if (Game.speedUp) {
			if (car.speed < maxSpeed) {
				car.speed += 0.001;
			}
			
		} else if (Game.applybreak) {
			if (car.speed > 0) car.speed *= 0.1;
		} else {
			if (car.speed > 0) car.speed *= 0.99;
			//else car.speed = 0;
		}
		
		if (Game.turnLeft) {
			
			//carX = carX - dx - (dx * car.speed/7);
			carX = carX - dx - (dx * car.speed * centrifugal);
			//System.out.println("left carX: "+carX);
		} else if (Game.turnRight) {
			//carX = carX + dx + (dx * car.speed/7);
			carX = carX + dx + (dx * car.speed * centrifugal);
			//System.out.println("right carX: "+carX);
		}
		
		//carX = carX - (dx * car.speed * findSegment(carZ).x * centrifugal / maxSpeed);
		//carY = carPos.getY();
		//car.setCenterPosition(new Vector2D(carX,carY));
		
		if (car.speed > 0 || preCarX != carX) {
			curZpos = increase(curZpos, deltaMs * car.speed, trackLength);
		}
		if (time > updateDelay) {
			
			//System.out.println("preZpos"+preZpos+", curZpos:"+curZpos);
			if (carPos.getX() != carPrePos.getX() || curZpos != preZpos) {
				road.clear();
				rumbles.clear();
				grasses.clear();
			    updateTrack(deltaMs);
			    carPrePos = carPos;
			    startTrack = false;
			    preZpos = curZpos;
			    Game.runUpdate = true;
			}

			time = 0;			

		}
		
		
	}
	
	@Override
	public void render(RenderingContext rc) {		
		//super.render(rc);
	}
	//*	
	public double increase(double start, double increment, double max) {
		double result = start + increment;
		while (result > max) result -= increment;
		while (result < 0)  result += increment; 
		return result;		
	}
	//*/

	public void updateTrack(long dt) {
		
		RoadSegment baseSegment = findSegment(curZpos);
		RoadSegment carSegment = findSegment(curZpos+carZ);
				
		double basePercent = percentRemaining(curZpos, segmentLength);
		double carPercent = percentRemaining(curZpos+carZ,segmentLength);
		double dx = - baseSegment.x * basePercent;
		double maxY = Game.WORLD_HEIGHT;
		
		double x = 0;
		carY = interpolate(carSegment.lowerY, carSegment.upperY, carPercent);
		double cZ = 0;
		
		for(int n = 0 ; n < drawDistance ; n++) {

			RoadSegment segment = track.segments.get((int)(baseSegment.index + n) % track.segments.size());
			if (segment.index < baseSegment.index) {
				cZ = segmentLength;
			}

			worldX = segment.x;
			worldY = segment.lowerY;
			worldZ = segment.lowerZ;
			projectSegment((roadWidth * carX) - x, carY + cameraHeight, curZpos - cZ ,cameraDepth,Game.WORLD_WIDTH,Game.WORLD_HEIGHT, roadWidth);			
			segment.lowerScreenX = screenX;
			segment.lowerScreenY = screenY;
			segment.lowerScreenW = screenW;
			
			worldY = segment.upperY;
			worldZ = segment.upperZ;
			projectSegment((roadWidth * carX)- x - dx, carY + cameraHeight, curZpos - cZ, cameraDepth, Game.WORLD_WIDTH, Game.WORLD_HEIGHT, roadWidth);
			segment.upperScreenX = screenX;
			segment.upperScreenY = screenY;
			segment.upperScreenW = screenW;

			x  = x + dx;
			dx = dx + segment.x;
			
			//*
			 // already rendered
			if ( segment.upperScreenY >= segment.lowerScreenY ||  segment.upperScreenY >= maxY)  {            
				//System.out.println("upperCamZ:"+segment.upperCamZ+", cameraDepth:"+cameraDepth+
				//		", upperScreenY:"+segment.upperScreenY+", lowerScreeny:"+segment.lowerScreenY+", maxY"+maxY);
				continue;
			}
		    //*/
			
			maxY = segment.lowerScreenY;
			createPolygons(Game.WORLD_WIDTH,lanes,segment,n);			
			
		}

	}
	
	private void createPolygons(int worldWidth, int lanes, RoadSegment segment, int index) {

		double w1 = segment.lowerScreenW;
		double w2 = segment.upperScreenW;
		double x1 = segment.lowerScreenX;
		double x2 = segment.upperScreenX;
		double y1 = segment.lowerScreenY;
		double y2 = segment.upperScreenY;
	    double r1 = rumbleWidth(w1, lanes);
	    double r2 = rumbleWidth(w2, lanes);
	    double l1 = laneMarkerWidth(w1, lanes);
	    double l2 = laneMarkerWidth(w2, lanes);
	    segment.roadCenter = ((x2+w2)-(x1-w1))/2;
	    Color rumbleColor;
	    Color grassColor;
	    Color roadColor = Color.gray;
	    
	    boolean drawSeparator = false;
	    
	    if (Math.floor(segment.index % 2) == 0) {
	    	rumbleColor = Color.red;
	    	grassColor = new Color(20,144,4);
	    } else {
	    	rumbleColor = Color.white;
	    	grassColor = new Color(20,175,4);
	    	drawSeparator = true;
	    }
	    
	    // left & right grass fields
	    addPolygon(index, segment.index, PolyHolder.Type.GRASS, grassColor, 0, y1, 0, y2, (x2-w2-r2),y2, (x1-w1-r1),y1);
	    addPolygon(index, segment.index, PolyHolder.Type.GRASS, grassColor, worldWidth, y1, worldWidth, y2, (x2+w2+r2), y2, (x1+w1+r1),y1);
	    
	    if (segment.index == 12 || segment.index == 14) {
	    	roadColor = Color.white;  // start line
	    } else if (segment.index == totalIndex - 48 || segment.index == totalIndex - 50) {
	    	roadColor = Color.red;    // finish line
	    }
	    // left & right rumble lanes	    
	    addPolygon(index, segment.index, PolyHolder.Type.RUMBLE, rumbleColor, x1-w1-r1, y1, x1-w1, y1, x2-w2, y2, x2-w2-r2, y2);
	    addPolygon(index, segment.index, PolyHolder.Type.RUMBLE, rumbleColor, x1+w1+r1, y1, x1+w1, y1, x2+w2, y2, x2+w2+r2, y2);
	    
	    // the road
	    addPolygon(index, segment.index, PolyHolder.Type.ROAD, roadColor, x1-w1, y1, x1+w1, y1, x2+w2, y2, x2-w2, y2);
	    
    	
	    if (drawSeparator) {  
		    double lanew1 = w1*2/lanes;
		    double lanew2 = w2*2/lanes;
		    double lanex1 = x1 - w1 + lanew1; 
		    double lanex2 = x2 - w2 + lanew2; 
	        for(int lane = 1 ; lane < lanes ; lanex1 += lanew1, lanex2 += lanew2, lane++) {
	        	addPolygon(index, segment.index, PolyHolder.Type.SEPARATOR, rumbleColor, lanex1 - l1/2, y1, lanex1 + l1/2, y1, lanex2 + l2/2, y2, lanex2 - l2/2, y2);
	        }
	    }
	        
	}

	public void addPolygon(int i, int si, PolyHolder.Type t, Color c, double x1, double y1,
			double x2, double y2, double x3, double y3, double x4, double y4) {
		PolyHolder ph = new PolyHolder();
		ph.points = new ArrayList<Vector2D>();
		ph.points.add(new Vector2D(x1,y1));
		ph.points.add(new Vector2D(x2,y2));
		ph.points.add(new Vector2D(x3,y3));
		ph.points.add(new Vector2D(x4,y4));
		ph.C = c;
		ph.T = t;
		ph.index = i;
		ph.segmentIndex = si;
		//System.out.println(" adding polygon type:"+t);
		if (t == PolyHolder.Type.SEPARATOR || t == PolyHolder.Type.ROAD) {
			road.add(ph);
		} else if (t == PolyHolder.Type.GRASS) {
			grasses.add(ph);
		} else if (t == PolyHolder.Type.RUMBLE) {
			rumbles.add(ph);
		}		
	}
	
	//rumbleWidth:     
    public double rumbleWidth(double roadWidth, int lanes) { 
    	return roadWidth/Math.max(9,  2*lanes); 
    }
    
    //laneMarkerWidth: 
    public double laneMarkerWidth(double roadWidth, int lanes) { 
    	return roadWidth/Math.max(32, 8*lanes); 
    }
    
	public double percentRemaining(double n, double total)  { 
		return (n%total)/total;                                         
	}
	
	public void projectSegment(double cX, double cY, double cZ, 
			double cameraDepth, double width, double height, double roadWidth) {
		cameraX = worldX - cX;
		cameraY = worldY - cY;
		cameraZ = worldZ - cZ;
		double scaleFactor = cameraDepth/cameraZ;
		screenX = Math.round((width/2)  + (scaleFactor * cameraX  * width/2));
		screenY = Math.round((height/2) - (scaleFactor * cameraY  * height/2));
		screenW = Math.round(scaleFactor * roadWidth   * width/2);
	}

	public RoadSegment findSegment(double z) {
		return track.segments.get((int) (Math.floor(z/segmentLength) % track.segments.size())); 
	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActivation(boolean a) {
		// TODO Auto-generated method stub
		
	}
	
	public double interpolate(double a, double b, double percent) { 
		return a + (b-a)*percent;                                       
	}

}