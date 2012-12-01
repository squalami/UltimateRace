package UltimateRace;

/**
 * UltimateRace: Car Object 
 * F. Doan
 * 
 */

import jig.engine.physics.vpe.VanillaAARectangle;
import jig.engine.util.Vector2D;


public class Car extends VanillaAARectangle {
	
	public enum State { STRAIGHT, UP, DOWN }
	
	double speed = 0;
	Vector2D startPos;
	RoadSegment curSegment;
	int lap = 0;
	long elapsedTime = 0;
        int RacePos;
	
	public State state = State.STRAIGHT;
	
	public Car(String sprite, Vector2D pos) {
		super(sprite);
		// TODO Auto-generated constructor stub
		position = pos;
		startPos = pos;

	}

	@Override
	public void update(long deltaMs) {
		updateFrames(deltaMs);
				
	}
	
	public void render(final RenderingContext rc) {
		if (!active) {
			return;
		}

		AffineTransform at = AffineTransform.getTranslateInstance(position
				.getX(), position.getY());
		/*
		Manipulating the at with at.scale(x,y) will resize the sprite and i dont think we will have to worry about
		keeping track of the resize variable because colision will only happen when it is resied to a certain range
		around the size it is allocated at
		*/
		super.render(rc, at);

		if (renderMarkup) {
			imgBoundingRectangle.get(0).render(rc, at);
		}
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
	

}
