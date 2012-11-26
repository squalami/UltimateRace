package UltimateRace;
/**
 * CS447/547 Project 2 by F.Doan
 * 
 */

import java.util.logging.Level;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.hli.StaticScreenGame;
import jig.engine.physics.AbstractBodyLayer;
import jig.engine.physics.BodyLayer;
import jig.engine.physics.vpe.VanillaPhysicsEngine;
import jig.engine.util.Vector2D;

import java.awt.event.KeyEvent;



public class Game extends StaticScreenGame {
	
	static final int WORLD_WIDTH = 640;
	static final int WORLD_HEIGHT = 480;
	static int level = 1;
	static boolean turnLeft = false;
	static boolean turnRight = false;
	static boolean speedUp = false;
	static boolean applybreak = false;
	static boolean runUpdate = false;
	
	int totalCars      = 2;  
	
	static final String XMLFILE = "resources/sprites.xml";
	static final String SPRITE_SHEET = "resources/sprites.png";
	
	VanillaPhysicsEngine physics;

	Car car1;
	Car car2;
	RaceTrack raceTrack;
	Road road;
	Grass grass;
	Rumbles rumbles;
	Background background;
	
	static Vector2D car1Pos;   // used for network race track update
	static Vector2D car2Pos;
	
	BodyLayer<Car> car1Layer;
	BodyLayer<Car> car2Layer;
	BodyLayer<RaceTrack> rtLayer;
	BodyLayer<RaceTrack> rumbleLayer;
	BodyLayer<Grass> grassLayer;
	BodyLayer<Road> roadLayer;
	BodyLayer<Rumbles> rbLayer;
	
	BodyLayer<Background> backgroundLayer;
	
	CarHitRumbles hitRumbles;
	CarHitGrass hitGrass;
	
	VanillaPhysicsEngine checkCollisions = new VanillaPhysicsEngine();
	
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
				new Vector2D((WORLD_WIDTH/2 - 140),WORLD_HEIGHT-45));
		car1Pos = car1.getPosition();
		
		car2 = new Car(SPRITE_SHEET + "#greenCar",
				new Vector2D((3*WORLD_WIDTH/4 - 90),WORLD_HEIGHT-45));
		car2Pos = car2.getPosition();
		
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
		
		hitRumbles = new CarHitRumbles(car1,rbLayer);
		hitGrass = new CarHitGrass(car1,grassLayer);


	}
	
	public void update(long deltaMs) {
		super.update(deltaMs);
		car1Pos = car1.getPosition();
		car2Pos = car2.getPosition();
		checkUserInput ();
		CollisionHandlers(deltaMs);
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
		checkCollisions.applyLawsOfPhysics(deltaMs);
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
