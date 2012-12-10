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
import jig.engine.ResourceFactory;
import jig.engine.audio.AudioState;
import jig.engine.audio.jsound.AudioClip;
import jig.engine.audio.jsound.AudioStream;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;

public class RaceTrack extends VanillaAARectangle {

	static int cameraHeight  = 500;
	static int roadWidth     = 400;            
	static int segmentLength = 25;  
	static int fieldOfView = 135;  
	static int drawDistance   = 150;             
	static int rumbleLength  = 3; 

	int trackLength    = 0;            
	int lanes = 3;             

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
	double orgCarX;
	double carY;
	double centrifugal  = 0.3;             	
	double maxSpeed = 5; 
	double curMaxSpeed = maxSpeed;
	double offRoadDecel   = -maxSpeed/2;     
	double dx;

	BuildTrack track;
	Vector2D carPos, carPrePos;
	Car car;

	long time = 0;
	long currentLapTime = 0;    

	int curIndex = 0;
	int totalIndex = 0;
	int updateDelay = 0;
	int bigLoop = 7777777;
	int medLoop = 77777;
	int smallLoop = 777;
	RoadSegment curSegment;

	int fireTimer = 450;
	int fireTimerCount = 0;
	boolean startFireTimer = false;

	boolean hitGrass = false;
	boolean hitRumble = false;
	boolean carHitObject = false;
	Road Rd;

	ArrayList<PolyHolder> grasses;
	ArrayList<PolyHolder> rumbles;
	ArrayList<PolyHolder> road;
	BodyLayer<OffroadObjects> offroadLayer;

	AudioClip crash;
	AudioStream decel;
	AudioStream full;
	AudioStream idle;
	AudioStream revup;
	AudioStream rsound;
	AudioStream skid;
	AudioStream standRevup;
	AudioStream boostUp;
	RoadSegment baseSegment;
	RoadSegment carSegment;

	public RaceTrack(String sprite, GameFrame gameframe, Car car, Grass g, Road rd, Rumbles r) {
		super(sprite);
		// TODO Auto-generated constructor stub		
		track = new BuildTrack(carZ,car);
		totalIndex = track.segments.size();
		System.out.println("track size:"+totalIndex);
		trackLength = track.segments.size() * segmentLength;
		this.car = car;
		carPrePos = car.getPosition();
		preZpos = curZpos;
		grasses = g.grasses;
		rumbles = r.rumbles;
		this.Rd = rd;
		road = rd.road;
		curIndex = rd.curIndex;
		crash = ResourceFactory.getFactory().getAudioClip("resources/" + "Crash.wav");
		decel = new AudioStream("resources/" + "Decel.wav");
		full = new AudioStream("resources/" + "FullIdle.wav");
		idle = new AudioStream("resources/" + "idle.wav");
		revup = new AudioStream("resources/" + "revup.wav");
		rsound = new AudioStream("resources/" + "Rumble.wav");
		skid = new AudioStream("resources/" + "Skid.wav");
		standRevup = new AudioStream("resources/" + "standRevup.wav");
		boostUp = new AudioStream("resources/" + "speedBoost.wav");
		road.clear();
		rumbles.clear();
		grasses.clear();
		curSegment = null;
		updateTrack();		    
	}
	
	public void resetRaceTrack() {
		curZpos = 0;
		carX = 0;
		carY = 0;
		track = new BuildTrack(carZ,car);
		road.clear();
		rumbles.clear();
		grasses.clear();
		curSegment = null;
		updateTrack();
	}

	@Override
	public void update(long deltaMs) {

		if (Game.standRevup) {
			car.setFire = true;
			car.setSmoke = true;
			idle.pause();
			if (standRevup.getState() == AudioState.PAUSED) {
				standRevup.resume();
			} else {
				standRevup.loop(1.5,medLoop);
			}
		} else {
			car.setFire = false;
			car.setSmoke = false;
			pauseAllAudio();
			if (idle.getState() == AudioState.PAUSED) 
				idle.resume();
			else if (idle.getState() != AudioState.PLAYING)
				idle.loop(1,medLoop);
			
		}

		if (Game.gameIsRun) {
			if (car.speed < 0.7) 
				dx = 0.005;
			else
				dx = 0.002 * (car.speed * 10);  

			double preCarX = carX;		
			time++;
			curIndex = Rd.curIndex;

			if (startFireTimer) {
				fireTimerCount++;
			}	
			if (hitGrass) {
				curMaxSpeed = 0.15;
			} else {
				curMaxSpeed = maxSpeed;
			}
			updateDelay = (int) (27 - (maxSpeed * car.speed * 10));

			carPos = car.getPosition();
			carZ  = cameraHeight * cameraDepth;

			if (curIndex + 2 > totalIndex) {
				curZpos = 0;
				car.lap++;
			}

			car.state = curSegment.state;



			if (Game.speedUp) {
				if (startFireTimer) {
					car.setFire = true;
					if (full.getState() == AudioState.PLAYING) full.pause();
					if (boostUp.getState() == AudioState.PAUSED ) {
						boostUp.resume();
					} else if (boostUp.getState() == AudioState.PRE
							|| boostUp.getState() == AudioState.STOPPED){
						boostUp.loop(1.5,medLoop);
					}
					if (car.speed < curMaxSpeed) car.speed += 0.002;
					if (fireTimerCount >= fireTimer) {
						fireTimerCount = 0;
						startFireTimer = false;
						car.setFire = false;
						boostUp.pause();
						startFireTimer = false;
					}
				}

				else if (car.speed < curMaxSpeed) {

					if (car.speed < 1.3) {
						car.speed += 0.001;
					} else if (car.speed >= 1.3 && car.speed < 2.5) {
						car.speed += 0.0002;
					} else if (car.speed >= 2.5) {
						car.speed += 0.00005;
					}
					if (decel.getState() == AudioState.PLAYING) decel.pause();
					if (car.speed > 0 && car.speed < 0.7) {
						car.setFire = true;
						car.setSmoke = true;
						if (car.offRoad) {
							car.setSmoke = false;
						}
						if (full.getState() == AudioState.PLAYING) full.pause();
						if (revup.getState() == AudioState.PAUSED ) {
							revup.resume();
						} else if (revup.getState() == AudioState.PRE
								|| revup.getState() == AudioState.STOPPED){
							revup.loop(1.5,medLoop);
						}
					} else if (car.speed >= 0.7) {
						car.setSmoke = false;
						car.setFire = true;
						revup.pause();
						if (full.getState() == AudioState.PAUSED )
							full.resume();
						else
							full.loop(2,bigLoop);

						if (car.speed >= 1.3) {
							car.setFire = false;
						}
					}
				} else {
					if (car.speed > 0.7) {
						revup.pause();
						if (full.getState() == AudioState.PAUSED )
							full.resume();
						else
							full.loop(2,bigLoop);
					} 
				}

			} else if (Game.applybreak) {
				if (car.speed > 0) car.speed *= 0.1;
			} else {

				if (boostUp.getState() == AudioState.PLAYING) 
					boostUp.pause();

				car.speed *= 0.99;

				if (car.speed > 0.05) {
					full.pause();
					revup.pause();
					if (decel.getState() == AudioState.PAUSED ) 
						decel.resume();
					else
						decel.loop(0.6,smallLoop);
				} else {
					if (decel.getState() == AudioState.PLAYING )
						decel.pause();
					if (idle.getState() == AudioState.PAUSED ) 
						idle.resume();
				}
				//else car.speed = 0;
			}

			if (Game.turnLeft) {
				carX = carX - dx - (dx * car.speed * centrifugal);
			} else if (Game.turnRight) {
				carX = carX + dx + (dx * car.speed * centrifugal);
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
					curSegment = null;
					updateTrack();
					preZpos = curZpos;
					Game.runUpdate = true;
				}

				time = 0;			

			}

			checkCollision();
		}
	}

	private void checkCollision() {
		// TODO Auto-generated method stub
		double xL = carPos.getX();
		double xR = xL + car.getWidth();
		double cY = carPos.getY();

		// hit rumble strips

		if ((xL < curSegment.rumbleLeft && xR > curSegment.rumbleLeft - rumbleLength) || 
				(xR > curSegment.rumbleRight && xL < curSegment.rumbleRight + rumbleLength)) {
			//System.out.println("hit rumble");
			hitRumble = true;
			if (car.speed > 0) {
				if (revup.getState() == AudioState.PLAYING) revup.pause();
				if (full.getState() == AudioState.PLAYING) full.pause();
				if (idle.getState() == AudioState.PLAYING) idle.pause();
				if (rsound.getState() == AudioState.PAUSED)
					rsound.resume();
				else
					rsound.loop(5, smallLoop);

				if (cY == carPrePos.getY()) {
					car.setPosition(new Vector2D(carPos.getX(),carPos.getY()-5));

				}
				else if (cY < carPrePos.getY()) {
					car.setPosition(new Vector2D(carPos.getX(),carPrePos.getY()));

				} 

				car.speed *= 0.98;
			}
		}

		// hit grasses
		else if (xR <= curSegment.grassLeft || xL >= curSegment.grassRight) {
			hitGrass = true;
			rsound.pause();
			car.offRoad = true;
			if (car.speed > 0.15) {
				if (full.getState() == AudioState.PLAYING)
					full.pause();
				car.speed -= 0.1;
				if (decel.getState() == AudioState.PAUSED) 
					decel.resume();
				else
					decel.loop(1.5,medLoop);

			}
		} else {
			hitGrass = false;
			hitRumble = false;
			rsound.pause();
			car.offRoad = false;
			//if (decel.getState() == AudioState.PLAYING) decel.pause();
			if (car.speed < 0.0005 && !Game.standRevup) {
				if (revup.getState() == AudioState.PLAYING)  revup.pause();
				if (idle.getState() != AudioState.PLAYING) {
					idle.loop(1,medLoop);
				}
			} else if (car.speed > 0) {
				idle.pause();

			}
			if (cY < carPrePos.getY()) {
				car.setPosition(new Vector2D(carPos.getX(),carPrePos.getY()));

			}
		}

		if (curSegment.curve && car.speed > 0.4) {
			car.setSmoke = true;
			if (skid.getState() == AudioState.PAUSED)
				skid.resume();
			else
				skid.loop(1.5,smallLoop);
		} else {
			skid.pause();
		}

		if (carHitObject) {
			car.speed = -(car.speed * 1.5);
			crash.play(2.5);
			if (car.getPosition().getX() < road.get(0).xL) 
				carX = carX + 7*dx + (7*dx * car.speed *centrifugal);
			else
				carX = carX - (7*dx) - (7*dx * car.speed * centrifugal);
			car.speed = 0;
			carHitObject = false;
		}
	}

	@Override
	public void render(RenderingContext rc) {		
		//super.render(rc);
	}

	public double increase(double start, double increment, double max) {
		double result = start + increment;
		while (result > max) result -= increment;
		while (result < 0)  result += increment; 
		return result;		
	}

	public void updateTrack() {

		baseSegment = findSegment(curZpos);
		carSegment = findSegment(curZpos+carZ);

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
		segment.grassLeft = x2-w2-r2;
		segment.grassRight = x2+w2+r2;
		segment.rumbleLeft = x2-w2;
		segment.rumbleRight = x2+w2;
		Color rumbleColor;
		Color grassColor;
		Color roadColor = Color.gray;

		boolean drawSeparator = false;

		if (curSegment == null) curSegment = segment;

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
		ph.xL = minVal(x1,x2,x3,x4);
		ph.xR = maxVal(x1,x2,x3,x4);
		ph.yT = y2;
		ph.yB = y1;
		ph.w = ph.xR - ph.xL;
		//System.out.println(" adding polygon type:"+t);
		if (t == PolyHolder.Type.ROAD) {
			road.add(ph);
		} else if (t == PolyHolder.Type.GRASS) {
			grasses.add(ph);
		} else if (t == PolyHolder.Type.SEPARATOR || t == PolyHolder.Type.RUMBLE) {
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

	public void pauseAllAudio() {
		if (decel.getState() == AudioState.PLAYING) decel.pause();
		if (full.getState() == AudioState.PLAYING) full.pause();
		if (idle.getState() == AudioState.PLAYING) idle.pause();
		if (revup.getState() == AudioState.PLAYING) revup.pause();
		if (rsound.getState() == AudioState.PLAYING) rsound.pause();
		if (skid.getState() == AudioState.PLAYING) skid.pause();
		if (standRevup.getState() == AudioState.PLAYING) standRevup.pause();
		if (boostUp.getState() == AudioState.PLAYING) boostUp.pause();
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

	private double minVal(double x1, double x2, double x3, double x4) {
		double min1 = 0;
		double min2 = 0;
		if (x1 < x2) min1 = x1;
		else min1 = x2;
		if (x3 < x4) min1 = x3;
		else min2 = x4;
		return (min1 < min2)? min1: min2;
	}

	private double maxVal(double x1, double x2, double x3, double x4) {
		double max1 = 0;
		double max2 = 0;
		if (x1 < x2) max1 = x2;
		else max1 = x1;
		if (x3 < x4) max2 = x4;
		else max2 = x3;
		return (max1 > max2)? max1: max2;
	}
}
