package UltimateRace;

/**
 * UltimateRace: Car Object 
 * F. Doan
 * 
 */

import java.awt.geom.AffineTransform;
import java.util.List;

import jig.engine.ImageResource;
import jig.engine.RenderingContext;
import jig.engine.ResourceFactory;
import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;


public class Car extends VanillaAARectangle {

	public enum State { STRAIGHT, UP, DOWN }

	double speed = 0;
	Vector2D startPos;
	RoadSegment curSegment;
	int lap = 1;
	long elapsedTime = 0;
	
	int time2updateFire = 25;
	int fireCount = 0;
	int time2updateSmoke = 9;
	int smokeCount = 0;
	int time2updateGrass = 7;
	int grassCount = 0;
	
	int curFireFrame = 0;
	int curSmokeFrame = 0;
	boolean setFire = false;
	boolean setSmoke = false;
	boolean offRoad = false;
	boolean grassActive = false;
	
	int RacePos = 1;
	int currWidth;
	int currHeight;
	//for scaling 
	double scalFactorW;
	double scalFactorH;
	double scal2W;
	double scal2H;
	double xPos;
	double yPos;
	public State state = State.STRAIGHT;
	
	List<ImageResource> Fire;
	List<ImageResource> Smoke;
	List<ImageResource> cGrass;

	public Car(String sprite, Vector2D pos, String fire, String smoke, String cutGrass) {
		super(sprite);
		// TODO Auto-generated constructor stub
		position = pos;
		startPos = pos;
		scalFactorH=1;
		scalFactorW=1;
		scal2W=1;
		scal2H=1;
		currWidth=width;
		currHeight=height;
		Smoke = ResourceFactory.getFactory().getFrames(smoke);
		Fire = ResourceFactory.getFactory().getFrames(fire);
		cGrass = ResourceFactory.getFactory().getFrames(cutGrass);	
		xPos = position.getX();
		yPos = position.getY();
	}

	@Override
	public void update(long deltaMs) {
		updateFrames(deltaMs);
		if (Game.gameIsRun) {
			elapsedTime = (System.currentTimeMillis() - Game.iniTime)/1000;
		}
	}

	public void render(final RenderingContext rc) {
		if (!active) {
			return;
		}
		
		
		AffineTransform at = AffineTransform.getTranslateInstance(xPos,yPos);
		/*
		Manipulating the at with at.scale(x,y) will resize the sprite and i dont think we will have to worry about
		keeping track of the resize variable because colision will only happen when it is resied to a certain range
		around the size it is allocated at
		 */
		//System.out.println("scaling with scal2H="+scal2H);
		at.scale(scal2H, scal2H);
		scalFactorW=scal2W;
		scalFactorH=scal2H;

		super.render(rc, at);

		if(renderMarkup){
			imgBoundingRectangle.get(0).render(rc, at);
		}
		
		if (setFire) updateFire(rc);
		if (setSmoke) updateSmoke(rc);
		//updateSmoke(rc);
		if (offRoad) updateOffroad(rc);
		
	}

	private void updateFrames(long deltaMs) {

		if (state == State.UP) {
			setFrame(1);
		} else if (state == State.DOWN) {
			setFrame(2);
		} else {
			setFrame(0);
		}

	}
	
	private void updateFire(RenderingContext rc) {
		fireCount++;
		if (fireCount > time2updateFire) {
			fireCount = 0;
			if (curFireFrame < 2) curFireFrame++;
			else curFireFrame = 0;
		}
		Fire.get(curFireFrame).render(rc, 
				AffineTransform.getTranslateInstance(xPos+15,yPos+25));
		Fire.get(curFireFrame).render(rc, 
				AffineTransform.getTranslateInstance(xPos+50,yPos+25));

	}


	private void updateSmoke(RenderingContext rc) {
		smokeCount++;
		if (smokeCount > time2updateSmoke) {
			smokeCount = 0;
			if (curSmokeFrame < 1) curSmokeFrame = 1;
			else curSmokeFrame = 0;
		}
		Smoke.get(curSmokeFrame).render(rc, 
				AffineTransform.getTranslateInstance(xPos-25,yPos));
		
	}
	
	private void updateOffroad(RenderingContext rc){
		grassCount++;
		if (grassCount > time2updateGrass) {
			grassCount = 0;
			grassActive = !grassActive;
		}
		if (grassActive) {
			cGrass.get(0).render(rc, AffineTransform.getTranslateInstance(xPos,yPos));
		}
	}
}
