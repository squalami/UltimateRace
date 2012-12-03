package UltimateRace;
/**
 * CS447/547 Project 2 by F.Doan
 * 
 */

import UltimateRace.Car.State;
import java.util.logging.Level;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.audio.jsound.AudioStream;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.util.Vector2D;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import jig.engine.audio.jsound.AudioClip;



public class Game extends StaticScreenGame {

	static final int WORLD_WIDTH = 640;
	static final int WORLD_HEIGHT = 480;
	static int level = 1;
	static boolean turnLeft = false;
	static boolean turnRight = false;
	static boolean speedUp = false;
	static boolean applybreak = false;
	static boolean runUpdate = false;
	static boolean gameIsRun = false;
	static long iniTime = 0;

	static final String XMLFILE = "resources/sprites.xml";
	static final String SPRITE_SHEET = "resources/sprites.png";

	VanillaPhysicsEngine physics;
	AudioStream music;

	Car car1;
	Car car2;
	RaceTrack raceTrack;
	Road road;
	Grass grass;
	Rumbles rumbles;
	Background background;
	SpeedBoost boost;

	static Vector2D car1Pos;   // used for network race track update
	static Vector2D car2Pos;

	BodyLayer<Car> car1Layer;
	BodyLayer<Car> car2Layer;
	BodyLayer<RaceTrack> rtLayer;
	BodyLayer<RaceTrack> rumbleLayer;
	BodyLayer<Grass> grassLayer;
	BodyLayer<Road> roadLayer;
	BodyLayer<Rumbles> rbLayer;
	BodyLayer<SpeedBoost> boostLayer;

	BodyLayer<Background> backgroundLayer;
	NetworkC GameNet;
	boolean isServ;
	boolean isNet=false;
	String IP;
	RoadSegment carRoad;
	CarHitRumbles hitRumbles;
	CarHitGrass hitGrass;

	VanillaPhysicsEngine checkCollisions = new VanillaPhysicsEngine();

	public Game() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new VanillaPhysicsEngine();
		//music = new AudioStream("resources/RhythmSphere3.mp3");
		ResourceFactory.getFactory().loadSheet(SPRITE_SHEET, XMLFILE);

		background = new Background(SPRITE_SHEET + "#background");
		backgroundLayer = new AbstractBodyLayer.IterativeUpdate<Background>();
		backgroundLayer.add(background);
		gameObjectLayers.add(backgroundLayer);

		road = new Road(SPRITE_SHEET + "#trans",gameframe);
		rumbles = new Rumbles(SPRITE_SHEET + "#trans",gameframe);
		grass = new Grass(SPRITE_SHEET + "#trans",gameframe);
		
		boost = new SpeedBoost(SPRITE_SHEET + "#boost", gameframe);

		car1 = new Car(SPRITE_SHEET + "#redCar",
				new Vector2D((WORLD_WIDTH/2 - 120),WORLD_HEIGHT-70));
		car1Pos = car1.getPosition();

		car2 = new Car(SPRITE_SHEET + "#greenCar",
				new Vector2D((3*WORLD_WIDTH/4 - 120),WORLD_HEIGHT-70));
		car2Pos = car2.getPosition();
		if(isNet){
			System.out.println("Please enter none for host or an ip for client: ");
			Scanner s=new Scanner(System.in);
			IP=s.nextLine();
			GameNet=new NetworkC(IP);
			if(IP.equalsIgnoreCase("none")){
				isServ=true;
				raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car1,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
			}
			else{
				isServ=false;
				raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car2,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
			}
		}
		else
			raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
					car1,  // this to be modified in network mode
					grass,
					road,
					rumbles
					);

		rtLayer = new AbstractBodyLayer.IterativeUpdate<RaceTrack>();
		rtLayer.add(raceTrack);
		gameObjectLayers.add(rtLayer);
		physics.manageViewableSet(rtLayer);
		
		boostLayer = new AbstractBodyLayer.IterativeUpdate<SpeedBoost>();
		boostLayer.add(boost);
		gameObjectLayers.add(boostLayer);
		physics.manageViewableSet(boostLayer);
		
		roadLayer = new AbstractBodyLayer.IterativeUpdate<Road>();
		roadLayer.add(road);
		gameObjectLayers.add(roadLayer);
		physics.manageViewableSet(roadLayer);

		rbLayer = new AbstractBodyLayer.IterativeUpdate<Rumbles>();
		rbLayer.add(rumbles);
		gameObjectLayers.add(rbLayer);
		physics.manageViewableSet(rbLayer);

		grassLayer = new AbstractBodyLayer.IterativeUpdate<Grass>();
		grassLayer.add(grass);
		gameObjectLayers.add(grassLayer);
		physics.manageViewableSet(grassLayer);

		car1Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
		car1Layer.add(car1);		
		car2Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
		car2Layer.add(car2);

		gameObjectLayers.add(car1Layer);
		physics.manageViewableSet(car1Layer);
		gameObjectLayers.add(car2Layer);
		physics.manageViewableSet(car2Layer);

		hitRumbles = new CarHitRumbles(car1,rumbles);
		hitGrass = new CarHitGrass(car1,grassLayer);
		if (isNet) {
			if (isServ) {
				gameObjectLayers.add(new GameUI(car1));
			} else {
				gameObjectLayers.add(new GameUI(car2));
			}
		} else {
			gameObjectLayers.add(new GameUI(car1));
		}


	}

	public void update(long deltaMs) {
		super.update(deltaMs);
		carS p1;
		carS p2;
		int j;
		int k;
		int k2;
		double car1Dist;
		double car2Dist; 
		double calcX;
		RoadSegment segOrg=new RoadSegment(State.STRAIGHT,false);
		if(isNet){
			if(isServ){
				p1=GameNet.reciveData();
				car2Dist=p1.carDist;
				p2=new carS();
				p2.p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
				j=p2.p1.index;
				k=p1.p1.index+1;
				k2=p1.p1.index;
				p2.lap=car1.lap;
				p2.runTime=car1.elapsedTime;
				if(car1.state==State.DOWN){
					p2.state=1;
				}
				else if(car1.state==State.STRAIGHT){
					p2.state=2;
				}
				else
					p2.state=3;
				p2.carDist=car1.getPosition().getY()-p1.p1.lowerScreenY;
				car1Dist=p2.carDist;
				GameNet.sendData(p2);
				car2.setActivation(false);
				for(int i=0;i<raceTrack.drawDistance-30;i++){
					if(j==k2){
						segOrg=raceTrack.track.segments.get(j);

						//System.out.println(p1.p1.index+","+j);
						car2.setActivation(true);

						calcX=car2.startPos.getX()-p1.p1.lowerScreenX;
						//System.out.println(segOrg.lowerScreenX);
						double pp=(segOrg.upperScreenX+segOrg.upperScreenW-segOrg.lowerScreenX+segOrg.lowerScreenW);
						double ppp=(p2.p1.upperScreenX+p2.p1.upperScreenW-p2.p1.lowerScreenX+p2.p1.lowerScreenW);
						calcX=calcX*(pp/ppp);
						car2.scal2H=(segOrg.lowerScreenY-segOrg.upperScreenY)/(p2.p1.lowerScreenY-p2.p1.upperScreenY);
						car2.currWidth=(int)(car2.getWidth()*car2.scal2H);
						car2.currHeight=(int)(car2.getHeight()*car2.scal2H);
						//car2.setPosition(new Vector2D(segOrg.lowerScreenX,car2.startPos.getY()));
						if(calcX<0)
							car2.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car2.getWidth()/2,segOrg.lowerScreenY-car2.currHeight));
						else
							car2.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car2.getWidth()/2,segOrg.lowerScreenY-car2.currHeight));
						//will use segment resizing factor to determine car resizing


						//car2.scal2W=segOrg.lowerScreenW/p2.p1.lowerScreenW;
						break;
					}
					else{
						j++;
						if(j>raceTrack.totalIndex){
							j=0;
						}
					}
				}

				car2.curSegment=p1.p1;
				car2.elapsedTime=p1.runTime;
				car2.lap=p1.lap;
				if(p1.state==1){
					car2.state=State.DOWN;
				}
				else if(p1.state==2){
					car2.state=State.STRAIGHT;
				}
				else
					car2.state=State.UP;
				car2.curSegment=segOrg;
				//finding postion use lap numbe first
				if(car1.lap>car2.lap){
					car1.RacePos=1;
					car2.RacePos=2;
				}
				else if(car1.lap<car2.lap){
					car2.RacePos=1;
					car1.RacePos=2;
				}
				else{
					//same lap use segment number
					if(p2.p1.index>segOrg.index){
						car1.RacePos=1;
						car2.RacePos=2;
					}
					else if(p2.p1.index<segOrg.index){
						car2.RacePos=1;
						car1.RacePos=2;
					}
					else{
						//same segment, will use distance to end of segment in the y for position
						if(car1Dist<car2Dist){
							car2.RacePos=1;
							car1.RacePos=2;
						}
						else{
							car1.RacePos=1;
							car2.RacePos=2;
						}
					}
				}        
				//carRoad.roadCenter=p1.p1.roadCenter;


			}
			else{
				//send ovet all needed data
				p1=new carS();
				p1.p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
				p1.lap=car2.lap;
				p1.runTime=car2.elapsedTime;
				if(car2.state==State.DOWN){
					p1.state=1;
				}
				else if(car2.state==State.STRAIGHT){
					p1.state=2;
				}
				else
					p1.state=3;
				p1.carDist=car2.getPosition().getY()-p1.p1.lowerScreenY;
				car2Dist=p1.carDist;
				GameNet.sendData(p1);
				p2=GameNet.reciveData();
				car1Dist=p2.carDist;
				j=p1.p1.index;
				k=p2.p1.index+1;
				k2=p2.p1.index;
				car1.setActivation(false);
				//go through all visable segments to find if car is on one
				for(int i=0;i<raceTrack.drawDistance-30;i++){
					if(j==k2){
						//segment indexes match, so we grab the segment
						segOrg=raceTrack.track.segments.get(j);
						car1.setActivation(true);

						//calculate the x distance travel on other cars screen
						calcX=car1.startPos.getX()-p2.p1.lowerScreenX;

						//get lenght of segment car will be put one
						double pp=(segOrg.upperScreenX+segOrg.upperScreenW-segOrg.lowerScreenX+segOrg.lowerScreenW);
						//get length of orginal segment
						double ppp=(p2.p1.upperScreenX+p2.p1.upperScreenW-p2.p1.lowerScreenX+p2.p1.lowerScreenW);

						//times x distance by ratio to get actual distance the car will travel on the new segment
						calcX=calcX*(pp/ppp);
						car1.scal2H=(segOrg.lowerScreenY-segOrg.upperScreenY)/(p1.p1.lowerScreenY-p1.p1.upperScreenY);
						car1.currWidth=(int)(car1.getWidth()*car1.scal2H);
						car1.currHeight=(int)(car1.getHeight()*car1.scal2H);
						//if calcX< o car is to the left of x mid, else its to the right
						if(calcX<0)
							car1.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car1.getWidth()/2,segOrg.lowerScreenY-car1.currHeight));
						else
							car1.setCenterPosition(new Vector2D(segOrg.lowerScreenX+calcX+car1.getWidth()/2,segOrg.lowerScreenY-car1.currHeight));


						//car1.scal2W=segOrg.lowerScreenW/p1.p1.lowerScreenW;
						//System.out.println(car1.scal2H+","+car2.scal2W);
						break;
					}
					else{
						j++;
						if(j>raceTrack.totalIndex){
							j=0;
						}
					}
				}
				//set car1 values what we got
				car1.curSegment=p2.p1;
				car1.elapsedTime=p2.runTime;
				car1.lap=p2.lap;
				if(p2.state==1){
					car1.state=State.DOWN;
				}
				else if(p2.state==2){
					car1.state=State.STRAIGHT;
				}
				else
					car1.state=State.UP;
				car1.curSegment=segOrg;

				if(car1.lap>car2.lap){
					car1.RacePos=1;
					car2.RacePos=2;
				}
				else if(car1.lap<car2.lap){
					car2.RacePos=1;
					car1.RacePos=2;
				}
				else{
					//same lap use segment number
					if(segOrg.index>p1.p1.index){
						car1.RacePos=1;
						car2.RacePos=2;
					}
					else if(segOrg.index<p1.p1.index){
						car2.RacePos=1;
						car1.RacePos=2;
					}
					else{
						//same segment, will use distance to end of segment in the y for position
						if(car1Dist<car2Dist){
							car2.RacePos=1;
							car1.RacePos=2;
						}
						else{
							car1.RacePos=1;
							car2.RacePos=2;
						}
					}
				}


			}

		}
		car1Pos = car1.getPosition();
		car2Pos = car2.getPosition();
		checkUserInput ();
		CollisionHandlers(deltaMs);
		//music.loop(0.05, 25);
		
		if (!gameIsRun) {
			iniTime = System.currentTimeMillis();
			gameIsRun = true;
		}

	}

	/**
	 * Render the shapes, and possible shape markup
	 */
	@Override
	public void render(RenderingContext rc) {		
		super.render(rc);
	}

	public void checkUserInput () {
		turnLeft = keyboard.isPressed(KeyEvent.VK_LEFT);
		turnRight = keyboard.isPressed(KeyEvent.VK_RIGHT);
		speedUp = keyboard.isPressed(KeyEvent.VK_UP);
		applybreak = keyboard.isPressed(KeyEvent.VK_DOWN);
	}

	public void CollisionHandlers(long deltaMs) {
		checkCollisions.registerCollisionHandler(hitRumbles);
		checkCollisions.registerCollisionHandler(hitGrass);
		//checkCollisions.applyLawsOfPhysics(deltaMs);
	}

	public static void main(String[] args) {
		//jig.engine.lwjgl.LWResourceFactory.makeCurrentResourceFactory();

		ResourceFactory.getJIGLogger().setLevel(Level.WARNING);
		ResourceFactory.getJIGLogger().getHandlers()[0].setLevel(Level.WARNING);

		Game a;
		a = new Game();
		a.run();
		a.gameframe.getConsole().appendLineToPrompt("fps histogram");

	}

}
