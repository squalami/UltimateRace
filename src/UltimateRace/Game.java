package UltimateRace;
/**
 * CS447/547 Project 2 
 * 
 * by Fredton Doan, Brian Lamb & Perry Miller
 * 
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


public class Game extends StaticScreenGame {

	static final int WORLD_WIDTH = 640;
	static final int WORLD_HEIGHT = 480;
	static int level = 1;
	static int maxLevel = 3;
	static int lap2Win = 2;
	static boolean turnLeft = false;
	static boolean turnRight = false;
	static boolean speedUp = false;
	static boolean applybreak = false;
	static boolean runUpdate = false;
	static boolean gameIsRun = false;
	static boolean startGame = false;
	static boolean displayMenu = true;
	static boolean standRevup = false;
	static boolean finishCurLevel = false;
	static boolean displayNextLevel = false;

	static long iniTime = 0;

	static final String XMLFILE = "resources/sprites.xml";
	static final String SPRITE_SHEET = "resources/sprites.png";

	VanillaPhysicsEngine physics;
	AudioStream music;

	//new stuff needed for more than 2 playe multi
	Car TrueCar;        //since we have may cars now we will use this variable to refer to the cleints actual car
	int carNum;
	Car otherCar1;      //These are needed to simplyfy code in networking. see networking code bellow
	Car otherCar2;
	int NumberOfC;

	Car car1;
	Car car2;
	Car car3;

	CarProjection dummycar1;
	CarProjection dummycar2;
	CarProjection dummycar3;

	RaceTrack raceTrack;
	Road road;
	Grass grass;
	Rumbles rumbles;
	Background background;
	SpeedBoost boost;
	OffroadObjects cactus;
	OffroadObjects billboard;
	OffroadObjects bush;
	OffroadObjects tree1;
	OffroadObjects tree2;
	OffroadObjects stump;
	Oil sleekOil;

	CarHitSpeedBoost hitSpeedBoost;
	CarHitOffroadObjects hitOffroadObject;
	CarHitCar carHit1;
	CarHitCar carHit2;
	CarHitOil hitOil;
	static Vector2D car1Pos;   // used for network race track update
	static Vector2D car2Pos;

	BodyLayer<Car> car1Layer;
	BodyLayer<Car> car2Layer;
	BodyLayer<Car> car3Layer;
	BodyLayer<CarProjection> otherCarLayer;

	BodyLayer<RaceTrack> rtLayer;
	BodyLayer<RaceTrack> rumbleLayer;
	BodyLayer<Grass> grassLayer;
	BodyLayer<Road> roadLayer;
	BodyLayer<Rumbles> rbLayer;
	BodyLayer<SpeedBoost> boostLayer;
	BodyLayer<OffroadObjects> offroadLayer;
	BodyLayer<Oil> oilLayer;

	BodyLayer<Background> backgroundLayer;
	NetworkC GameNet;
	boolean isServ;
	boolean isNet=false;
	String IP;
	RoadSegment carRoad;

	VanillaPhysicsEngine checkCollisions = new VanillaPhysicsEngine();

	double car1X = WORLD_WIDTH/2 - 180;
	double carY = WORLD_HEIGHT-70;
	double car2X = 3*WORLD_WIDTH/4 - 200;
	double car3X = 3*WORLD_WIDTH/4 - 60;

	public Game() {
		super(WORLD_WIDTH, WORLD_HEIGHT, false);

		physics = new VanillaPhysicsEngine();
		ResourceFactory.getFactory().loadSheet(SPRITE_SHEET, XMLFILE);

		background = new Background(SPRITE_SHEET + "#background");
		backgroundLayer = new AbstractBodyLayer.IterativeUpdate<Background>();
		backgroundLayer.add(background);
		gameObjectLayers.add(backgroundLayer);

		road = new Road(SPRITE_SHEET + "#trans",gameframe);
		rumbles = new Rumbles(SPRITE_SHEET + "#trans",gameframe);
		grass = new Grass(SPRITE_SHEET + "#trans",gameframe);

		car1 = new Car(SPRITE_SHEET + "#redCar",
				new Vector2D(car1X,carY),
				SPRITE_SHEET + "#fire",
				SPRITE_SHEET + "#smoke",
				SPRITE_SHEET + "#cutgrass");


		offroadLayer = new AbstractBodyLayer.IterativeUpdate<OffroadObjects>();
		boostLayer = new AbstractBodyLayer.IterativeUpdate<SpeedBoost>();
		oilLayer =  new AbstractBodyLayer.IterativeUpdate<Oil>();
		otherCarLayer = new AbstractBodyLayer.IterativeUpdate<CarProjection>();

		System.out.println("");
		System.out.println("Enter 1 for single player demo mode or enter 2 for mulitplayer mode");
		System.out.print("->");
		Scanner s3=new Scanner(System.in);
		int GameChoice=s3.nextInt();
		if(GameChoice==1){
			isNet=false;
		}
		else{
			isNet=true;
		}

		if(isNet){
			dummycar1 = new CarProjection(SPRITE_SHEET + "#redCar",
					new Vector2D(car3X,carY),
					SPRITE_SHEET + "#fire",
					SPRITE_SHEET + "#smoke",
					SPRITE_SHEET + "#cutgrass");
			car2 = new Car(SPRITE_SHEET + "#greenCar",
					new Vector2D(car2X,carY),
					SPRITE_SHEET + "#fire",
					SPRITE_SHEET + "#smoke",
					SPRITE_SHEET + "#cutgrass");
			dummycar2 = new CarProjection(SPRITE_SHEET + "#greenCar",
					new Vector2D(car2X,carY),
					SPRITE_SHEET + "#fire",
					SPRITE_SHEET + "#smoke",
					SPRITE_SHEET + "#cutgrass");

			car3 = new Car(SPRITE_SHEET + "#orangeCar",
					new Vector2D(car3X,carY),
					SPRITE_SHEET + "#fire",
					SPRITE_SHEET + "#smoke",
					SPRITE_SHEET + "#cutgrass");

			dummycar3 = new CarProjection(SPRITE_SHEET + "#orangeCar",
					new Vector2D(car3X,carY),
					SPRITE_SHEET + "#fire",
					SPRITE_SHEET + "#smoke",
					SPRITE_SHEET + "#cutgrass");
			//new multiplayer setup for more than 3 players
			otherCarLayer.add(dummycar1);
			otherCarLayer.add(dummycar2);
			otherCarLayer.add(dummycar3);
			Scanner s2=new Scanner(System.in);
			System.out.println("Host or client: ");
			IP=s2.nextLine();
			if(IP.equalsIgnoreCase("host")){
				System.out.println("Number of other players: ");
				int playerNum=s2.nextInt();
				NumberOfC=playerNum;
				GameNet=new NetworkC("none",playerNum);
				isServ=true;
				carS s1;
				s1=new carS();
				s1.carNum=2;
				s1.carN=NumberOfC;
				GameNet.sendData2(s1,0);
				if(NumberOfC>1){
					s1.carNum=3;
					GameNet.sendData2(s1,1);
				}
				raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
						car1,  // this to be modified in network mode
						grass,
						road,
						rumbles
						);
			}
			else{
				System.out.println("Host ip address: ");
				IP=s2.nextLine();
				GameNet=new NetworkC(IP,0);
				isServ=false;
				carS s1;
				s1=GameNet.reciveData();
				carNum=s1.carNum;
				NumberOfC=s1.carN;
				if(s1.carNum==2){
					otherCar1=car1;
					otherCar2=car3;
					raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
							car2,  // this to be modified in network mode
							grass,
							road,
							rumbles
							);
					TrueCar=car2;
					//TO do setup 2nd car stuff
				}
				else{                                
					otherCar1=car1;
					otherCar2=car2;
					raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
							car3,  // this to be modified in network mode
							grass,
							road,
							rumbles
							);
					TrueCar=car3;
					//TO do setup 3rd car stuff
				}
			}
		}
		else{

			raceTrack = new RaceTrack(SPRITE_SHEET + "#trans",gameframe,
					car1,  // this to be modified in network mode
					grass,
					road,
					rumbles
					);
		}

		rtLayer = new AbstractBodyLayer.IterativeUpdate<RaceTrack>();
		rtLayer.add(raceTrack);
		gameObjectLayers.add(rtLayer);
		physics.manageViewableSet(rtLayer);

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
		gameObjectLayers.add(car1Layer);
		physics.manageViewableSet(car1Layer);

		if (isNet) {
			car2Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
			car2Layer.add(car2);
			gameObjectLayers.add(car2Layer);
			physics.manageViewableSet(car2Layer);
			car3Layer = new AbstractBodyLayer.IterativeUpdate<Car>();
			car3Layer.add(car3);
			gameObjectLayers.add(car3Layer);
			physics.manageViewableSet(car3Layer);
		}

		for (int i=90; i < raceTrack.totalIndex - 150; ) {
			int rd = 0;
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				boost = new SpeedBoost(SPRITE_SHEET + "#boost", raceTrack,i,(rd++)%3);
				boostLayer.add(boost);
				i += 250;
			} else {
				i++;
			}			
		}

		for (int i=170; i < raceTrack.totalIndex - 150; ) {
			int rd = 0;
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				sleekOil = new Oil(SPRITE_SHEET + "#oil", raceTrack,i,(rd++)%5);
				oilLayer.add(sleekOil);
				i += 200;
			} else {
				i++;
			}			
		}

		int repeat = 35;
		for (int i=15; i < raceTrack.totalIndex - 150;) {
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,false);
				offroadLayer.add(tree1);
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,true);
				offroadLayer.add(tree1);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				billboard = new OffroadObjects(SPRITE_SHEET+"#billboard",raceTrack,i,false);
				offroadLayer.add(billboard);
				i += repeat;
			}
			//trees
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {				
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,false);
				offroadLayer.add(tree1);
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,true);
				offroadLayer.add(tree1);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,false);
				offroadLayer.add(tree2);
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,true);
				offroadLayer.add(tree2);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {				
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,false);
				offroadLayer.add(tree1);
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,true);
				offroadLayer.add(tree1);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,false);
				offroadLayer.add(tree2);
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,true);
				offroadLayer.add(tree2);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {				
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,false);
				offroadLayer.add(tree1);
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,true);
				offroadLayer.add(tree1);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,false);
				offroadLayer.add(tree2);
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,true);
				offroadLayer.add(tree2);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				cactus = new OffroadObjects(SPRITE_SHEET+"#cactus",raceTrack,i,false);
				offroadLayer.add(cactus);
				cactus = new OffroadObjects(SPRITE_SHEET+"#cactus",raceTrack,i,true);
				offroadLayer.add(cactus);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				bush = new OffroadObjects(SPRITE_SHEET+"#bush",raceTrack,i,true);
				offroadLayer.add(bush);
				bush = new OffroadObjects(SPRITE_SHEET+"#bush",raceTrack,i,false);
				offroadLayer.add(bush);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				//trees
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,false);
				offroadLayer.add(tree1);
				tree1 = new OffroadObjects(SPRITE_SHEET+"#tree1",raceTrack,i,true);
				offroadLayer.add(tree1);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,false);
				offroadLayer.add(tree2);
				tree2 = new OffroadObjects(SPRITE_SHEET+"#tree2",raceTrack,i,true);
				offroadLayer.add(tree2);
				i += repeat;
			}
			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				billboard = new OffroadObjects(SPRITE_SHEET+"#billboard",raceTrack,i,true);
				offroadLayer.add(billboard);
				i += repeat;
			}

			if (BuildTrack.curChk.containsKey(i) && !BuildTrack.curChk.get(i)) {
				stump = new OffroadObjects(SPRITE_SHEET+"#stump",raceTrack,i,false);	
				offroadLayer.add(stump);
				i += repeat;
				//*/

			} else {
				i++;
			}

		}

		gameObjectLayers.add(offroadLayer);
		physics.manageViewableSet(offroadLayer);
		gameObjectLayers.add(boostLayer);
		physics.manageViewableSet(boostLayer);
		gameObjectLayers.add(oilLayer);
		physics.manageViewableSet(oilLayer);
		gameObjectLayers.add(otherCarLayer);

		if (isNet) {
			if (isServ) {
				gameObjectLayers.add(new GameUI(car1));
				hitOil = new CarHitOil(car1,oilLayer,raceTrack);
				hitSpeedBoost = new CarHitSpeedBoost(car1,boostLayer,raceTrack);
				hitOffroadObject = new CarHitOffroadObjects(car1,offroadLayer,raceTrack);
				carHit1=new CarHitCar(car1,car2);
				if(NumberOfC>1){
					carHit2=new CarHitCar(car1,car3);
				}
			} else {

				if(carNum==2) {
					gameObjectLayers.add(new GameUI(car2));
					hitOil = new CarHitOil(car2,oilLayer,raceTrack);
					hitSpeedBoost = new CarHitSpeedBoost(car2,boostLayer,raceTrack);
					hitOffroadObject = new CarHitOffroadObjects(car2,offroadLayer,raceTrack);
					carHit1=new CarHitCar(car2,car1);
					if(NumberOfC>1){
						carHit2=new CarHitCar(car2,car3);
					}

				}
				else if(carNum==3) {
					gameObjectLayers.add(new GameUI(car3));
					hitSpeedBoost = new CarHitSpeedBoost(car3,boostLayer,raceTrack);
					hitOil = new CarHitOil(car3,oilLayer,raceTrack);
					hitOffroadObject = new CarHitOffroadObjects(car3,offroadLayer,raceTrack);
					carHit1=new CarHitCar(car3,car1);
					carHit2=new CarHitCar(car3,car2);

				}
				else {
					gameObjectLayers.add(new GameUI(car2));
					hitOil = new CarHitOil(car2,oilLayer,raceTrack);
					hitSpeedBoost = new CarHitSpeedBoost(car2,boostLayer,raceTrack);
					hitOffroadObject = new CarHitOffroadObjects(car2,offroadLayer,raceTrack);
					carHit1=new CarHitCar(car2,car1);
				}
			}
		} else { // single player mode
			gameObjectLayers.add(new GameUI(car1));
			hitSpeedBoost = new CarHitSpeedBoost(car1,boostLayer,raceTrack);
			hitOil = new CarHitOil(car1,oilLayer,raceTrack);
			hitOffroadObject = new CarHitOffroadObjects(car1,offroadLayer,raceTrack);

		}

	}

	public void resetGame() {
		if (level < maxLevel) {
			level++;
		}
		raceTrack.resetRaceTrack();
	}

	public void update(long deltaMs) {
		super.update(deltaMs);

		if (finishCurLevel) {
			resetGame();
			finishCurLevel = false;
			displayNextLevel = true;
		}
		//new mulitplayer stuff

		//updateCarFrames();
		//car1Pos = car1.getPosition();
		//car2Pos = car2.getPosition();
		checkUserInput ();
		CollisionHandlers(deltaMs);

	}

	//3 car base race postioning finding, will try to far car farthest ahead then use 2 car for other 2
	public void Positioning(Car c1,Car c2,Car c3,RoadSegment seg1,RoadSegment seg2,RoadSegment seg3){
		if(c1.lap>c2.lap){
			if(c1.lap>c3.lap){
				c1.RacePos=1;
				doPos(c2,c3,seg2,seg3);
				c2.RacePos++;
				c3.RacePos++;
			}
			else if(c1.lap<c3.lap){
				c3.RacePos=1;
				c1.RacePos=2;
				c2.RacePos=3;
			}
			else{
				c2.RacePos=3;
				doPos(c1,c3,seg1,seg3);

			}
		}
		else if(c1.lap<c2.lap){
			if(c2.lap>c3.lap){
				c2.RacePos=1;
				doPos(c1,c3,seg1,seg3);
				c1.RacePos++;
				c3.RacePos++;
			}
			else if(c2.lap<c3.lap){
				c3.RacePos=1;
				c1.RacePos=3;
				c2.RacePos=2;
			}
			else{
				c1.RacePos=3;
				doPos(c2,c3,seg2,seg3);
			}
		}
		else{
			if(c3.lap>c2.lap){
				c3.RacePos=1;
				doPos(c1,c2,seg1,seg2);
				c1.RacePos++;
				c2.RacePos++;
			}
			else if(c2.lap>c3.lap){
				c3.RacePos=3;
				doPos(c1,c2,seg1,seg2);
			}
			else{
				//Laps same so we do segments
				if(seg1.index>seg2.index){
					if(seg1.index>seg3.index){
						c1.RacePos=1;
						doPos(c2,c3,seg2,seg3);
						c2.RacePos++;
						c3.RacePos++;
					}
					else if(seg1.index<seg3.index){
						c3.RacePos=1;
						c1.RacePos=2;
						c2.RacePos=3;
					}
					else{
						c2.RacePos=3;
						doPos(c1,c3,seg1,seg3);
					}
				}
				else if(seg1.index<seg2.index){
					if(seg2.index>seg3.index){
						c2.RacePos=1;
						doPos(c1,c3,seg1,seg3);
						c1.RacePos++;
						c3.RacePos++;
					}
					else if(seg2.index<seg3.index){
						c3.RacePos=1;
						c2.RacePos=2;
						c1.RacePos=3;
					}
					else{
						c2.RacePos=3;
						doPos(c1,c3,seg1,seg3);
					}
				}
				else{
					if(seg3.index>seg2.index){
						c3.RacePos=1;
						doPos(c1,c2,seg1,seg2);
						c1.RacePos++;
						c2.RacePos++;
					}
					else if(seg3.index<seg2.index){
						c3.RacePos=3;
						doPos(c1,c2,seg1,seg2);
					}
					else{
						//same lap and segment now we look at postion
						if(seg1.upperScreenY>seg2.upperScreenY){
							if(seg1.upperScreenY>seg3.upperScreenY){
								c1.RacePos=1;
								doPos(c2,c3,seg2,seg3);
								c3.RacePos++;
								c2.RacePos++;
							}
							else if(seg1.upperScreenY<seg3.upperScreenY){
								c3.RacePos=1;
								c1.RacePos=2;
								c2.RacePos=3;
							}
							else{
								c2.RacePos=3;
								doPos(c1,c3,seg1,seg3);
							}
						}
						else if(seg1.upperScreenY<seg2.upperScreenY){
							if(seg2.upperScreenY>seg3.upperScreenY){
								c2.RacePos=1;
								doPos(c1,c3,seg1,seg3);
								c1.RacePos++;
								c3.RacePos++;
							}
							else if(seg2.upperScreenY<seg3.upperScreenY){
								c3.RacePos=1;
								c2.RacePos=2;
								c1.RacePos=3;
							}
							else{
								c2.RacePos=3;
								doPos(c1,c3,seg1,seg3);
							}
						}
						else{
							if(seg3.upperScreenY>seg2.lowerScreenY){
								c3.RacePos=1;
								doPos(c1,c2,seg1,seg2);
								c1.RacePos++;
								c2.RacePos++;
							}
							else if(seg3.upperScreenY<seg2.upperScreenY){
								c3.RacePos=3;
								doPos(c1,c2,seg1,seg2);
							}
							else{
								c1.RacePos=1;
								c2.RacePos=1;
								c3.RacePos=1;
							}
						}
					}
				}
			}
		}
	}

	//Two car calculations
	public void doPos(Car c1,Car c2,RoadSegment seg1,RoadSegment seg2){
		if(c1.lap>c2.lap){
			c1.RacePos=1;
			c2.RacePos=2;
		}
		else if(c1.lap<c2.lap){
			c2.RacePos=1;
			c1.RacePos=2;
		}
		else{
			//same lap use segment number
			if(seg1.index>seg2.index){
				c1.RacePos=1;
				c2.RacePos=2;
			}
			else if(seg1.index<seg2.index){
				c2.RacePos=1;
				c1.RacePos=2;
			}
			else{
				//same segment, will use current segments upper x value;
				if(seg1.upperScreenY>seg2.upperScreenY){
					c2.RacePos=2;
					c1.RacePos=1;
				}
				else{
					c1.RacePos=1;
					c2.RacePos=2;
				}
			}
		}
	}



	/**
	 * Render the shapes, and possible shape markup
	 */
	@Override
	public void render(RenderingContext rc) {		
		super.render(rc);
		updateCarFrames();
	}

	public void checkUserInput () {
		turnLeft = keyboard.isPressed(KeyEvent.VK_LEFT);
		turnRight = keyboard.isPressed(KeyEvent.VK_RIGHT);
		speedUp = keyboard.isPressed(KeyEvent.VK_UP);
		applybreak = keyboard.isPressed(KeyEvent.VK_DOWN);
		standRevup = keyboard.isPressed(KeyEvent.VK_SPACE);
		if(isNet){
			if(isServ){
				if (keyboard.isPressed(KeyEvent.VK_F1)) {
					startGame = true;
					displayMenu = false;
					displayNextLevel = false;
					carS p1=new carS();
					p1.carN=-1;
					GameNet.sendData2(p1,0);
					if(NumberOfC>1){
						GameNet.sendData2(p1,1);
					}
				}
			}
		}
		else{
			if (keyboard.isPressed(KeyEvent.VK_F1)) {
				startGame = true;
				displayMenu = false;
				displayNextLevel = false;
			}
		}
	}

	public void CollisionHandlers(long deltaMs) {
		checkCollisions.registerCollisionHandler(hitOil);
		checkCollisions.registerCollisionHandler(hitSpeedBoost);
		checkCollisions.registerCollisionHandler(hitOffroadObject);
		if (carHit1 != null) {
			checkCollisions.registerCollisionHandler(carHit1);
		}
		if (carHit2 != null) {
			checkCollisions.registerCollisionHandler(carHit2);
		}
		checkCollisions.applyLawsOfPhysics(deltaMs);
	}

	public void updateCarFrames() {
		carS clientSentPackage[] = new carS[2];
		carS serverSentPackage[] = new carS[2];
		RoadSegment oppSeg = new RoadSegment(State.STRAIGHT,false,0);
		double calcX;
		//normal 2 player stuff

		if(isNet){
			if(isServ){
				//more than 1 car stuff
				//
				//reciving data 
				clientSentPackage[0] = GameNet.readData2(0); // client 1;
				//RoadSegment tp = clientSentPackage[0].p1;

				//System.out.println(tp.index+", road center:"+tp.roadCenter);
				//System.out.println("car2 x-coord: "+car2.getCenterPosition().getX());
				if(NumberOfC > 1){
					clientSentPackage[1]=GameNet.readData2(1); // client 2 data

				}

				//sending the data
				serverSentPackage[0]=new carS();
				serverSentPackage[0].p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
				serverSentPackage[0].lap=car1.lap;
				serverSentPackage[0].runTime=car1.elapsedTime;

				if(car1.state==State.DOWN){
					serverSentPackage[0].state=1;
				}
				else if(car1.state==State.STRAIGHT){
					serverSentPackage[0].state=2;
				}
				else
					serverSentPackage[0].state=3;


				serverSentPackage[0].carDist=car1.getPosition().getY()-clientSentPackage[0].p1.lowerScreenY;
				serverSentPackage[0].carNum=1;
				serverSentPackage[1] = clientSentPackage[1]; // client 2

				GameNet.sendData2(serverSentPackage[0],0);

				if(NumberOfC>1){
					GameNet.sendData2(serverSentPackage[1],0); // send to client 1 info of client 2
					serverSentPackage[1]=clientSentPackage[0];
					GameNet.sendData2(serverSentPackage[0],1); // send client 2 info of of server
					GameNet.sendData2(serverSentPackage[1],1); // send client 2 info of client 1
				}

				//start calcualtions
				dummycar1.setActivation(false);
				car2.setActivation(false);
				dummycar2.setActivation(false);
				car3.setActivation(false);
				dummycar3.setActivation(false);
				//int car2Seg;
				//int car3Seg;
				carS car2Data;      
				carS car3Data;
				car3Data=new carS();
				car2Data=clientSentPackage[0];
				if(NumberOfC>1){
					car3Data=clientSentPackage[1];
				}

				//System.out.println("My index: "+serverSentPackage[0].p1.index+", car 2 index: "+car2Data.p1.index);

				if(car2Data.p1.index < (serverSentPackage[0].p1.index + 140) && car2Data.p1.index > serverSentPackage[0].p1.index-2 && !(car2Data.p1.hill < 0 && serverSentPackage[0].p1.hill >0 )){
					//*
					

					oppSeg = raceTrack.track.segments.get(car2Data.p1.index);  // the segment to project on 

					//System.out.println("oppSeg.roadCenter: "+oppSeg.roadCenter);
					//working scale
					car2.scal2H = oppSeg.roadWidth / serverSentPackage[0].p1.roadWidth;

					//dummycar2.scal2H = car2.scal2H;


					calcX = car2.startPos.getX() - clientSentPackage[0].p1.roadCenter; // client segment x	
					//System.out.println("pre project calcX: "+calcX);
					
					calcX = calcX*car2.scal2H;
					//System.out.println("post2 project calcX: "+calcX);


					car2.currHeight = (int)((car2.getHeight() * car2.scal2H));
					car2.currWidth = (int)((car2.getWidth() * car2.scal2H));

					//car2.setCenterPosition(new Vector2D(car2Data.p1.roadCenter ,segOrg.lowerScreenY-car2.currHeight));

					double calX = oppSeg.roadCenter + calcX + car2.getWidth()/2;
					
					double calY = oppSeg.roadY - car2.currHeight;

					car2.setCenterPosition(new Vector2D(calX,calY));
					//dummycar2.setCenterPosition(new Vector2D(calX,calY));
					//dummycar2.setActivation(true);
					car2.setActivation(true);
					/*
						System.out.println("car 2 y: "+ (oppSeg.lowerScreenY-car2.currHeight));
						System.out.println("car1: "+car1.getCenterPosition());
						System.out.println("car2: "+car2.getCenterPosition());
						//do ofther car2 stuff
						//*/
				}
				if (NumberOfC > 1){
					if(car3Data.p1.index < (serverSentPackage[0].p1.index + 140) && car3Data.p1.index > serverSentPackage[0].p1.index-2){
						

						oppSeg = raceTrack.track.segments.get(car2Data.p1.index);// the segment to project on 
						//working scale
						car3.scal2H = oppSeg.roadWidth / serverSentPackage[0].p1.roadWidth;
						//dummycar3.scal2H = car3.scal2H;

						calcX = car3.startPos.getX() - clientSentPackage[1].p1.roadCenter;
						calcX = calcX * car3.scal2H;

						car3.currHeight=(int)(car3.getHeight()*car3.scal2H);
						car3.currWidth=(int)(car3.getWidth()*car3.scal2H);

						double calX = oppSeg.roadCenter + calcX + car3.getWidth()/2;
						double calY = oppSeg.roadY - car3.currHeight;
						
						car3.setCenterPosition(new Vector2D(calX,calY));
						//dummycar3.setCenterPosition(new Vector2D(calX,calY));
						//dummycar3.setActivation(true);
						car3.setActivation(true);

						//do other car 3 stuff
					}
				}

				car2.curSegment=car2Data.p1;
				car2.elapsedTime=car2Data.runTime;
				car2.lap=car2Data.lap;
				if(car2Data.state==1){
					car2.state=State.DOWN;
				}
				else if(car2Data.state==2){
					car2.state=State.STRAIGHT;
				}
				else
					car2.state=State.UP;
				if(NumberOfC>1){
					car3.curSegment=car3Data.p1;
					car3.elapsedTime=car3Data.runTime;
					car3.lap=car3Data.lap;
					if(car3Data.state==1){
						car3.state=State.DOWN;
					}
					else if(car3Data.state==2){
						car3.state=State.STRAIGHT;
					}
					else
						car3.state=State.UP;
					Positioning(car1,car2,car3,serverSentPackage[0].p1,car2.curSegment,car3.curSegment);
				}
				else{
					doPos(car1,car2,serverSentPackage[0].p1,car2.curSegment);
				}

			}
			else{  // clients

				//more than 2 player stuff
				//
				clientSentPackage[0]=new carS();
				clientSentPackage[0].p1=raceTrack.findSegment(raceTrack.curZpos+raceTrack.carZ);
				clientSentPackage[0].lap=TrueCar.lap;
				clientSentPackage[0].runTime=TrueCar.elapsedTime;

				if(TrueCar.state==State.DOWN){
					clientSentPackage[0].state=1;
				}
				else if(TrueCar.state==State.STRAIGHT){
					clientSentPackage[0].state=2;
				}
				else
					clientSentPackage[0].state=3;

				clientSentPackage[0].carDist=TrueCar.getPosition().getY()-clientSentPackage[0].p1.lowerScreenY;
				clientSentPackage[0].carNum=carNum;
				GameNet.sendData(clientSentPackage[0]);
				serverSentPackage[0]=GameNet.reciveData();

				carS other1Data;      
				carS other2Data;
				other2Data=new carS();
				
				dummycar1.setActivation(false);
				dummycar2.setActivation(false);
				dummycar3.setActivation(false);
				otherCar1.setActivation(false);
				otherCar2.setActivation(false);

				if(NumberOfC>1){
					serverSentPackage[1]=GameNet.reciveData();

					if(serverSentPackage[0].carNum<serverSentPackage[1].carNum){
						other1Data=serverSentPackage[0];
						other2Data=serverSentPackage[1];
					}
					else{
						other1Data=serverSentPackage[1];
						other2Data=serverSentPackage[0];
					}
				}
				else{
					other1Data=serverSentPackage[0];
				}


				if(other1Data.carN == -1){ // network start
					startGame = true;
					displayMenu = false;
					displayNextLevel = false;
				}
				else{
					if(other1Data.p1.index < (clientSentPackage[0].p1.index+145) && other1Data.p1.index > clientSentPackage[0].p1.index-2){ // me & the other on the sam
						

						oppSeg = raceTrack.track.segments.get(other1Data.p1.index);
						//System.out.println("oppSeg.roadCenter: "+oppSeg.roadCenter);

						otherCar1.scal2H = oppSeg.roadWidth / clientSentPackage[0].p1.roadWidth;
						//dummycar1.scal2H = otherCar1.scal2H;

						calcX = otherCar1.startPos.getX() - serverSentPackage[0].p1.roadCenter;
						
						//System.out.println("pre project calcX: "+calcX);
						calcX = calcX*otherCar1.scal2H;
						//System.out.println("post2 project calcX: "+calcX);

						otherCar1.currHeight = (int)(otherCar1.getHeight() * otherCar1.scal2H);
						otherCar1.currWidth = (int)(otherCar1.getWidth() * otherCar1.scal2H);

						double calX = oppSeg.roadCenter + calcX + otherCar1.getWidth()/2;
						
						double calY = oppSeg.roadY - otherCar1.currHeight;
						
						/*
						System.out.println("car 2 y: "+ (oppSeg.lowerScreenY-otherCar1.currHeight));
						System.out.println("server car1: "+otherCar1.getCenterPosition());
						System.out.println("car2: "+car2.getCenterPosition());
						//*/
						//do ofther car2 stuff
						otherCar1.setCenterPosition(new Vector2D(calX,calY));
						//dummycar1.setCenterPosition(new Vector2D(calX,calY));
						//dummycar1.setActivation(true);
						otherCar1.setActivation(true);
						//do ofther car2 stuff
					}
					if(NumberOfC>1){
						if(other2Data.p1.index < (clientSentPackage[0].p1.index+145) && other2Data.p1.index > clientSentPackage[0].p1.index-2){
							
							
							oppSeg = raceTrack.track.segments.get(other2Data.p1.index);
							//System.out.println("oppSeg.roadCenter: "+oppSeg.roadCenter);

							otherCar2.scal2H = oppSeg.roadWidth / clientSentPackage[0].p1.roadWidth;
							/*if (carNum == 2)
								dummycar3.scal2H = otherCar2.scal2H;
							else
								dummycar2.scal2H = otherCar2.scal2H;
							 */

							calcX = otherCar2.startPos.getX() - serverSentPackage[1].p1.roadCenter;
							//System.out.println("pre project calcX: "+calcX);
							calcX = calcX*otherCar2.scal2H;
							//System.out.println("post2 project calcX: "+calcX);

							otherCar2.currHeight = (int)(otherCar2.getHeight() * otherCar2.scal2H);
							otherCar2.currWidth = (int)(otherCar2.getWidth() * otherCar2.scal2H);

							double calX = oppSeg.roadCenter + calcX + otherCar2.getWidth()/2;
							double calY = oppSeg.roadY - otherCar2.currHeight;
							otherCar2.setCenterPosition(new Vector2D(calX,calY));
							otherCar2.setActivation(true);
							/*
							if (carNum == 2){
								dummycar3.setCenterPosition(new Vector2D(calX,calY));
								dummycar3.setActivation(true);
							} else{
								dummycar2.setCenterPosition(new Vector2D(calX,calY));
								dummycar2.setActivation(true);
							}
							 */
							//do other car 3 stuff
						}
					}

					otherCar1.curSegment=other1Data.p1;
					otherCar1.elapsedTime=other1Data.runTime;
					otherCar1.lap=other1Data.lap;
					if(other1Data.state==1){
						otherCar1.state=State.DOWN;
					}
					else if(other1Data.state==2){
						otherCar1.state=State.STRAIGHT;
					}
					else
						otherCar1.state=State.UP;
					if(NumberOfC>1){
						otherCar2.curSegment=other2Data.p1;
						otherCar2.elapsedTime=other2Data.runTime;
						otherCar2.lap=other2Data.lap;
						if(other2Data.state==1){
							otherCar2.state=State.DOWN;
						}
						else if(other2Data.state==2){
							otherCar2.state=State.STRAIGHT;
						}
						else
							otherCar2.state=State.UP;
						//DO positioning
						Positioning(TrueCar,otherCar1,otherCar2,clientSentPackage[0].p1,otherCar1.curSegment,otherCar2.curSegment);
					}
					else{
						doPos(car1,car2,car1.curSegment,clientSentPackage[0].p1);
					}
				}
			}
		}
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
